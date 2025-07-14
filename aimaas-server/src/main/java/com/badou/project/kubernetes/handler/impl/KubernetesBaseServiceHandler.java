package com.badou.project.kubernetes.handler.impl;

import com.badou.project.common.exception.ParamErrorException;
import com.badou.project.kubernetes.KubernetesConstants;
import com.badou.project.kubernetes.handler.KubernetesServiceHandler;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName DefaultServiceHandler
 * @Description k8s基础处理服务
 * @date 2022/9/20 17:44
 * @Version 1.0
 */
@Component
@Slf4j
public class KubernetesBaseServiceHandler implements KubernetesServiceHandler {

    @Override
    public V1ServiceList getAllServices(KubernetesApiClient kubernetesApiClient) throws ApiException {
        V1ServiceList v1ServiceList = kubernetesApiClient.getCoreV1Api().listServiceForAllNamespaces(null, null,
                null, null, null, null,
                null, null, null, null);
        return v1ServiceList;
    }

    @Override
    public V1ServiceList getAllServiceByNameSpace(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException {
        V1ServiceList serviceList = kubernetesApiClient.getCoreV1Api().listNamespacedService(nameSpace, null, null, null,
                null, null, null,
                null, null, null, null);
        return serviceList;
    }

    @Override
    public V1Status deleteService(KubernetesApiClient kubernetesApiClient,String nameSpace,String serviceCode) throws ApiException {
        V1Status status = kubernetesApiClient.getCoreV1Api().deleteNamespacedService(serviceCode,
                nameSpace, "false", null, null, null,
                null, null);
        return status;
    }

    @Override
    public V1ServiceList getServiceByCode(KubernetesApiClient kubernetesApiClient,String nameSpace, String appCodes) throws ApiException {
        V1ServiceList v1ServiceList = kubernetesApiClient.getCoreV1Api().listNamespacedService(nameSpace,
                null, null, null, null, "app="+appCodes,
                null, null, null, null, null);
        return v1ServiceList;
    }

    @Override
    public String createServiceByDefault(KubernetesApiClient kubernetesApiClient,String nameSpace, String appCode, Integer port) throws ApiException {
        if(StringHandlerUtil.isContainChinese(appCode) || (!StringHandlerUtil.containsAlphabetic(appCode))){
            throw new ParamErrorException("编码不能包含中文以及首字符必须为字母");
        }
        Map<String, String> labels = new HashMap<>();
        labels.put("app",appCode);
        V1Service svc = new V1ServiceBuilder()
                .withApiVersion("v1")
                .withKind(KubernetesConstants.K8S_DEPLOY_SERVICE_NAME)
                .withNewMetadata()
                .withLabels(labels)
                .withNamespace(nameSpace)
                .withName(appCode)
                .endMetadata()
                .withNewSpec()
                //.withSessionAffinity("ClientIP")
                .withType("ClusterIP")
                .addNewPort()
                .withProtocol("TCP")
                .withName("http")
                //配置容器端口
                .withPort(port)
                .withTargetPort(new IntOrString(port))
                //配置宿主机端口
                .endPort()
                .addNewPort()
                .withProtocol("TCP")
                .withName("websocket")
                //配置容器端口
                .withPort(32007)
                .withTargetPort(new IntOrString(32007))
//                //配置宿主机端口
                .endPort()
                //选择容器 匹配机制
                .addToSelector("app",appCode)
                .endSpec()
                .build();
        /**
         *  * 注意 容器的控制台依赖于 websocket.现在是开启了netty服务 然后统一监听32007作为websocket的统一入口
         *    所以创建容器的deployment要配置port 代表开放当前容器的一些端口 比如320007
         *    所以创建容器的service要配置port 代表连接内网网络服务的时候当前容器的一些端口 比如320007
         *    所以创建容器的ingress要配置port 代表连接外网网关的时候 开放当前容器的一些端口 比如320007
         */
        String result = Yaml.dump(svc);
        log.info("创建网络服务配置");
        log.info(result);
        V1Service createResult = kubernetesApiClient.getCoreV1Api().createNamespacedService(nameSpace, svc, null, null, null);
        return result;
    }

    @Override
    public String createServiceAndDeploy(KubernetesApiClient kubernetesApiClient,String nameSpace,String serviceCode,String appCode,String protocol, Integer port, Integer targetPort, Integer nodePort,boolean testMode) throws ApiException {
        //检查port和nodePort是否已分配
        if(port == null || port == 0){
            throw new ApiException("容器端口非法");
        }
        if(nodePort == null || nodePort == 0){
            throw new ApiException("外部端口非法");
        }
        if(StringHandlerUtil.isContainChinese(serviceCode)){
            throw new ParamErrorException("应用编码禁止出现中文");
        }
        if(serviceCode.contains("_")){
            throw new ParamErrorException("应用编码禁止出现下划线");
        }
        Map<String, String> labels = new HashMap<>();
        labels.put("app",appCode);
        V1Service svc = new V1ServiceBuilder()
                .withApiVersion("v1")
                .withKind(KubernetesConstants.K8S_DEPLOY_SERVICE_NAME)
                .withNewMetadata()
                .withName(serviceCode).withNamespace(nameSpace)
                .withLabels(labels)
                .endMetadata()
                .withNewSpec()
                //.withSessionAffinity("ClientIP")
                .withType("NodePort")
                .addNewPort()
                .withProtocol(protocol)
                .withName(serviceCode)
                //配置容器端口
                .withPort(port)
                //配置外部端口
                .withNodePort(nodePort)
                //配置宿主机端口
                .withTargetPort(new IntOrString(port))
                .endPort()
                //选择容器 匹配机制
                .addToSelector("app",appCode)
                .endSpec()
                .build();
        String dump = Yaml.dump(svc);
        if(testMode){
            return Yaml.dump(svc);
        }
        log.info(dump);
        V1Service createResult = kubernetesApiClient.getCoreV1Api().createNamespacedService(nameSpace, svc, null, null, null);
        return Yaml.dump(createResult);
    }

}
