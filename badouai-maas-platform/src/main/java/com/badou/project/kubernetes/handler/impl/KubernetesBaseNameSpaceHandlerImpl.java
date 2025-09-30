package com.badou.project.kubernetes.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.badou.project.exception.DataErrorException;
import com.badou.project.kubernetes.KubernetesConstants;
import com.badou.project.kubernetes.config.KubernetesConfig;
import com.badou.project.kubernetes.config.KubernetesYamlConfig;
import com.badou.project.kubernetes.handler.KubernetesNameSpaceHandler;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.registryaddress.model.RegistryAddressEntity;
import com.badou.project.maas.registryaddress.service.IRegistryAddressService;
import com.badou.tools.common.util.StringUtils;
import com.google.gson.internal.LinkedTreeMap;
import io.kubernetes.client.Metrics;
import io.kubernetes.client.custom.PodMetricsList;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @ClassName DefaultNameSpaceHandlerImpl
 * @Description TODO
 * @date 2022/9/20 16:54
 * @Version 1.0
 */

@Component
@Slf4j
public class KubernetesBaseNameSpaceHandlerImpl implements KubernetesNameSpaceHandler {

    private static Logger logger = LoggerFactory.getLogger(KubernetesBaseNameSpaceHandlerImpl.class);
    @Autowired
    private IRegistryAddressService registryAddressService;

    @Override
    public PodMetricsList getNameSpaceResoureAllAppStatus(KubernetesApiClient kubernetesApiClient, String nameSpaceName) throws ApiException {
        Metrics metrics = new Metrics(kubernetesApiClient.getApiClient());
        PodMetricsList podMetrics = metrics.getPodMetrics(nameSpaceName);
        return podMetrics;
    }

    @Override
    public String getNameSpaceResoureStr(KubernetesApiClient kubernetesApiClient, String nameSpaceName) throws ApiException {
        //1.获取资源数据
        CoreV1Api api = kubernetesApiClient.getCoreV1Api();
        V1ResourceQuota v1ResourceQuota = api.readNamespacedResourceQuota(nameSpaceName, nameSpaceName, null, null, null);
        //2.将资源数据处理组装
        Map<String, Quantity> hard = v1ResourceQuota.getStatus().getHard();
        Map<String, Quantity> used = v1ResourceQuota.getStatus().getUsed();
        String cpuUsed = "N/A";
        String cpuTotal = "N/A";
        String memoryTotal = "N/A";
        String memoryUsed = "N/A";
        if(Objects.isNull(used) || Objects.isNull(hard)){
            return "CPU:分配N/A,总N/A.内存:分配N/A,总N/A";
        }
//        cpuTotal = ResourceUtil.coverCpu(hard.get("limits.cpu"));
//        cpuUsed = ResourceUtil.coverCpu(used.get("limits.cpu"));
//
//        memoryTotal = ResourceUtil.coverMemory(hard.get("limits.memory"));
//        memoryUsed = ResourceUtil.coverMemory(used.get("limits.memory"));
        return "CPU:已分配"+cpuUsed+"/总共"+cpuTotal+"----内存:已分配"+memoryUsed+"/总共"+memoryTotal;
    }

    @Override
    public V1Secret createDockerSecret(KubernetesApiClient kubernetesApiClient,String nameSpace,String secretName,String registryServerAddress,String username,String password) throws UnsupportedEncodingException, ApiException {
        String type = "kubernetes.io/dockerconfigjson";
        CoreV1Api coreV1Api = kubernetesApiClient.getCoreV1Api();
        V1SecretList v1SecretList = coreV1Api.listNamespacedSecret(nameSpace, null, null, null,
                null, "app=" + secretName, null, null, null, null, null);
        if(v1SecretList.getItems()!=null && v1SecretList.getItems().size()>=1){
            V1Secret v1Secret = v1SecretList.getItems().get(0);
//            更新账号密码地址
            String dockerSecret = createDockerSecret(registryServerAddress, username, password);
            Map data = new LinkedTreeMap();
            data.put(".dockerconfigjson",dockerSecret.getBytes("UTF-8"));
            v1Secret.data(data);
//            存在则更新
            coreV1Api.replaceNamespacedSecret(v1Secret.getMetadata().getName(),nameSpace,v1Secret,null,null,null);
            logger.info("更新密钥"+secretName+"成功");
            return v1Secret;
        }

        //2.创建密钥
        V1Secret v1Secret = new V1Secret();
        V1ObjectMeta meta = new V1ObjectMeta().name(secretName).namespace(nameSpace);
        String dockerSecret = createDockerSecret(registryServerAddress, username, password);

        v1Secret.setMetadata(meta);
        Map data = new LinkedTreeMap();

        data.put(".dockerconfigjson",dockerSecret.getBytes("UTF-8"));
        v1Secret.data(data);
        v1Secret.type(type);
        v1Secret.apiVersion("v1");
        v1Secret.setKind(KubernetesConstants.K8S_DEPLOY_Secret_NAME);
        //设置标签 方便查询
        Map labels = new LinkedHashMap();
        labels.put("app",secretName);
        labels.put("account",Base64.getEncoder().encodeToString((username+":"+password).getBytes("UTF-8")).replace("=",""));
        meta.setLabels(labels);

        V1Secret secret = coreV1Api.createNamespacedSecret(
                nameSpace,
                v1Secret, null,
                null, null
        );
        logger.info("创建密钥"+secretName+"成功");
        Yaml.dump(secret);
        return secret;
    }

    @Override
    public V1Namespace readNamespace(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException {
        //所有命名空间名字 不允许大写.统一转成小写
        nameSpace = nameSpace.toLowerCase();
        try {
            V1Namespace v1Namespace = kubernetesApiClient.getCoreV1Api().readNamespace(nameSpace, null, null, null);
            return v1Namespace;
        }catch (ApiException e){
            if(!"Not Found".equals(e.getMessage())){
                e.printStackTrace();
                throw e;
            }
        }
        return null;
    }

    @Override
    public V1Status deleteNameSpace(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException {
        return kubernetesApiClient.getCoreV1Api().deleteNamespace(nameSpace,null,null,null,null,
                null,null);
    }

    @Override
    public V1Namespace createNameSpace(KubernetesApiClient kubernetesApiClient,String nameSpace,String secretName,RegistryAddressEntity registryAddressEntity) throws ApiException {
        if (registryAddressEntity == null){
            registryAddressEntity = registryAddressService.getDefaultRegistryAddress();
            if (registryAddressEntity == null){
                throw new DataErrorException("未配置默认镜像仓库!请联系管理员!");
            }
        }
        if (registryAddressEntity!=null && StringUtils.isNotBlank(registryAddressEntity.getKeyName())){
            secretName = registryAddressEntity.getKeyName();
        }

        V1Namespace v1Namespace = readNamespace(kubernetesApiClient, nameSpace);
        if(Objects.isNull(v1Namespace)){
            v1Namespace = new V1Namespace();
            Map<String, String> labels = new HashMap<>();
            v1Namespace.setMetadata(new V1ObjectMeta().labels(labels));
            v1Namespace.setMetadata(new V1ObjectMeta().name(nameSpace));
            //创建命名空间
            v1Namespace = kubernetesApiClient.getCoreV1Api().createNamespace(v1Namespace,null,null,null);
        }
        if (v1Namespace!=null){
            //创建默认的镜像仓库密钥
            try {
                createDockerSecret(kubernetesApiClient,nameSpace, StringUtils.isEmpty(secretName)?KubernetesConfig.getImagePullSecrets():secretName,
                        registryAddressEntity.getAddress(),registryAddressEntity.getUserName(),registryAddressEntity.getPwd());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                deleteNameSpace(kubernetesApiClient,nameSpace);
                log.error("创建密钥失败!");
                return null;
            }
        }
        return v1Namespace;
    }

    @Override
    public V1ResourceQuota setNameSpaceLimit(KubernetesApiClient kubernetesApiClient,String nameSpace,String cpuLimit,String memoryLimit) throws ApiException {
        //转换k8s单位
//        memoryLimit = ResourceUtil.coverK8sMemory(memoryLimit);

        V1ResourceQuota v1ResourceQuota = new V1ResourceQuota();
        v1ResourceQuota.setApiVersion(KubernetesYamlConfig.getApiVersion());
        v1ResourceQuota.setKind(KubernetesConstants.K8S_DEPLOY_ResourceQuota_NAME);
        v1ResourceQuota.setMetadata(new V1ObjectMeta().name(nameSpace
        ).namespace(nameSpace));

        Map hard = new HashMap();
        //最低的限制 requests.cpu: "1" requests.memory: 1Gi
        //设置命名空间最大的CPU限制
        hard.put("limits.cpu",cpuLimit);
        //设置命名空间最大的内存限制
        hard.put("limits.memory",memoryLimit);
        v1ResourceQuota.spec(new V1ResourceQuotaSpec().hard(hard));

        //设置整个命名空间资源限制
        V1ResourceQuota namespacedResourceQuota = kubernetesApiClient.getCoreV1Api().createNamespacedResourceQuota(nameSpace,
                v1ResourceQuota, null, null, null);
        return namespacedResourceQuota;
    }

    @Override
    public V1ResourceQuota getNameSpaceLimit(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException {
        V1ResourceQuota v1ResourceQuota = kubernetesApiClient.getCoreV1Api()
                .readNamespacedResourceQuota(nameSpace,
                nameSpace, null, null, null);
        return v1ResourceQuota;
    }

    @Override
    public V1LimitRange getPodLimit(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException {
        return kubernetesApiClient.getCoreV1Api().readNamespacedLimitRange(nameSpace,nameSpace,null,null,null);
    }

    @Override
    public V1LimitRange updateNameSpacePodLimit(KubernetesApiClient kubernetesApiClient,String nameSpace, String podCpuLimit, String podMemoryLimit, String podCpuRequest,
                                                String podMemoryRequest, String resourceCpuLimit, String resourceMemoryLimit) throws ApiException {
        //转换k8s单位
//        podMemoryLimit = ResourceUtil.coverK8sMemory(podMemoryLimit);
//        podMemoryRequest = ResourceUtil.coverK8sMemory(podMemoryRequest);
//        resourceMemoryLimit = ResourceUtil.coverK8sMemory(resourceMemoryLimit);

        V1LimitRange v1LimitRange = new V1LimitRange();
//        v1LimitRange.setApiVersion(KubernetesYamlConfig.getApiVersion());
        v1LimitRange.setKind(KubernetesConstants.K8S_DEPLOY_LimitRange_NAME);
        v1LimitRange.setMetadata(new V1ObjectMeta().name(nameSpace
        ).namespace(nameSpace));
        List<V1LimitRangeItem> limits = new ArrayList<>();
        V1LimitRangeItem v1LimitRangeItem = new V1LimitRangeItem();
        String minCpu = podCpuRequest;
        String maxCpu = podCpuLimit;
        String minMemory = podMemoryRequest;
        String maxMemory = podMemoryLimit;
        //设置最小限制
        Map<String, Quantity> min = new HashMap<>();
        min.put("cpu",new Quantity(minCpu));
        min.put("memory",new Quantity(minMemory));
        //设置最大限制
        Map<String, Quantity> max = new HashMap<>();
        max.put("cpu",new Quantity(resourceCpuLimit));
        max.put("memory",new Quantity(resourceMemoryLimit));
        //设置资源请求
        Map<String, Quantity> defaultMap = new HashMap<>();
        defaultMap.put("cpu",new Quantity(maxCpu));
        defaultMap.put("memory",new Quantity(maxMemory));
        //设置资源限制
        Map<String, Quantity> defaultRequest = new HashMap<>();
        defaultRequest.put("cpu",new Quantity(minCpu));
        defaultRequest.put("memory",new Quantity(minMemory));
        v1LimitRangeItem.min(min);
        v1LimitRangeItem.max(max);
        v1LimitRangeItem.defaultRequest(defaultRequest);
        v1LimitRangeItem._default(defaultMap);
        v1LimitRangeItem.setType("Container");
        limits.add(v1LimitRangeItem);

        v1LimitRange.setSpec(new V1LimitRangeSpec().limits(limits));
        V1LimitRange namespacedLimitRange = kubernetesApiClient.getCoreV1Api().replaceNamespacedLimitRange(nameSpace,nameSpace,
                v1LimitRange, null, null, null);

        V1ResourceQuota v1ResourceQuota = new V1ResourceQuota();
//        v1ResourceQuota.setApiVersion(KubernetesYamlConfig.getApiVersion());
        v1ResourceQuota.setKind(KubernetesConstants.K8S_DEPLOY_ResourceQuota_NAME);
        v1ResourceQuota.setMetadata(new V1ObjectMeta().name(nameSpace
        ).namespace(nameSpace));

        Map hard = new HashMap();
        //最低的限制 requests.cpu: "1" requests.memory: 1Gi
        //设置命名空间最大的CPU限制
        hard.put("limits.cpu",resourceCpuLimit);
        //设置命名空间最大的内存限制
        hard.put("limits.memory",resourceMemoryLimit);
        v1ResourceQuota.spec(new V1ResourceQuotaSpec().hard(hard));

        //设置整个命名空间资源限制
        V1ResourceQuota namespacedResourceQuota = kubernetesApiClient.getCoreV1Api().replaceNamespacedResourceQuota(nameSpace,nameSpace,
                v1ResourceQuota, null, null, null);

        return namespacedLimitRange;
    }

    @Override
    public V1LimitRange setNameSpacePodLimit(KubernetesApiClient kubernetesApiClient,String nameSpace, String podCpuLimit, String podMemoryLimit, String podCpuRequest, String podMemoryRequest) throws ApiException {
        //转换k8s单位
//        podMemoryLimit = ResourceUtil.coverK8sMemory(podMemoryLimit);
//        podMemoryRequest = ResourceUtil.coverK8sMemory(podMemoryRequest);

        V1LimitRange v1LimitRange = new V1LimitRange();
//        v1LimitRange.setApiVersion(KubernetesYamlConfig.getApiVersion());
        v1LimitRange.setKind(KubernetesConstants.K8S_DEPLOY_LimitRange_NAME);
        v1LimitRange.setMetadata(new V1ObjectMeta().name(nameSpace
        ).namespace(nameSpace));
        List<V1LimitRangeItem> limits = new ArrayList<>();
        V1LimitRangeItem v1LimitRangeItem = new V1LimitRangeItem();
        String minCpu = podCpuRequest;
        String maxCpu = podCpuLimit;
        String minMemory = podMemoryRequest;
        String maxMemory = podMemoryLimit;
        //设置最小限制
        Map<String, Quantity> min = new HashMap<>();
        min.put("cpu",new Quantity(minCpu));
        min.put("memory",new Quantity(minMemory));
        //设置最大限制
        Map<String, Quantity> max = new HashMap<>();
        max.put("cpu",new Quantity(maxCpu));
        max.put("memory",new Quantity(maxMemory));
        //设置资源请求
        Map<String, Quantity> defaultMap = new HashMap<>();
        defaultMap.put("cpu",new Quantity(maxCpu));
        defaultMap.put("memory",new Quantity(maxMemory));
        //设置资源限制
        Map<String, Quantity> defaultRequest = new HashMap<>();
        defaultRequest.put("cpu",new Quantity(minCpu));
        defaultRequest.put("memory",new Quantity(minMemory));
        v1LimitRangeItem.min(min);
        v1LimitRangeItem.max(max);
        v1LimitRangeItem.defaultRequest(defaultRequest);
        v1LimitRangeItem._default(defaultMap);
        v1LimitRangeItem.setType("Container");
        limits.add(v1LimitRangeItem);

        v1LimitRange.setSpec(new V1LimitRangeSpec().limits(limits));
        V1LimitRange namespacedLimitRange = kubernetesApiClient.getCoreV1Api().createNamespacedLimitRange(nameSpace,
                v1LimitRange, null, null, null);
        return namespacedLimitRange;
    }

    @Override
    public void deleteNameSpaceLimit(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException {
        kubernetesApiClient.getCoreV1Api().deleteNamespacedLimitRange(nameSpace,nameSpace,null,null,null,null
        ,null,null);
        kubernetesApiClient.getCoreV1Api().deleteNamespacedResourceQuota(nameSpace,nameSpace,null,null,null,null,
                null,null);
    }

    /**
     * 创建Docker类型的密钥
     * @param registryServerAddress 镜像仓库地址
     * @param username 镜像仓库账号
     * @param password 镜像仓库密码
     * @return 生成的JSON字符串
     * @throws UnsupportedEncodingException
     */
    public static String createDockerSecret(String registryServerAddress,String username,String password) throws UnsupportedEncodingException {
        /**
         * 最终需要生成的secretyaml
         * apiVersion: v1
         * data:
         *   .dockerconfigjson: eyJhdXRocyI6eyJodHRwczovL3JlZ2lzdHJ5LmJhZG91Ijp7InVzZXJuYW1lIjoiemhhb2xpbnpoaUBiYWRvdXNvZnQuY29tIiwicGFzc3dvcmQiOiJGeG9lXjIxNjMkIiwiYXV0aCI6ImVtaGhiMnhwYm5wb2FVQmlZV1J2ZFhOdlpuUXVZMjl0T2taNGIyVmVNakUyTXlRPSJ9fX0=
         * kind: Secret
         * metadata:
         *   creationTimestamp: null
         *   name: dch-app-docker-secret
         *   namespace: bddevns
         *
         *   .dockerconfigjson由以下字符串使用base64加密做成
         *   {"auths":{"https://registry.badou":{"username":"xxxx","password":"xxxx","auth":"xxxx"}}}
         *   其中auth:xxxx 这里面的值由username:password加密而成,格式为:账号:密码
         */
        //空认证处理
        if (username == null){
            log.info("空认证处理用户名");
            username = "";
        }
        if (password == null){
            log.info("空认证处理");
            password = "";
        }

        Map jsonObject = new LinkedHashMap();
        Map auths = new LinkedHashMap();
        Map content = new LinkedHashMap();
        content.put("username",username);
        content.put("password",password);
        content.put("auth", Base64.getEncoder().encodeToString((username+":"+password).getBytes("UTF-8")));
        auths.put(registryServerAddress,content);
        jsonObject.put("auths",auths);
        return JSONObject.toJSONString(jsonObject);
    }

}
