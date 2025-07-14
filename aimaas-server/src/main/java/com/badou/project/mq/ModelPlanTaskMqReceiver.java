package com.badou.project.mq;

import com.badou.project.exception.DataValidException;
import com.badou.project.gpucalc.GpuCalcHandler;
import com.badou.project.gpucalc.GpuCalcFactory;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.trainplan.service.ITrainPlanService;
import com.badou.project.util.BuildCustomFileUtil;
import com.badou.project.util.ParamInvalidUtil;
import com.badou.tools.common.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.kubernetes.KubernetesConstants;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesNameSpaceHandler;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.kubernetes.vo.DeployAppVo;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.StopCenter;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.trainplan.web.TrainPlanSaveAction;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.maas.tuningplanparams.model.TuningPlanParamsEntity;
import com.badou.project.maas.tuningplanparams.service.ITuningPlanParamsService;
import com.badou.project.maas.tuningprogramqueue.service.ITuningProgramQueueService;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName TaskReceiver
 * @Description 执行模型微调任务
 * @date 2023/3/1 10:21
 * @Version 1.0
 */

@Component
@Slf4j
public class ModelPlanTaskMqReceiver implements IBaseTaskMqReceiver{

    @Autowired
    private IModelWarehouseService modelWarehouseService;
    @Autowired
    private TaskMqSender taskMqSender;
    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;
    @Autowired
    private ITuningModelnService tuningModelnService;
    @Autowired
    private FileControllerService fileControllerService;
    @Autowired
    private KubernetesNameSpaceHandler kubernetesNameSpaceHandler;
    @Autowired
    private IModelAppService modelAppService;
    @Autowired
    private ITuningProgramQueueService tuningProgramQueueService;
    @Autowired
    private ITuningPlanParamsService tuningPlanParamsService;
    @Autowired
    private ITrainPlanService trainPlanService;


    /**
     * 1.@RabbitListener 注解是指定某方法作为消息消费的方法，例如监听某 Queue 里面的消息。
     * <p>
     * 2.@RabbitListener标注在方法上，直接监听指定的队列，此时接收的参数需要与发送市类型一致
     *
     * @param message 收到的消息
     */
    @RabbitListener(queues = {"${spring.rabbitmq.plan-queue}"})
    @RabbitHandler
    @Override
    public void process(String message) {
        TuningModelnEntity tuningModelnEntity = null;
        try {
            tuningModelnEntity = JSONObject.parseObject(message, TuningModelnEntity.class);
            if (tuningModelnEntity == null) {
                log.error("转换对象为空!");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        boolean stop = StopCenter.checkStopFlag(tuningModelnEntity.getId(), "ModelPlanTaskMqReceiver-模型微调任务-01停止");
        if (stop) {
            return;
        }
        if (Objects.nonNull(tuningModelnEntity)) {
            //首次启动先挂载状态为执行
            if (tuningModelnEntity.getCreatePath() == null && tuningModelnEntity.getDoStatus() == 0) {
                String creatTrain = "检测到服务" + tuningModelnEntity.getCode() + "未生成训练集,开始生成";
                log.info(creatTrain);
                setRunning(tuningModelnEntity, creatTrain);
                reloadTask(JSONObject.toJSONString(tuningModelnEntity));
                return;
            }
            boolean successFlg = false;
            String podName = null;
            try {
                //已启动 正式开始执行 检查训练集情况
                if (tuningModelnEntity.getCreatePath() == null && tuningModelnEntity.getDoStatus() == 1 && tuningModelnEntity.getPodName() == null) {
                    long startTime = System.currentTimeMillis();
                    createUpdateUser();
                    log.info("开始生成训练集");
                    //多机
                    if (tuningModelnEntity.getServerId().split(",").length>1){
                        String sourceServerId = tuningModelnEntity.getServerId();
                        for (String serverId : sourceServerId.split(",")){
                            tuningModelnEntity.setServerId(serverId);
                            //检查是否生成了训练集文件 如果没有生成则生成
                            checkTrainFile(tuningModelnEntity);
                        }
                        tuningModelnEntity.setServerId(sourceServerId);
                    }else {
                        //检查是否生成了训练集文件 如果没有生成则生成
                        checkTrainFile(tuningModelnEntity);
                    }
                    log.info("结束生成训练集");
                    if (tuningModelnEntity.getDoStatus().equals(MaasConst.DOPLAN_FAIL_STATUS)) {
                        setFailStatus(tuningModelnEntity, tuningModelnEntity.getPlanMsg(), null);
                        return;
                    }
                    long endTime = System.currentTimeMillis();
                    long diffTime = (endTime - startTime) / 1000;
                    log.info("服务" + tuningModelnEntity.getCode() + "已生成训练集.花费时间" + diffTime + "S");
                    //如果是失败 则结束任务
                    successFlg = checkFailOrSuccess(tuningModelnEntity);
                    if (MaasConst.DOPLAN_FAIL_STATUS == tuningModelnEntity.getDoStatus()) {
                        setFailStatus(tuningModelnEntity, tuningModelnEntity.getPlanMsg(), null);
                        return;
                    }
                    setRunning(tuningModelnEntity, "完成生成训练集");
                    //创建好训练集后 正式启动微调任务
                    try {
                        createTask(tuningModelnEntity);
                        if (MaasConst.DOPLAN_FAIL_STATUS == tuningModelnEntity.getDoStatus()) {
                            log.error("执行失败!"+tuningModelnEntity.getPlanMsg());
                            return;
                        }
                        stop = StopCenter.checkStopFlag(tuningModelnEntity.getId(), "ModelPlanTaskMqReceiver-模型微调任务-01停止");
                        if (stop) {
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        setFailStatus(tuningModelnEntity, "创建服务失败!"+e.getMessage(), podName);
                        return;
                    }
                    reloadTask(tuningModelnEntity);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                setFailStatus(tuningModelnEntity, "无法创建训练文件!请联系管理员", podName);
                return;
            }

            try {
                successFlg = checkFailOrSuccess(tuningModelnEntity);
                //如果是应用任务 本次微调全部结束.切换给下一个任务!
                if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_APP_STATUS){
                    return;
                }
                //如果是失败或成功 则结束任务
                if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_FAIL_STATUS) {
                    setFailStatus(tuningModelnEntity, tuningModelnEntity.getPlanMsg(), podName);
                    return;
                }
                if (successFlg && tuningModelnEntity.getAutoStartFlg() == 0) {
                    //检查是否生成了训练集文件 如果没有则生成
//                    tuningModelnEntity = tuningModelnService.getPodLogs(defaultClient, tuningModelnEntity.getWorkSpace(), tuningModelnEntity.getPodName(),tuningModelnEntity,false);
                    LogonCertificate logonCertificate = LogonCertificateHolder.getLogonCertificate();
                    if (Objects.isNull(logonCertificate)) {
                        logonCertificate = new LogonCertificate();
                        logonCertificate.setUserId("TRAINPLAN");
                        logonCertificate.setUserName("trainplan");
                        LogonCertificateHolder.setLogonCertificate(logonCertificate);
                    }
//                    message = JSONObject.toJSONString(tuningModelnEntity);
                    tuningModelnService.update(tuningModelnEntity);
                    return;
                }
                LogonCertificate logonCertificate = LogonCertificateHolder.getLogonCertificate();
                if (Objects.isNull(logonCertificate)) {
                    logonCertificate = new LogonCertificate();
                    logonCertificate.setUserId("TRAINPLAN");
                    logonCertificate.setUserName("trainplan");
                    LogonCertificateHolder.setLogonCertificate(logonCertificate);
                }
                message = JSONObject.toJSONString(tuningModelnEntity);
                tuningModelnService.update(tuningModelnEntity);

                //如果状态不是成功或者失败则继续监听
                reloadTask(message);
            } catch (Exception e) {
                e.printStackTrace();
                setFailStatus(tuningModelnEntity, "获取状态失败!请联系管理员", podName);
            }

        }
    }

    private boolean checkFailOrSuccess(TuningModelnEntity tuningModelnEntity) {
        //查询POD状态 如果是Completed 则代表任务完成
        try {
            KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(tuningModelnEntity.getRealServerId());
            V1Pod pod = kubernetesPodHandler.getPod(kubernetesApiClient, tuningModelnEntity.getWorkSpace(), tuningModelnEntity.getCode());
            if (pod!=null && MaasConst.K8S_POD_SUCCEEDED.equals(pod.getStatus().getPhase())){
                tuningModelnEntity.setPlanMsg(kubernetesPodHandler.readPodAllLog(kubernetesApiClient, tuningModelnEntity.getWorkSpace(), tuningModelnEntity.getCode(), 999999));
                tuningModelnService.setSucccessStatus(tuningModelnEntity,"微调成功");
            }else if (pod!=null && MaasConst.K8S_POD_FAILED.equals(pod.getStatus().getPhase())){
                tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_FAIL_STATUS);
                tuningModelnEntity.setPlanMsg(kubernetesPodHandler.readPodAllLog(kubernetesApiClient, tuningModelnEntity.getWorkSpace(), tuningModelnEntity.getCode(), 999999));
                return true;
            }
        } catch (Exception e) {
            tuningModelnService.setFailStatus(tuningModelnEntity, "微调失败!未能成功加载客户端!");
            return false;
        }

        createUpdateUser();
        if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_FAIL_STATUS || tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_SUCCESS_STATUS
                || tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_APP_STATUS) {
            log.info("微调任务结束!" + tuningModelnEntity.getPlanMsg() + ",失败或成功完成,移除服务");
            ModelWarehouseEntity modelWarehouseEntity = modelWarehouseService.find(tuningModelnEntity.getModelId());
            if (modelWarehouseEntity == null || modelWarehouseEntity.getId() == null){
                tuningModelnService.setFailStatus(tuningModelnEntity, "微调失败!未能成功加载模型信息!");
                return false;
            }
            if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_APP_STATUS && tuningModelnEntity.getAutoStartFlg() == 1) {
                //成功 创建一个模型应用对象 发送启动模型任务
                ModelAppEntity modelAppEntity = new ModelAppEntity();
                modelAppEntity.setName("微调自启-"+tuningModelnEntity.getModelAllName());
                modelAppEntity.setDefineName(tuningModelnEntity.getName() + "的启动模型");
                modelAppEntity.setModelId(tuningModelnEntity.getModelId());
                modelAppEntity.setModelName(tuningModelnEntity.getModelName());
                modelAppEntity.setStatus(MaasConst.DOPLAN_WAIT_STATUS);
                modelAppEntity.setModelResultId(tuningModelnEntity.getId());
                modelAppEntity.setFlgDeleted(0);
                modelAppEntity.setTunModelId(tuningModelnEntity.getId());
                modelAppEntity.setServerName(tuningModelnEntity.getServerName());
                modelAppEntity.setServerId(tuningModelnEntity.getServerId());
                modelAppEntity.setType(modelWarehouseEntity.getType());
                modelAppEntity.setSource(MaasConst.MODEL_SOURCE_TUN);
                modelAppEntity.setAppDesc("微调方案"+tuningModelnEntity.getPlanName()+"设置应用自动启动");
                modelAppEntity.setGpuCount(1);
                modelAppEntity.setModelProvider(modelWarehouseEntity.getModelProvider());
                modelAppEntity.setWorkPath(MaasConst.buildModelOutPath(tuningModelnEntity));
                log.info("微调成功!开始启动目标微调模型.加载目录"+modelAppEntity.getWorkPath());
                modelAppEntity.setUpdateTime(new Date());
                modelAppEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
                modelAppEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
                modelAppService.create(modelAppEntity);

                //如果是需要启动模型 这里任务就不能结束 转成应用状态
                tuningModelnEntity.setModelAppId(modelAppEntity.getId());
                tuningModelnEntity.setModelAPPName(modelAppEntity.getName());
                //20240722增加更新 任务列表的信息
                String taskId = tuningModelnEntity.getTaskId();
                tuningProgramQueueService.updateStatusByPlanId(taskId, MaasConst.DOPLAN_SUCCESS_STATUS);
                taskMqSender.sendModelAppMessage(JSONObject.toJSONString(modelAppEntity));
            }
            String openAutoDelete = DictionaryLib.getItemValueByItemCode("OPEN_AUTO_DELETE", "0");
            if ("0".equals(openAutoDelete)) {
                log.info("开启失败自动删除");
                //更新任务状态
                try {
                    String[] serverIdSplit = tuningModelnEntity.getServerId().split(",");
                    for (String serverId : serverIdSplit) {
                        kubernetesPodHandler.deleteOnePod(FileControllerService.getCustomClient(serverId), MaasConst.TRIAN_PLAN_NSPACE, tuningModelnEntity.getCode());
                    }
                } catch (Exception e) {
                }
            } else {
                log.info("关闭-失败自动删除");
            }
            tuningModelnService.updateImmediately(tuningModelnEntity);
            return true;
        }
        return false;
    }

    private TuningModelnEntity createTask(TuningModelnEntity tuningModelnEntity) throws Exception {
        if (StringUtils.isEmpty(tuningModelnEntity.getCreateDir())) {
            return tuningModelnService.setFailStatus(tuningModelnEntity,"错误的识别信息!请联系管理员!");
        }
        String modelName = tuningModelnEntity.getModelName();
        Integer tunFrame = tuningModelnEntity.getDoFrame();
        TrainPlanEntity trainPlanEntity = trainPlanService.find(tuningModelnEntity.getPlanId());

        //20240815 lm 所有微调任务都改成调用llamafactory
        DictionaryCacheObject taskDic = DictionaryLib.getDictionaryByCode(MaasConst.LLAMA_FACTORY_DIC);
        DictionaryCacheObject modelDic = null;
        String nameSpace = MaasConst.TRIAN_PLAN_NSPACE;
        String appcode = tuningModelnEntity.getCode();
        String imageName = taskDic.getValue();
        if (tunFrame == 0) {
            modelDic = DictionaryLib.getDictionaryByCode("llama_factory");
            if (MaasConst.TUN_PLAN_TYPE_MULIT.equals(trainPlanEntity.getDoWay())){
                imageName = imageName.replace(":","-multi:");
            }
        } else {
            Integer targetModel = -1;
            for (int i = 0; i < TrainPlanSaveAction.allowModel.size(); i++) {
                String[] allows = TrainPlanSaveAction.allowModel.get(i);
                for (String allowModel : allows) {
                    if (allowModel.equals(modelName)) {
                        targetModel = i;
                    }
                }
            }
            modelDic = DictionaryLib.getDictionaryByCode(MaasConst.getTuningAllName(
                    String.valueOf(targetModel), modelName));
            taskDic = modelDic;
            imageName = taskDic.getValue();
        }
        if (trainPlanEntity.getPreGpucache() == 0) {
            return tuningModelnService.setFailStatus(tuningModelnEntity,"该微调方案未配置对应模型框架的预估GPU显存配置!请联系管理员!");
        }
        if (modelDic.getItems().size() == 0) {
            return tuningModelnService.setFailStatus(tuningModelnEntity,"模型" + modelName + "未设置任务信息!请联系管理员设置!");
        }

        //镜像名字一般是 xx:xxx 所以一般会有: 如果没有就是不合法的镜像信息
        if (!imageName.contains(":")) {
            return tuningModelnService.setFailStatus(tuningModelnEntity,"模型" + modelName + "未设置合法的任务本体信息!请联系管理员修复!");
        }
        // 获取模型路径等信息
        String value = modelDic.getRemark();
        //如果llama-factory 默认采用llamafacoty的配置参数
        if (tunFrame == 0 && StringUtils.isNotBlank(taskDic.getRemark())) {
            value = taskDic.getRemark();
        }
        String modelPath = null;
        String dataPath = null;
        String outPut = null;
        String serverModelPath = null;

        try {
            JSONObject taskMsg = JSONObject.parseObject(value);
            modelPath = taskMsg.getString("modelPath");
            dataPath = taskMsg.getString("dataPath");
            outPut = taskMsg.getString("outPut");
            serverModelPath = taskMsg.getString("serverModelPath");
        } catch (Exception e) {
            return tuningModelnService.setFailStatus(tuningModelnEntity,"模型" + modelName + "未设置合法的任务本体信息!请联系管理员修复!");
        }
        if (tunFrame != 0) {
            if (JsonResultUtil.isNull(modelPath, dataPath, outPut, serverModelPath)) {
                return tuningModelnService.setFailStatus(tuningModelnEntity,"模型" + modelName + "任务信息无效!请联系管理员");
            }
        }
        //20250415 暂定从模型仓库读取模型路径数据
        ModelWarehouseEntity modelWarehouseEntity = modelWarehouseService.find(tuningModelnEntity.getModelId());
        serverModelPath = modelWarehouseEntity.getPath();

        //端口暴露服务的对应的是/ Service.Spec.Selector下的值
        Map<String, String> selectLabels = new HashMap<>();
        selectLabels.put("app", appcode);
        selectLabels.put("logkey", nameSpace + "-" + appcode);

        Map<String, Quantity> limits = new HashMap<>();

        List<V1EnvVar> envVarList = new ArrayList<>();
//		envVarList.add(new V1EnvVar().name("PRE_SEQ_LEN").value("128"));
//		envVarList.add(new V1EnvVar().name("LR").value("2e-2"));
//		envVarList.add(new V1EnvVar().name("NUM_GPUS").value("1"));
//		envVarList.add(new V1EnvVar().name("MAX_SEQ_LEN").value("2048"));
//		envVarList.add(new V1EnvVar().name("DEV_BATCH_SIZE").value("1"));
//		envVarList.add(new V1EnvVar().name("GRAD_ACCUMULARION_STEPS").value("16"));
//		envVarList.add(new V1EnvVar().name("MAX_STEP").value("100"));
//		envVarList.add(new V1EnvVar().name("SAVE_NITERVAL").value("100"));
//		envVarList.add(new V1EnvVar().name("FINE_TUNING_ID").value(UUID.fastUUID().toString()));
//		envVarList.add(new V1EnvVar().name("FINE_TUNING_NAME").value(appcode));
        //启动qlora 量化方式
        if (trainPlanEntity.getMode() == 1){
            envVarList.add(new V1EnvVar().name("ENV_QUANTIZATION_METHOD").value("bitsandbytes"));
        }
        //设置训练集名称 固定用
        envVarList.add(new V1EnvVar().name("DATASET_NAME").value(tuningModelnEntity.getDoFrame() == 0 ? "train.json" : "train.jsonl"));
        envVarList.add(new V1EnvVar().name("DATESTR").value(String.valueOf(System.currentTimeMillis())));
        envVarList.add(new V1EnvVar().name("CUDA_HOME").value(MaasConst.CUDA_HOME));
//        V1EnvVar v1EnvVar = new V1EnvVar();
//        v1EnvVar.setName("CUDA_VISIBLE_DEVICES");
//        v1EnvVar.setValue(gpuCardSelect.getString("tunCardNo"));
//        envVarList.add(v1EnvVar);

        String modelTemplate = DictionaryLib.getItemValue(MaasConst.LLAMA_FACTORY_TEMPLATE_DIC, tuningModelnEntity.getModelName());

        if (StringUtils.isEmpty(modelTemplate) && tuningModelnEntity.getDoFrame() == 0) {
            //尝试二次获取
            String[] splitName = tuningModelnEntity.getModelName().split("-");
            if (splitName.length>0){
                DictionaryItemCacheObject secondCheck = DictionaryLib.getDictionaryItemByName(MaasConst.LLAMA_FACTORY_TEMPLATE_DIC,splitName[0]);
                if (secondCheck!=null){
                    modelTemplate = secondCheck.getValue();
                }
            }
            if (StringUtils.isEmpty(modelTemplate) && tuningModelnEntity.getDoFrame() == 0){
                throw new DataValidException(tuningModelnEntity.getModelName() + "未设置模板参数！请联系管理员！");
            }
        }

        Map<String, String> runEnvParams = new HashMap<>();
        runEnvParams.put("newTunModel", tuningModelnEntity.getCreateDir());
        runEnvParams.put("modelTemplate", modelTemplate);
        //指定调用的训练集文件
        runEnvParams.put("trainFile", "trainFile");
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("plan_id", tuningModelnEntity.getPlanId(), null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
        List<TuningPlanParamsEntity> tuningPlanParamsEntities = tuningPlanParamsService.find(queryCriterion);
        for (TuningPlanParamsEntity programParamsEntity : tuningPlanParamsEntities) {
            String v = null;
            for (String key : runEnvParams.keySet()) {
                v = ParamInvalidUtil.replaceVarValue(programParamsEntity.getValue(), key, runEnvParams.get(key));
                if (!v.contains("$")) {
                    break;
                }
            }
            if (v == null) {
                v = programParamsEntity.getValue();
            }
            envVarList.add(new V1EnvVar().name(programParamsEntity.getCode()).value(v));
            v = null;
        }
        if (envVarList.size() == 0) {
            return tuningModelnService.setFailStatus(tuningModelnEntity,"环境参数异常!请联系管理员!");
        }
        /**
         *    //PRE_SEQ_LEN=128                               可以保持默认
         *         //LR=2e-2                                               可以保持默认
         *         //NUM_GPUS=1                                     可以保持默认
         *         //MAX_SEQ_LEN=2048                           可以保持默认
         *         //DEV_BATCH_SIZE=1                             可以保持默认
         *         //GRAD_ACCUMULARION_STEPS=16    可以保持默认
         *         //MAX_STEP=1000                                 可以保持默认
         *         //SAVE_INTERVAL=500                          可以保持默认
         *         //
         *         //FINE_TUNING_ID=PTV2_0000001       微调的ID，用于生成微调文件目录命名
         *         //FINE_TUNING_NAME=tool_alpaca_pt  微调名，用于生成微调文件目录命名
         *         //DATASET_NAME=tool_alpaca.jsonl       数据集文件名，用于/fine_tuning/formatted_data/目录组合路径给微调使用
         *         //DATESTR=YYYYmmDD-HHMMSS        微调时间戳，用于生成微调文件目录命名
         */
        //根据当前字典的训练集路径 进行正确的挂载
        //从数据字典数据里面设置文件控制器的控制路径
//        DictionaryItemCacheObject trainPathDic = DictionaryLib.getDictionaryItemByName(MaasConst.FILE_PATH_DIC, "formatteddata");
        String oldServerId = tuningModelnEntity.getServerId();
        //如果是多节点的情况下 第一个节点就是Master节点 默认的POD都是Master节点的配置
        String defaultTrainPath = null;
        if (tuningModelnEntity.getMultipleServersConfigs().size()>1){
            tuningModelnEntity.setServerId(tuningModelnEntity.getMultipleServersConfigs().get(0).getK8sServerConfEntity().getId());
        }
        defaultTrainPath = MaasConst.buildTrainFilePath(tuningModelnEntity);

        if (StringUtils.isEmpty(defaultTrainPath)) {
            return tuningModelnService.setFailStatus(tuningModelnEntity,"未设置数据路径!请联系管理员!");
        }
        String serverOutPut = MaasConst.buildOutPath(tuningModelnEntity);
        String serverForMatData = tuningModelnEntity.getCreatePath();

        log.info("微调任务目录信息输出 ->>> " + modelName + ",挂载容器内部模型路径" + modelPath);
        log.info("微调任务目录信息输出 ->>> " + modelName + ",挂载容器内部训练集路径" + dataPath);
        log.info("微调任务目录信息输出 ->>> " + modelName + ",挂载容器内部输出路径" + outPut);
        log.info("微调任务目录信息输出 ->>> " + modelName + ",服务器内部模型路径" + serverModelPath);
        log.info("微调任务目录信息输出 ->>> " + modelName + ",服务器内部输出路径" + serverOutPut);
        log.info("微调任务目录信息输出 ->>> " + modelName + ",服务器内部训练集路径" + serverForMatData);
//        V1Volume[] v1Volumes = new V1Volume[
//                ]{new V1Volume().name("modelpath").hostPath(new V1HostPathVolumeSource().path(modelWarehouseService.find(tuningModelnEntity.getModelId()).getPath()).type("DirectoryOrCreate")),
//                new V1Volume().name("formatteddata").hostPath(new V1HostPathVolumeSource().path(serverForMatData).type("DirectoryOrCreate")),
//                //设置训练集的生成路径
////				new V1Volume().name("formatteddata").hostPath(new V1HostPathVolumeSource().path(trainDataEntity.getPath()).type("DirectoryOrCreate")),
//                new V1Volume().name("output").hostPath(new V1HostPathVolumeSource().path(serverOutPut).type("DirectoryOrCreate"))};
        //20241101 lm 增加共享内存配置的修改以及增加cuda服务的挂载
        V1Volume[] v1Volumes = new V1Volume[
                ]{new V1Volume().name("modelpath").hostPath(new V1HostPathVolumeSource().path(modelWarehouseService.find(tuningModelnEntity.getModelId()).getPath()).type("DirectoryOrCreate")),
                new V1Volume().name("formatteddata").hostPath(new V1HostPathVolumeSource().path(serverForMatData).type("DirectoryOrCreate")),
                //原本k8s自带的共享内存区域只有64MB很小.为了多卡微调功能考虑 这里手动增大共享内存区域
                new V1Volume().name("dshm").emptyDir(MaasConst.SHM_AREA_2GB),
                new V1Volume().name("cuda").hostPath(new V1HostPathVolumeSource().path(MaasConst.CUDA_HOME).type("DirectoryOrCreate")),
                //设置训练集的生成路径
//				new V1Volume().name("formatteddata").hostPath(new V1HostPathVolumeSource().path(trainDataEntity.getPath()).type("DirectoryOrCreate")),
                new V1Volume().name("output").hostPath(new V1HostPathVolumeSource().path(serverOutPut).type("DirectoryOrCreate"))};
        ArrayList<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
        v1VolumeMounts.add(new V1VolumeMount().name("modelpath").mountPath(modelPath));
        v1VolumeMounts.add(new V1VolumeMount().name("output").mountPath(outPut));
        v1VolumeMounts.add(new V1VolumeMount().name("dshm").mountPath("/dev/shm"));
        //默认的训练路径一般是/fine_tuning/formatted_data
        v1VolumeMounts.add(new V1VolumeMount().name("formatteddata").mountPath("/fine_tuning/dataset"));
        v1VolumeMounts.add(new V1VolumeMount().name("cuda").mountPath(MaasConst.CUDA_HOME));
        KubernetesApiClient defaultClient = FileControllerService.getCustomClient(tuningModelnService.getServerId(tuningModelnEntity));

        //20241106 lm增加多卡功能 判断显卡参数 如果显卡数量>0并且选择了显卡框架 则启动多卡方案 但是都要检查是否有可用的卡
        if (tuningModelnEntity.getMultipleServersConfigs().size()>1) {
            tuningModelnEntity.setServerId(oldServerId);
        }
        GpuCalcHandler suitGpuCalcHandler = null;
        try {
            suitGpuCalcHandler = GpuCalcFactory.getSuitGpuCalcHandler(trainPlanEntity);
            if (suitGpuCalcHandler == null) {
                return tuningModelnService.setFailStatus(tuningModelnEntity,"未配置可用显卡配置");
            }
            suitGpuCalcHandler.exec(trainPlanEntity, limits, v1VolumeMounts, v1Volumes, envVarList, tuningModelnEntity,defaultClient);
        }catch (Exception e){
            return tuningModelnService.setFailStatus(tuningModelnEntity,e.getMessage());
        }


        //        limits.put("aliyun.com/gpu-mem", new Quantity(trainPlanEntity.getPreGpucache().toString()));
        //检查是否有挂载文件的配置
        if (JsonResultUtil.isNotNull(tuningModelnEntity.getNameFilesPath(), tuningModelnEntity.getHostFilesPath(), tuningModelnEntity.getMountFilesPath())) {
            //再次检查合法性。
            //检查配置规则是否合法
            String[] mountNameSize = tuningModelnEntity.getNameFilesPath().split(",");
            if (tuningModelnEntity.getHostFilesPath().split(",").length != mountNameSize.length || tuningModelnEntity.getMountFilesPath().split(",").length != mountNameSize.length) {
                return tuningModelnService.setFailStatus(tuningModelnEntity,"运行配置不合法!--002");
            }
            //检查通过 开始额外加载挂载配置
            String[] volumeNames = tuningModelnEntity.getNameFilesPath().split(",");
            String[] hostPaths = tuningModelnEntity.getHostFilesPath().split(",");
            String[] mountPaths = tuningModelnEntity.getMountFilesPath().split(",");
            List<V1Volume> newV1Volumes = new ArrayList<>(Arrays.asList(v1Volumes));
            for (int i = 0; i < volumeNames.length; i++) {
                v1VolumeMounts.add(new V1VolumeMount().name(volumeNames[i]).mountPath(mountPaths[i]));
                String fileType = hostPaths[i].contains(".") ? "FileOrCreate" : "DirectoryOrCreate";
                newV1Volumes.add(new V1Volume().name(volumeNames[i]).hostPath(new V1HostPathVolumeSource().path(hostPaths[i]).type(fileType)));
            }
            v1Volumes = newV1Volumes.toArray(new V1Volume[newV1Volumes.size()]);
        }

        //20240426 训练集的路径不再是某个路径 而是默认拿取所有可用的文件映射
        DeployAppVo deployAppVo = new DeployAppVo();
        deployAppVo.setV1VolumeMounts(v1VolumeMounts);
        deployAppVo.setV1Volumes(v1Volumes);
        deployAppVo.setEnvVarList(envVarList);

        deployAppVo.setCommands(new String[]{"sleep"});
        String[] args = new String[]{"infinity"};
        deployAppVo.setArgs(Arrays.asList(args));
        //开始发布
        try {
            kubernetesNameSpaceHandler.createNameSpace(defaultClient, nameSpace);
            V1Pod readPod = kubernetesPodHandler.getPod(defaultClient, nameSpace, appcode);
            if (Objects.nonNull(readPod)) {
                return tuningModelnService.setFailStatus(tuningModelnEntity,"已创建服务!请耐心等待任务完成!");
            }
        } catch (ApiException e) {
            if (e.getMessage() != null && e.getMessage().equals("Not Found")) {
                log.info("未查找到应用" + appcode);
            } else {
                e.printStackTrace();
                return tuningModelnService.setFailStatus(tuningModelnEntity,"创建初始服务失败!");
            }
        }
        List<V1LocalObjectReference> secrets = new ArrayList<>();
        secrets.add(new V1LocalObjectReference().name("badouregistrykey"));

        V1Pod v1Pod = new V1Pod().apiVersion("v1");
        v1Pod.setKind(KubernetesConstants.K8S_DEPLOY_Pod_NAME);
        v1Pod.setMetadata(new V1ObjectMetaBuilder()
                .withName(appcode)
                .withNamespace(nameSpace)
                .withLabels(selectLabels)
                .build());

        List<V1Container> containers = new ArrayList<>();
        containers.add(new V1ContainerBuilder()
                .withResources(new V1ResourceRequirements().limits(limits))
                .withName(appcode)//设置docker名
                .withImage(imageName)//设置 docker镜像名
                .withImagePullPolicy("IfNotPresent")//镜像本地拉去策略
                .withEnv(envVarList)
                .withVolumeMounts(v1VolumeMounts)
                .build());
        V1LocalObjectReference imagePullSecret = new V1LocalObjectReference();
        imagePullSecret.setName("badouregistrykey");

        List<V1LocalObjectReference> secretlists = new ArrayList<>();
        secretlists.add(imagePullSecret);

        v1Pod.setSpec(new V1PodSpec().restartPolicy("Never").containers(containers).imagePullSecrets(secretlists).volumes(Arrays.asList(v1Volumes)));
        try {
            //20240113 增加多机多卡情况下的支持
            List<V1Pod> v1Pods = suitGpuCalcHandler.buildServerConfig(tuningModelnEntity, v1Pod);
            if (v1Pods==null){
                kubernetesPodHandler.createPod(defaultClient,v1Pod, nameSpace, appcode);
            }else {
                //多服务器的情况 要找对应服务器的K8S客户端来创建POD
                Map<String, KubernetesApiClient> execClientServerMap = new HashMap<>();
                for (MultipleServersConfig multipleServersConfig : tuningModelnEntity.getMultipleServersConfigs()) {
                    execClientServerMap.put(multipleServersConfig.getK8sServerConfEntity().getCode(), FileControllerService.getCustomClient(multipleServersConfig.getK8sServerConfEntity().getId()));
                }
                //检查执行POD 是否都拥有对应的K8S客户端
                for (V1Pod pod : v1Pods) {
                    KubernetesApiClient kubernetesApiClient = execClientServerMap.get(pod.getSpec().getNodeName());
                    if (kubernetesApiClient == null){
                        return tuningModelnService.setFailStatus(tuningModelnEntity,pod.getSpec().getNodeName()+"服务器状态异常!请联系管理员!");
                    }
                }
                //实际创建 集群多机多卡微调任务
                for (V1Pod pod : v1Pods) {
                    log.info(Yaml.dump(pod));
                    KubernetesApiClient kubernetesApiClient = execClientServerMap.get(pod.getSpec().getNodeName());
                    kubernetesPodHandler.createPod(kubernetesApiClient, pod, nameSpace, appcode);
                }
            }
            suitGpuCalcHandler.buildServerConfigExtend(defaultClient,tuningModelnEntity);
            log.info("创建微调任务" + appcode + "完成!");
        } catch (Exception e) {
            e.printStackTrace();
            setFailStatus(tuningModelnEntity,"无法创建任务!请联系管理员",v1Pod.getMetadata().getName());
            return tuningModelnEntity;
        }
        V1Pod readPod = kubernetesPodHandler.getPod(defaultClient, nameSpace, appcode);
        if (Objects.isNull(readPod)) {
            return tuningModelnService.setFailStatus(tuningModelnEntity,"无法创建任务!请联系管理员");
        }
        tuningModelnEntity.setPodName(readPod.getMetadata().getName());
        //最多尝试10次
        int retryCount = 100;
        int nowCount = 0;
        while (true) {
            if (nowCount >= retryCount) {
                if (readPod!=null){
                    kubernetesPodHandler.deleteOnePod(defaultClient,nameSpace,readPod.getMetadata().getName());
                }
                String baseStr = "创建服务超时!2分钟请联系管理员!";
                if (v1Pod.getStatus()!=null && v1Pod.getStatus().getPhase()!=null){
                    baseStr+=v1Pod.getStatus().getPhase();
                }
                if (v1Pod.getStatus()!=null && v1Pod.getStatus().getReason()!=null){
                    baseStr+=v1Pod.getStatus().getReason();
                }
                setFailStatus(tuningModelnEntity,baseStr, v1Pod.getMetadata().getName());
                tuningModelnService.createEntity(tuningModelnEntity);
                return tuningModelnEntity;
            }
            boolean stop = StopCenter.checkStopFlag(tuningModelnEntity.getId(), "ModelPlanTaskMqReceiver-模型微调任务-01停止");
            if (stop) {
                return tuningModelnEntity;
            }
            V1PodStatus status = readPod.getStatus();
            String phase = status.getPhase();
            nowCount++;
            if ("Pending".equals(phase)) {
                readPod = kubernetesPodHandler.getPod(defaultClient, nameSpace, appcode);
                TimeUnit.SECONDS.sleep(6);
                continue;
            }
            break;
        }
        log.info("创建状态:" + readPod.getStatus());
        return tuningModelnEntity;
    }


    private boolean checkTrainFile(TuningModelnEntity tuningModelnEntity) {
        //调用容器里面的服务 下载这个文件
        try {
            try {
                //如果创建失败 则关掉任务
                tuningModelnService.createTrainFile(tuningModelnEntity);
                if (tuningModelnEntity.getDoStatus() == 3) {
                    return false;
                }
            }catch (Exception e){
                //图片的情况下 一般会打印一个事务报错 不用管
                e.printStackTrace();
            }
            //20250429 循环下载训练集到每一个微调服务器
            String[] serverIdSplit = tuningModelnEntity.getServerId().split(",");
            for (String serverId : serverIdSplit) {
                KubernetesApiClient customClient = FileControllerService.getCustomClient(serverId);
                String trainDataPath = tuningModelnEntity.getCreatePath() + "/" + tuningModelnEntity.getModelFile();
                //微调模式 0.指令监督微调数据集 1.预训练数据集 2.偏好数据集 3.KTO数据集 4.多模态
                //检查当前框架和数据集类型 是否存在需要额外挂载的执行文件
                DictionaryCacheObject configFilesDic = MaasConst.buildConfigDic(tuningModelnEntity);
                if (configFilesDic != null) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("trainDataPath", tuningModelnEntity.getCreatePath());
                    for (DictionaryItemCacheObject item : configFilesDic.getItems()) {
                        if (StringUtils.isNotBlank(item.getValue())) {
                            String mountFilePath = item.getName();
                            if (mountFilePath.contains("${") && mountFilePath.contains("}")) {
                                mountFilePath = ParamInvalidUtil.replaceVarValue(mountFilePath, params);
                            }
                            String hostFilesPath = MaasConst.buildConfigPath(tuningModelnEntity)+ mountFilePath;
//                        String hostFilesPath = "/home/aimodel/config/"+tuningModelnEntity.getModelName()+"/"+tuningModelnEntity.getCode() + mountFilePath;

                            fileControllerService.createPath(tuningModelnEntity,MaasConst.buildModelOutPath(tuningModelnEntity),customClient);
                            JSONObject configFileResult = fileControllerService.downFile(tuningModelnEntity,item.getValue(), hostFilesPath, customClient, null);
                            if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_FAIL_STATUS) {
                                log.error(configFileResult.toJSONString());
                                return true;
                            }
                            if (tuningModelnEntity.getNameFilesPath() == null){
                                tuningModelnEntity.setNameFilesPath("");
                            }
                            //只配置主节点
                            if (!tuningModelnEntity.getNameFilesPath().contains(item.getCode())){
                                tuningModelnEntity.setHostFilesPath(StringUtils.isEmpty(tuningModelnEntity.getHostFilesPath()) ? hostFilesPath : tuningModelnEntity.getHostFilesPath() + "," + hostFilesPath);
                                tuningModelnEntity.setMountFilesPath(StringUtils.isEmpty(tuningModelnEntity.getMountFilesPath()) ? mountFilePath : tuningModelnEntity.getMountFilesPath() + "," + mountFilePath);
                                tuningModelnEntity.setNameFilesPath(StringUtils.isEmpty(tuningModelnEntity.getNameFilesPath()) ? item.getCode() : tuningModelnEntity.getNameFilesPath() + "," + item.getCode());
                            }
                        }
                    }
                }
                //20241025 lm 增加微调的时候 可以指定奖励模型用于评估训练效果
                if (tuningModelnEntity.getDoFrame() == 0 && StringUtils.isNotBlank(tuningModelnEntity.getRewardId())) {
                    //如果有奖励模型 原本的配置文件将不生效 统一改成奖励训练 去掉原本自带的template-lora-sft.yaml文件
                    String mountFilesPathCheck = tuningModelnEntity.getMountFilesPath();
                    int reIdx = -1;
                    if (mountFilesPathCheck!=null && mountFilesPathCheck.contains("/fine_tuning/template-lora-sft.yaml")){
                        String[] split = mountFilesPathCheck.split(",");
                        for (int i = 0; i < split.length; i++) {
                            if ("/fine_tuning/template-lora-sft.yaml".equals(split[i])){
                                reIdx = i;
                            }
                        }
                        String newNameFiles = tuningModelnEntity.getNameFilesPath().replace(tuningModelnEntity.getNameFilesPath().split(",")[reIdx], "");
                        String newMountFiles = tuningModelnEntity.getMountFilesPath().replace(tuningModelnEntity.getMountFilesPath().split(",")[reIdx], "");
                        String newHostFile = tuningModelnEntity.getHostFilesPath().replace(tuningModelnEntity.getHostFilesPath().split(",")[reIdx], "");
                        tuningModelnEntity.setNameFilesPath(StringHandlerUtil.removeFirstOrLastPoint(newNameFiles));
                        tuningModelnEntity.setMountFilesPath(StringHandlerUtil.removeFirstOrLastPoint(newMountFiles));
                        tuningModelnEntity.setHostFilesPath(StringHandlerUtil.removeFirstOrLastPoint(newHostFile));
                    }
                    log.info("启动奖励模型模式");
                    //1.步骤1 计算文件挂载配置 让容器服务把文件挂载文件
                    String mountName = tuningModelnEntity.getNameFilesPath();
                    String hostFilesPath = tuningModelnEntity.getHostFilesPath();
                    String mountPath = tuningModelnEntity.getMountFilesPath();
                    //在所有需要的文件都下载完成后 配置额外的奖励模型挂载路径进去
                    log.info("检查到需要启动奖励模型配置");
                    TuningModelnEntity rewardModelEntity = tuningModelnService.find(tuningModelnEntity.getRewardId());
                    if (rewardModelEntity == null) {
                        tuningModelnService.setFailStatus(tuningModelnEntity, "配置了无效的奖励模型配置!请切换为有效的奖励模型配置");
                        return true;
                    }
                    mountName = StringUtils.isEmpty(mountName) ? "rewardmodel" : mountName + ",rewardmodel";
                    String modelOutPut = MaasConst.buildModelOutPath(rewardModelEntity);
                    hostFilesPath = StringUtils.isEmpty(hostFilesPath) ? modelOutPut + "-adapter" : hostFilesPath + "," + modelOutPut + "-adapter";
                    //如果是奖励模型的情况 需要再次更换当前微调的配置
                    String rewardModelIPath = "/fine_tuning/rewardmodel";
                    mountPath = StringUtils.isEmpty(mountPath) ? rewardModelIPath : mountPath + "," + rewardModelIPath;
                    log.info("奖励模型配置完成!");
                    log.info("mountName:" + mountName);
                    log.info("hostFilesPath:" + hostFilesPath);
                    log.info("mountPath:" + mountPath);

                    tuningModelnEntity.setNameFilesPath(mountName+=",trainfile");

                    String configPath = MaasConst.buildConfigPath(tuningModelnEntity);

                    tuningModelnEntity.setHostFilesPath(hostFilesPath+","+configPath +"/fine_tuning/template-lora-sft.yaml");
                    tuningModelnEntity.setMountFilesPath(mountPath+",/fine_tuning/template-lora-sft.yaml");

                    //2.生成用来挂载的配置文件到指定位置
                    BuildCustomFileUtil.loadPPOTrainFile(tuningModelnEntity,customClient);
                    tuningModelnService.update(tuningModelnEntity);
                }
                log.info("开始生成训练集");
                //下载训练集
                JSONObject jsonObject = fileControllerService.downFile(tuningModelnEntity,tuningModelnEntity.getTrainFileId(), trainDataPath, customClient, fileControllerService.getOauthUrl());
                if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_FAIL_STATUS){
                    return true;
                }
                log.info("生成数据集成功!生成到" + trainDataPath);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void reloadTask(String message) {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        taskMqSender.sendTaskMessage(message);
    }

    public void reloadTask(TuningModelnEntity tuningModelnEntity) {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        taskMqSender.sendTaskMessage(JSONObject.toJSONString(tuningModelnEntity));
    }

    public static void createUpdateUser() {
        LogonCertificate logonCertificate = LogonCertificateHolder.getLogonCertificate();
        if (Objects.isNull(logonCertificate)) {
            logonCertificate = new LogonCertificate();
            logonCertificate.setUserId("TRAINPLAN");
            logonCertificate.setUserName("trainplan");
            LogonCertificateHolder.setLogonCertificate(logonCertificate);
        }
    }

    public void setRunning(TuningModelnEntity tuningModelnEntity, String msg) {
        createUpdateUser();
        tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_RUN_STATUS);
        tuningModelnEntity.setPlanMsg(msg);
        tuningModelnService.update(tuningModelnEntity);
        //20240722增加更新 任务列表的信息
        String taskId = tuningModelnEntity.getTaskId();
        tuningProgramQueueService.updateStatusByPlanId(taskId, MaasConst.DOPLAN_RUN_STATUS);
    }

    public void setFailStatus(TuningModelnEntity tuningModelnEntity, String msg, String podName) {
        tuningModelnService.setFailStatus(tuningModelnEntity,msg);
    }

}
