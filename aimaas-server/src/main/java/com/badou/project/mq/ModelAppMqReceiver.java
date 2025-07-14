package com.badou.project.mq;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.gpucalc.BaseGpuCalcHandler;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.client.KubernetesApiClientFactory;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.StopCenter;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanqEntity;
import com.badou.project.maas.evaluationinstan.model.EvaluationMqEntity;
import com.badou.project.maas.evaluationinstan.service.IEvaluationInstanService;
import com.badou.project.maas.evaluationinstan.service.IEvaluationInstanqService;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.trainplan.service.ITrainPlanService;
import com.badou.project.maas.tuningevaluationn.model.TuningEvaluationnEntity;
import com.badou.project.maas.tuningevaluationn.service.ITuningEvaluationnService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.maas.tuningprogramqueue.service.ITuningProgramQueueService;
import com.badou.project.util.ParamInvalidUtil;
import com.badou.tools.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ModelAppMqReceiver implements IBaseTaskMqReceiver{

    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;
    @Autowired
    private IModelAppService modelAppService;
    @Autowired
    private TaskMqSender taskMqSender;
    @Autowired
    private ITuningModelnService tuningModelnService;
    @Autowired
    private IEvaluationInstanService evaluationInstanService;
    @Autowired
    private IEvaluationInstanqService evaluationInstanqService;
    @Autowired
    private ITrainPlanService trainPlanService;
    @Autowired
    private ITrainFileDialogueService trainFileDialogueService;
    @Autowired
    private ITuningEvaluationnService tuningEvaluationnService;
    @Autowired
    private ITuningProgramQueueService tuningProgramQueueService;
    @Autowired
    private EvaluationMqReceiver evaluationMqReceiver;

    /**
     * 模型应用建立服务的处理类
     *
     * @param message
     */
    @RabbitListener(queues = {"${spring.rabbitmq.modelapp-queue}"})
    @RabbitHandler
    @Override
    public void process(String message) {
        ModelAppEntity modelAppEntity = JSONObject.parseObject(message, ModelAppEntity.class);
        boolean stop = StopCenter.checkStopFlag(modelAppEntity.getTunModelId(), "ModelPlanTaskMqReceiver-模型应用任务-02停止");
        if (stop) {
            return;
        }
        DictionaryCacheObject modelAppMsg = DictionaryLib.getDictionaryByCode(MaasConst.TRAIN_REGISTRY_MODEL + modelAppEntity.getModelName());
        TuningModelnEntity tuningModelnEntity = tuningModelnService.find(modelAppEntity.getTunModelId());
        try {
            String modelResultId = modelAppEntity.getModelResultId();
            if (StringUtils.isEmpty(modelResultId)) {
                setFail(tuningModelnEntity,modelAppEntity,"模型启动失败.不存在模型连接信息");
                return;
            }
            setStartApp(tuningModelnEntity);
            boolean modelApp = createModelApp(tuningModelnEntity,modelAppEntity);
            if (!modelApp) {
                //如果是false则移除
                log.info("主键" + modelAppEntity.getId() + ",:启动模型任务结果返回false.任务删除.信息:" + modelAppEntity.getMsg());
                TuningModelnEntity linkAppModel = modelAppService.getLinkAppModel(modelAppMsg.getId());
                if (linkAppModel != null) {
                    String openAutoDelete = DictionaryLib.getItemValueByItemCode("OPEN_AUTO_DELETE", "0");
                    if ("0".equals(openAutoDelete)) {
                        log.info("开启失败自动删除");
                        KubernetesApiClient kubernetesApiClient = KubernetesApiClientFactory.buildK8sClient(tuningModelnService.getServerId(linkAppModel));
                        kubernetesPodHandler.deleteOnePod(kubernetesApiClient, MaasConst.MODEL_APP_NSAPCE, modelAppEntity.getModelServerId());
                    } else {
                        log.info("关闭-失败自动删除");
                    }
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            setFail(tuningModelnEntity,modelAppEntity, "创建应用失败!请联系管理员!原因:" + e.getMessage());
            return;
        }
        try {
            //检查模型是否运行成功
            startEvaluation(modelAppEntity);
        } catch (Exception e) {
            e.printStackTrace();
            setFail(tuningModelnEntity,modelAppEntity,"启动评价任务失败!"+e.getMessage());
        }

    }

    public void startEvaluation(ModelAppEntity modelAppEntity) throws Exception {
        //查询当前模型
        TuningModelnEntity tuningModelnEntity = tuningModelnService.find(modelAppEntity.getTunModelId());
        //获取模型关联的评价模型
        TrainPlanEntity trainPlanEntity = trainPlanService.find(tuningModelnEntity.getPlanId());
        if (StringUtils.isEmpty(trainPlanEntity.getEvaluationId())) {
            //没有评价任务 更新模型任务为成功
            tuningModelnService.setSucccessStatus(tuningModelnEntity,"模型微调成功!模型自启成功!");
            return;
        }
        log.info("开始评价任务...");

        TuningEvaluationnEntity tuningEvaluationnEntity = tuningEvaluationnService.find(trainPlanEntity.getEvaluationId());

        //下一个任务一般是评价任务
        //获取当前任务 然后获取下一个任务
        String nextTaskId = tuningProgramQueueService.find(tuningModelnEntity.getTaskId()).getNextTaskId();
        if (StringUtils.isEmpty(nextTaskId)) {
            setFail(tuningModelnEntity,modelAppEntity, "未配置评价任务信息.任务未启动!请联系管理员!");
            return;
        }
        //获取训练集
        EvaluationInstanEntity evaluationInstanEntity = new EvaluationInstanEntity();
        List<EvaluationInstanqEntity> evaluationInstanqEntities = new LinkedList<>();
        evaluationInstanEntity.setModelId(modelAppEntity.getModelId());
        evaluationInstanEntity.setModelName(modelAppEntity.getModelName());
        evaluationInstanEntity.setEvaModelId(trainPlanEntity.getEvaluationId());
        evaluationInstanEntity.setModelAppId(modelAppEntity.getId());
        evaluationInstanEntity.setTunModelId(modelAppEntity.getTunModelId());
        evaluationInstanEntity.setFlgDeleted(0);
        evaluationInstanEntity.setTunPlanId(tuningModelnEntity.getPlanId());
        evaluationInstanEntity.setTunProgramId(tuningModelnEntity.getTunPlanId());
        ModelPlanTaskMqReceiver.createUpdateUser();
        evaluationInstanEntity.setTaskId(nextTaskId);
        //配置总体的评价信息
        evaluationInstanEntity.setExexCount(0);
        evaluationInstanEntity.setTotalScore(0.0);
        evaluationInstanEntity.setStatus(MaasConst.DOPLAN_WAIT_STATUS);
        BeanUtils.copyProperties(tuningEvaluationnEntity, evaluationInstanEntity,"createTime","id","updateTime","creator","updator");
        //先计算总数 然后再来单独处理每条任务
        List<TrainFileDialogueEntity> datas = trainFileDialogueService.getListByEvaluation(tuningEvaluationnEntity.getId());
        ModelPlanTaskMqReceiver.createUpdateUser();
        evaluationInstanEntity.setQustionCount(datas.size());
        evaluationInstanEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
        evaluationInstanEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
        evaluationInstanEntity.setUpdateTime(new Date());
        evaluationInstanService.createEntity(evaluationInstanEntity);
        log.info("本次评价总数量:" + evaluationInstanEntity.getQustionCount());
        //更新任务为评价状态.不要等任务结束 直接提交刷新到数据库
        tuningModelnEntity.setEvaluatioInstanId(evaluationInstanEntity.getId());
        setEvaScore(tuningModelnEntity,evaluationInstanEntity);

        //缺少完全等待模型启动的功能
        TimeUnit.SECONDS.sleep(20);
        log.info("等待结束!");
        //重置任务计算--必须在评价任务发送前
        evaluationMqReceiver.clearTaskIdx(evaluationInstanEntity.getId());
        //更新状态到微调计划-任务列表
        tuningProgramQueueService.updateStatusByPlanId(nextTaskId, MaasConst.DOPLAN_RUN_STATUS);

        for (TrainFileDialogueEntity trainFileDialogueEntity : datas) {
            boolean stop = StopCenter.checkStopFlag(evaluationInstanEntity.getTunModelId(), "EvaluationMqReceiver-模型评价任务-03停止");
            if (stop) {
                return;
            }
            EvaluationInstanqEntity evaluationInstanqEntity = new EvaluationInstanqEntity();
            BeanUtils.copyProperties(trainFileDialogueEntity, evaluationInstanqEntity, "createTime", "id", "updateTime", "creator", "updator");
            evaluationInstanqEntity.setInstanq(evaluationInstanEntity.getId());
            evaluationInstanqEntity.setFlgDeleted(0);
            evaluationInstanqEntity.setEvaFlag(0);
            evaluationInstanqEntity.setFeedback("");
            evaluationInstanqEntity.setStandardAnswer(trainFileDialogueEntity.getFeedback());
            if (StringUtils.isEmpty(evaluationInstanEntity.getId()) || StringUtils.isEmpty(evaluationInstanqEntity.getQuestion()) ||
                    StringUtils.isEmpty(evaluationInstanqEntity.getStandardAnswer())) {
                tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_FAIL_STATUS);
                tuningModelnEntity.setPlanMsg("检查评价数据不通过!");
                tuningModelnService.update(tuningModelnEntity);
                log.info(JSONObject.toJSONString(evaluationInstanqEntity));
                throw new Exception("检查评价数据不通过");
            }
            //边新增 边处理数据
            //evaluationInstanqEntities.add(evaluationInstanqEntity);
            evaluationInstanqEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            evaluationInstanqEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            evaluationInstanqEntity.setUpdateTime(new Date());
            evaluationInstanqService.create(evaluationInstanqEntity);
            taskMqSender.senEvaluationdMessage(new EvaluationMqEntity(evaluationInstanqEntity, evaluationInstanEntity));
        }
        //20241204 lm 变更.以前是拿自身微调的数据去评价。现在完全交给评价模块关联的训练集去评价
//        queryCriterion = new QueryCriterion();
//        queryCriterion.addParam(new QueryHibernatePlaceholderParam("evaluation_id", tuningEvaluationnEntity.getId(), null, QueryOperSymbolEnum.eq));
//        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
//        List<EvaluationTFileEntity> evaluationTFileEntities = evaluationTFileService.find(queryCriterion);
//        AtomicInteger totalSize = new AtomicInteger();
//        for (EvaluationTFileEntity evaluationTFileEntity : evaluationTFileEntities) {
//            QueryCriterion tempCriterion1 = new QueryCriterion();
//            tempCriterion1.addParam(new QueryHibernatePlaceholderParam("id", evaluationTFileEntity.getTrainFileId(), null, QueryOperSymbolEnum.eq));
//            tempCriterion1.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
//            List<TrainFileEntity> trainFileEntities = trainFileService.find(tempCriterion1);
//            //获取对话
//            trainFileEntities.forEach(trainFileEntity -> {
//                QueryCriterion tempCriterion2 = new QueryCriterion();
//                tempCriterion2.addParam(new QueryHibernatePlaceholderParam("trainFileId", trainFileEntity.getId(), null, QueryOperSymbolEnum.eq));
//                tempCriterion2.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
//                List<TrainFileDialogueEntity> trainFileDialogueEntities = trainFileDialogueService.find(tempCriterion2);
//                totalSize.addAndGet(trainFileDialogueEntities.size());
//                for (int i = 0; i < trainFileDialogueEntities.size(); i++) {
//                    TrainFileDialogueEntity trainFileDialogueEntity = trainFileDialogueEntities.get(i);
//                    i++;
//                    //20241203 Lm 不解析了.直接把实体的数据复制过去
////                    try {
////                        //做校验
////                        JSONObject jsonObject = null;
////                        jsonObject = tuningModelnService.buildOneRound(trainFileDialogueEntity, tuningModelnEntity, trainFileEntity.getRoleDesc(), trainPlanEntity);
////                        if (jsonObject == null || jsonObject.size() == 0) {
////                            setFail(modelAppEntity, trainFileDialogueEntity.getQuestion() + "与数据格式不匹配");
////                            return;
////                        }
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
//                    EvaluationInstanqEntity evaluationInstanqEntity = new EvaluationInstanqEntity();
//                    BeanUtils.copyProperties(trainFileDialogueEntity, evaluationInstanqEntity, "createTime", "id", "updateTime", "creator", "updator");
//                    evaluationInstanqEntity.setInstanq(evaluationInstanEntity.getId());
//                    evaluationInstanqEntity.setFlgDeleted(0);
//                    evaluationInstanqEntity.setFeedback("");
//                    evaluationInstanqEntity.setStandardAnswer(trainFileDialogueEntity.getFeedback());
//                    evaluationInstanqEntity.setUpdateTime(new Date());
//                    evaluationInstanqEntity.setUpdator("system");
//                    evaluationInstanqEntity.setUpdatorName("system");
//                    if (StringUtils.isEmpty(evaluationInstanEntity.getId()) || StringUtils.isEmpty(evaluationInstanqEntity.getQuestion()) ||
//                            StringUtils.isEmpty(evaluationInstanqEntity.getStandardAnswer())) {
//                        tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_FAIL_STATUS);
//                        log.info(JSONObject.toJSONString(evaluationInstanqEntity));
//                        tuningModelnEntity.setPlanMsg("检查评价数据不通过!");
//                        tuningModelnService.update(tuningModelnEntity);
//                        return;
//                    }
//                    //边新增 边处理数据
////                   evaluationInstanqEntities.add(evaluationInstanqEntity);
//                    evaluationInstanqService.create(evaluationInstanqEntity);
//                    taskMqSender.senEvaluationdMessage(new EvaluationMqEntity(evaluationInstanqEntity, evaluationInstanEntity));
//                }
//            });
//        }
        //检查双方模型状态是否正常

//        evaluationInstanqService.batchCreate(evaluationInstanqEntities);
//        evaluationInstanqEntities.forEach(evaluationInstanqEntity -> {
////                try {
////                    TimeUnit.MILLISECONDS.sleep(100);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//            taskMqSender.senEvaluationdMessage(new EvaluationMqEntity(evaluationInstanqEntity, evaluationInstanEntity));
//            count.getAndIncrement();
//        });
//        if (!(count.get() == evaluationInstanqEntities.size())) {
//            try {
//                throw new Exception("MQ数量不等于");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    public boolean createModelApp(TuningModelnEntity tuningModelnEntity,ModelAppEntity modelAppEntity) {
        try {
            ModelPlanTaskMqReceiver.createUpdateUser();
            //20250117 如果本次任务是多机多卡情况来训练时,则在启动时为该任务寻找闲置的服务器部署
            if (tuningModelnEntity.getServerId().contains(",")){
                KubernetesApiClient kubernetesApiClient = FileControllerService.getCacheK8sClient(tuningModelnEntity.getServerId());
                new BaseGpuCalcHandler().calcTargetCardAndDeploy(trainPlanService.find(tuningModelnEntity.getPlanId()), tuningModelnEntity);
                //不用在乎哪个卡 只要这个服务器有存在满足需求的显卡 就分配过去
                if (tuningModelnEntity.getMultipleServersConfigs().size() == 0){
                    setFail(tuningModelnEntity,modelAppEntity,"可用服务器0,请联系管理员!");
                    return false;
                }
                //选择其中一个服务器就行
                MultipleServersConfig multipleServersConfig = tuningModelnEntity.getMultipleServersConfigs().get(0);
                modelAppEntity.setServerId(multipleServersConfig.getK8sServerConfEntity().getId());
                modelAppEntity.setServerName(multipleServersConfig.getK8sServerConfEntity().getRemark());
            }
            boolean startmodel = tuningModelnService.startmodel(null, modelAppEntity);
            if(startmodel == false){
                setFail(tuningModelnEntity,modelAppEntity,modelAppEntity.getMsg());
                return false;
            }
            //存在评价的情况下 才会去确认模型是否正确启动
            TrainPlanEntity trainPlanEntity = trainPlanService.find(tuningModelnEntity.getPlanId());
            if (StringUtils.isNotBlank(trainPlanEntity.getEvaluationId())){
                log.info("开始等待模型启动成功!");
                String started = modelAppService.checkAppStarted(modelAppEntity, MaasConst.TIMEOUT_AIMODEL_START_WAIT);
                if (StringUtils.isNotBlank(started)){
                    log.error("实际启动失败!"+started);
                    return false;
                }
            }
            //启动模型后.自动上架该应用
            tuningModelnService.coverTunModel(tuningModelnEntity,1, ParamInvalidUtil.createNowNo(),"应用微调成功.自动启动自动上架.上架时间"+
                    DateUtil.getDateStrMin(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
            modelAppEntity.setStatus(MaasConst.DOPLAN_FAIL_STATUS);
            modelAppEntity.setMsg("启动失败!!" + e.getMessage());
            return false;
        }
        return true;
    }

    public void setStartApp(TuningModelnEntity tuningModelnEntity) {
        tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_APP_STATUS);
        tuningModelnService.updateImmediately(tuningModelnEntity);
    }

    public void setEvaScore(TuningModelnEntity tuningModelnEntity,EvaluationInstanEntity evaluationInstanEntity) {
        tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_SCORE_STATUS);
//        tuningModelnEntity.setPlanMsg("开始评价...");
        tuningModelnEntity.setEvaTotalCount(evaluationInstanEntity.getExexCount()+"/"+evaluationInstanEntity.getQustionCount());
        tuningModelnEntity.setEvaTotalScore(evaluationInstanEntity.getTotalScore()+"/"+evaluationInstanEntity.getQustionCount());
        tuningModelnService.updateImmediately(tuningModelnEntity);
    }

    public ModelAppEntity setFail(TuningModelnEntity tuningModelnEntity,ModelAppEntity modelAppEntity, String msg) {
        modelAppService.stopApp(modelAppEntity.getId(),true,msg);
        tuningModelnService.setFailStatus(tuningModelnEntity,msg);
        return modelAppEntity;
    }


}
