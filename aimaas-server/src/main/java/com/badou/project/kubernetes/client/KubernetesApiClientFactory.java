package com.badou.project.kubernetes.client;

import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.service.IEnvServerBindService;
import com.badou.project.server.service.K8sServerConfServiceImpl;
import com.badou.tools.common.util.SpringHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName KubernetesApiClientFactory
 * @Description k8s客户端工厂
 * @date 2023/2/1 14:31
 * @Version 1.0
 */

public class KubernetesApiClientFactory {

    //使用内存存储客户端缓存
    public static Map<String, KubernetesApiClient> clientCache = new HashMap<>();
    //使用内存存储k8s配置信息
    public static Map<String, K8sServerConfEntity> k8sConfigCache = new HashMap<>();

    public static KubernetesApiClient build(K8sServerConfEntity k8sServerConfEntity) throws Exception {
        String key = k8sServerConfEntity.getAddress()+":"+k8sServerConfEntity.getPort()+k8sServerConfEntity.getAuthType();
        KubernetesApiClient cacheClient = clientCache.get(key);
        if(cacheClient == null){
            KubernetesApiClient kubernetesApiClient = new KubernetesApiClient(k8sServerConfEntity);
            if(kubernetesApiClient.getApiClient() == null){
                throw new Exception("获取不到api客户端");
            }
            clientCache.put(key,kubernetesApiClient);
            return kubernetesApiClient;
        }
        if(cacheClient.getApiClient() == null){
            throw new Exception("获取不到api客户端");
        }
        k8sConfigCache.put(k8sServerConfEntity.getId(),k8sServerConfEntity);
        return cacheClient;
    }

    /**
     * 提供环境ID获取k8s客户端
     * @param envId 环境ID
     * @return k8s客户端
     */
    public static KubernetesApiClient buildK8sClient(String envId) throws Exception {
        K8sServerConfEntity k8sServerConfEntity = k8sConfigCache.get(envId);
        if(JsonResultUtil.isNull(k8sServerConfEntity)){
            IEnvServerBindService envServerBindService = SpringHelper.getBean(IEnvServerBindService.class);
            K8sServerConfEntity newK8sConfig = envServerBindService.getOneEnvServer(null, envId);
            k8sServerConfEntity = newK8sConfig;
        }
        return build(k8sServerConfEntity);
    }

}
