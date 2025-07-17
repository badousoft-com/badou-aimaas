package com.badou.project.vllm;

import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;

import java.util.List;

public interface VllmParamsHandler {
    /**
     * 检查当前执行环境是否合法
     * @param modelWarehouseEntity
     * @param kubernetesApiClient
     * @return
     */
    String checkEnvLegality(ModelWarehouseEntity modelWarehouseEntity, KubernetesApiClient kubernetesApiClient);

    /**
     * 部署环境参数
     * @param deployParams 部署参数
     * @return 参数转换结果
     */
    String buildEnvParams(List<Object> deployParams);
}
