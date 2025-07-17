package com.badou.project.maas.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.GlobalConsts;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.gpucalc.BaseGpuCalcHandler;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesExecHandler;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.mq.ModelPlanTaskMqReceiver;
import com.badou.project.server.model.ServerGpuEntity;
import com.badou.project.server.model.ServerGpuHistoryEntity;
import com.badou.project.server.service.IServerGpuHistoryService;
import com.badou.tools.redis.JedisConfig;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  扫描模型微调任务和模型部署任务的显存/线程使用记录
 */
@Component("scanTaskStatus")
@Slf4j
public class ScanTaskStatus {
    @Autowired
    private ITuningModelnService tuningModelnService;
    @Autowired
    private IModelAppService modelAppService;
    @Autowired
    private KubernetesExecHandler kubernetesExecHandler;
    @Autowired
    private BaseGpuCalcHandler baseGpuCalcHandler;
    @Autowired
    private IServerGpuHistoryService serverGpuHistoryService;
    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;

    // 固定延迟：上一次执行结束后延迟 5 秒执行下一次
    @Scheduled(fixedDelay = 65000)
//    @Scheduled(fixedRate = 3000)
    // 使用 Cron 表达式：每天凌晨 2 点执行
//    @Scheduled(cron = "0 0 2 * * ?")
    public void execute() {
        if ("localhost".equals(JedisConfig.host) || "127.0.0.1".equals(JedisConfig.host)){
//            log.info("集群状态采集服务.当前服务器为本地 不执行采集");
            return;
        }
        String dateStrMin = DateUtil.getDateStrMin(new Date());
        Date startTime = new Date();
        log.info("开始时间"+dateStrMin);
        //1.扫描微调和模型部署任务
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted", GlobalConsts.ZERO,
                null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("do_status",GlobalConsts.TWO,null,
                QueryOperSymbolEnum.lt));
        List<TuningModelnEntity> tuningModelnEntities = tuningModelnService.find(queryCriterion);
        queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted",GlobalConsts.ZERO,
                null,QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("status",GlobalConsts.ZERO,
                null,QueryOperSymbolEnum.gt));
        List<ModelAppEntity> modelAppEntities = modelAppService.find(queryCriterion);

        //检查运行状态和资源占用情况
        for (TuningModelnEntity tuningModelnEntity : tuningModelnEntities) {
            KubernetesApiClient kubernetesApiClient = null;
            try {
                kubernetesApiClient = FileControllerService.getCustomClient(tuningModelnEntity.getServerId());
                if (GlobalConsts.ONE.equals(tuningModelnEntity.getDoStatus())){
                    //微调中 看下有没有任务 如果状态是微调中并且30分钟内没有任务 则判断为初始化超时
                    V1Pod v1Pod = kubernetesPodHandler.getOnePod(kubernetesApiClient, tuningModelnEntity.getCode(), MaasConst.TRIAN_PLAN_NSPACE);
                    //代表任务创建成功 看下是不是Running 如果超过3分钟没有进入Running 代表任务启动失败
                    if (JsonResultUtil.isNotNull(v1Pod,v1Pod.getStatus(),v1Pod.getStatus().getPhase())){
                        if (!MaasConst.K8S_POD_RUNNING.equals(v1Pod.getStatus().getPhase())){
                            //代表任务创建成功 但是3分钟后都未进入RunnIng状态
                            int timeOutMin = 3;
                            if (isTimeout(tuningModelnEntity,timeOutMin)){
                                log.info(tuningModelnEntity.getCode()+"任务启动超时.启动超过"+timeOutMin+"分钟");
//                                tuningModelnService.setFailStatus(tuningModelnEntity,"任务启动超时.启动超过"+timeOutMin+"分钟");
                                continue;
                            }
                        }
                    }
                    if (v1Pod == null){
                        //代表正在初始化 看下是否超时
                        int timeOutMin = 30;
                        if (isTimeout(tuningModelnEntity,timeOutMin)){
                            log.info(tuningModelnEntity.getCode()+"初始化超时.初始化超过"+timeOutMin+"分钟");
//                            tuningModelnService.setFailStatus(tuningModelnEntity,"初始化超时.初始化超过"+timeOutMin+"分钟");
                            continue;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info(MaasConst.TUN_PLAN_NAME+"计算"+tuningModelnEntity.getName());
//            buildOneHistory(GlobalConsts.ONE,tuningModelnEntity.getName(),tuningModelnEntity.getId(),
//                    kubernetesApiClient,tuningModelnEntity.getCode(),tuningModelnEntity.getExecGpuCard());
            log.info(MaasConst.TUN_PLAN_NAME+"计算完成"+tuningModelnEntity.getName());
        }

        for (ModelAppEntity modelAppEntity : modelAppEntities) {
            KubernetesApiClient kubernetesApiClient = null;
            try {
                kubernetesApiClient = FileControllerService.getCustomClient(modelAppEntity.getServerId());

                //不存在POD
            } catch (Exception e) {
                e.printStackTrace();
            }
            V1Pod modelAppPod = modelAppService.getModelAppPod(modelAppEntity, kubernetesApiClient);
            if (modelAppPod == null || !MaasConst.K8S_POD_RUNNING.equals(modelAppPod.getStatus().getPhase())){
                //运行中 查看是否死亡
                //创建时间和现在时间对比 5分钟内都不能进入Running状态 则判断为超时
                int mins = 5;
                if (modelAppPod!=null && isTimeout(modelAppEntity,mins)){
                    log.warn(MaasConst.MODEL_APP_NAME+"->"+modelAppEntity.getModelServerId()+"初始化大于"+mins+"分钟.超时");
//                    modelAppService.setFail(modelAppEntity,modelAppService.getLogs(null,modelAppEntity,"0",3000,3));
                    continue;
                }
                log.warn(MaasConst.MODEL_APP_NAME+"->"+modelAppEntity.getModelResultId()+"状态是"+
                        MaasConst.K8S_POD_FAILED+",结束失败任务!");
                try {
//                    modelAppService.setFail(modelAppEntity,modelAppService.getLogs(null,modelAppEntity,"0",3000,3));
                    //老板希望失败自动删除 如果不是希望删除 那就默认设置成失败 如果后面不删除了 需要调整查询参数和状态更新 确保已经结束的任务不要再查询出来处理
//                    modelAppService.delete(modelAppEntity);
//                    if (modelAppPod!=null){
//                        modelAppService.removeAppResouce(modelAppEntity,kubernetesApiClient,modelAppPod);
//                    }
                    continue;
                } catch (Exception e) {
                    //注意 有可能出现Not Found错误 是正常的 有可能测试任务被服务器手动移除了
                    e.printStackTrace();
                }
            }else {

            }
            //计算当前任务占用的资源添加到算力表
            log.info(MaasConst.MODEL_APP_NAME+"计算"+modelAppEntity.getName());
            buildOneHistory(GlobalConsts.ONE,modelAppEntity.getId(),modelAppEntity.getName(),modelAppEntity,kubernetesApiClient,modelAppPod.getMetadata().getName(),"/startup,python");
            log.info(MaasConst.MODEL_APP_NAME+"计算完成"+modelAppEntity.getName());
        }
        log.info("结束时间"+ DateUtil.getDateStrMin(new Date()));
        log.info("花费时间:"+DateUtil.getTimeDiff(startTime,new Date()));

    }

    private ServerGpuHistoryEntity buildOneHistory(int type,String id,String name,ModelAppEntity modelAppEntity,KubernetesApiClient kubernetesApiClient,String podName,String thredTarget){
        ServerGpuHistoryEntity serverGpuHistoryEntity = new ServerGpuHistoryEntity();
        BeanUtils.copyProperties(modelAppEntity,serverGpuHistoryEntity,"createTime","id","updateTime","creator","updator");
        serverGpuHistoryEntity.setCollectObjName(name);
        serverGpuHistoryEntity.setCollectObjId(id);

        ModelPlanTaskMqReceiver.createUpdateUser();
        //获取线程
        //目前只需要显示VLLM相关的
        try {
            serverGpuHistoryEntity.setThreadMsg(JSONObject.toJSONString(kubernetesExecHandler.getPodProcessStatus(podName,
                    MaasConst.MODEL_APP_NSAPCE,kubernetesApiClient,thredTarget)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取显卡当前占用和已分配信息
        List<MultipleServersConfig> serversConfigs = JSONArray.parseArray(serverGpuHistoryEntity.getExecGpuCard(),MultipleServersConfig.class);
        int totalUsedGraphicsMemory = 0;
        int totalMaxGraphicsMemory = 0;
        int totalCurrentPowerDissipation = 0;
        int totalMaxPowerDissipation = 0;
        int totalUsageRate = 0;
        double totalTemperature = 0.0;
        int gpuCount = 0;

        List<ServerGpuEntity> serverGpuEntityList = new ArrayList<>();
        for (MultipleServersConfig serversConfig : serversConfigs) {
            Map<Integer, ServerGpuEntity> currentNewCardStatusMap = baseGpuCalcHandler.getCurrentNewCardStatusMap(serversConfig.getK8sServerConfEntity().getGpuMsgUrl());
            for (Integer key : serversConfig.getCanGpuCardNoMap().keySet()) {
                ServerGpuEntity nowStatus = currentNewCardStatusMap.get(key);
                totalUsedGraphicsMemory += nowStatus.getUsedGraphicsMemory();
                totalMaxGraphicsMemory += nowStatus.getMaxGraphicsMemory();
                totalCurrentPowerDissipation += nowStatus.getCurrentPowerDissipation();
                totalMaxPowerDissipation += nowStatus.getMaxPowerDissipation();
                totalUsageRate += nowStatus.getUsageRate();
                totalTemperature += nowStatus.getTemperature();
                serverGpuEntityList.add(nowStatus);
                gpuCount++;
            }
        }

        double averageUsedGraphicsMemory = gpuCount > 0 ? (double) totalUsedGraphicsMemory / gpuCount : 0;
        double averageMaxGraphicsMemory = gpuCount > 0 ? (double) totalMaxGraphicsMemory / gpuCount : 0;
        double averageCurrentPowerDissipation = gpuCount > 0 ? (double) totalCurrentPowerDissipation / gpuCount : 0;
        double averageMaxPowerDissipation = gpuCount > 0 ? (double) totalMaxPowerDissipation / gpuCount : 0;
        double averageUsageRate = gpuCount > 0 ? (double) totalUsageRate / gpuCount : 0;
        double averageTemperature = gpuCount > 0 ? totalTemperature / gpuCount : 0;

        // 设置平均值到 serverGpuHistoryEntity
        serverGpuHistoryEntity.setUsedGraphicsMemory((int) averageUsedGraphicsMemory);
        serverGpuHistoryEntity.setMaxGraphicsMemory((int) averageMaxGraphicsMemory);
        serverGpuHistoryEntity.setCurrentPowerDissipation((int) averageCurrentPowerDissipation);
        serverGpuHistoryEntity.setMaxPowerDissipation((int) averageMaxPowerDissipation);
        serverGpuHistoryEntity.setUsageRate((int) averageUsageRate);
        serverGpuHistoryEntity.setTemperature(averageTemperature);

        serverGpuHistoryEntity.setCollectType(type);
        serverGpuHistoryEntity.setServerGpuList(JSONObject.toJSONString(serverGpuEntityList));

        serverGpuHistoryService.create(serverGpuHistoryEntity);
        return serverGpuHistoryEntity;
    }


    /**
     * 判断是否超时
     * @param obj
     * @return
     */
    public static boolean isTimeout(ModelAppEntity obj,int mins) {
        // 获取当前时间
        Date now = new Date();
        // 计算时间差（毫秒）
        long timeDifference = now.getTime() - obj.getCreateTime().getTime();
        // 将时间差转换为分钟
        long minutesDifference = timeDifference / (1000 * 60);
        // 判断是否超过 5 分钟
        return minutesDifference > mins;
    }

    /**
     * 判断是否超时
     * @param obj
     * @return
     */
    public static boolean isTimeout(TuningModelnEntity obj,int min) {
        // 获取当前时间
        Date now = new Date();
        // 计算时间差（毫秒）
        long timeDifference = now.getTime() - obj.getCreateTime().getTime();
        // 将时间差转换为分钟
        long minutesDifference = timeDifference / (1000 * 60);
        // 判断是否处于 Running 状态且超过 30 分钟
        return minutesDifference > min;
    }

}

