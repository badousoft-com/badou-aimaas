package com.badou.project.gpucalc;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.QueryParam;
import com.badou.brms.dboperation.query.QueryParamGroup;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.GlobalConsts;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataValidException;
import com.badou.project.exception.ExecFailException;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesNameSpaceHandler;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.kubernetes.handler.KubernetesServiceHandler;
import com.badou.project.kubernetes.handler.impl.KubernetesBasePodHandlerImpl;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.kubernetes.vo.DeployAppVo;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.model.ServerGpuEntity;
import com.badou.project.server.service.IK8sServerConfService;
import com.badou.tools.common.util.SpringHelper;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
/**
 * 基础GPU计算处理器
 */
@Component
public class BaseGpuCalcHandler implements GpuCalcHandler {

    private static final String BASE_MASTER_HOST = "badouaimaas-master0";

    @Override
    public void checkServerConfigStatus(KubernetesApiClient kubernetesApiClient, TuningModelnEntity tuningModelnEntity,String masterLog) throws ApiException {
        //查看关联的任务是否失败
        final List<MultipleServersConfig> multipleServersConfigs = tuningModelnEntity.getMultipleServersConfigs();
        KubernetesPodHandler kubernetesPodHandler = SpringHelper.getBean(KubernetesPodHandler.class);
        if (multipleServersConfigs.size() > 1) {
            log.info("多机多卡任务-检查从机任务状态");
            //移除多卡情况下的任务
            int rank = 1;
            for (MultipleServersConfig multipleServersConfig : multipleServersConfigs) {
                K8sServerConfEntity k8sServerConfEntity = multipleServersConfig.getK8sServerConfEntity();
                //这里只移除从机的任务 主机的任务其他地方已经移除了
                if (GlobalConsts.ONE.equals(k8sServerConfEntity.getLevelSort())) {
                    String slaveName = StringHandlerUtil.createSlaveName(tuningModelnEntity.getCode(), rank);
                    V1Pod pod = kubernetesPodHandler.getOnePod(kubernetesApiClient, slaveName, MaasConst.TRIAN_PLAN_NSPACE);
                    if (pod != null) {
                        ITuningModelnService tuningModelnService = SpringHelper.getBean(ITuningModelnService.class);
                        try {
                            boolean failFlag = kubernetesPodHandler.checkPodFail(pod);
                            if (failFlag){
                                tuningModelnService.setFailStatus(tuningModelnEntity,masterLog+"\n==================== badoumaas-"+k8sServerConfEntity.getCode()+"节点异常=====================================\n"+kubernetesPodHandler.readPodAllLog(kubernetesApiClient,MaasConst.TRIAN_PLAN_NSPACE,slaveName,99999));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                tuningModelnService.setFailStatus(tuningModelnEntity,masterLog+"\n==================== badoumaas-"+k8sServerConfEntity.getCode()+"节点异常=====================================\n"+kubernetesPodHandler.readPodAllLog(kubernetesApiClient,MaasConst.TRIAN_PLAN_NSPACE,slaveName,99999));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }

                    }
                    rank++;
                }
            }
        }
    }

    @Override
    public void buildServerConfigExtend(KubernetesApiClient kubernetesApiClient, TuningModelnEntity tuningModelnEntity) throws ApiException {
        if (tuningModelnEntity.getMultipleServersConfigs().size() > 1) {
            //如果是多机多卡的情况下 要设置本次服务提供的端口 先固定一个端口
            KubernetesServiceHandler kubernetesServiceHandler = SpringHelper.getBean(KubernetesServiceHandler.class);
            kubernetesServiceHandler.createServiceAndDeploy(kubernetesApiClient,MaasConst.TRIAN_PLAN_NSPACE,tuningModelnEntity.getCode(),tuningModelnEntity.getCode(),
                    "TCP",32767,32767,32767,false);
        }
    }

    @Override
    public List<V1Pod> buildServerConfig(TuningModelnEntity tuningModelnEntity, V1Pod v1Pod) {
        List<V1Pod> execPods = new LinkedList<>();
        //指定本次任务执行的服务
            if (tuningModelnEntity.getMultipleServersConfigs().size() == 1) {
            v1Pod.getSpec().setNodeName(tuningModelnEntity.getMultipleServersConfigs().get(0).getK8sServerConfEntity().getCode());
            execPods.add(v1Pod);
            return execPods;
        }
        //如果是多机多卡的情况
        if (tuningModelnEntity.getMultipleServersConfigs().size() > 1) {
            log.info("集群多机多卡任务确认.开始配置");

            List<V1EnvVar> v1EnvVars = v1Pod.getSpec().getContainers().get(0).getEnv();
            List<V1EnvVar> v1NewEnvVars = new ArrayList<>();
            v1NewEnvVars.addAll(v1EnvVars);

            MultipleServersConfig master = tuningModelnEntity.getMultipleServersConfigs().get(0);

            //节点序号
            Integer rank = 1;
            //处理主节点
            //处理子节点
            for (MultipleServersConfig multipleServersConfig : tuningModelnEntity.getMultipleServersConfigs()) {
                K8sServerConfEntity k8sServerConfEntity = multipleServersConfig.getK8sServerConfEntity();
                List<V1EnvVar> env = null;
                //如果是主机的配置
                if (multipleServersConfig.getK8sServerConfEntity().getId().equals(master.getK8sServerConfEntity().getId())) {
                    log.info("配置master-0");
                    V1Pod masterPod = JSONObject.parseObject(JSONObject.toJSONString(v1Pod),V1Pod.class);
                    masterPod.getSpec().setHostNetwork(true);

                    List<V1EnvVar> envVars = new ArrayList<>();
                    envVars.addAll(v1NewEnvVars);
                    masterPod.getSpec().getContainers().get(0).setEnv(envVars);
                    env = envVars;

                    envVars.add(new V1EnvVar().name("RANK").value("0"));
                    //增加本次任务执行的显卡
                    envVars.add(new V1EnvVar().name("NVIDIA_VISIBLE_DEVICES").value(multipleServersConfig.getCanGpuCardNos()));
                    //把等待超时时间改成1小时
                    envVars.add(new V1EnvVar().name("TORCH_NCCL_HEARTBEAT_TIMEOUT_SEC").value("6000"));
                    execPods.add(masterPod);
                } else {
                    V1Pod slave = JSONObject.parseObject(JSONObject.toJSONString(v1Pod),V1Pod.class);
                    slave.getMetadata().setName(StringHandlerUtil.createSlaveName(slave.getMetadata().getName(), rank));
                    //如果是子机的配置
                    log.info("配置slave" + rank);
                    slave.getSpec().setHostNetwork(true);

                    List<V1EnvVar> envVars = new ArrayList<>();
                    envVars.addAll(v1NewEnvVars);
                    slave.getSpec().getContainers().get(0).setEnv(envVars);
                    env = envVars;

                    envVars.add(new V1EnvVar().name("RANK").value(rank + ""));
                    envVars.add(new V1EnvVar().name("NVIDIA_VISIBLE_DEVICES").value(multipleServersConfig.getCanGpuCardNos()));
                    rank++;
                    execPods.add(slave);
                }

                env.add(new V1EnvVar().name("NCCL_SOCKET_IFNAME").value(k8sServerConfEntity.getNcclSocket()));
//                env.add(new V1EnvVar().name("MASTER_ADDR").value(master.getK8sServerConfEntity().getAddress()));
                env.add(new V1EnvVar().name("MASTER_ADDR").value(BASE_MASTER_HOST));
                env.add(new V1EnvVar().name("MASTER_PORT").value(master.getK8sServerConfEntity().getMaterPort() + ""));
                env.add(new V1EnvVar().name("NNODES").value(tuningModelnEntity.getMultipleServersConfigs().size() + ""));

                env.add(new V1EnvVar().name("NCCL_P2P_DISABLE").value("1"));
//                env.add(new V1EnvVar().name("NCCL_DEBUG").value("INFO"));

                V1PodSpec spec = execPods.get(execPods.size() - 1).getSpec();
                spec.nodeName(k8sServerConfEntity.getCode());
                spec.hostAliases(Arrays.asList(new V1HostAlias[]{new V1HostAlias().ip(master.getK8sServerConfEntity().getAddress()+"")
                        .addHostnamesItem(BASE_MASTER_HOST)}));
                spec.getContainers().get(0).setResources(null);
            }
            return execPods;
        }
        return null;
    }


    @Override
    public Object exec(Object... params) throws Exception {
        return null;
    }

    @Override
    public String calcTargetCardAndDeploy(TrainPlanEntity trainPlanEntity, TuningModelnEntity tuningModelnEntity) throws Exception {
        //20241225 增加多卡多服务器的支持 要考虑只有一台服务器的情况
        //所有可用的服务器
        //20250312 优化服务器选择 只拿当前任务需要的服务器进行计算 并且额外增加查找当前服务器是否有主/子服务器 如果有也加入计算
        QueryCriterion queryCriterio = new QueryCriterion();
        queryCriterio.addParamGroup(new QueryParamGroup(QueryOperSymbolEnum.or)
                .addParams(new QueryHibernatePlaceholderParam("id",tuningModelnEntity.getServerId().split(","),null,QueryOperSymbolEnum.in, QueryParam.PARAM_PLACEHOLDER_NAME)));
        List<K8sServerConfEntity> k8sServerConfEntities = SpringHelper.getBean(IK8sServerConfService.class).find(queryCriterio);
        if (k8sServerConfEntities.size() == 0) {
            throw new DataEmptyException("未存在可用的服务器!请联系管理员!");
        }
        log.info("加载服务器成功.可用服务器数量为" + k8sServerConfEntities.size());
        //默认识别为单卡任务 默认的服务器一般是主服务器
        Map<String, Map<Integer, GpuCalcCardModel>> serverCardMap = new HashMap<>();
        Map<String, K8sServerConfEntity> serverConfigMap = new HashMap<>();
        Map<String, Map<Integer, GpuCalcCardModel>> mainServerMap = new HashMap<>();
        //提前转换服务器的显卡信息 交给后续的计算
        //注意只拿目前需要的主服务器.主服务器可能有多台.
        for (K8sServerConfEntity e : k8sServerConfEntities) {
            //检查服务器显卡接口是否可用 也确认是否是正确可用的服务器
            Map<Integer, GpuCalcCardModel> currentCardStatus = null;
            try {
                currentCardStatus = getCurrentCardStatus(e.getGpuMsgUrl());
            } catch (Exception e1) {
                e1.printStackTrace();
                log.error("异常服务器" + e.getAddress());
                throw new Exception("存在服务器异常.无法启动.请联系管理员!");
            }
            if (currentCardStatus == null) {
                log.error("异常服务器1" + e.getAddress());
                throw new Exception("存在服务器异常.无法启动.请联系管理员!");
            }
            serverCardMap.put(e.getAddress(), currentCardStatus);
            serverConfigMap.put(e.getAddress(), e);
            //单机的情况下 配置一个当前的服务器
            if (GlobalConsts.ZERO.equals(trainPlanEntity.getServerGpuMode()) && e.getId().equals(tuningModelnEntity.getServerId())){
                mainServerMap.put(e.getAddress(),currentCardStatus);
            }
            KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(e.getId());
            if (JsonResultUtil.isNull(kubernetesApiClient,kubernetesApiClient.getApiClient(),kubernetesApiClient.getServer())){
                throw new DataValidException("不存在可用的服务器操作信息!请联系管理员!");
            }
            //把所有k8s里面包含有使用显卡资源的任务全部提取出来.基于它当前分配的显存 与显卡物理分配的情况进行对比 把已经分配不满足要求的设置为不可分配
            //扫描目前的任务 查看显卡占用情况
            for (String nameSpace:MaasConst.CARD_TASK_SPACE){
                KubernetesPodHandler kubernetesPodHandler = SpringHelper.getBean(KubernetesPodHandler.class);
                V1PodList existTaskPod = kubernetesPodHandler.getAllPodByNameSpace(kubernetesApiClient,nameSpace);
                for(V1Pod v1Pod:existTaskPod.getItems()){
                    calcPodUsedCard(v1Pod, serverCardMap, trainPlanEntity.getPreGpucache());
                }
            }
        }




        //20250102 lm 只有多卡的情况 才需要显卡计算 如果是单卡的情况 让显卡插件自动分配
        if (trainPlanEntity.getGpuCount() == null || GlobalConsts.ZERO.equals(trainPlanEntity.getGpuCount())) {
            trainPlanEntity.setGpuCount(1);
        }
        Integer needGpuCount = 0;
        Integer gpuCount = trainPlanEntity.getGpuCount();
        log.info("显卡总体信息计算完成.开始计算需要分配的显卡");

        //如果是单机的情况下 服务器列表应该重置为 拿当前选择的服务器来计算
        //单机的情况下 配置一个当前的服务器
        if (GlobalConsts.ZERO.equals(trainPlanEntity.getServerGpuMode())){
            serverCardMap.clear();
            serverCardMap.putAll(mainServerMap);
        }

        //为当前任务寻找合适的服务器部署
        for (String key : serverCardMap.keySet()) {
            Map<Integer, GpuCalcCardModel> gpuCalcCardModelMap = serverCardMap.get(key);
            MultipleServersConfig multipleServersConfig = new MultipleServersConfig(trainPlanEntity, serverConfigMap.get(key), gpuCalcCardModelMap);
            log.info("服务器当前显卡信息: " + multipleServersConfig.printCardMsg());
            /**
             * 分配的显卡有4种情况 主机可以操作子机器的东西
             * 1.是当前主机满足服务器显卡要求
             * 2.当前主机不满足 但是配合子机可以满足
             * 3.当前子机满足
             * 4.全部都不满足
             */
            int multipleSeverDiv = trainPlanEntity.getGpuCount()/2;
            String remark = multipleServersConfig.getK8sServerConfEntity().getRemark();
            Map<Integer, GpuCalcCardModel> canGpuCardNoMap = multipleServersConfig.getCanGpuCardNoMap();
            if (canGpuCardNoMap.size() == 0){
                log.warn("该服务器可用显卡为0.跳过处理");
                continue;
            }
            //20250116 lm 最新的修改 当模型是 集群多卡的情况.需要将显卡的数量/2.要求每一个服务器都有这么多显卡才可以执行集群分布式任务
            if (GlobalConsts.TWO.equals(trainPlanEntity.getServerGpuMode())){
                if (canGpuCardNoMap.size() < multipleSeverDiv){
                    throw new DataEmptyException("可分配显卡不足!服务器"+multipleServersConfig.getK8sServerConfEntity().getCode()
                            +"剩余满足需求显卡数量"+canGpuCardNoMap.size()+",需要"+multipleSeverDiv);
                }
                //当前主机拥有足够数量的显卡 只拿显卡的一半.
                gpuCount = multipleSeverDiv;
            }
            //判断一台服务器够不够分配当前任务需要的显卡 当前服务器满足就返回
            if (canGpuCardNoMap.size() == gpuCount) {
                log.info("=" + remark + "提供显卡:" + multipleServersConfig.getCanGpuCardNos());
                needGpuCount += multipleServersConfig.getCanGpuCardNoMap().size();
                tuningModelnEntity.getMultipleServersConfigs().add(multipleServersConfig);
                gpuCount -= canGpuCardNoMap.size();
                break;
            }
            //只拿一个卡
            //判断一台服务器够不够分配当前任务需要的显卡 当前服务器满足就返回
            if (multipleServersConfig.getCanGpuCardNoMap().size()>0) {
                int startCount = 0;
                int removeCount = gpuCount;
                Iterator<Map.Entry<Integer, GpuCalcCardModel>> iterator = canGpuCardNoMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, GpuCalcCardModel> next = iterator.next();
                    if (startCount >= removeCount) {
                        multipleServersConfig.getEmptyGpuCardNoMap().put(next.getKey(), next.getValue());
                        iterator.remove();
                    } else {
                        multipleServersConfig.getCanGpuCardNoMap().put(next.getKey(), next.getValue());
                        //把还没有计算的可分配显卡 转成未分配显卡 代表不用分配了.
                        startCount++;
                    }
                }
                multipleServersConfig.setEmptyGpuCardNos(multipleServersConfig.reBuildCardNos(multipleServersConfig.getEmptyGpuCardNoMap()));
                multipleServersConfig.setCanGpuCardNos(multipleServersConfig.reBuildCardNos(canGpuCardNoMap));
                log.info(">" + remark + "提供显卡:" + multipleServersConfig.getCanGpuCardNos());
                needGpuCount += multipleServersConfig.getCanGpuCardNoMap().size();
                tuningModelnEntity.getMultipleServersConfigs().add(multipleServersConfig);
                gpuCount -= canGpuCardNoMap.size();
                if (GlobalConsts.TWO.equals(trainPlanEntity.getServerGpuMode())) {
                    //如果多机多卡的情况下 一台服务器的判断是不够的 需要继续往下走 判断其他服务器
                    if (needGpuCount < trainPlanEntity.getGpuCount()){
                        continue;
                    }
                }
                break;
            }
            //单机多卡
            if (canGpuCardNoMap.size() > gpuCount && GlobalConsts.ONE.equals(trainPlanEntity.getServerGpuMode())){
                int startCount = 0;
                int removeCount = gpuCount;
                Iterator<Map.Entry<Integer, GpuCalcCardModel>> iterator = canGpuCardNoMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, GpuCalcCardModel> next = iterator.next();
                    if (startCount >= removeCount) {
                        multipleServersConfig.getEmptyGpuCardNoMap().put(next.getKey(), next.getValue());
                        iterator.remove();
                    } else {
                        multipleServersConfig.getCanGpuCardNoMap().put(next.getKey(), next.getValue());
                        //把还没有计算的可分配显卡 转成未分配显卡 代表不用分配了.
                        startCount++;
                    }
                }
                multipleServersConfig.setEmptyGpuCardNos(multipleServersConfig.reBuildCardNos(multipleServersConfig.getEmptyGpuCardNoMap()));
                multipleServersConfig.setCanGpuCardNos(multipleServersConfig.reBuildCardNos(canGpuCardNoMap));
                log.info(">" + remark + "提供显卡:" + multipleServersConfig.getCanGpuCardNos());
                gpuCount -= canGpuCardNoMap.size();
                break;
            }
            //当前主机不满足 但是配合子机可以满足
            if (canGpuCardNoMap.size() > gpuCount) {
                int startCount = 0;
                int removeCount = gpuCount;
                if (GlobalConsts.ONE.equals(trainPlanEntity.getServerGpuMode())) {
                    removeCount = multipleSeverDiv;
                }
                Iterator<Map.Entry<Integer, GpuCalcCardModel>> iterator = canGpuCardNoMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, GpuCalcCardModel> next = iterator.next();
                    if (startCount >= removeCount) {
                        multipleServersConfig.getEmptyGpuCardNoMap().put(next.getKey(), next.getValue());
                        iterator.remove();
                    } else {
                        multipleServersConfig.getCanGpuCardNoMap().put(next.getKey(), next.getValue());
                        //把还没有计算的可分配显卡 转成未分配显卡 代表不用分配了.
                        startCount++;
                    }
                }
                multipleServersConfig.setEmptyGpuCardNos(multipleServersConfig.reBuildCardNos(multipleServersConfig.getEmptyGpuCardNoMap()));
                multipleServersConfig.setCanGpuCardNos(multipleServersConfig.reBuildCardNos(canGpuCardNoMap));
                log.info(">" + remark + "提供显卡:" + multipleServersConfig.getCanGpuCardNos());
                gpuCount -= canGpuCardNoMap.size();
                //20250116 lm 最新的修改 当模型是 集群多卡的情况.需要将显卡的数量 /2.要求每一个服务器都有这么多显卡
                if (GlobalConsts.ONE.equals(trainPlanEntity.getServerGpuMode())){
                    if (canGpuCardNoMap.size() < multipleSeverDiv){
                        throw new DataEmptyException("可分配显卡不足!服务器"+multipleServersConfig.getK8sServerConfEntity().getCode()
                                +"剩余满足需求显卡数量"+canGpuCardNoMap.size()+",需要"+multipleSeverDiv);
                    }
                    //当前主机拥有足够数量的显卡 只拿显卡的一半. 这里还原 让下面继续计算完当前的分配
                    if (trainPlanEntity.getGpuCount()!=2){
                        gpuCount = trainPlanEntity.getGpuCount();
                    }
                }
                if (gpuCount == 0) {
                    needGpuCount += canGpuCardNoMap.size();
                    //分配结束
                    tuningModelnEntity.getMultipleServersConfigs().add(multipleServersConfig);
                    //多机的情况下 继续走
                    if (multipleSeverDiv == GlobalConsts.ONE){
                        continue;
                    }
                    break;
                }
            }
            //判断当前的计算是否满足当前任务的要求
            if (canGpuCardNoMap.size() == 0) {
                continue;
            }
            needGpuCount += canGpuCardNoMap.size();
            tuningModelnEntity.getMultipleServersConfigs().add(multipleServersConfig);
            log.info("-计算显卡成功!" + remark + "提供显卡:" + multipleServersConfig.getCanGpuCardNos());
            gpuCount -= canGpuCardNoMap.size();
        }
        //检查最终分配是否满足需求
        if (trainPlanEntity.getGpuCount() > needGpuCount || gpuCount != 0) {
            throw new DataEmptyException("当前服务器满足条件显卡不足!需要显卡数量" + trainPlanEntity.getGpuCount() + ",剩余显卡" + needGpuCount);
        }
        if (needGpuCount > trainPlanEntity.getGpuCount()) {
            throw new DataEmptyException("溢出" + needGpuCount);
        }
        //20250313取消占位 变更为系统手动控制

        //如果是单卡 不用占位 交给显卡插件自动分配.确认是哪个服务器即可
        //去对应的服务器 占位 为多卡做准备
//        if (trainPlanEntity.getGpuCount() > 1) {
//            //20250113 占位变更 先把它所有的位置占满 然后清理占位里面无效的数据 这里这样做是因为显卡是按顺序递增的 不能直接指定某个显卡
//            for (MultipleServersConfig multipleServersConfig : tuningModelnEntity.getMultipleServersConfigs()) {
//                //20250313 lm 增加逻辑 查询当前服务器的全部占位任务 用来判断是否已占用 如果占用 则提示报错 如果未占用 则全部都占用一次然后移除不需要的资源
//                List<String> podNames = new ArrayList<>();
//                for (Integer cardNo : multipleServersConfig.getCanGpuCardNoMap().keySet()) {
//                    String podName = startPosition(cardNo, multipleServersConfig.getK8sServerConfEntity().getCode(), tuningModelnEntity, kubernetesApiClient);
//                    podNames.add(podName);
//                }
//                //确认占位任务都在RUNNING了才开始任务
//                for (String podName : podNames) {
//                    try {
//                        int maxWaitSeconds = 10;
//                        int nowCount = 0;
//                        V1Pod onePod = kubernetesPodHandler.getOnePod(kubernetesApiClient, podName, MaasConst.NGINX_POSIT_NSPACE);
//                        while (onePod!=null && !"Running".equals(onePod.getStatus().getPhase())){
//                            if (nowCount>=maxWaitSeconds){
//                                break;
//                            }
//                            log.info(onePod.getMetadata().getName()+"状态:"+onePod.getStatus().getPhase());
//                            onePod = kubernetesPodHandler.getOnePod(kubernetesApiClient, podName, MaasConst.NGINX_POSIT_NSPACE);
//                            TimeUnit.MILLISECONDS.sleep(500);
//                            nowCount++;
//                        }
//                        if (!"Running".equals(onePod.getStatus().getPhase())){
//                            removeTunPlanTargetCard(trainPlanEntity,tuningModelnEntity,kubernetesApiClient);
//                            throw new ExecFailException("服务器多卡能力初始化超时.请联系管理员!");
//                        }
//                    } catch (ApiException ex) {
//                        throw new ExecFailException("服务器初始化多卡失败!");
//                    }
//                }
//                podNames.forEach(e->{
//
//                });
//            }
//        }
        return null;
    }

    @Override
    public List<Integer> removeTunPlanTargetCard(TrainPlanEntity trainPlanEntity, TuningModelnEntity tuningModelnEntity) throws Exception {
        KubernetesPodHandler kubernetesPodHandler = SpringHelper.getBean(KubernetesPodHandler.class);
        //增加多机多卡任务移除
        if (tuningModelnEntity.getServerId().split(",").length > 1){
            JSONArray jsonArray = JSONArray.parseArray(tuningModelnEntity.getExecGpuCard());
            tuningModelnEntity.setServerId(jsonArray.getJSONObject(0).getJSONObject("k8sServerConfEntity").getString("id"));
        }
        KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(tuningModelnEntity.getServerId());
        //1.先判断有没有开启多卡
        log.info("微调多卡任务结束.准备开始释放多卡任务占用");
        String code = tuningModelnEntity.getCode();
        V1Pod v1Pod = kubernetesPodHandler.getOnePod(kubernetesApiClient, code, tuningModelnEntity.getWorkSpace());
        //移除APP相关服务
        V1Pod appPod = kubernetesPodHandler.getPod(kubernetesApiClient, MaasConst.MODEL_APP_NSAPCE, tuningModelnEntity.getCode());
        if (appPod != null) {
            log.info("存在APP.移除");
            kubernetesPodHandler.deleteOnePod(kubernetesApiClient, MaasConst.MODEL_APP_NSAPCE, tuningModelnEntity.getCode());
        }
        V1Pod pod = kubernetesPodHandler.getPod(kubernetesApiClient, MaasConst.TRIAN_PLAN_NSPACE, tuningModelnEntity.getCode());
        if (pod != null) {
            kubernetesPodHandler.deleteOnePod(kubernetesApiClient, MaasConst.TRIAN_PLAN_NSPACE, tuningModelnEntity.getCode());
        }
        //任务不存在或者多机多卡的判断 根据任务信息对象判断 一般是创建任务前就失败的情况下会调用这个
        //还要考虑直接删除的情况
        if (v1Pod == null) {
            for (MultipleServersConfig multipleServersConfig : tuningModelnEntity.getMultipleServersConfigs()) {
                String nodeName = multipleServersConfig.getK8sServerConfEntity().getCode();
                for (Integer cardNo : multipleServersConfig.getCanGpuCardNoMap().keySet()) {
                    String podName = createPositionName(cardNo, nodeName);
                    pod = kubernetesPodHandler.getOnePod(kubernetesApiClient, podName, MaasConst.NGINX_POSIT_NSPACE);
                    if (pod != null) {
                        kubernetesPodHandler.deleteOnePod(kubernetesApiClient, MaasConst.NGINX_POSIT_NSPACE, podName);
                        log.info("释放" + podName);
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(tuningModelnEntity.getExecGpuCard())) {
            //移除服务
            KubernetesServiceHandler kubernetesServiceHandler = SpringHelper.getBean(KubernetesServiceHandler.class);
            V1ServiceList masterSvc = kubernetesServiceHandler.getServiceByCode(kubernetesApiClient, MaasConst.TRIAN_PLAN_NSPACE, tuningModelnEntity.getCode());
            if (masterSvc.getItems().size()>0) {
                kubernetesServiceHandler.deleteService(kubernetesApiClient, MaasConst.TRIAN_PLAN_NSPACE, tuningModelnEntity.getCode());
            }

            List<MultipleServersConfig> multipleServersConfigs = JSONArray.parseArray(tuningModelnEntity.getExecGpuCard(), MultipleServersConfig.class);
            if (multipleServersConfigs.size() > 1) {
                log.info("开始移除多机多卡的配置");
                //移除多卡情况下的任务
                int rank = 1;
                for (int i=1;i<multipleServersConfigs.size();i++) {
                    K8sServerConfEntity k8sServerConfEntity = multipleServersConfigs.get(i).getK8sServerConfEntity();
                    kubernetesApiClient = FileControllerService.getCustomClient(k8sServerConfEntity.getId());
                    //移除占位
//                    for (Integer cardNo : multipleServersConfig.getCanGpuCardNoMap().keySet()) {
//                        String podName = createPositionName(cardNo, k8sServerConfEntity.getCode());
//                        pod = kubernetesPodHandler.getOnePod(kubernetesApiClient, podName, MaasConst.NGINX_POSIT_NSPACE);
//                        if (pod != null) {
//                            kubernetesPodHandler.deleteOnePod(kubernetesApiClient, MaasConst.NGINX_POSIT_NSPACE, podName);
//                            log.info("多机多卡-节点占位任务-释放" + podName);
//                        }
//                    }
                    //这里只移除从机的任务 主机的任务其他地方已经移除了
                    if (multipleServersConfigs.size()>1) {
                        String slaveName = StringHandlerUtil.createSlaveName(tuningModelnEntity.getCode(), rank);
                        pod = kubernetesPodHandler.getOnePod(kubernetesApiClient, slaveName, MaasConst.TRIAN_PLAN_NSPACE);
                        if (pod != null) {
                            kubernetesPodHandler.deleteOnePod(kubernetesApiClient, MaasConst.TRIAN_PLAN_NSPACE, slaveName);
                            log.info("多机多卡-节点任务-释放" + slaveName);
                        }
                        rank++;
                    }
                }
                log.info("移除完成多机多卡的配置");
            }
        }
//        if (JsonResultUtil.isNotNull(v1Pod)) {
//            //还需要考虑多卡分配的情况
//            if (v1Pod.getSpec() != null && v1Pod.getSpec().getContainers() != null && v1Pod.getSpec().getContainers().size() > 0) {
//                List<V1EnvVar> env = v1Pod.getSpec().getContainers().get(0).getEnv();
//                for (V1EnvVar v1EnvVar : env) {
//                    if ("NVIDIA_VISIBLE_DEVICES".equals(v1EnvVar.getName())) {
//                        String nvidiaVisibleDevices = v1EnvVar.getValue();
//                        if (StringUtils.isNotBlank(nvidiaVisibleDevices)) {
//                            String[] useCards = nvidiaVisibleDevices.split(",");
//                            for (String cardNo : useCards) {
//                                String positionName = createPositionName(cardNo, v1Pod.getSpec().getNodeName());
//                                pod = kubernetesPodHandler.getOnePod(kubernetesApiClient, positionName, MaasConst.NGINX_POSIT_NSPACE);
//                                if (pod!=null){
//                                    log.info("释放显卡:" + cardNo);
//                                    kubernetesPodHandler.deleteOnePod(kubernetesApiClient, MaasConst.NGINX_POSIT_NSPACE,positionName);
//                                }
//                            }
//                            log.info("多卡任务结束!对应的所有显卡释放成功!");
//                        }
//                    }
//                }
//            }
//        }

        return null;
    }

    private String startPosition(Integer cardNo, String nodeName, TuningModelnEntity tuningModelnEntity, KubernetesApiClient kubernetesApiClient) throws Exception {
        log.info("K8S-GPU显卡插件占位任务-等待内部初始化分配");
        TimeUnit.SECONDS.sleep(1);

        KubernetesPodHandler kubernetesPodHandler = SpringHelper.getBean(KubernetesPodHandler.class);
        KubernetesNameSpaceHandler kubernetesNameSpaceHandler = SpringHelper.getBean(KubernetesNameSpaceHandler.class);

        String nginxPosit = MaasConst.NGINX_POSIT_NSPACE;
        //确认命名空间
        kubernetesNameSpaceHandler.createNameSpace(kubernetesApiClient, nginxPosit);
        DeployAppVo deployAppVo = new DeployAppVo();
        //只有POD支持 deployment是不支持Never的
        deployAppVo.setRestartPolicy("Never");
        deployAppVo.setAppCode(createPositionName(cardNo, nodeName));
        deployAppVo.setImageName("registry.badou/badoullms/nginx:1.22.1");
        deployAppVo.setNameSpace(nginxPosit);
        deployAppVo.setSecretName("badouregistrykey");
        deployAppVo.setReplicas(1);
        deployAppVo.setKubernetesApiClient(kubernetesApiClient);
        deployAppVo.setNodeName(nodeName);
        Map<String, Quantity> limits = new HashMap<>();
        limits.put("aliyun.com/gpu-mem", new Quantity("22"));
        deployAppVo.setLimits(limits);

        V1Pod podByParams = kubernetesPodHandler.createPodByParams(deployAppVo.getImageName(), kubernetesApiClient, 1, nginxPosit, deployAppVo.getAppCode(), deployAppVo);
        if (podByParams == null) {
            throw new Exception("多卡初始化分配失败!请联系管理员!");
        }
        return deployAppVo.getAppCode();
    }

    private String createPositionName(String cardNo, String nodeName) {
        return "nginx-card-" + nodeName + "-" + cardNo;
    }

    private String createPositionName(Integer cardNo, String nodeName) {
        return "nginx-card-" + nodeName + "-" + cardNo;
    }

    private void calcPodUsedCard(V1Pod v1Pod, Map<String, Map<Integer, GpuCalcCardModel>> serverCardMap, Integer needGpuNum) throws DataEmptyException {
        //20250102 lm 增加考虑到多服务器的情况
        //获取当前显卡的列表情况
        Map<String, String> annotations = v1Pod.getMetadata().getAnnotations();
        if (v1Pod.getStatus() == null) {
            throw new DataEmptyException("服务器状态异常!请联系管理员");
        }
        String hostIP = v1Pod.getStatus().getHostIP();
        if (StringUtils.isEmpty(hostIP)) {
            log.info(Yaml.dump(v1Pod));
//            throw new DataEmptyException("服务器配置非法!请联系管理员");
            return;
        }
        Map<Integer, GpuCalcCardModel> gpuCalcCardModelMap = serverCardMap.get(hostIP);

        if (gpuCalcCardModelMap == null){
            log.warn("注意"+hostIP+"服务器显卡信息为空.计算该服务器的显卡分配信息失败!");
            return;
        }
        //还需要考虑多卡分配的情况
        if (v1Pod.getSpec() != null && v1Pod.getSpec().getContainers() != null
                && v1Pod.getSpec().getContainers().size() > 0) {
            List<V1EnvVar> env = v1Pod.getSpec().getContainers().get(0).getEnv();
            if (env!=null){
                for (V1EnvVar v1EnvVar : env) {
                    if ("NVIDIA_VISIBLE_DEVICES".equals(v1EnvVar.getName())) {
                        if ("all".equals(v1EnvVar.getValue())) {
                            //all代表操作全部显卡.只有测试的情况会使用.一般实际系统不会这样配置.跳过
                            break;
                        }
                        String nvidiaVisibleDevices = v1EnvVar.getValue();
                        if (StringUtils.isNotBlank(nvidiaVisibleDevices)) {
                            String[] useCards = nvidiaVisibleDevices.split(",");
                            log.info("服务器" + hostIP + "的POD" + v1Pod.getMetadata().getName() + "拿走显卡" + nvidiaVisibleDevices);
                            for (String card : useCards) {
                                //目前只支持到显卡级别 直接设置满的
                                GpuCalcCardModel gpuCalcCardModel = gpuCalcCardModelMap.get(Integer.parseInt(card));
                                gpuCalcCardModel.setCurrentVMemory(gpuCalcCardModel.getCurrentVMemory());
                            }
                        }
                    }
                }
            }
        }
        if (annotations != null) {
            //如果存在这个注解 代表已自动分配的
            String aliyunComGpuMemIdx = annotations.get("ALIYUN_COM_GPU_MEM_IDX");
            String aliyunComGpuMemDev = annotations.get("ALIYUN_COM_GPU_MEM_DEV");
            String aliyunComGpuMemPod = annotations.get("ALIYUN_COM_GPU_MEM_POD");
            if (aliyunComGpuMemDev == null) {
                return;
            }
            GpuCalcCardModel gpuCalcCardModel = gpuCalcCardModelMap.get(Integer.parseInt(aliyunComGpuMemIdx));
            log.info("服务器" + hostIP + "的POD" + v1Pod.getMetadata().getName() + "拿走显卡" + aliyunComGpuMemIdx);
            //直接分配满了
            if (aliyunComGpuMemPod.equals(aliyunComGpuMemDev)) {
                gpuCalcCardModel.setCurrentVMemory(gpuCalcCardModel.getMaxVMemory());
                return;
            }
            //不满足当前需求
            int totalGpuCache = Integer.parseInt(aliyunComGpuMemDev);
            int usedGpuCache = Integer.parseInt(aliyunComGpuMemPod);
            if ((totalGpuCache - usedGpuCache) < needGpuNum) {
                //目前只支持到显卡级别 直接设置满的
                gpuCalcCardModel.setCurrentVMemory(usedGpuCache);
                log.info("服务器" + hostIP + "的POD" + v1Pod.getMetadata().getName() + "拿走显存" + aliyunComGpuMemPod);
                return;
            }
        }
    }

    public static void main(String[] args) {
        List<ServerGpuEntity> currentNewCardStatus = new BaseGpuCalcHandler().getCurrentNewCardStatus("http://192.168.8.247:8998/v1/gpus/monitorInfo");
        System.out.println(currentNewCardStatus);
    }

    /**
     * 根据显卡集群服务器提供的显卡采集信息接口 获取当前目标服务器的显卡状态 如显卡数量、序号、当前/最大显存等信息
     * @param gpuMsgUrl 显卡服务器HTTP地址
     * @return 采集显卡结果
     */
    public List<ServerGpuEntity> getCurrentNewCardStatus(String gpuMsgUrl) {
        String post = HttpUtil.post(gpuMsgUrl, new HashMap<>());
        JSONObject jsonObject = JSONObject.parseObject(post);
        JSONArray data = jsonObject.getJSONArray("data");
        List<ServerGpuEntity> results = new ArrayList<>();
        for (Object rowObj : data) {
            JSONObject row = (JSONObject) rowObj;
            JSONArray cardInfos = row.getJSONArray("card_info");
            ServerGpuEntity serverGpu = getServerGpu(cardInfos);
            serverGpu.setNameNum(serverGpu.getName()+"-"+serverGpu.getOrderNum());
            serverGpu.setGpuCache("99df847c-429d-4faf-8dfb-559f0d23266a"+serverGpu.getOrderNum()+"="+serverGpu.getUsedGraphicsMemory()+"-"+serverGpu.getMaxGraphicsMemory());
            results.add(serverGpu);
        }
        return results;
    }

    /**
     * 根据显卡集群服务器提供的显卡采集信息接口 获取当前目标服务器的显卡状态 如显卡数量、序号、当前/最大显存等信息
     * @param gpuMsgUrl 显卡服务器HTTP地址
     * @return 采集显卡结果
     */
    public Map<Integer,ServerGpuEntity>  getCurrentNewCardStatusMap(String gpuMsgUrl) {
        String post = HttpUtil.post(gpuMsgUrl, new HashMap<>());
        JSONObject jsonObject = JSONObject.parseObject(post);
        JSONArray data = jsonObject.getJSONArray("data");
        Map<Integer,ServerGpuEntity> serverGpuEntityMap = new HashMap<>();
        for (Object rowObj : data) {
            JSONObject row = (JSONObject) rowObj;
            JSONArray cardInfos = row.getJSONArray("card_info");
            ServerGpuEntity gpuEntity = getServerGpu(cardInfos);
            serverGpuEntityMap.put(gpuEntity.getOrderNum(),gpuEntity);
        }
        return serverGpuEntityMap;
    }

    private ServerGpuEntity getServerGpu(JSONArray cardInfos){
        if (cardInfos.size() == 8) {
            ServerGpuEntity gpuEntity = new ServerGpuEntity();
            gpuEntity.setOrderNum(cardInfos.getInteger(0));
            gpuEntity.setType("Tesla");
            gpuEntity.setName("P100-PCIE-16GB");
            gpuEntity.setFanSpeedPer(Integer.parseInt(cardInfos.getString(3).replace("%", "")));
            gpuEntity.setTemperature(Double.parseDouble(cardInfos.getString(4).replace("C", "")));
            gpuEntity.setPerformanceStatus(0);
            String[] powers = cardInfos.getString(5).replace("W", "").split("/");
            gpuEntity.setCurrentPowerDissipation(Integer.parseInt(powers[0]));
            gpuEntity.setMaxPowerDissipation(Integer.parseInt(powers[1]));
            String[] caches = cardInfos.getString(6).replace("MiB", "").split("/");
            gpuEntity.setUsedGraphicsMemory(Integer.parseInt(caches[0]));
            gpuEntity.setMaxGraphicsMemory(Integer.parseInt(caches[1]));
            gpuEntity.setUsageRate(Integer.parseInt(cardInfos.getString(7)
                    .replace("%", "")));
            return gpuEntity;
        }
        if (cardInfos.size() == 12) {
            ServerGpuEntity gpuEntity = new ServerGpuEntity();
            gpuEntity.setOrderNum(cardInfos.getInteger(0));
            gpuEntity.setType(cardInfos.getString(1) + cardInfos.getString(2));
            gpuEntity.setName(cardInfos.getString(3) + cardInfos.getString(4));
            gpuEntity.setFanSpeedPer(Integer.parseInt(cardInfos.getString(6).replace("%", "")));
            gpuEntity.setTemperature(Double.parseDouble(cardInfos.getString(7).replace("C", "")));
            gpuEntity.setPerformanceStatus(Integer.parseInt(cardInfos.getString(8).replace("P", "")));
            String[] powers = cardInfos.getString(9).replace("W", "").split("/");
            gpuEntity.setCurrentPowerDissipation(Integer.parseInt(powers[0]));
            gpuEntity.setMaxPowerDissipation(Integer.parseInt(powers[1]));
            String[] caches = cardInfos.getString(10).replace("MiB", "").split("/");
            gpuEntity.setUsedGraphicsMemory(Integer.parseInt(caches[0]));
            gpuEntity.setMaxGraphicsMemory(Integer.parseInt(caches[1]));
            gpuEntity.setStatus(0);
            gpuEntity.setUsageRate(Integer.parseInt(cardInfos.getString(11)
                    .replace("%", "")));
            return gpuEntity;
        }
        if (cardInfos.size() == 11 && "4090".equals(cardInfos.getString(3))){
            ServerGpuEntity gpuEntity = new ServerGpuEntity();
            gpuEntity.setOrderNum(cardInfos.getInteger(0));
            gpuEntity.setType(cardInfos.getString(1) + cardInfos.getString(2));
            gpuEntity.setName(cardInfos.getString(3));
            gpuEntity.setFanSpeedPer(Integer.parseInt(cardInfos.getString(5).replace("%", "")));
            gpuEntity.setTemperature(Double.parseDouble(cardInfos.getString(6).replace("C", "")));
            gpuEntity.setPerformanceStatus(Integer.parseInt(cardInfos.getString(7).replace("P", "")));
            String[] powers = cardInfos.getString(8).replace("W", "").split("/");
            gpuEntity.setCurrentPowerDissipation(Integer.parseInt(powers[0]));
            gpuEntity.setMaxPowerDissipation(Integer.parseInt(powers[1]));
            String[] caches = cardInfos.getString(9).replace("MiB", "").split("/");
            gpuEntity.setUsedGraphicsMemory(Integer.parseInt(caches[0]));
            gpuEntity.setMaxGraphicsMemory(Integer.parseInt(caches[1]));
            gpuEntity.setStatus(0);
            gpuEntity.setUsageRate(Integer.parseInt(cardInfos.getString(10)
                    .replace("%", "")));
            return gpuEntity;
        }else if (cardInfos.size() == 11) {
            ServerGpuEntity gpuEntity = new ServerGpuEntity();
            gpuEntity.setOrderNum(cardInfos.getInteger(0));
            gpuEntity.setType(cardInfos.getString(1) + cardInfos.getString(2));
            gpuEntity.setName(cardInfos.getString(3)+cardInfos.getString(4));
            gpuEntity.setFanSpeedPer(Integer.parseInt(cardInfos.getString(6).replace("%", "")));
            gpuEntity.setTemperature(Double.parseDouble(cardInfos.getString(7).replace("C", "")));
//                gpuEntity.setPerformanceStatus(Integer.parseInt(cardInfos.getString(8).replace("P", "")));
            String[] powers = cardInfos.getString(8).replace("W", "").split("/");
            gpuEntity.setCurrentPowerDissipation(Integer.parseInt(powers[0]));
            gpuEntity.setMaxPowerDissipation(Integer.parseInt(powers[1]));
            String[] caches = cardInfos.getString(9).replace("MiB", "").split("/");
            gpuEntity.setUsedGraphicsMemory(Integer.parseInt(caches[0]));
            gpuEntity.setMaxGraphicsMemory(Integer.parseInt(caches[1]));
            gpuEntity.setStatus(0);
            gpuEntity.setUsageRate(Integer.parseInt(cardInfos.getString(10)
                    .replace("%", "")));
            return gpuEntity;
        }
        if (cardInfos.size() == 10) {
            ServerGpuEntity gpuEntity = new ServerGpuEntity();
            gpuEntity.setOrderNum(cardInfos.getInteger(0));
            gpuEntity.setType(cardInfos.getString(2));
            gpuEntity.setName(cardInfos.getString(1));

            gpuEntity.setFanSpeedPer(Integer.parseInt(cardInfos.getString(4).equals("N/A")?"0":cardInfos.getString(4).replace("%", "")));
            gpuEntity.setTemperature(Double.parseDouble(cardInfos.getString(5).replace("C", "")));
            gpuEntity.setPerformanceStatus(Integer.parseInt(cardInfos.getString(6).replace("P", "")));
            String[] powers = cardInfos.getString(7).replace("W", "").split("/");
            gpuEntity.setCurrentPowerDissipation(Integer.parseInt(powers[0]));
            gpuEntity.setMaxPowerDissipation(Integer.parseInt(powers[1]));
            String[] caches = cardInfos.getString(8).replace("MiB", "").split("/");
            gpuEntity.setUsedGraphicsMemory(Integer.parseInt(caches[0]));
            gpuEntity.setMaxGraphicsMemory(Integer.parseInt(caches[1]));
            gpuEntity.setStatus(0);
            gpuEntity.setUsageRate(Integer.parseInt(cardInfos.getString(9)
                    .replace("%", "")));
            gpuEntity.setId(UUID.randomUUID().toString().replace("-","a"));
            return gpuEntity;
        }
        return null;
    }

    /**
     * 根据显卡集群服务器提供的显卡采集信息接口 获取当前目标服务器的显卡状态 如显卡数量、序号、当前/最大显存等信息
     * @param gpuMsgUrl 显卡服务器HTTP地址
     * @return 采集显卡结
     *     }果
     */
    public Map<Integer, GpuCalcCardModel> getCurrentCardStatus(String gpuMsgUrl) {
        // 创建带超时配置的POST请求
        HttpRequest postRequest = HttpUtil.createPost(gpuMsgUrl)
                .timeout(10000); // 设置总超时时间1秒（包含连接+读取）
        JSONObject jsonObject = null;
        // 执行请求并获取响应
        try {
            // 执行请求并获取响应
            HttpResponse response = postRequest.body("").execute();

            // 检查响应状态码（可选）
            if (response.getStatus() != 200) {
                throw new RuntimeException("请求失败，状态码：" + response.getStatus());
            }

            // 处理响应内容
            String result = response.body();
            log.info("请求"+gpuMsgUrl+"显卡成功，结果：" + result);
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            // 超时或其他异常处理
            if (e.getMessage().contains("timeout")) {
                throw new RuntimeException("请求超时（1秒）", e);
            } else {
                throw new RuntimeException("请求异常：" + e.getMessage(), e);
            }
        }

        //测试A100用的
//        JSONObject jsonObject=  JSONObject.parseObject("{\"hasOK\":true,\"timestamp\":\"1744713150.5621774\",\"data\":[{\"card_id\":0,\"card_info\":[\"0\",\"A100-SXM4-40GB\",\"00000000:81:00.0\",\"0\",\"40C\",\"36W/400W\",\"35386MiB/40960MiB\",\"0%\"]}]}");

        JSONArray data = jsonObject.getJSONArray("data");
        Map<Integer, GpuCalcCardModel> gpuCalcCardModels = new HashMap<>();
        for (Object rowObj : data) {
            JSONObject row = (JSONObject) rowObj;
            String cardId = row.getString("card_id");
            JSONArray cardInfos = row.getJSONArray("card_info");
            if (cardInfos.size() == 12){
                ServerGpuEntity gpuEntity = new ServerGpuEntity();
                gpuEntity.setOrderNum(cardInfos.getInteger(0));
                gpuEntity.setType(cardInfos.getString(1)+cardInfos.getString(2));
                gpuEntity.setName(cardInfos.getString(3)+cardInfos.getString(4));
                gpuEntity.setFanSpeedPer(Integer.parseInt(cardInfos.getString(6).replace("%","")));
                gpuEntity.setTemperature(Double.parseDouble(cardInfos.getString(7).replace("C","")));
                gpuEntity.setPerformanceStatus(Integer.parseInt(cardInfos.getString(8).replace("P","")));
                String[] powers = cardInfos.getString(9).replace("W","").split("/");
                gpuEntity.setCurrentPowerDissipation(Integer.parseInt(powers[0]));
                gpuEntity.setMaxPowerDissipation(Integer.parseInt(powers[1]));

                String[] caches = cardInfos.getString(10).replace("MiB","").split("/");
                gpuEntity.setUsedGraphicsMemory(Integer.parseInt(caches[0]));
                gpuEntity.setMaxPowerDissipation(Integer.parseInt(caches[1]));

                gpuEntity.setUsageRate(Integer.parseInt(cardInfos.getString(11)
                        .replace("%","")));
                System.out.println(gpuEntity);
            }
            if (cardInfos instanceof JSONArray) {
                for (Object cardInfo : cardInfos) {
                    String cardInfoStr = cardInfo.toString();
                    if (cardInfoStr.contains("MiB")) {
                        String[] gpuMs = cardInfoStr.replace("MiB", "").split("/");
                        GpuCalcCardModel calcModel = new GpuCalcCardModel(Integer.parseInt(cardId), Integer.parseInt(gpuMs[0]), Integer.parseInt(gpuMs[1]));
                        log.info(calcModel.toString());
                        gpuCalcCardModels.put(calcModel.getOrderNo(), calcModel);
                    }
                }
            }
        }
        return gpuCalcCardModels;
    }

}
