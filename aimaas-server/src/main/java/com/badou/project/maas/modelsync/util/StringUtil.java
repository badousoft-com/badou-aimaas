package com.badou.project.maas.modelsync.util;

import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;

public class StringUtil {

    public static String buildTaskCode(KubernetesApiClient kubernetesApiClient, ModelWarehouseEntity modelWarehouseEntity)
    {
        return modelWarehouseEntity.getModelName()+"-"+kubernetesApiClient.getServer().getCode();
    }

}
