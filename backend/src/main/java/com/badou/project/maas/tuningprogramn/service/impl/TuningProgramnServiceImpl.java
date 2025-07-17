package com.badou.project.maas.tuningprogramn.service.impl;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.GlobalConsts;
import com.badou.project.common.webparams.handler.WebParamHandler;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataValidException;
import com.badou.project.gpucalc.BaseGpuCalcHandler;
import com.badou.project.gpucalc.GpuCalcFactory;
import com.badou.project.gpucalc.GpuCalcHandler;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.evaluationinstan.model.EvaluationTFileEntity;
import com.badou.project.maas.evaluationinstan.service.IEvaluationTFileService;
import com.badou.project.maas.modeevalqu.model.TunModeEvalQuEntity;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.trainplan.service.ITrainPlanService;
import com.badou.project.maas.tuningevaluationn.model.TuningEvaluationnEntity;
import com.badou.project.maas.tuningevaluationn.service.ITuningEvaluationnService;
import com.badou.project.maas.tuningevaluationq.model.TuningEvaluationqEntity;
import com.badou.project.maas.tuningevaluationq.service.ITuningEvaluationqService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.maas.tuningplanparams.model.TuningPlanParamsEntity;
import com.badou.project.maas.tuningplanparams.service.ITuningPlanParamsService;
import com.badou.project.maas.tuningprogramparams.service.ITuningProgramParamsService;
import com.badou.project.maas.tuningprogramqueue.model.TuningProgramQueueEntity;
import com.badou.project.maas.tuningprogramqueue.service.ITuningProgramQueueService;
import com.badou.project.mq.TaskMqSender;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.openapi.models.V1Pod;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.tuningprogramn.dao.ITuningProgramnDAO;
import com.badou.project.maas.tuningprogramn.model.TuningProgramnEntity;
import com.badou.project.maas.tuningprogramn.service.ITuningProgramnService;

import static java.lang.Character.toUpperCase;


/**
 * @author badousoft
 * @date 2024-04-30 16:22:32.674
 * @todo 微调计划管理 Service接口实现类
 **/
@Service
public class TuningProgramnServiceImpl extends BaseSpringService<TuningProgramnEntity, Serializable> implements ITuningProgramnService {

    @Autowired
    private ITuningProgramnDAO tuningProgramnDAO;
    @Autowired
    private IModelWarehouseService modelWarehouseService;

    @Autowired
    public void setTuningProgramnDAO(ITuningProgramnDAO tuningProgramnDAO) {
        this.tuningProgramnDAO = tuningProgramnDAO;
        super.setBaseDAO(tuningProgramnDAO);
    }

    @Autowired
    private TaskMqSender taskMqSender;
    //微调评价
    @Autowired
    private ITuningEvaluationnService tuningEvaluationnService;
    //微调评价问题
    @Autowired
    private ITuningEvaluationqService tuningEvaluationqService;
    @Autowired
    private ITuningModelnService tuningModelnService;
    @Autowired
    private ITrainPlanService trainPlanService;
    @Autowired
    private ITuningPlanParamsService tuningPlanParamsService;
    @Autowired
    private FileControllerService fileControllerService;
    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;
    @Autowired
    private ITuningProgramQueueService tuningProgramQueueService;
    @Autowired
    private IEvaluationTFileService evaluationTFileService;

    @Override
    public boolean startPlan(Map<String, String[]> trainPlanEntityMap, String id) throws Exception {
        TuningProgramnEntity tuningProgramnEntity = tuningProgramnDAO.find(id);
        QueryCriterion queryCriterion = null;
        String[] tunPlans = tuningProgramnEntity.getTuningPlanId().split(",");
        List<TrainPlanEntity> trainPlanEntities = new ArrayList<>();
        for (String planId:tunPlans){
            trainPlanEntities.add(trainPlanService.find(planId));
        }

        Integer taskNum = tuningProgramnEntity.getTaskNum()==null?0: tuningProgramnEntity.getTaskNum();
        String doModels = tuningProgramnEntity.getDoModels()==null?"":tuningProgramnEntity.getDoModels();
        String newDoModels = "";

        tuningProgramnEntity.setTaskNum(taskNum+trainPlanEntities.size());
        //先检查是否在运行
        //检查是否在还运行中的任务
        for(TrainPlanEntity trainPlanEntity:trainPlanEntities){
            String serverId = trainPlanEntity.getServerId();
            if(StringUtils.isEmpty(serverId)){
                throw new Exception("未选择服务器!");
            }
            if (StringUtils.isEmpty(trainPlanEntity.getCode())) {
                throw new Exception("编码是必填项");
            }
            if (StringUtils.isNotBlank(trainPlanEntity.getEvaluationId())){
                //检查评价是否有配置评价训练集
                queryCriterion = new QueryCriterion();
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("evaluation_id", trainPlanEntity.getEvaluationId(), null, QueryOperSymbolEnum.eq));
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
                List<EvaluationTFileEntity> evaluationTFileEntities = evaluationTFileService.find(queryCriterion);
                if (evaluationTFileEntities.size() == 0){
                    throw new DataEmptyException("微调方案:"+trainPlanEntity.getName()+"中的评价信息"+trainPlanEntity.getEvaluationName()+"必须配置评价训练集");
                }
            }
            queryCriterion = new QueryCriterion();
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("planId",trainPlanEntity.getId(),null,QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("doStatus", GlobalConsts.ZERO,GlobalConsts.ONE,QueryOperSymbolEnum.between));
            List<TuningModelnEntity> tuningModelnEntities = tuningModelnService.find(queryCriterion);
            if (tuningModelnEntities.size() > GlobalConsts.ZERO){
                throw new Exception(trainPlanEntity.getName()+"方案存在运行!请先停止！r");
            }
            //20250429 lm 增加多机多卡 多服务器的支持
            String[] serverIds = serverId.split(",");
            if (serverIds.length>GlobalConsts.ONE){
                //判断是否合法
                if (trainPlanEntity.getGpuCount()< GlobalConsts.ONE
                || !GlobalConsts.TWO.equals(trainPlanEntity.getServerGpuMode())){
                    throw new Exception(trainPlanEntity.getName()+"方案配置错误!只有多机多卡模式才允许选择多个服务器!");
                }
                for (String serverId1:serverIds){
                    KubernetesApiClient defaultClient = FileControllerService.getCustomClient(serverId1);
                    V1Pod pod = null;
                    try {
                        pod  = kubernetesPodHandler.getPod(defaultClient, MaasConst.TRIAN_PLAN_NSPACE, trainPlanEntity.getCode());
                    }catch (Exception e){
                        e.printStackTrace();
                        throw new Exception("服务器异常请联系管理员!");
                    }
                    if(Objects.nonNull(pod)){
                        throw new Exception(trainPlanEntity.getName()+"方案存在运行!请先停止！r");
                    }
                }
                continue;
            }
            KubernetesApiClient defaultClient = FileControllerService.getCustomClient(serverId);
            V1Pod pod = null;
            try {
                pod  = kubernetesPodHandler.getPod(defaultClient, MaasConst.TRIAN_PLAN_NSPACE, trainPlanEntity.getCode());
            }catch (Exception e){
                e.printStackTrace();
                throw new Exception("服务器异常请联系管理员!");
            }
            if(Objects.nonNull(pod)){
                throw new Exception(trainPlanEntity.getName()+"方案存在运行!请先停止！r");
            }
        }

        for(TrainPlanEntity trainPlanEntity:trainPlanEntities){
            if (JsonResultUtil.isNull(trainPlanEntity.getMode())){
                throw new Exception("请设置有效的微调方案类型");
            }
            if (trainPlanEntity.getPreGpucache() == 0){
                throw new Exception("微调方案"+trainPlanEntity.getName()+"未配置对应模型框架的预估GPU显存配置!请联系管理员!");
            }
            //偏好数据格式 不能和奖励模型混在一起
            if (trainPlanEntity.getDoWay() == 2 && StringUtils.isNotBlank(trainPlanEntity.getRewardId())){
                throw new Exception("微调方案"+trainPlanEntity.getName()+"中选择了偏好格式。但该不支持奖励模型评价");
            }
            if (!doModels.contains(trainPlanEntity.getModelName())){
                newDoModels+=trainPlanEntity.getModelName();
            }
            if(Objects.isNull(trainPlanEntity) || Objects.isNull(trainPlanEntity.getId())){
                throw new Exception("无效的方案数据!请确认是否删除或者重新选择方案");
            }
            String serverId = trainPlanEntity.getServerId();
            if(StringUtils.isEmpty(serverId)){
                throw new Exception("未选择服务器!");
            }
            if (StringUtils.isEmpty(trainPlanEntity.getCode())) {
                throw new Exception("编码是必填项");
            }
            String code = trainPlanEntity.getCode();
            if (WebParamHandler.isHasNullAtPart(trainPlanEntityMap)) {
                logger.info("错误字段" + WebParamHandler.getFailField(trainPlanEntityMap));
                throw new Exception("无效的启动参数!请联系管理员!");
            }
            //20241025 lm 增加微调的时候指定奖励模型
            if (StringUtils.isNotBlank(trainPlanEntity.getRewardId())){
                //检查有效性
                //构建奖励模型存储路径
//                JSONObject jsonObject = fileControllerService.checkFileExist(, defaultClient);
            }
            tuningProgramnEntity.setModelAppId(trainPlanEntity.getModelId());
            tuningProgramnEntity.setModelAppName(trainPlanEntity.getModelName());
            checkAutoStart(trainPlanEntity);
            //1.检查数据集有效性
            String evaluationId = trainPlanEntity.getEvaluationId();
            //微调模型的评价
            List<TunModeEvalQuEntity> tunModeEvalQuEntities = new ArrayList<>();
            //如果设置了评价模型信息 则为该模型创建相关的记录
            if (Objects.nonNull(evaluationId)) {
                //多模态暂时不支持评价
                if (MaasConst.TUN_PLAN_TYPE_MULIT.equals(trainPlanEntity.getDoWay())){
                    throw new Exception("多模态暂时不支持评价!");
                }
                queryCriterion = new QueryCriterion();
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("id", evaluationId, null, QueryOperSymbolEnum.eq));
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
                List<TuningEvaluationnEntity> tuningEvaluationnEntities = tuningEvaluationnService.find(queryCriterion);
                if (JsonResultUtil.arrayEmpty(tuningEvaluationnEntities)) {
                    throw new Exception("评价数据获取失败!");
                }
                //获取评价的问题
                queryCriterion = new QueryCriterion();
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("evaluationId", evaluationId, null, QueryOperSymbolEnum.eq));
                List<TuningEvaluationqEntity> tuningEvaluationqEntities = tuningEvaluationqService.find(queryCriterion);
                for (TuningEvaluationqEntity tuningEvaQuestionEntity : tuningEvaluationqEntities) {
                    TunModeEvalQuEntity tunModeEvalQuEntity = new TunModeEvalQuEntity();
                    BeanUtils.copyProperties(tuningEvaQuestionEntity, tunModeEvalQuEntity);
                    tunModeEvalQuEntities.add(tunModeEvalQuEntity);
                }
            }
            queryCriterion = new QueryCriterion();
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("planId",trainPlanEntity.getId(), null, QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
            List<TuningPlanParamsEntity> tuningPlanParamsEntities = tuningPlanParamsService.find(queryCriterion);
            if(JsonResultUtil.arrayEmpty(tuningPlanParamsEntities)){
                throw new Exception("未设置微调参数!请先保存!");
            }
            //生成模型信息
            TuningModelnEntity tuningModelnEntity = new TuningModelnEntity();
            BeanUtils.copyProperties(trainPlanEntity,tuningModelnEntity,new String[]{"id","updateTime","updator","updatorName","createTime","creatorName","creator"});
            if (tuningModelnEntity.getId()!=null || tuningModelnEntity.getCreateTime()!=null){
                throw new Exception("无法处理!");
            }
            tuningModelnEntity.setPlanMsg("等待调度执行");
            tuningModelnEntity.setFlgDeleted(0);
            tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_WAIT_STATUS);
            tuningModelnEntity.setDoParams(tuningProgramnEntity.getDoParams());
            tuningModelnEntity.setPlanMsg("创建完成开始启动!");
            tuningModelnEntity.setModelAllName(trainPlanEntity.getModelAllName());
            tuningModelnEntity.setPlanId(trainPlanEntity.getId());
            tuningModelnEntity.setDoMethod(trainPlanEntity.getMode());
            tuningModelnEntity.setDoFrame(trainPlanEntity.getTunFrame());
            tuningModelnEntity.setPlanName(trainPlanEntity.getName());
            tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_WAIT_STATUS);
            tuningModelnEntity.setWorkSpace(MaasConst.TRIAN_PLAN_NSPACE);
            tuningModelnEntity.setStartTime(new Date());
            tuningModelnEntity.setCreateTime(new Date());
            tuningModelnEntity.setModelId(trainPlanEntity.getModelId());
            tuningModelnEntity.setModelName(trainPlanEntity.getModelName());
            tuningModelnEntity.setCreateDir(MaasConst.buildNewTunModel(tuningModelnEntity.getModelName()));
            tuningModelnEntity.setName(trainPlanEntity.getName()+"方案的微调任务");
            tuningModelnEntity.setTunPlanId(tuningProgramnEntity.getId());
            tuningModelnEntity.setShelvesStatus(0);
            tuningModelnEntity.setTunPlanName(tuningProgramnEntity.getName());

            //计算当前服务是否有模板
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
            //为本次任务分配显卡
            new BaseGpuCalcHandler().calcTargetCardAndDeploy(trainPlanEntity,tuningModelnEntity);

            if (tuningModelnEntity.getMultipleServersConfigs().size() == 0){
                throw new Exception("分配显卡异常!请联系管理员!");
            }
            if(tuningModelnEntity.getModelName() == null || tuningModelnEntity.getModelId()==null){
                throw new Exception("数据异常!");
            }
            tuningModelnEntity.setExecGpuCard(JSONArray.toJSONString(tuningModelnEntity.getMultipleServersConfigs()));

            //如果是基础模型类型
            ModelWarehouseEntity modelWarehouseEntity = modelWarehouseService.find(tuningModelnEntity.getModelId());
            if (MaasConst.MODEL_SOURCE_BASE.equals(modelWarehouseEntity.getSource())){
                tuningModelnEntity.setBaseModelId(modelWarehouseEntity.getId());
            }else if (MaasConst.MODEL_SOURCE_TUN.equals(modelWarehouseEntity.getSource())){
                tuningModelnEntity.setBaseModelId(modelWarehouseEntity.getBaseModelId());
            }

            //20240722增加创建微调任务
            TuningProgramQueueEntity tuningProgramQueueEntity = new TuningProgramQueueEntity();
            tuningProgramQueueEntity.setExecStatus(MaasConst.DOPLAN_WAIT_STATUS);
            tuningProgramQueueEntity.setPlanId(trainPlanEntity.getId());
            tuningProgramQueueEntity.setPlanName(trainPlanEntity.getName());
            tuningProgramQueueEntity.setProgramId(tuningProgramnEntity.getId());
            tuningProgramQueueEntity.setProgramName(tuningProgramnEntity.getName());
            tuningProgramQueueEntity.setModelId(trainPlanEntity.getModelId());
            tuningProgramQueueEntity.setModelName(trainPlanEntity.getName());
            tuningProgramQueueEntity.setFlgDeleted(0);
            tuningProgramQueueEntity.setTaskName(trainPlanEntity.getName()+"方案的微调任务");
            tuningProgramQueueEntity.setTaskType(0);
            tuningModelnService.createEntity(tuningModelnEntity);
            tuningProgramQueueEntity.setTaskId(tuningModelnEntity.getId());

            tuningProgramQueueEntity.setUpdateTime(new Date());
            tuningProgramQueueEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            tuningProgramQueueEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            tuningProgramQueueService.create(tuningProgramQueueEntity);
            tuningModelnEntity.setTaskId(tuningProgramQueueEntity.getId());
            tuningModelnService.update(tuningModelnEntity);
            //发送任务到MQ一直监听启动状态并实时更新状态
            taskMqSender.sendTaskMessage(JSONObject.toJSONString(tuningModelnEntity));
            //再创建评价任务 如果有开启的前提
            if(StringUtils.isNotEmpty(trainPlanEntity.getEvaluationId())){
                //获取评价ID保持起来
                TuningProgramQueueEntity evaEntity = new TuningProgramQueueEntity();
                BeanUtils.copyProperties(tuningProgramQueueEntity,evaEntity);
                evaEntity.setTaskType(1);
                evaEntity.setTaskId(trainPlanEntity.getEvaluationId());
                evaEntity.setExecStatus(MaasConst.DOPLAN_WAIT_STATUS);
                evaEntity.setTaskName(tuningProgramnEntity.getName()+"的评价任务");
                //创建评价实例
                tuningProgramQueueService.create(evaEntity);
                evaEntity.setCreateTime(new Date(evaEntity.getCreateTime().getTime()+1000));
                evaEntity.setUpdateTime(new Date(evaEntity.getUpdateTime().getTime()+1000));
                tuningProgramQueueEntity.setNextTaskId(evaEntity.getId());
                //更新评价任务 创建时间晚一秒
                tuningProgramQueueService.update(evaEntity);
                tuningProgramQueueService.update(tuningProgramQueueEntity);
            }
        }
        if (StringUtils.isNotEmpty(newDoModels)){
            String newModels1 = String.join(",", newDoModels);
            tuningProgramnEntity.setDoModels(doModels+=","+newModels1);
        }
        tuningProgramnDAO.update(tuningProgramnEntity);
        return true;
    }

    /**
     * 检查自动启动所需要的数据
     * @param trainPlanEntity
     */
    private void checkAutoStart(@NotNull TrainPlanEntity trainPlanEntity) throws Exception {
        String modelName = trainPlanEntity.getModelName();
        ModelWarehouseEntity modelWarehouseEntity = modelWarehouseService.find(trainPlanEntity.getModelId());

        if (modelWarehouseEntity == null){
            throw new Exception(modelName+"未设置模型信息.请联系管理员!!");
        }else if (StringUtils.isEmpty(modelWarehouseEntity.getPath())){
            throw new Exception(modelName+"未设置模型信息无效.请联系管理员!!");
        }
//        if(trainPlanEntity.getAutoStartFlg()==1){
//            DictionaryCacheObject modelParams = DictionaryLib.getDictionaryByCode(MaasConst.TRAIN_REGISTRY_MODEL + modelName);
//            if(modelParams==null || modelParams.getItems().size()==0 || modelParams.getItems().size()<2){
//                throw new Exception(modelName+"未配置启动信息请联系管理员!");
//            }
//            String value = modelParams.getValue();
//            if(StringUtils.isEmpty(value)){
//                throw new Exception(modelName+"未配置启动服务信息请联系管理员!");
//            }
//            AtomicBoolean portFlg = new AtomicBoolean(false);
//            modelParams.getItems().forEach(e->{
//               if (e.getCode().contains("port")){
//                    portFlg.set(true);
//                }
//            });
//            if(portFlg.get()==false){
//                throw new Exception(modelName+"未配置正确服务信息请联系管理员!");
//            }
//        }
    }

    @Override
    public boolean createEntity(TuningProgramnEntity tuningProgramnEntity) {
        baseDAO.save(tuningProgramnEntity);
        return false;
    }

    @Override
    public void updateAttachId(String id) {
        tuningProgramnDAO.updateAttachId(id);
    }

    private void setFailStatus(TuningModelnEntity tuningModelnEntity, String msg) {
        tuningModelnEntity.setDoStatus(MaasConst.DOPLAN_FAIL_STATUS);
        tuningModelnEntity.setPlanMsg(msg);
    }

}
 
 