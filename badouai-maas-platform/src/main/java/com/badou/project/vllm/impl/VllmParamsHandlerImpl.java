package com.badou.project.vllm.impl;

import cn.hutool.core.util.ReflectUtil;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.vllm.VllmParamsHandler;

import java.util.List;

public class VllmParamsHandlerImpl implements VllmParamsHandler {


    @Override
    public String checkEnvLegality(ModelWarehouseEntity modelWarehouseEntity, KubernetesApiClient kubernetesApiClient) {
        return null;
    }

    @Override
    public String buildEnvParams(List<Object> deployParams) {
        for (Object deployParam : deployParams) {
//            ReflectUtil.invoke(deployParam,"get")
        }
        //校验合法的参数会拼接起来 变成 abc=123 ac1=22a这种格式
        return null;
    }

}
