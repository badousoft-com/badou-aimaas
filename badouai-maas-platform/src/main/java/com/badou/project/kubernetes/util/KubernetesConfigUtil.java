package com.badou.project.kubernetes.util;

import com.badou.brms.dictionary.DictionaryLib;
import com.badou.project.kubernetes.KubernetesConstants;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.config.KubernetesYamlConfig;
import com.badou.project.maas.registryaddress.model.RegistryAddressEntity;
import com.badou.project.server.model.K8sServerConfEntity;
import io.kubernetes.client.openapi.models.*;

/**
 * @ClassName KubernetesConfigUtil
 * @Description k8s配置工具类
 * @date 2022/12/14 16:27
 * @Version 1.0
 */

public class KubernetesConfigUtil {

    /**
     * 构建Mysql地址
     * @param kubernetesApiClient k8s客户端
     * @param dbName 数据库名字
     * @param outPort 对外端口
     * @return
     */
    public static String buildMysqlUrl(K8sServerConfEntity kubernetesApiClient, String dbName, int outPort){
        return "jdbc:mysql://"+kubernetesApiClient.getAddress()+":"+outPort+"/"+dbName+"?characterEncoding=UTF-8&useSSL=false";
    }


    /**
     * 构建Mysql地址
     * @param kubernetesApiClient k8s客户端
     * @param dbName 数据库名字
     * @param outPort 对外端口
     * @return
     */
    public static String buildMysqlUrl(KubernetesApiClient kubernetesApiClient,String dbName,int outPort){
        return "jdbc:mysql://"+kubernetesApiClient.getServer().getAddress()+":"+outPort+"/"+dbName+"?characterEncoding=UTF-8&useSSL=false";
    }

    public static String buildReplicasResoureType(String appCode,Integer resourceType){
        return appCode+"-"+DictionaryLib.getItemName(KubernetesConstants.REPLICAS_DIR_TYPE, resourceType);
    }

    public static V1IngressRule createHostIngress(String host, String path, String appCode, int port){
        V1IngressRule v1IngressRule = new V1IngressRule();
        V1HTTPIngressRuleValue ruleValue = new V1HTTPIngressRuleValue();
        v1IngressRule.setHttp(ruleValue);
        v1IngressRule.setHost(host);
        if(!path.contains("/")){
            path="/"+path;
        }
        ruleValue.addPathsItem(new V1HTTPIngressPath().path(path).pathType(KubernetesConstants.Ingress_PATH_SETVICE_PREFIX)
                .backend(new V1IngressBackend().service(new V1IngressServiceBackend().name(appCode).port(new V1ServiceBackendPort().number(port)))));
        return v1IngressRule;
    }

    public static String buildIngressName(String nameSpaceName){
        return nameSpaceName+KubernetesConstants.SEP+KubernetesConstants.INGRESS_PREFIX;
    }

    /**
     * 构建镜像名字
     * @param registryAddressEntity 镜像仓库实体
     * @param imageName 镜像名称
     * @param tag 镜像标签
     * @return
     */
    public static String buildImageName(RegistryAddressEntity registryAddressEntity, String imageName, String tag){
        return registryAddressEntity.getAddress()+"/"+registryAddressEntity.getProjectName() +"/"+imageName+":"+tag;
    }

    public static String buildImageName(String registryAddress,String projectName,String imageName,String tag){
        return registryAddress+"/"+projectName +"/"+imageName+":"+tag;
    }

    /**
     * 通过镜像完整名字来构建镜像 比如vllm/vllm-openai:v0.7.3.custom、llama-factory-lora:v1.4
     * @param registryAddress 镜像仓库
     * @param projectName 项目名
     * @param imageAllName 镜像全名
     * @return
     */
    public static String buildImageName(String registryAddress,String projectName,String imageAllName){
        return registryAddress+"/"+projectName +"/"+imageAllName;
    }

    public static String getImageRegistry(String registryAddress){
        int i = registryAddress.indexOf("/");
        String imageAddress = registryAddress.substring(0, i);
        return imageAddress;
    }

    /**
     * 检查是否是不合格的镜像名字
     * @param imageName 镜像名字
     * @return true代表不合格 false代表合格
     */
    public static boolean checkImageName(String imageName){
        if(imageName.split(":").length!=2){
            return true;
        }else if(imageName.contains("none")){
            return true;
        }
        return false;
    }


}
