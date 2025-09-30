package com.badou.project.gpucalc;

import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import netscape.javascript.JSObject;

import java.util.List;
import java.util.Map;

/**
 * 计算GPU分配信息的处理器
 */
public interface GpuCalcHandler {

    /**
     * 检查多机多卡情况下的状态
     * @param tuningModelnEntity 任务实体
     * @param kubernetesApiClient k8s客户端
     * @return
     */
    void checkServerConfigStatus(KubernetesApiClient kubernetesApiClient,TuningModelnEntity tuningModelnEntity,String masterLog) throws ApiException;

    /**
     * 构建多机多卡的情况下需要的额外配置
     * @param tuningModelnEntity 任务实体
     * @param kubernetesApiClient k8s客户端
     * @return
     */
    void buildServerConfigExtend(KubernetesApiClient kubernetesApiClient,TuningModelnEntity tuningModelnEntity) throws ApiException;

    /**
     * 构建多机多卡的情况下需要的配置
     * @param tuningModelnEntity 任务实体
     * @param v1Pod 任务执行对象
     * @return
     */
    List<V1Pod> buildServerConfig(TuningModelnEntity tuningModelnEntity, V1Pod v1Pod);

    /**
     * 执行多卡的分配功能
     *
     * @param params 执行参数
     * @return 执行结果
     * @throws Exception
     */
    Object exec(Object... params) throws Exception;

    /**
     * * 计算目标的显卡 选择合适的规则
     * * 判断规则
     * * 1.查询是否还有满足条件的显卡
     * * 2.查询该显卡
     * @param tuningModelnEntity 任务实体
     * @return
     * @throws Exception
     */
    String calcTargetCardAndDeploy(TrainPlanEntity trainPlanEntity,TuningModelnEntity tuningModelnEntity) throws Exception;

    /**
     * 微调任务-多卡释放
     * @param tuningModelnEntity 任务实体
     * @param kubernetesApiClient k8s客户端
     * @return 释放的卡数组
     * @throws Exception
     */
    List<Integer> removeTunPlanTargetCard(TrainPlanEntity trainPlanEntity,TuningModelnEntity tuningModelnEntity) throws Exception;

    /**
     * 根据GPU地址采集GPU信息
     * @param gpuMsgUrl
     * @return
     */
    Map<Integer, GpuCalcCardModel> getCurrentCardStatus(String gpuMsgUrl);

    /**
     * 将自定义显卡 转成实际的显卡执行对象
     * @param serverId 服务器主键
     * @param startNeedGpum 启动需要显存
     * @param customGpuCardName 自定义显卡名称
     * @param customGpuCard 自定义显卡
     * @return
     */
    List<MultipleServersConfig> coverToServerCard(String serverId,Integer startNeedGpum,String customGpuCardName,String customGpuCard);
}
