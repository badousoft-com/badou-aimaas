package com.badou.project.kubernetes.handler;

import com.badou.project.kubernetes.client.KubernetesApiClient;
import io.kubernetes.client.openapi.ApiException;
/**
 * @ClassName KubernetesIngressHandler
 * @Description k8s错误操作类
 * @date 2023/9/13 16:23
 * @Version 1.0
 */

public interface KubernetesErrorHandler {
    /**
     * 出现错误 获取具体的错误资料返回错误信息
     * @param kubernetesApiClient k8s客户端
     * @param nameSpace 命名空间
     * @param appCode 应用编码(deployment)
     * @return 错误信息
     * @throws ApiException
     */
    String getResetErrorMessage(KubernetesApiClient kubernetesApiClient,String nameSpace,String appCode) throws ApiException;

}
