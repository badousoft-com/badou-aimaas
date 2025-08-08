package com.badou.project.kubernetes.handler;

import com.alibaba.fastjson.JSONObject;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.vo.ProcessStatusVo;
import io.kubernetes.client.openapi.ApiException;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName KubernetesIngressHandler
 * @Description k8s容器内命令操作类
 * @date 2023/6/10 14:43
 * @Version 1.0
 */

public interface KubernetesExecHandler {
    /**
     * 获取POD的详细进程信息
     * @param podName 容器名字
     * @param namespace 命名空间
     * @param kubernetesApiClient k8s客户端
     * @return 执行结果字符串
     * @return
     */
    List<ProcessStatusVo> getPodProcessStatus(String podName, String namespace, KubernetesApiClient kubernetesApiClient,String filterStr) throws InterruptedException, ApiException, IOException;

    /**
     * 执行容器内的执行
     * @param podName 容器名字
     * @param namespace 命名空间
     * @param kubernetesApiClient k8s客户端
     * @param commands 执行命令
     * @return 执行结果字符串
     */
    JSONObject execCommandOnce(String podName, String namespace, KubernetesApiClient kubernetesApiClient,String[] commands) throws IOException, ApiException, InterruptedException;

}
