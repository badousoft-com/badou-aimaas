package com.badou.project.maas.tuningmodeln.service.impl;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.attach.AttachConfig;
import com.badou.brms.attach.AttachFactory;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.attach.service.IAttachService;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.*;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.brms.util.InstanceFactory;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.GlobalConsts;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataErrorException;
import com.badou.project.exception.DataExecFailException;
import com.badou.project.exception.DataValidException;
import com.badou.project.gpucalc.BaseGpuCalcHandler;
import com.badou.project.gpucalc.GpuCalcCardModel;
import com.badou.project.gpucalc.GpuCalcFactory;
import com.badou.project.gpucalc.impl.VllmGpuHandler;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.config.KubernetesConfig;
import com.badou.project.kubernetes.handler.KubernetesNameSpaceHandler;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.kubernetes.handler.KubernetesServiceHandler;
import com.badou.project.kubernetes.util.KubernetesConfigUtil;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.kubernetes.util.ZipUtils;
import com.badou.project.kubernetes.vo.DeployAppVo;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.StopCenter;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.k8sport.service.IK8sPortService;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.model.ModelAppVllmParamEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.modelapp.service.IModelAppVllmParamService;
import com.badou.project.maas.modelsync.service.IModelSyncTaskService;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.model.WareHouseVllmParamEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;
import com.badou.project.maas.modelwarehouse.service.IWareHouseVllmParamService;
import com.badou.project.maas.planlink.model.PlanLinkEntity;
import com.badou.project.maas.planlink.service.IPlanLinkService;
import com.badou.project.maas.registryaddress.model.RegistryAddressEntity;
import com.badou.project.maas.registryaddress.service.IRegistryAddressService;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfile.model.TrainMultiDetailFileEntity;
import com.badou.project.maas.trainfile.model.TrainMultiFileEntity;
import com.badou.project.maas.trainfile.service.ITrainFileService;
import com.badou.project.maas.trainfile.service.ITrainMultiDetailFileService;
import com.badou.project.maas.trainfile.service.ITrainMultiFileService;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.project.maas.trainfiledialoguedetail.model.TrainFileDialoguedetailEntity;
import com.badou.project.maas.trainfiledialoguedetail.service.ITrainFileDialoguedetailService;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.trainplan.service.ITrainPlanService;
import com.badou.project.maas.tuningmodeln.model.TrainPlanlogstatus;
import com.badou.project.maas.tuningprogramn.service.ITuningProgramnService;
import com.badou.project.maas.tuningprogramqueue.service.ITuningProgramQueueService;
import com.badou.project.mq.ModelPlanTaskMqReceiver;
import com.badou.project.mq.util.RabbitMqUtil;
import com.badou.project.util.BuildTrainDataUtil;
import com.badou.project.util.ParamInvalidUtil;
import com.badou.tools.common.util.SpringHelper;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.badou.brms.base.support.spring.BaseSpringService;
import com.badou.project.maas.tuningmodeln.dao.ITuningModelnDAO;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import static java.lang.Character.toUpperCase;


/**
 * @author badousoft
 * @date 2024-04-30 16:20:58.82
 * @todo 微调模型管理 Service接口实现类
 **/
@Service
@Slf4j
public class TuningModelnServiceImpl extends BaseSpringService<TuningModelnEntity, Serializable> implements ITuningModelnService {

    @Autowired
    private KubernetesServiceHandler kubernetesServiceHandler;
    @Autowired
    private ITuningModelnDAO tuningModelnDAO;
    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;
    @Autowired
    private IRegistryAddressService registryAddressService;
    @Autowired
    private ITuningProgramnService tuningProgramnService;
    @Autowired
    private ITrainPlanService trainPlanService;
    @Autowired
    private ITrainFileService trainFileService;
    @Autowired
    private ITrainFileDialogueService trainFileDialogueService;
    @Autowired
    private ITrainFileDialoguedetailService trainFileDialoguedetailService;
    @Autowired
    private IPlanLinkService planLinkService;
    @Autowired
    private IModelAppService modelAppService;
    @Autowired
    private IK8sPortService ik8sPortService;
    @Autowired
    private KubernetesNameSpaceHandler kubernetesNameSpaceHandler;
    @Autowired
    private IModelWarehouseService modelWarehouseService;
    @Autowired
    private ITrainMultiFileService trainMultiFileService;
    @Autowired
    private ITrainMultiDetailFileService trainMultiDetailFileService;
    @Autowired
    private FileControllerService fileControllerService;
    @Autowired
    private ITuningProgramQueueService tuningProgramQueueService;
    @Autowired
    private ITuningModelnService tuningModelnService;
    @Autowired
    private IWareHouseVllmParamService wareHouseVllmParamService;
    @Autowired
    private IModelAppVllmParamService modelAppVllmParamService;
    private static final Map<String, String> serverIdMap = new HashMap<>();
    @Autowired
    private BaseGpuCalcHandler baseGpuCalcHandler;
    @Autowired
    private IModelSyncTaskService modelSyncTaskService;

    public TuningModelnServiceImpl() throws Exception {
    }

    @Autowired
    public void setTuningModelnDAO(ITuningModelnDAO tuningModelnDAO) {
        this.tuningModelnDAO = tuningModelnDAO;
        super.setBaseDAO(tuningModelnDAO);
    }

    @Override
    public JsonReturnBean coverTunModel(TuningModelnEntity tuningModelnEntity, Integer type, String pubVersion, String pubMsg) {
        if (!(MaasConst.DOPLAN_SUCCESS_STATUS == tuningModelnEntity.getDoStatus())){
            return JsonResultUtil.errorMsg("只有微调成功的任务才允许上下架模型");
        }
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("tun_model_id", tuningModelnEntity.getId(), null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted", 0, null, QueryOperSymbolEnum.eq));
        List<ModelWarehouseEntity> modelWarehouseEntities = modelWarehouseService.find(queryCriterion);
        //上架
        if (type == 1) {
            if (modelWarehouseEntities.size() != 0) {
                return JsonResultUtil.errorMsg("已上架.请勿重复上架");
            }
            ModelWarehouseEntity oldModel = modelWarehouseService.find(tuningModelnEntity.getModelId());
            //基础模型不允许上下架操作
//            if (oldModel.getSource() == MaasConst.MODEL_SOURCE_BASE){
//                return JsonResultUtil.errorMsg("基础模型不允许上下架操作");
//            }
            //生成微调成功的路径
            ModelWarehouseEntity newWarehouseEntity = new ModelWarehouseEntity();
            BeanUtils.copyProperties(oldModel, newWarehouseEntity, "createTime", "id", "updateTime", "creator", "updator");
            newWarehouseEntity.setPath(MaasConst.buildMergedModelOutPath(tuningModelnEntity));
            newWarehouseEntity.setName("");
            newWarehouseEntity.setDescription("经过微调的模型.版本:"+pubVersion+",备注:"+pubMsg);
            newWarehouseEntity.setCreateTime(new Date());
            newWarehouseEntity.setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
            newWarehouseEntity.setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            newWarehouseEntity.setUpdateTime(new Date());
            newWarehouseEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            newWarehouseEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            newWarehouseEntity.setParentId(oldModel.getId());
            newWarehouseEntity.setDeployNum(1);
            newWarehouseEntity.setSource(MaasConst.MODEL_SOURCE_TUN);
            newWarehouseEntity.setTunModelId(tuningModelnEntity.getId());
            newWarehouseEntity.setPubVersion(pubVersion);
            newWarehouseEntity.setPubMsg(pubMsg);
            newWarehouseEntity.setFlgDeleted(0);
            newWarehouseEntity.setBaseModelId(tuningModelnEntity.getBaseModelId());
            newWarehouseEntity.setType(MaasConst.MODEL_TYPE_OTHER);
            newWarehouseEntity.setCustomGpuCard(null);
            newWarehouseEntity.setCustomGpuCardName(null);
            tuningModelnEntity.setShelvesStatus(1);
            updateImmediately(tuningModelnEntity);
            modelWarehouseService.create(newWarehouseEntity);
            return JsonResultUtil.success(newWarehouseEntity.getId());
        } else if (type == 0) {
            if (modelWarehouseEntities.size() == 0) {
                return JsonResultUtil.errorMsg("未上架!请先上架");
            }
            //检查是否有发布任务
            queryCriterion = new QueryCriterion();
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("model_id", tuningModelnEntity.getModelId(), null, QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted", 0, null, QueryOperSymbolEnum.eq));
            //状态小于2 是0未启动或者1正在运行的状态
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("do_status", 2, null, QueryOperSymbolEnum.lt));
            List<TuningModelnEntity> execingTask = tuningModelnDAO.find(queryCriterion);
            if (execingTask.size() != 0) {
                return JsonResultUtil.errorMsg("该模型正在微调中.请等待微调结束或者作废该任务");
            }
            //查询所有当前模型的应用 移除
            queryCriterion = new QueryCriterion();
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("model_id", tuningModelnEntity.getModelId(), null, QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted", 0, null, QueryOperSymbolEnum.eq));
            List<ModelAppEntity> modelAppEntities = modelAppService.find(queryCriterion);
            for (ModelAppEntity modelAppEntity : modelAppEntities) {
                modelAppService.stopApp(modelAppEntity.getId(),true,null);
            }
            ModelWarehouseEntity modelWarehouseEntity = modelWarehouseEntities.get(0);
            //下架
            modelWarehouseService.delete(modelWarehouseEntity);
            tuningModelnEntity.setShelvesStatus(0);
            tuningModelnDAO.update(tuningModelnEntity);
        } else {
            return JsonResultUtil.error();
        }
        return JsonResultUtil.success();
    }

    @Override
    public JsonReturnBean buildTaskStatusPanel(int type, TuningModelnEntity tuningModelnEntity) throws Exception {
        KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(tuningModelnEntity);
        String nameSpace = MaasConst.SPACE_TENSORBOARD;
        String appCode = tuningModelnEntity.getCode();
        V1PodList v1PodList = kubernetesPodHandler.queryPodByApp(kubernetesApiClient, nameSpace, appCode);
        String url = null;
        if (v1PodList.getItems().size() != 0) {
            V1Pod v1Pod = v1PodList.getItems().get(0);
            if ("Running".equals(v1Pod.getStatus().getPhase())) {
                V1ServiceList serviceByCode = kubernetesServiceHandler.getServiceByCode(kubernetesApiClient, nameSpace, appCode);
                if (serviceByCode.getItems().size() == 1) {
                    V1Service v1Service = serviceByCode.getItems().get(0);
                    Integer nodePort = v1Service.getSpec().getPorts().get(0).getNodePort();
                    url = kubernetesApiClient.getServer().getExposeAddress() + ":" + nodePort + "/#scalars";
                }
            }
        }
        if (StringUtils.isNotBlank(url)) {
            return JsonResultUtil.success(url);
        }
        //当服务存在而且不是运行状态时 移除 重新启动
        if (v1PodList.getItems().size() != 0) {
            try {
                kubernetesPodHandler.deleteOnePod(kubernetesApiClient, nameSpace, appCode);
                kubernetesServiceHandler.deleteService(kubernetesApiClient, nameSpace, appCode);
            } catch (Exception e) {

            }
        }

        String dic = MaasConst.DIC_PANEL_PARAMS + type;
        DictionaryCacheObject paramsDic = DictionaryLib.getDictionaryByCode(dic);

        if (JsonResultUtil.isNull(paramsDic)) {
            return JsonResultUtil.errorMsg("未配置服务信息!请联系管理员!");
        }
        //必须至少存在端口和工作目录的配置
        boolean portFlag = false;
        boolean workDirFlag = false;
        int port = -1;
        DictionaryItemCacheObject workDir = null;
        for (DictionaryItemCacheObject item : paramsDic.getItems()) {
            if ("port".equals(item.getCode())) {
                portFlag = true;
                port = Integer.parseInt(item.getValue());
            } else if ("workdir".equals(item.getCode())) {
                workDirFlag = true;
                workDir = item;
            }
        }
        if (!portFlag || workDirFlag == false || port == -1) {
            throw new DataEmptyException("未存在可执行的参数");
        }

        ArrayList<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
        v1VolumeMounts.add(new V1VolumeMount().mountPath(workDir.getValue()).name(workDir.getCode()));
        String path = MaasConst.buildModelOutPath(tuningModelnEntity);
        switch (tuningModelnEntity.getDoFrame()) {
            case 0:
                path += "-adapter/vis_data";
                break;
            case 2:
                path += "/runs";
                break;
            default:
                return JsonResultUtil.errorMsg("当前微调框架未支持分析功能!请联系管理员!");
        }

        DeployAppVo deployAppVo = new DeployAppVo(kubernetesApiClient, MaasConst.SPACE_TENSORBOARD, appCode, paramsDic.getValue(), 1, KubernetesConfig.getImagePullSecrets()
                , null, v1VolumeMounts, new V1Volume[]{new V1Volume().name(workDir.getCode()).hostPath(new V1HostPathVolumeSource().type("").path(path))}, null);
        kubernetesPodHandler.createPodByParams(paramsDic.getValue(), kubernetesApiClient, 1, nameSpace, appCode, deployAppVo);
        String result = kubernetesPodHandler.checkPodRunning(kubernetesApiClient, nameSpace, appCode, 3);
        if (StringUtils.isNotBlank(result)) {
            try {
                kubernetesPodHandler.deleteOnePod(kubernetesApiClient, nameSpace, appCode);
            } catch (Exception e) {

            }
            return JsonResultUtil.errorMsg(result);
        }
        try {
            int serviceByNewPort = createServiceByNewPort(kubernetesApiClient, nameSpace, appCode, port);
//            http://192.168.8.249:31220/#scalars
            return JsonResultUtil.success(kubernetesApiClient.getServer().getExposeAddress() + ":" + serviceByNewPort + "/#scalars");
        } catch (Exception e) {
            e.printStackTrace();
            kubernetesPodHandler.deleteOnePod(kubernetesApiClient, nameSpace, appCode);
            return JsonResultUtil.errorMsg("创建服务失败!" + e.getMessage() + "请联系管理员!");
        }
    }

    @Override
    public String closeTask(String id) throws Exception {
        String userName = LogonCertificateHolder.getLogonCertificate().getUserName();
        //还需要把线程关闭 这里还没做
        TuningModelnEntity tuningModelnEntity = tuningModelnDAO.find(id);
        //增加多机多卡任务移除
        if (tuningModelnEntity.getServerId().split(",").length > 1){
            JSONArray jsonArray = JSONArray.parseArray(tuningModelnEntity.getExecGpuCard());
            tuningModelnEntity.setServerId(jsonArray.getJSONObject(0).getJSONObject("k8sServerConfEntity").getString("id"));
        }
        try {
            TrainPlanEntity trainPlanEntity = trainPlanService.find(tuningModelnEntity.getPlanId());
            GpuCalcFactory.getSuitGpuCalcHandler(trainPlanEntity).removeTunPlanTargetCard(trainPlanEntity, tuningModelnEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StopCenter.startStop(tuningModelnEntity.getId(), "用户手动终止微调任务!");
        tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_CLOSE_STATUS);
        tuningModelnEntity.setPlanMsg("用户" + userName + "在" + DateUtil.getDateStrMin(new Date()) + "作废本任务!" + "\n应用历史日志\n" + tuningModelnEntity.getPlanMsg());
        tuningModelnDAO.update(tuningModelnEntity);
        return null;
    }

    @Override
    public String getServerId(TuningModelnEntity tuningModelnEntity) {
        String s = serverIdMap.get(tuningModelnEntity.getId());
        if (s != null) {
            return s;
        }
        if (StringUtils.isNotBlank(tuningModelnEntity.getServerId())) {
            return tuningModelnEntity.getServerId();
        }
        String serverId = tuningProgramnService.get(tuningModelnEntity.getTunPlanId()).getServerId();

        serverIdMap.put(tuningModelnEntity.getId(), serverId);
        return serverId;
    }

    @Override
    public List<TrainPlanlogstatus> getCurrentPlanLog(String id) {
        return null;
    }

    @Override
    public TuningModelnEntity getPodLogs(KubernetesApiClient kubernetesClient, String nameSpace, String podNam, @NotNull TuningModelnEntity tuningModelnEntity, boolean allFlg) throws IOException, ApiException {
        if (tuningModelnEntity.getDoStatus() != MaasConst.DOPLAN_RUN_STATUS) {
            return tuningModelnEntity;
        }
        if (StringUtils.isEmpty(tuningModelnEntity.getCreateDir())) {
            return setFailStatus(tuningModelnEntity, "错误的识别信息!请联系管理员!");
        }
        //检查状态
        V1Pod pod = kubernetesPodHandler.getPod(kubernetesClient, nameSpace, tuningModelnEntity.getCode());
        if (Objects.isNull(pod)) {
            return setFailStatus(tuningModelnEntity, "未能启动任务");
        }
        String phase = pod.getStatus().getPhase();
        if (StringUtils.isEmpty(phase)) {
            return setFailStatus(tuningModelnEntity, "任务执行失败!" + phase);
        }
        if ("padding".equals(phase)) {
            tuningModelnEntity.setPlanMsg("初始化任务中!");
            return tuningModelnEntity;
        }

        //获取全部日志
        String logs = kubernetesPodHandler.readPodAllLog(kubernetesClient, nameSpace, podNam, 999999);
        if (tuningModelnEntity.getDoStatus() != MaasConst.DOPLAN_RUN_STATUS) {
            return tuningModelnEntity;
        }
        //20240115 lm 增加检查多机多卡的时候 要额外检查子节点的状态
        new BaseGpuCalcHandler().checkServerConfigStatus(kubernetesClient, tuningModelnEntity, logs);
        if ("Running".equals(phase)) {
            setRunStatus(tuningModelnEntity, allFlg ? logs + "执行中" : "执行中");
        }
        if ("Failed".equals(phase)) {
            return setFailStatus(tuningModelnEntity, logs);
        }
        if (MaasConst.K8S_POD_SUCCEEDED.equals(phase)) {
            //llamafactory方式
            if (tuningModelnEntity.getDoFrame() == 0) {
                if (logs.indexOf("Model merge completed") != -1) {
                    //采集日志状态
                    return setSucccessStatus(tuningModelnEntity, logs + "执行微调完成!" + logs);
                }
            }
            if (tuningModelnEntity.getModelName().contains("chatglm3")) {
                String chatglm3Out = RabbitMqUtil.findChatglm3Out(logs);
                if (StringUtils.isEmpty(chatglm3Out)) {
                    return setFailStatus(tuningModelnEntity, logs + "获取chatglm3的实际信息失败!请联系管理员!");
                }
            }
            //采集日志状态
            return setSucccessStatus(tuningModelnEntity, logs + "执行微调完成!" + logs);
        }
        boolean runFlag = false;
        boolean checkFlg = false;
        boolean startTrain = false;
        Integer trainCount = null;

        //检查是否有错误
        int osError = logs.indexOf("Error");
        String osErrorMsg = null;
        // 检查是否有错误
        if (osError != -1) {
//            String errorAllMsg = logs.substring(osError);
//            int reLineIdx = errorAllMsg.indexOf("\n");
//            if (reLineIdx == -1) {
//                return setFailStatus(tuningModelnEntity, allFlg ? logs + "执行异常!获取错误失败!请联系管理员!" : "执行异常!获取错误失败!请联系管理员!");
//            }
//            osErrorMsg = logs.substring(osError, osError + reLineIdx);
            return setFailStatus(tuningModelnEntity, logs);
        }
        if (logs.contains("Running training")) {
            startTrain = true;
            int trainCountIdx = logs.indexOf("Num examples ");
            String[] trainCountArr = logs.substring(trainCountIdx, trainCountIdx + 30).split("\r\n");
            if (trainCountArr != null && trainCountArr.length > 1) {
                trainCount = Integer.parseInt(trainCountArr[0].split("=")[1].trim());
                logger.info("训练集数据个数");
            }
            int trainSteped = 0;
            if (logs.contains("{'loss':")) {
                int i = 0;
                String newStr = "{'loss':";
                while (logs.indexOf(newStr, i) >= 0) {
                    trainSteped++;
                    i = logs.indexOf(newStr, i) + newStr.length();
                }
            }
            logger.info("已训练" + trainSteped);
            int stepIdx = logs.indexOf("Total optimization steps");
            if (stepIdx != -1) {
                String[] stepArr = logs.substring(stepIdx, stepIdx + 30).split("\r\n");
                if (stepArr != null && stepArr.length > 0) {
                    String step = stepArr[0].split("=")[1].trim();
                    if (step.contains("\n")) {
                        step = step.split("\n")[0];
                    }
//				trainCount = Integer.parseInt(step);
                    logger.info("本次步数:" + trainCount);
                }
            } else {
                log.warn("无法计算步数");
            }
//            setRunStatus(tuningModelnEntity, allFlg ? logs + "训练中.... 进度:" + trainSteped + "/" + trainCount : "训练中.... 进度:" + trainSteped + "/" + trainCount);
            setRunStatus(tuningModelnEntity, logs);
//			if(trainSteped==trainCount){
//				//这里想办法解析 设置微调后的文件信息 然后生成模型文件
//				return setSucccessStatus(tuningModelnEntity,allFlg?logs+"微调完成.进度:"+trainSteped+"/"+trainCount:"微调完成.进度:"+trainSteped+"/"+trainCount);
//			}
        }

        //检查训练进度
        if (logs.contains("Sanity Check >>>>>>>>>>>>>")) {
//			checkFlg = true;
            return setRunStatus(tuningModelnEntity, allFlg ? logs + "启动中.训练集检查中" : "启动中.训练集检查中");
        }
        if (logs.contains("finetune.py")) {
//			runFlag = true;
            return setRunStatus(tuningModelnEntity, "启动中.执行finetune.py");
        }

        return tuningModelnEntity;
    }

    @Override
    public TuningModelnEntity getPodLogsUnUpdate(KubernetesApiClient kubernetesClient, String nameSpace, String podName, TuningModelnEntity tuningModelnEntity, boolean allFlg) throws IOException, ApiException {
        //应用成功
        if (MaasConst.DOPLAN_SUCCESS_STATUS == tuningModelnEntity.getDoStatus() && tuningModelnEntity.getAutoStartFlg() == GlobalConsts.ONE) {
            String msg = "模型微调成功!模型自启成功!";
            tuningModelnEntity.setPlanMsg(msg + "\n" + tuningModelnEntity.getPlanMsg() + "\n" + msg);
            return tuningModelnEntity;
        }
        //评价成功
        if (MaasConst.DOPLAN_SUCCESS_STATUS == tuningModelnEntity.getDoStatus() && tuningModelnEntity.getAutoStartFlg() == GlobalConsts.ONE
                && StringUtils.isNotBlank(tuningModelnEntity.getEvaluatioInstanId())) {
            String msg = "模型微调->模型自动启动->模型评价->成功!";
            tuningModelnEntity.setPlanMsg(msg + "\n" + tuningModelnEntity.getPlanMsg() + msg);
            return tuningModelnEntity;
        }
        if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_APP_STATUS) {
            tuningModelnEntity.setPlanMsg("正在启动模型" + tuningModelnEntity.getModelAllName() + "...");
            return tuningModelnEntity;
        }
        if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_SCORE_STATUS) {
            tuningModelnEntity.setPlanMsg("正在评价模型" + tuningModelnEntity.getModelAllName() + "...");
            return tuningModelnEntity;
        }
        if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_FAIL_STATUS || tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_SUCCESS_STATUS || tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_CLOSE_STATUS) {
            return tuningModelnEntity;
        }

        //检查状态
        V1Pod pod = kubernetesPodHandler.getPod(kubernetesClient, nameSpace, tuningModelnEntity.getCode());
        if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_RUN_STATUS && pod == null) {
            tuningModelnEntity.setPlanMsg("任务初始化中...");
            return tuningModelnEntity;
        }
        if (pod == null) {
            if (tuningModelnEntity.getPlanMsg().length() > 100) {
                return tuningModelnEntity;
            }
            return setFailStatusUnUpdate(tuningModelnEntity, "未创建任务!请启动微调!");
        }
        try {
            boolean b = kubernetesPodHandler.checkPodFail(pod);
            if (b) {
                if (pod!=null){
                    try {
                        tuningModelnEntity.setPlanMsg(kubernetesPodHandler.readPodAllLog(kubernetesClient, nameSpace, podName, 99999));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return setFailStatusUnUpdate(tuningModelnEntity, "任务失败!请联系管理员"+tuningModelnEntity.getPlanMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return setFailStatusUnUpdate(tuningModelnEntity, "任务异常!请联系管理员");
        }

        V1Pod onePod = kubernetesPodHandler.getOnePod(kubernetesClient, podName, nameSpace);
        if (onePod != null) {
           try {
               //获取全部日志
               String logs = kubernetesPodHandler.readPodAllLog(kubernetesClient, nameSpace, podName, 999999);
               tuningModelnEntity.setPlanMsg(logs.replace("\u001B[A", ""));
           }catch (Exception e){
               log.warn("POD存在 但获取日志异常");
           }
        }
        return tuningModelnEntity;
    }

    @Override
    public void createEntity(TuningModelnEntity tuningModelnEntity) {
        baseDAO.save(tuningModelnEntity);
    }

    @Override
    public void createTrainFile(TuningModelnEntity tuningModelnEntity) {
        //调用容器里面的服务 下载这个文件
        String createPath = MaasConst.buildTrainFilePath(tuningModelnEntity);
//        String createPath = MaasConst.TRAIN_MAIN_PATH;
        //改成支持现在的数据集流程
        //凌萌 20240701 增加系统角色支持 所有对象增加 {system: xxx}
        StringBuilder allTrainContent = new StringBuilder();
        JSONArray allResult = new JSONArray();
        //20240072 从微调方案里面读取数据
        List<TrainPlanEntity> trainPlanEntitys = trainPlanService.findByIds(tuningModelnEntity.getPlanId().split(","));
        if (Objects.isNull(trainPlanEntitys) || trainPlanEntitys.size() == 0) {
            setFailStatus(tuningModelnEntity, "微调计划的方案数据异常! 请联系管理员!");
            return;
        }
        for (TrainPlanEntity trainPlanEntity : trainPlanEntitys) {
            QueryCriterion queryCriterion = new QueryCriterion();
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("planLinkId", trainPlanEntity.getId(), null, QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
            List<PlanLinkEntity> planLinkEntities = planLinkService.find(queryCriterion);
            List<String> trainFileIds = new ArrayList<>();
            planLinkEntities.forEach(e -> {
                trainFileIds.add(e.getTrainDataId());
            });
            List result = new ArrayList();
            queryCriterion = new QueryCriterion();
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("id", trainFileIds, null, QueryOperSymbolEnum.in, QueryParam.PARAM_PLACEHOLDER_NAME));
            //		queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null,QueryOperSymbolEnum.eq));
            if (trainFileIds.size() == 0) {
                setFailStatus(tuningModelnEntity, "方案未设置训练集文件!请先设置!");
                return;
            }
            // 根据不同的训练集类型 如果不是多模态
            if (!MaasConst.TUN_PLAN_TYPE_MULIT.equals(trainPlanEntity.getDoWay())) {
                List<TrainFileEntity> trainFileEntities = trainFileService.find(queryCriterion);
                result = trainFileEntities;
                log.info("启用标准文本模型训练");
            } else {
                List<TrainMultiFileEntity> trainFileEntities = trainMultiFileService.find(queryCriterion);
                result = trainFileEntities;
                log.info("启用多模态训练");
            }
            if (JsonResultUtil.arrayEmpty(result)) {
                setFailStatus(tuningModelnEntity, "方案设置训练集的文件存在空数据");
                return;
            }
            //2.内容拼接起来
            try {
                if (!MaasConst.TUN_PLAN_TYPE_MULIT.equals(trainPlanEntity.getDoWay())) {
                    //根据方案获取训练集
                    for (Object obj : result) {
                        TrainFileEntity trainFileEntity = (TrainFileEntity) obj;
                        String roleDesc = trainFileEntity.getRoleDesc();
                        queryCriterion = new QueryCriterion();
                        queryCriterion.addParam(new QueryHibernatePlaceholderParam("trainFileId", trainFileEntity.getId(), null, QueryOperSymbolEnum.eq));
                        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
                        List<TrainFileDialogueEntity> trainFileDialogueEntities = trainFileDialogueService.find(queryCriterion);
                        if (trainFileDialogueEntities.size() == 0) {
                            setFailStatus(tuningModelnEntity, "训练集" + trainFileEntity.getName() + "的数据为空!");
                            return;
                        }
                        //查询具体的明细
                        //根据问答生成训练集
                        List<String> contentIds = new ArrayList<>();
                        for (TrainFileDialogueEntity e : trainFileDialogueEntities) {
                            //如果主问答包含问题和答案 则视为有效
                            if (JsonResultUtil.isNotNull(e.getQuestion())) {
                                //如果是作废了任务 这里就结束
                                boolean stop = StopCenter.checkStopFlag(tuningModelnEntity.getId(), "TuningModelnServiceImpl-模型微调任务-01停止");
                                if (stop) {
                                    return;
                                }
                                // JSON Line模式
                                /**
                                 * 适用范围
                                 * 1.chatglm3微调
                                 * 2.xtuner微调偏好模型时
                                 */
                                if (tuningModelnEntity.getModelName().contains("chatglm") || (tuningModelnEntity.getDoFrame() == 1 && tuningModelnEntity.getDoWay() == 2)) {
                                    allTrainContent.append(buildOneRound(e, tuningModelnEntity, roleDesc, trainPlanEntity).toJSONString() + "\n");
                                } else {
                                    allResult.add(buildOneRound(e, tuningModelnEntity, roleDesc, trainPlanEntity));
                                }
                            }
                            contentIds.add(e.getId());
                        }
                        if (contentIds.size() != 0) {
                            buildDetailData(contentIds, allTrainContent, tuningModelnEntity);
                        }
                    }
                } else {
                    //处理多模态格式的数据集
                    for (Object obj : result) {
                        TrainMultiFileEntity trainMultiFileEntity = (TrainMultiFileEntity) obj;
                        queryCriterion = new QueryCriterion();
                        queryCriterion.addParam(new QueryHibernatePlaceholderParam("mutiTrainId", trainMultiFileEntity.getId(), null, QueryOperSymbolEnum.eq));
                        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
                        List<TrainMultiDetailFileEntity> trainMultiDetailFileEntities = trainMultiDetailFileService.find(queryCriterion);
                        if (trainMultiDetailFileEntities.size() < 2) {
                            setFailStatus(tuningModelnEntity, "训练集" + trainMultiFileEntity.getName() + "的数据数量必须大于1!");
                            return;
                        }
                        long startTime = System.currentTimeMillis();
                        log.info("开始多模态数据集");
                        List<TrainMultiDetailFileEntity> batchUpdate = new ArrayList<>();
                        List<ZipUtils.ZipRequest> zipRequests = new ArrayList<>();

                        for (TrainMultiDetailFileEntity trainMultiDetailFileEntity : trainMultiDetailFileEntities) {
                            //下载训练集到容器内部 多模态文件夹放到files文件夹
                            //计算文件的md5值来存储 做唯一标识
                            String md5Str = trainMultiDetailFileEntity.getImgDefind();
                            Attach attach = SpringHelper.getBean(IAttachService.class).getById(trainMultiDetailFileEntity.getAttachId());
                            //md5Str.contains("files")是可能有一些旧的脏数据需要重置
                            if (StringUtils.isEmpty(md5Str)) {
                                AttachFactory factory = SpringHelper.getBean(AttachFactory.class);
                                //这个不会返回Content(附件文件本身) 需要注意
                                String fileName = attach.getName();
                                byte[] fileContent = factory.getFileContent(trainMultiDetailFileEntity.getAttachId());
//                                md5Str = BuildCustomFileUtil.calcMd5ByBytes(fileContent);
                                trainMultiDetailFileEntity.setImgDefind(attach.getFileName().split("/")[3]);
                                trainMultiDetailFileEntity.setAttachFile(fileName);
                                batchUpdate.add(trainMultiDetailFileEntity);
                            }
                            trainMultiDetailFileEntity.setImgDefind(attach.getFileName().split("/")[3]);
                            //  /attach/20240812/1723453254466-110336995.attach
                            String filePath = AttachConfig.getInstance().getAttachSavePath() + "/" + attach.getFileName();
                            ZipUtils.ZipRequest zipRequest = new ZipUtils.ZipRequest(md5Str,filePath);
                            zipRequests.add(zipRequest);
                            allResult.add(BuildTrainDataUtil.buildLlamafactoryImaData(trainMultiDetailFileEntity, tuningModelnEntity, ""));

                        }
                        //遍历转成压缩包 下载训练集到容器
//                        String toPath = createPath + "/files/" + md5Str;


                        //检查下载训练集是否成功
//                        if (MaasConst.DOPLAN_FAIL_STATUS == tuningModelnEntity.getDoStatus()) {
//                            log.error("下载训练集失败.下载命令错误或者文件下载服务状态异常!");
//                            return;
//                        }

                        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        String zipFileName = System.getProperty("java.io.tmpdir") + "/compressed_files_" + timestamp + ".zip";
                        // 压缩文件（如果存在需要处理的文件）
                        if (!zipRequests.isEmpty()) {
                            try {
                                KubernetesApiClient kubernetesApiClient = FileControllerService.getCacheK8sClient(tuningModelnEntity.getServerId());
                                log.info("1.图片训练集开始创建压缩包路径: " + zipFileName);
                                ZipUtils.ZipResult zipArchive = ZipUtils.createZipArchive(zipRequests,zipFileName);
                                log.info("1.图片训练集压缩包创建成功，路径: " + zipFileName+",大小"+StringHandlerUtil.getNetFileSizeToMB(zipArchive.getBytes().length));
                                log.info("2.开始上传成Attach");
                                Attach zipAttach = createAttach(new File(zipFileName), timestamp + ".zip", zipArchive.getBytes(), ".zip");
                                // 事务手动提交 毁报错但是不用管
                                SpringHelper.getBean(PlatformTransactionManager.class).commit(TransactionAspectSupport.currentTransactionStatus());
                                log.info("2.上传Attach完成");
                                log.info("3.调用容器下载");
                                String toPath = createPath + "/files/"+timestamp+".zip";
                                JSONObject jsonObject = fileControllerService.downFile(tuningModelnEntity,zipAttach.getId(),toPath, kubernetesApiClient, fileControllerService.getOauthUrl());
                                if (MaasConst.DOPLAN_FAIL_STATUS == tuningModelnEntity.getDoStatus()) {
                                    throw new DataExecFailException("下载训练集失败.下载命令错误或者文件下载服务状态异常!");
                                }

                                log.info("3.调用容器下载完成");
                                log.info("4.调用容器解压");
                                fileControllerService.execCommand(tuningModelnEntity, new String[]{"unzip", "-o", toPath, "-d", createPath + "/files/"}, kubernetesApiClient);
                                fileControllerService.execCommand(tuningModelnEntity, new String[]{"rm", "-rf", toPath}, kubernetesApiClient);
                                log.info("4.调用容器解压完成");
                                //清理压缩包
                                ZipUtils.cleanupCompressedFile(zipFileName);

                                //上传图片压缩包完成 上传训练集
//                                log.info("开始生成训练集");
//                                String trainDataPath = tuningModelnEntity.getCreatePath() + "/" + tuningModelnEntity.getModelFile();
//
//                                下载训练集
//                                jsonObject = fileControllerService.downFile(tuningModelnEntity,tuningModelnEntity.getTrainFileId(), trainDataPath,kubernetesApiClient,fileControllerService.getOauthUrl());
//                                if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_FAIL_STATUS){
//                                    throw new Exception("下载图片训练集信息失败");
//                                }
//                                log.info("生成数据集成功!生成到" + trainDataPath);
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new Exception("图片训练集压缩包文件时出错: " + e.getMessage());
                            }
                        }
                        //把这个内容 上传到附件表
                        trainMultiDetailFileService.batchUpdate(batchUpdate);
                        long endTime = (System.currentTimeMillis() - startTime) / 1000;
                        log.info("结束多模态数据集的创建.使用时间->" + endTime + "s");
                    }
                }


                if (allResult.size() > 0 && !tuningModelnEntity.getModelName().contains("chatglm3")) {
                    allTrainContent.append(allResult.toJSONString());
                }
                log.info("获取训练集内容完成,内容长度:" + allTrainContent.length());
                String content = allTrainContent.toString();
                //20240819 以前是用方案编码当文件名字 对接上llamafacoty后 使用固定的文件名字来对应上微调配置文件对应的名字
//                String fileName = tuningModelnEntity.getCode() + ".jsonl";
                String fileName = tuningModelnEntity.getDoFrame() == 0 ? "train.json" : "train.jsonl";
                if (containsChineseFlg(fileName)) {
                    throw new Exception("编码不允许包含中文!");
                }
                String path = FileControllerService.class.getResource("/").getPath();
                String allPath = path + File.separator + "traindata";
                File file = new File(allPath);
                //如果文件不存在则创建 创建成功后把内容写入
                if (!file.exists()) {
                    file.mkdir();
                } else {
                    file.delete();
                }
                file = new File(allPath + File.separator + fileName);
                file.createNewFile();
                FileWriter fileWritter = new FileWriter(file);
                fileWritter.write(content);
                fileWritter.close();
                byte bytes[] = new byte[512];
                bytes = content.getBytes();
                int b = bytes.length;   //是字节的长度，不是字符串的长度
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bytes, 0, b);
                fos.close();

                log.info("生成文件到" + file.getPath());
                //训练集的格式必须是jsonl 否则会报错 raise JSONDecodeError("Extra data", s, end)
                Attach json = createAttach(file, fileName, bytes, "json");


//			String trainDataPath = MaasConst.TRAIN_MAIN_PATH+modelName+"/"+fileName;
//			fileControllerService.createPath(MaasConst.TRAIN_MAIN_PATH+modelName);
//			JSONObject jsonObject = fileControllerService.downFile(json.getId(), trainDataPath);
//			JSONObject fileExist = fileControllerService.checkFileExist(trainDataPath);
//			//如果结果有数据 代表上传文件
//			String msg = fileExist.getString("msg");
//			if(msg.contains("No such")){
//				throw new Exception("生成训练集失败!请联系管理员!");
//			}
//			log.info("生成数据集成功!生成到"+msg);
//			if(org.apache.commons.lang.StringUtils.isEmpty(msg)){
//				throw new Exception("创建数据集失败!无法生成实际的数据集文件!");
//			}
                tuningModelnEntity.setCreatePath(createPath);
                tuningModelnEntity.setModelFile(fileName);
                if (json.getId() == null) {
                    throw new Exception("训练集文件ID不能是空");
                }
                tuningModelnEntity.setTrainFileId(json.getId());
                return;
            } catch (Exception e) {
                e.printStackTrace();
                setFailStatus(tuningModelnEntity, e.getMessage());
                return;
            }

        }
    }

    @Override
    public boolean startmodel(ModelWarehouseEntity modelWarehouseEntity, ModelAppEntity modelAppEntity) throws Exception {
        RegistryAddressEntity defaultRegistryAddress = registryAddressService.getDefaultRegistryAddress();
        if (defaultRegistryAddress == null){
            throw new DataValidException("未配置默认镜像仓库!请联系管理员!");
        }
        //20250607 lm 增加同步任务的判断
        String checkValidTask = modelSyncTaskService.checkValidTask(modelWarehouseEntity);
        if (StringUtils.isNotBlank(checkValidTask)){
            throw new DataValidException(checkValidTask);
        }
        //自定义显卡模式不判断
        if (StringUtils.isEmpty(modelWarehouseEntity.getCustomGpuCardName())){
            //单机多机判断
            //单机
            if (modelWarehouseEntity.getServerGpuMode() < 2 && modelWarehouseEntity.getServerId().split(",").length >1){
                throw new DataValidException("单机模式只能选择一个服务器");
            }else if (GlobalConsts.ONE.equals(modelWarehouseEntity.getServerGpuMode()) && modelWarehouseEntity.getGpuCount()< GlobalConsts.TWO){
                //单机多卡
                throw new DataValidException("单机多卡模式 显卡数量至少为2");
            }else if (modelWarehouseEntity.getServerGpuMode().equals(GlobalConsts.TWO) && modelWarehouseEntity.getServerId().split(",").length == 1){
                throw new DataValidException("多机模式必须选择两个或以上的服务器");
            }
            if (GlobalConsts.TWO.equals(modelWarehouseEntity.getServerGpuMode())){
                throw new DataValidException("目前还未支持多机多卡模式");
            }
        }
        RegistryAddressEntity registryAddressEntity = registryAddressService.getDefaultRegistryAddress();
        if (registryAddressEntity == null){
            throw new DataErrorException("未配置默认镜像仓库!请联系管理员!");
        }
        modelWarehouseService.calcGpuCache(modelWarehouseEntity);

        String workDir = "/home/aimodel/out/chatglm3/computer10/tool_alpaca_pt-PTV2_0000001-1718435440061";

        if (modelWarehouseEntity.getPort()!=null){
            QueryCriterion queryCriterion = new QueryCriterion();
            List<Integer> runningStatus = new ArrayList<>();
            runningStatus.add(MaasConst.DOPLAN_RUN_STATUS);
            runningStatus.add(MaasConst.DOPLAN_SUCCESS_STATUS);
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted",0,null,QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("port",modelWarehouseEntity.getPort(),null,QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("status",runningStatus,null,QueryOperSymbolEnum.in, QueryParam.PARAM_PLACEHOLDER_NAME));
            List<ModelAppEntity> modelAppEntities = modelAppService.find(queryCriterion);

            if (modelAppEntities.size()!=0){
                throw new DataValidException(modelWarehouseEntity.getPort()+"端口已被其他模型占用!请先释放配置端口的模型!");
            }
        }

        String oldPath = null;
        if (modelAppEntity == null) {
            modelAppEntity = new ModelAppEntity();
            if (GlobalConsts.ZERO.equals(modelWarehouseEntity.getDeployFrame()) && modelWarehouseEntity.getContentLength() == 0.0){
                throw new DataValidException("模型上下文需要至少>0");
            }
            BeanUtils.copyProperties(modelWarehouseEntity,modelAppEntity,"createTime","id","updateTime","creator","updator");
            modelAppEntity.setContentLength(modelWarehouseEntity.getContentLength());
            modelAppEntity.setAppDesc("用户手动启动");
            modelAppEntity.setModelResultId(UUID.randomUUID().toString());
            JSONObject jsonObject = ParamInvalidUtil.buildModelAllName(JSONObject.parseObject(JSONObject.toJSONString(modelWarehouseEntity)));
            modelAppEntity.setName(modelWarehouseEntity.getModelName());
            modelAppEntity.setGpuCount(1);
            modelAppEntity.setModelId(modelWarehouseEntity.getId());
            modelAppEntity.setFlgDeleted(0);
            modelAppEntity.setWorkPath(workDir);
            modelAppEntity.setUpdateTime(new Date());
            modelAppEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            modelAppEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
        } else {
            modelWarehouseEntity = modelWarehouseService.find(modelAppEntity.getModelId());
            oldPath = modelWarehouseEntity.getPath();
            modelWarehouseEntity.setPath(modelAppEntity.getWorkPath());
        }
        modelAppEntity.setWarehouseId(modelWarehouseEntity.getId());

        DictionaryCacheObject modelParams = DictionaryLib.getDictionaryByCode(MaasConst.TRAIN_REGISTRY_MODEL + modelAppEntity.getModelName());
        ModelPlanTaskMqReceiver.createUpdateUser();
        List<V1EnvVar> envVarList = new ArrayList<>();
        DeployAppVo deployAppVo = new DeployAppVo();
        List<V1Volume> v1Volumes = new ArrayList<>();
        ArrayList<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
        Map<String, String> paramsMap = new HashMap<>();
        KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(modelAppEntity.getServerId());
        List<V1EnvVar> varsList = new ArrayList<>();

        boolean p100Flag = false;
        boolean useDefaultFlag = false;
        if (modelParams.getItems().size() == 0 || GlobalConsts.ZERO.equals(modelWarehouseEntity.getDeployFrame())) {
            //全部人固定拿qwenvl的
            modelParams = DictionaryLib.getDictionaryByCode(MaasConst.VLLM_PARAMS_DIC);
            //如果是p100服务器必须要换镜像
//            if (FileControllerService.getCacheK8sClient(modelAppEntity.getServerId()).getServer().getRemark().contains("p100")){
//                modelParams.setValue(modelParams.getValue()+".pascal");
//                p100Flag = true;
//            }
            useDefaultFlag = true;
//            log.error(MaasConst.TRAIN_REGISTRY_MODEL + modelAppEntity.getModelName()+"的启动参数字典不存在!");
//            throw new DataEmptyException(modelAppEntity.getModelName()+"的启动参数字典不存在!请联系管理员!");
        }

        //目前只有GLM系列才支持官方方式
        if (GlobalConsts.ONE.equals(modelWarehouseEntity.getDeployFrame())){
            if (!modelWarehouseEntity.getModelName().contains("glm")){
                throw new DataValidException("目前官方方式只支持GLM系列.请切换目标模型!");
            }else {
                modelParams = DictionaryLib.getDictionaryByCode("tuning_model_glm-4");
            }
            modelParams.getItems().forEach(e->{
                if (e.getCode().contains("ENV_")){
                    envVarList.add(new V1EnvVar().name(e.getCode()).value(e.getValue()));
                }
            });
        }
        String image = modelParams.getValue();

        String nameSpace = MaasConst.MODEL_APP_NSAPCE;
        String appCode = modelAppEntity.getModelServerId();
        //操作环境参数
        paramsMap.put("taskCode", modelAppEntity.getId());
        paramsMap.put("modelCode", modelAppEntity.getModelName());
        paramsMap.put("createDir", workDir);
        paramsMap.put("newTunModel", modelWarehouseEntity.getPath());

        String path = modelWarehouseEntity.getPath();
        if (StringUtils.isNotBlank(kubernetesApiClient.getServer().getMainLocalPath())){
            path = path.replace("/home",kubernetesApiClient.getServer().getMainLocalPath());
        }
        //20250310 lm 如果是VLLM默认 则固定添加VLLM执行环境参数
        List<WareHouseVllmParamEntity> params = null;
        if (GlobalConsts.ZERO.equals(modelWarehouseEntity.getDeployFrame())) {
            params = wareHouseVllmParamService.getListByWareHoseId(modelWarehouseEntity.getId());
            if (JsonResultUtil.arrayEmpty(params)) {
                throw new DataEmptyException("未设置VLLM参数.无法执行");
            }
            //增加模型挂载到容器
            v1Volumes.add(new V1Volume().name("modelpath").hostPath(new V1HostPathVolumeSource().path(path).type("DirectoryOrCreate")));
            v1VolumeMounts.add(new V1VolumeMount().name("modelpath").mountPath(modelParams.getRemark()));
            //增加共享到容器
            v1Volumes.add(new V1Volume().name("dshm").emptyDir(MaasConst.SHM_AREA_2GB));
            v1VolumeMounts.add(new V1VolumeMount().name("dshm").mountPath("/dev/shm"));
        } else {
//            Map<String, Quantity> limits = new HashMap<>();
//            limits.put("aliyun.com/gpu-mem", new Quantity(modelWarehouseEntity.getStartNeedGpum().toString()));
//            deployAppVo.setLimits(limits);
            for (DictionaryItemCacheObject e : modelParams.getItems()) {
                //额外处理模型路径
                //如果包含路径 则设置映射
                if (e.getCode().contains("path")) {
                    String pathne = e.getValue();
                    for (String key : paramsMap.keySet()) {
                        pathne = ParamInvalidUtil.replaceVarValue(e.getValue(), key, paramsMap.get(key));
                        if (!pathne.contains("$")) {
                            break;
                        }
                    }
                    if (pathne.contains("$") || path.contains("{") || pathne.contains("}")) {
                        log.error("启动" + e.getValue() + "参数异常!请联系管理员!");
                        setFail(modelAppEntity, "启动" + e.getValue() + "参数异常!请联系管理员!");
                        return false;
                    }
                    if (!pathne.contains("/") && useDefaultFlag == false) {
                        setFail(modelAppEntity, modelAppEntity.getName() + "未配置启动信息!请联系管理员!");
                        return false;
                    }
                    v1Volumes.add(new V1Volume().name(RabbitMqUtil.handerRightKey(e.getCode())).hostPath(new V1HostPathVolumeSource().path(pathne).type("DirectoryOrCreate")));
                    v1VolumeMounts.add(new V1VolumeMount().name(RabbitMqUtil.handerRightKey(e.getCode())).mountPath(e.getCode()));
                } else {
                    //处理环境参数
//                    String content = e.getValue();
//                    while (content.contains("{")) {
//                        try {
//                            content = StringHandlerUtil.replaceVarParams(paramsMap, content);
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                            setFail(modelAppEntity, "设置参数失败!请联系管理员!");
//                            return false;
//                        }
//                    }
//                    e.setValue(content);
//                    envVarList.add(new V1EnvVar().name((e.getCode())).value(e.getValue()));
                }
            }
        }
        String imageTag = DictionaryLib.getItemCodeByItemValue(MaasConst.DIC_VLLM_VERSION, modelWarehouseEntity.getFrameVersion());
        if (StringUtils.isEmpty(imageTag)){
            kubernetesPodHandler.deleteDeployment(kubernetesApiClient, nameSpace, appCode);
            throw new DataValidException("未识别的镜像信息!请联系管理员!");
        }

        //为该任务选择实际的执行显卡
        List<MultipleServersConfig> taskCard = baseGpuCalcHandler.coverToServerCard(modelWarehouseEntity.getServerId(),
                modelWarehouseEntity.getStartNeedGpum(),modelWarehouseEntity.getCustomGpuCardName(),modelWarehouseEntity.getCustomGpuCard());

        if (taskCard == null || taskCard.size() == 0){
            //为本次任务分配显卡
            TuningModelnEntity appCardMsg = modelAppService.getAppCardMsg(modelWarehouseEntity);
            if (appCardMsg.getMultipleServersConfigs().size() == 0){
                throw new DataEmptyException("当前服务器无可用资源");
            }else if(appCardMsg.getMultipleServersConfigs().size() > 1){
                throw new DataValidException("目前应用部署只支持单机多卡.未支持多机");
            }
            taskCard = appCardMsg.getMultipleServersConfigs();
        }
        for (MultipleServersConfig multipleServersConfig : taskCard) {
            Map<Integer, GpuCalcCardModel> gpuCalcCardModels = multipleServersConfig.getGpuCalcCardModels();
            for (Integer key:gpuCalcCardModels.keySet()){
                GpuCalcCardModel gpuCalcCardModel = gpuCalcCardModels.get(key);
                if (gpuCalcCardModel.getName().contains("P100")){
                    p100Flag = true;
                    log.info("当前模型部署启用P100显卡兼容模式");
                    if (!imageTag.contains("0.7.3")){
                        throw new DataValidException("P100显卡仅支持VLLM 0.7.3版本");
                    }
                }
            }
        }

        //执行VLLM GPU处理器
        BaseGpuCalcHandler gpuCalcHandler = new VllmGpuHandler();
        gpuCalcHandler.exec(modelWarehouseEntity,envVarList,taskCard,params);
        modelAppEntity.setExecGpuCard(JSONArray.toJSONString(taskCard));
        //当计算出来本次任务需要的显卡的时候 则判断下任务服务器是否有这个模型
        for (MultipleServersConfig multipleServersConfig : taskCard) {
//            String mainLocalPath = multipleServersConfig.getK8sServerConfEntity().getMainLocalPath();
//            String pathValue = mainLocalPath!=null?path.replace("/home",multipleServersConfig.getK8sServerConfEntity().getMainLocalPath()):path;
//            JSONObject jsonObject = fileControllerService.execModelCommand(multipleServersConfig.getK8sServerConfEntity(), new String[]{"ls",pathValue });
            JSONObject jsonObject = fileControllerService.execModelCommand(multipleServersConfig.getK8sServerConfEntity(), new String[]{"ls",path});
            if (jsonObject.getString("msg").contains("cannot access")){
                throw new DataValidException("服务器"+multipleServersConfig.getK8sServerConfEntity().getRemark()+"不存在"+modelWarehouseEntity.getModelName()+"模型!请联系管理员或修正服务器的模型扫描目录");
            }
        }

        deployAppVo.setEnvVarList(envVarList);
        //只有POD支持 deployment是不支持Never的
        deployAppVo.setRestartPolicy("Always");
        deployAppVo.setImagePullPolicy("IfNotPresent");
        deployAppVo.setV1Volumes(v1Volumes.toArray(new V1Volume[]{}));
        deployAppVo.setV1VolumeMounts(v1VolumeMounts);
        deployAppVo.setAppCode(modelAppEntity.getId());

        if (p100Flag){
            imageTag+=".pascal";
        }
//        deployAppVo.setImageName(image+":v0.7.6");
        deployAppVo.setImageName(KubernetesConfigUtil.buildImageName(registryAddressEntity,image,imageTag));
        deployAppVo.setNameSpace(MaasConst.MODEL_APP_NSAPCE);
        deployAppVo.setSecretName(defaultRegistryAddress.getKeyName());
        deployAppVo.setEnvVarList(envVarList);
        deployAppVo.setKubernetesApiClient(kubernetesApiClient);
        deployAppVo.setNodeName(FileControllerService.getCacheK8sClient(modelAppEntity.getServerId()).getServer().getCode());

        if (StringUtils.isEmpty(modelAppEntity.getId())) {
            modelAppService.create(modelAppEntity);
        }
        //运行成功 把模型仓库的VLLM参数复制给模型应用
        List<ModelAppVllmParamEntity> copys = new ArrayList<>();
        if (GlobalConsts.ZERO.equals(modelWarehouseEntity.getDeployFrame())){
            for (WareHouseVllmParamEntity param : params) {
                ModelAppVllmParamEntity modelAppVllmParamEntity = new ModelAppVllmParamEntity();
                BeanUtil.copyProperties(param, modelAppVllmParamEntity, "id", "creator", "creatorName", "createTime");
                modelAppVllmParamEntity.setModelAppId(modelAppEntity.getId());
                modelAppVllmParamEntity.setUpdateTime(new Date());
                modelAppVllmParamEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
                modelAppVllmParamEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
                copys.add(modelAppVllmParamEntity);
            }
        }
        //处理命名空间
        kubernetesNameSpaceHandler.createNameSpace(kubernetesApiClient, nameSpace,null,defaultRegistryAddress);

        //检查是否运行过!
        V1PodList pod = kubernetesPodHandler.getPodByDeployment(kubernetesApiClient, nameSpace, appCode);
        if (pod != null && pod.getItems().size() != 0) {
            setFail(modelAppEntity, "应用已创建!取消创建应用操作!");
            return false;
        }
        if (appCode.length() > 63) {
            setFail(modelAppEntity, "应用标识主键长度不允许>63");
            return false;
        }
        deployAppVo.setSecretName(defaultRegistryAddress.getKeyName());
        String deploymentAndDeploy = kubernetesPodHandler.createDeploymentAndDeploy(
                kubernetesApiClient, nameSpace, appCode, deployAppVo.getImageName(), 1, false,deployAppVo);
        //等待该deploy生成内容
//        V1PodList podByDeployment = kubernetesPodHandler.getPodByDeployment(kubernetesApiClient, nameSpace, appCode);

        String startResult = kubernetesPodHandler.checkPodRunning(kubernetesApiClient, nameSpace, appCode,1);
        if (StringUtils.isNotBlank(startResult)){
            kubernetesPodHandler.deleteDeployment(kubernetesApiClient, nameSpace, appCode);
            throw new DataExecFailException(startResult);
        }

        int currentPort = modelAppEntity.getPort()!=null?modelAppEntity.getPort():ik8sPortService.calcNextPort();

        modelAppEntity.setPort(currentPort);
        modelAppEntity.setStatus(MaasConst.DOPLAN_SUCCESS_STATUS);
        try {

            //任务启动 创建service
            String tcp = kubernetesServiceHandler.createServiceAndDeploy(kubernetesApiClient, nameSpace, appCode, appCode, "TCP", 8000, 8000,
                    currentPort, false);
            modelAppEntity.setBuildSvc(tcp);
        } catch (ApiException e) {
            if (e.getResponseBody().contains("provided port is already allocated")){
                throw new DataExecFailException(currentPort+"端口已被其他模型使用.请变更其他可用端口或请先停止目标端口模型");
            }
            e.printStackTrace();
            ik8sPortService.deleteNew(currentPort);
            kubernetesPodHandler.deleteDeployment(kubernetesApiClient, nameSpace, appCode);
            setFail(modelAppEntity, e.getMessage());
            return false;
        }
        //没问题 设置成功
        setSuccess(modelAppEntity);
        log.info("启动模型" + modelAppEntity.getModelName() + "成功！");
        modelAppService.update(modelAppEntity);
        //还原路径
        if (StringUtils.isNotBlank(oldPath)) {
            modelWarehouseEntity.setPath(oldPath);
            modelWarehouseService.update(modelWarehouseEntity);
        }
        if (GlobalConsts.ZERO.equals(modelWarehouseEntity.getDeployFrame())){
            modelAppVllmParamService.batchCreate(copys);
        }
        return true;
    }

    private int createServiceByNewPort(KubernetesApiClient kubernetesApiClient, String nameSpace, String appCode, int port) throws Exception {
        V1ServiceList serviceByCode = kubernetesServiceHandler.getServiceByCode(kubernetesApiClient, nameSpace, appCode);
        try {
            if (serviceByCode != null) {
                kubernetesServiceHandler.deleteService(kubernetesApiClient, nameSpace, appCode);
            }
        } catch (Exception e) {
            if (!"Not Found".equals(e.getMessage())) {
                throw e;
            }
        }
        int currentPort = ik8sPortService.calcNextPort();

        //任务启动 创建service
        String tcp = kubernetesServiceHandler.createServiceAndDeploy(kubernetesApiClient, nameSpace, appCode, appCode, "TCP", port, port, currentPort, false);
        log.info(tcp);
        return currentPort;
    }

    public void setSuccess(ModelAppEntity modelAppEntity) {
        modelAppEntity.setStatus(MaasConst.DOPLAN_SUCCESS_STATUS);
        modelAppEntity.setMsg("部署成功");
        modelAppService.update(modelAppEntity);
    }

    public ModelAppEntity setFail(ModelAppEntity modelAppEntity, String msg) {
       return modelAppService.setFail(modelAppEntity,msg);
    }

    private void buildDetailData(List<String> contentIds, StringBuilder allTrainContent, TuningModelnEntity tuningModelnEntity) {
        //根据问答详情生成训练集 比如多轮对话
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("dialogueId", contentIds, null, QueryOperSymbolEnum.in, QueryParam.PARAM_PLACEHOLDER_NAME));
        List<TrainFileDialoguedetailEntity> trainFileDialoguedetailEntities = trainFileDialoguedetailService.find(queryCriterion);
        if (trainFileDialoguedetailEntities.size() == 0) {
            return;
        }
        JSONObject trainLine = new JSONObject();
        JSONArray conversations = new JSONArray();
        trainLine.put("conversations", conversations);
        if (tuningModelnEntity.getModelName().contains("llama") || tuningModelnEntity.getModelName().contains("wen")) {
            log.info("生成llama训练集或者千问训练集");
            allTrainContent.append(buildLllamaData(trainFileDialoguedetailEntities));
        } else {
            trainFileDialoguedetailEntities.forEach(e -> {
                buildMoreRound(conversations, e.getQuestion(), e.getFeedback(), tuningModelnEntity);
            });
            allTrainContent.append(trainLine.toJSONString());
        }
    }

    /**
     * 构建多轮对话 本次只构建问题本身
     *
     * @param question           问题
     * @param feedback           回复
     * @param tuningModelnEntity 微调模型实体
     * @return 生成的对话数据1
     */
    private JSONObject buildMoreRound(JSONArray conversations, String question, String feedback, TuningModelnEntity tuningModelnEntity) {
        //增加问题
        JSONObject body = new JSONObject();
        body.put("content", question);
        body.put("role", "user");
        conversations.add(question);
        //增加回复
        JSONObject replyBody = new JSONObject();
        replyBody.put("content", feedback);
        replyBody.put("role", "assistant");
        conversations.add(replyBody);
        return replyBody;
    }

    /**
     * 构建一轮对话
     *
     * @param trainFileDialogueEntity 单行的训练信息
     * @param tuningModelnEntity      微调模型实体
     * @return 生成的对话数据1
     */
    @Override
    public JSONObject buildOneRound(TrainFileDialogueEntity trainFileDialogueEntity, TuningModelnEntity tuningModelnEntity, String roleDesc, TrainPlanEntity trainPlanEntity) throws Exception {
        //所有类型的预训练集格式都是一样的 所以这里直接生成并返回
        if (MaasConst.TUN_PLAN_TYPE_PTTRAIN.equals(tuningModelnEntity.getDoWay())) {
            JSONObject trainLine = new JSONObject();
            String appendData = StringUtils.isNotBlank(trainFileDialogueEntity.getFeedback()) ? "," + trainFileDialogueEntity.getFeedback() : "";
            trainLine.put("text", trainFileDialogueEntity.getQuestion() + appendData);
            return trainLine;
        }
        //20241029 xtuner支持偏好训练集格式
        if (tuningModelnEntity.getDoFrame() == 1 && MaasConst.TUN_PLAN_TYPE_PPO.equals(tuningModelnEntity.getDoWay())) {
            return BuildTrainDataUtil.buildXtunerRewardData(trainFileDialogueEntity, tuningModelnEntity, roleDesc);
        }
        //20240819 训练改成llamafacoty所以是统一格式了。训练每一行改成这种格式
        // [{"instruction":"hi","input": "","output": "No, I am an AI assistant developed by {{author}}."}]
        if (tuningModelnEntity.getDoFrame() == 0) {
            JSONObject trainLine = new JSONObject();

            //20241112 lm 增加支持history字段 ptv2不确定是否支持 目前只有llamafactory的指令监督类型支持
            //查询当前对话是否拥有历史记录
            QueryCriterion queryCriterion = new QueryCriterion();
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("dialogue_id", trainFileDialogueEntity.getId(), null, QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted", 0, null, QueryOperSymbolEnum.eq));
            queryCriterion.addOrder(new QueryOrderby(0, "orderNo", SortOrderEnum.ase));
//            List<TrainFileDialoguedetailHisEntity> trainFileDialoguedetailHisEntities = trainFileDialoguedetailHisService.find(queryCriterion);
//            if (trainFileDialoguedetailHisEntities!=null && trainFileDialoguedetailHisEntities.size()>0){
//                List<String> historyList = new LinkedList<>();
//                trainFileDialoguedetailHisEntities.forEach(e->{
//                    historyList.add(e.getInstruction());
//                    historyList.add(e.getOutput());
//                });
//                trainLine.put("history",historyList);
//            }
            if (MaasConst.TUN_PLAN_TYPE_SFT.equals(tuningModelnEntity.getDoWay())) {
                trainLine.put("instruction", trainFileDialogueEntity.getQuestion());
                //当开启多卡 转成torchrun方式 其实input必须是空的
                //单卡的时候 input至少是空字符串或者有值的 不能是null
                trainLine.put("input", trainFileDialogueEntity.getInput() == null ? "" : trainFileDialogueEntity.getInput());
                trainLine.put("output", trainFileDialogueEntity.getFeedback());
                //如果行级别有配置系统提示词 则优先用行 如果没有则用父字段的系统字段
                String system = StringUtils.isEmpty(trainFileDialogueEntity.getSystem()) ? roleDesc : trainFileDialogueEntity.getSystem();
                trainLine.put("system", system);
            } else if (MaasConst.TUN_PLAN_TYPE_RL.equals(tuningModelnEntity.getDoWay())) {
                if (MaasConst.TUN_PLAN_TYPE_RM.equals(trainPlanEntity.getRlhfWay()) ||
                        MaasConst.TUN_PLAN_TYPE_DPO.equals(trainPlanEntity.getRlhfWay())){
                    //构建偏好数据集
                    trainLine.put("instruction", trainFileDialogueEntity.getQuestion());
                    trainLine.put("chosen", trainFileDialogueEntity.getChosena());
                    trainLine.put("rejected", trainFileDialogueEntity.getRejecteda());
                }else if (MaasConst.TUN_PLAN_TYPE_KPO.equals(trainPlanEntity.getRlhfWay())){
                    //构建KTO数据集
                    /**
                     * [
                     *   {
                     *     "instruction": "人类指令（必填）",
                     *     "input": "人类输入（选填）",
                     *     "output": "模型回答（必填）",
                     *     "kto_tag": "人类反馈 [true/false]（必填）"
                     *   }
                     * ]
                     */
                    trainLine.put("instruction", trainFileDialogueEntity.getQuestion());
                    trainLine.put("input", trainFileDialogueEntity.getInput() == null ? "" : trainFileDialogueEntity.getInput());
                    trainLine.put("output", trainFileDialogueEntity.getFeedback());
                    trainLine.put("kto_tag", trainFileDialogueEntity.getKtoTag()==0?false:true);
                }else {
                    throw new Exception("您选了暂未支持的偏好对齐算法.任务关闭");
                }


            } else {
                throw new Exception("未存在可支持的数据格式!请联系管理员!");
            }
            return trainLine;
        }

        if (tuningModelnEntity.getModelName().contains("llama") || tuningModelnEntity.getModelName().contains("wen")) {
            JSONArray conversations = new JSONArray();
            ;
            //增加问题和回复
            JSONObject trainLine = new JSONObject();
            trainLine.put("input", trainFileDialogueEntity.getQuestion());
            trainLine.put("output", trainFileDialogueEntity.getFeedback());
            //增加llama的系统角色
            if (StringUtils.isNotEmpty(roleDesc) && tuningModelnEntity.getModelName().contains("llama3")) {
                trainLine.put("system", roleDesc);
            }
            //增加qianwen的系统角色
            if (StringUtils.isNotEmpty(roleDesc) && tuningModelnEntity.getModelName().contains("wen")) {
                trainLine.put("instruction", "你是中兴公司的模型助手.学习了大量中兴通讯公司CT通信网络运维下真实文档数据.能进行高效私域知识问答");
            }
            conversations.add(trainLine);
            JSONObject line = new JSONObject();
            line.put("conversation", conversations);
            return line;
        }
        JSONObject trainLine = new JSONObject();
        JSONArray conversations = new JSONArray();
        trainLine.put("conversations", conversations);
        //chatglm3增加系统角色
        if (StringUtils.isNotEmpty(roleDesc)) {
            JSONObject systemBody = new JSONObject();
            systemBody.put("content", roleDesc);
            systemBody.put("role", "system");
            conversations.add(systemBody);
        }
        //增加问题
        JSONObject body = new JSONObject();
        body.put("content", trainFileDialogueEntity.getQuestion());
        body.put("role", "user");
        conversations.add(body);
        //增加回复
        JSONObject replyBody = new JSONObject();
        replyBody.put("content", trainFileDialogueEntity.getFeedback());
        replyBody.put("role", "assistant");
        conversations.add(replyBody);
        return trainLine;
    }

    @Override
    public void updateImmediately(TuningModelnEntity tuningModelnEntity) {
        tuningModelnDAO.updateImmediately(tuningModelnEntity);
    }

    private String buildLllamaData(List<TrainFileDialoguedetailEntity> trainFileDialoguedetailEntities) {
        JSONArray conversations;
        JSONArray results = new JSONArray();
        for (int i = 0; i < trainFileDialoguedetailEntities.size(); i++) {
            TrainFileDialoguedetailEntity trainFileDialoguedetailEntity = trainFileDialoguedetailEntities.get(i);
            //增加问题和回复
            JSONObject question = new JSONObject();
            question.put("input", trainFileDialoguedetailEntity.getQuestion());
            question.put("output", trainFileDialoguedetailEntity.getQuestion());
            question.put("system", "你是一个懂中文的小助手");
            conversations = new JSONArray();
            conversations.add(question);
            JSONObject line = new JSONObject();
            line.put("conversation", conversations);
            results.add(line);
        }
        return results.toJSONString();
    }

    /**
     * 首字母大写(进行字母的ascii编码前移，效率是最高的)
     *
     * @param fieldName 需要转化的字符串
     */
    public static String getMethodName(String fieldName) throws Exception {
        char[] chars = fieldName.toCharArray();
        chars[0] = toUpperCase(chars[0]);
        return String.valueOf(chars);
    }

    public static String txt2String(File file) {
        StringBuilder result = new StringBuilder();
        try {
            // 构造一个BufferedReader类来读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            // 使用readLine方法，一次读一行
            while ((s = br.readLine()) != null) {
                result.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private void createUpdateUser() {
        LogonCertificate logonCertificate = LogonCertificateHolder.getLogonCertificate();
        if (Objects.isNull(logonCertificate)) {
            logonCertificate = new LogonCertificate();
            logonCertificate.setUserId("TRAINPLAN");
            logonCertificate.setUserName("trainplan");
            LogonCertificateHolder.setLogonCertificate(logonCertificate);
        }
    }

    public TuningModelnEntity setRunStatus(TuningModelnEntity trainPlanEntity, String msg) {
        createUpdateUser();
        trainPlanEntity.setDoStatus(MaasConst.DOPLAN_RUN_STATUS);
        trainPlanEntity.setPlanMsg(msg);
        tuningModelnDAO.update(trainPlanEntity);
        return trainPlanEntity;
    }

    @Override
    public TuningModelnEntity getRunningTask() {
        return null;
    }

    @Override
    public TuningModelnEntity setFailStatus(TuningModelnEntity tuningModelnEntity, String msg) {
        createUpdateUser();
        tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_FAIL_STATUS);
        tuningModelnEntity.setPlanMsg(msg);
        tuningModelnEntity.setCostTime(DateUtil.getTimeDiff(tuningModelnEntity.getStartTime(), new Date()));
        tuningModelnService.update(tuningModelnEntity);
        //20240722增加更新 任务列表的信息
        String taskId = tuningModelnEntity.getTaskId();
        tuningProgramQueueService.updateStatusByPlanId(taskId, MaasConst.DOPLAN_FAIL_STATUS);
        try {
            TrainPlanEntity trainPlanEntity = trainPlanService.find(tuningModelnEntity.getPlanId());
            GpuCalcFactory.getSuitGpuCalcHandler(trainPlanEntity).removeTunPlanTargetCard(trainPlanEntity, tuningModelnEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tuningModelnEntity;
    }

    @Override
    public void setAppRunStatus(TuningModelnEntity tuningModelnEntity) {
        tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_APP_STATUS);
//        tuningModelnEntity.setPlanMsg("开始启动"+tuningModelnEntity.getModelAllName()+"模型...");
        log.info("开始启动" + tuningModelnEntity.getModelAllName() + "模型...");
        tuningModelnService.updateImmediately(tuningModelnEntity);
        try {
            KubernetesApiClient defaultClient = FileControllerService.getCustomClient(tuningModelnEntity.getServerId());
            TrainPlanEntity trainPlanEntity = trainPlanService.find(tuningModelnEntity.getPlanId());
            GpuCalcFactory.getSuitGpuCalcHandler(trainPlanEntity).removeTunPlanTargetCard(trainPlanEntity, tuningModelnEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public TuningModelnEntity setSucccessStatus(TuningModelnEntity tuningModelnEntity, String msg) {
        log.info("设置微调任务"+tuningModelnEntity.getName()+"为成功");
        //如果是需要自动启动应用
        if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_RUN_STATUS && tuningModelnEntity.getAutoStartFlg() == 1) {
            setAppRunStatus(tuningModelnEntity);
            return tuningModelnEntity;
        }
        createUpdateUser();
        tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_SUCCESS_STATUS);
//        tuningModelnEntity.setPlanMsg(msg);
        tuningModelnEntity.setCostTime(DateUtil.getTimeDiff(tuningModelnEntity.getStartTime(), new Date()));
        tuningModelnService.update(tuningModelnEntity);
        try {
            TrainPlanEntity trainPlanEntity = trainPlanService.find(tuningModelnEntity.getPlanId());
            GpuCalcFactory.getSuitGpuCalcHandler(trainPlanEntity).removeTunPlanTargetCard(trainPlanEntity, tuningModelnEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tuningModelnEntity;
    }

    /**
     * 设置运行状态 不更新
     *
     * @param trainPlanEntity
     * @param msg
     * @return
     */
    public TuningModelnEntity setRunStatusUnUpdate(TuningModelnEntity trainPlanEntity, String msg) {
        createUpdateUser();
        trainPlanEntity.setDoStatus(MaasConst.DOPLAN_RUN_STATUS);
        trainPlanEntity.setPlanMsg(msg);
        return trainPlanEntity;
    }

    public TuningModelnEntity setFailStatusUnUpdate(TuningModelnEntity trainPlanEntity, String msg) {
        createUpdateUser();
        trainPlanEntity.setDoStatus(MaasConst.DOPLAN_FAIL_STATUS);
        trainPlanEntity.setPlanMsg(msg);
        trainPlanEntity.setCostTime(DateUtil.getTimeDiff(trainPlanEntity.getStartTime(), new Date()));
        return trainPlanEntity;
    }

    public TuningModelnEntity setSucccessStatusUnUpdate(TuningModelnEntity trainPlanEntity, String msg) {
        createUpdateUser();
        trainPlanEntity.setDoStatus(MaasConst.DOPLAN_SUCCESS_STATUS);
        trainPlanEntity.setPlanMsg(msg);
        trainPlanEntity.setCostTime(DateUtil.getTimeDiff(trainPlanEntity.getStartTime(), new Date()));
        return trainPlanEntity;
    }

    public Attach createAttach(File file, String fileName, byte bytes[], String... fileType) {
        if (file != null && org.apache.commons.lang.StringUtils.isNotBlank(fileName)) {
            Attach attach = new Attach();
            attach.setFileContent(bytes);
            attach.setName(fileName);

            AttachFactory attachFactory = (AttachFactory) InstanceFactory.getInstance(AttachFactory.class);
            LogonCertificate logon = LogonCertificateHolder.getLogonCertificate();
            attach.setExtendName(".json");
            attach.setGenPersonId(logon.getUserId());
            attach.setGenPersonName(logon.getUserName());
            attach.setFileSize(bytes.length);
            if (fileType != null && fileType.length > 0) {
                attach.setFileType(fileType[0]);
            }
            try {
                attachFactory.uploadFile(attach);
            } catch (Exception var10) {
                log.error(var10.getMessage(), var10);
            }

            return attach;
        } else {
            return null;
        }
    }

    public static boolean containsChineseFlg(String str) {
        if (str == null) {
            return false;
        }
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

}

