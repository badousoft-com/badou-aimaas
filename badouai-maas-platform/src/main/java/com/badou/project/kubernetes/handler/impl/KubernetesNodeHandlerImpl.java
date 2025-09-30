package com.badou.project.kubernetes.handler.impl;

import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesNodeHandler;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KubernetesNodeHandlerImpl implements KubernetesNodeHandler {

    @Override
    public V1Node getMasterNode(KubernetesApiClient kubernetesApiClient) throws ApiException {
        // 使用标签选择器获取 Master 节点（推荐）
        String labelSelector = "node-role.kubernetes.io/master,node-role.kubernetes.io/control-plane";
        V1NodeList v1NodeList = kubernetesApiClient.getCoreV1Api().listNode(null, null, null, null, labelSelector,
                null, null, null, null, null);
        log.info("获取到主节点数量:"+v1NodeList.getItems().size());
        if (v1NodeList!=null && v1NodeList.getItems().size() > 0 ){
            return v1NodeList.getItems().get(0);
        }
        return null;
    }

    @Override
    public List<V1Node> getAllNode(KubernetesApiClient kubernetesApiClient) throws ApiException {

        return null;
    }

}
