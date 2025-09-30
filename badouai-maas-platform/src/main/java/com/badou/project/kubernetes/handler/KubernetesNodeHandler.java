package com.badou.project.kubernetes.handler;

import com.badou.project.kubernetes.client.KubernetesApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Node;

import java.util.List;

public interface KubernetesNodeHandler {
    /**
     * 获取主节点
     * @param kubernetesApiClient
     */
    V1Node getMasterNode(KubernetesApiClient kubernetesApiClient) throws ApiException;

    /**
     * 获取全部节点
     * @param kubernetesApiClient k8s客户端
     * @return
     * @throws ApiException
     */
    List<V1Node> getAllNode(KubernetesApiClient kubernetesApiClient) throws ApiException;

}
