package com.badou.project.kubernetes.handler;

import com.badou.project.kubernetes.client.KubernetesApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1Status;

/**
 * @ClassName KubernetesServiceHandler
 * @Description k8s服务操作类 nodePort形式逐渐废弃 采用ingress+service的方式进行对外开放
 * @date 2022/9/20 17:43
 * @Version 1.0
 */
public interface KubernetesServiceHandler {

    /**
     * 以最低的默认配置启动服务
     * @param nameSpace 命名空间
     * @param appCode 应用编码
     * @param port 容器端口
     * @return 生成服务配置文件
     */
    String createServiceByDefault(KubernetesApiClient kubernetesApiClient, String nameSpace, String appCode, Integer port) throws ApiException;

    /**
     * 基于nodePort创建服务
     * @param serviceCode 服务编码
     * @param appCode 应用编码
     * @param protocol 通信协议
     * @param port 服务端口
     * @param targetPort 容器端口
     * @param nodePort 外部端口
     * @param testMode 是否测试模式
     * @return
     * @throws ApiException
     */
    String createServiceAndDeploy(KubernetesApiClient kubernetesApiClient,String nameSpace,String serviceCode,String appCode, String protocol, Integer port, Integer targetPort, Integer nodePort, boolean testMode) throws ApiException;

    /**
     * 获取全部的Service
     */
    V1ServiceList getAllServices(KubernetesApiClient kubernetesApiClient) throws ApiException;

    /**
     * 根据命名空间获取Service
     * @param nameSpace 命名空间名字
     */
    V1ServiceList getAllServiceByNameSpace(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException;

    /**
     * 根据命名空间删除服务
     * @param nameSpace 命名空间
     * @param serviceCode 服务编码
     * @return 生成的结果yaml
     * @throws ApiException
     */
    V1Status deleteService(KubernetesApiClient kubernetesApiClient,String nameSpace, String serviceCode) throws ApiException;

    V1ServiceList getServiceByCode(KubernetesApiClient kubernetesApiClient,String nameSpace,String appCodes) throws ApiException;

}
