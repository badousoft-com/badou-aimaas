package com.badou.project.kubernetes.handler;

import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.registryaddress.model.RegistryAddressEntity;
import io.kubernetes.client.custom.PodMetricsList;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * k8s命名空间操作类
 */
public interface KubernetesNameSpaceHandler {


    /**
     * 获取命名空间全部应用的状态
     * @param kubernetesApiClient 客户端
     * @param nameSpaceName 命名空间名字
     * @return 获取到的数据
     * @throws ApiException
     */
    PodMetricsList getNameSpaceResoureAllAppStatus(KubernetesApiClient kubernetesApiClient, String nameSpaceName) throws ApiException;

    /**
     * 获取命名空间的资源分配情况 包括CPU和内存的已分配+总共
     * @param kubernetesApiClient 操作客户端
     * @param nameSpaceName 命名空间名字
     * @return 获取到并生成的结果
     * @throws ApiException
     */
    String getNameSpaceResoureStr(KubernetesApiClient kubernetesApiClient, String nameSpaceName) throws ApiException;

    /**
     * 创建Docker镜像仓库认证密钥
     * @param nameSpace 命名空间
     * @param secretName 密钥名字
     * @param registryServerAddress 镜像仓库地址
     * @param username 用户名
     * @param password 密码
     * @return 创建的结果信息
     * @throws UnsupportedEncodingException
     * @throws ApiException
     */
    V1Secret createDockerSecret(KubernetesApiClient kubernetesApiClient, String nameSpace, String secretName, String registryServerAddress, String username, String password) throws UnsupportedEncodingException, ApiException;

    /**
     * 获取一个命名空间
     * @param namespaceName 命名空间
     * @return 读取到的命名空间数据
     */
    V1Namespace readNamespace(KubernetesApiClient kubernetesApiClient,String namespaceName) throws ApiException;

    /**
     * 移除命名空间
     * @param kubernetesApiClient
     * @param nameSpace
     * @return
     * @throws ApiException
     */
    V1Status deleteNameSpace(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException;

    /**
     * @return
     * 创建命名空间 带镜像仓库密钥
     * @param kubernetesApiClient k8s客户端
     * @param nameSpaceName 命名空间
     * @param secretName 密钥名称
     * @param registryAddressEntity 镜像仓库名称
     * @return
     * @throws ApiException
     */
    V1Namespace createNameSpace(KubernetesApiClient kubernetesApiClient, String nameSpaceName, String secretName,RegistryAddressEntity registryAddressEntity) throws ApiException;

    /**
     * 设置命名空间资源限制
     * @param nameSpace  命名空间
     * @param cpuLimit   cpu限制
     * @param memoryLimit  内存限制
     * @return
     */
    V1ResourceQuota setNameSpaceLimit(KubernetesApiClient kubernetesApiClient,String nameSpace, String cpuLimit, String memoryLimit) throws ApiException;

    V1ResourceQuota getNameSpaceLimit(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException;

    V1LimitRange getPodLimit(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException ;

    V1LimitRange setNameSpacePodLimit(KubernetesApiClient kubernetesApiClient,String nameSpace, String podCpuLimit, String podMemoryLimit, String podCpuRequest,
                                      String podMemoryRequest) throws ApiException;

    void deleteNameSpaceLimit(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException;

    V1LimitRange updateNameSpacePodLimit(KubernetesApiClient kubernetesApiClient,String nameSpace, String podCpuLimit, String podMemoryLimit, String podCpuRequest,
                                         String podMemoryRequest,String resourceCpuLimit,String resourceMemoryLimit) throws ApiException;

}
