package com.badou.project.kubernetes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @ClassName KubernetesConfig
 * @Description k8s服务器配置
 * @date 2022/9/26 14:57
 * @Version 1.0
 */
@Component
@Order(-1)
public class KubernetesConfig {

    private static String registryAddress = "";
    private static String authContent = "";
    private static String authType = "configFile";
    private static String masterAddress = "";
    private static String masterPort = "6443";

    /**
     * 镜像拉取使用的认证key
     * 镜像仓库默认是私有的,不可以直接访问的.
     * 通过kubectl create secret创建了对应镜像仓库的密钥,并在创建容器时指定密钥拉取私有仓库的镜像
     *  imagePullSecrets写根据当前正在使用的镜像仓库的账号信息生成的key名
     */
    private static String imagePullSecrets = "badouregistrykey";

    public static String getRegistryAddress() {
        return registryAddress;
    }

    public static String getImagePullSecrets() {
        return imagePullSecrets;
    }

    public static void setImagePullSecrets(String imagePullSecrets) {
        KubernetesConfig.imagePullSecrets = imagePullSecrets;
    }

    @Override
    public String toString() {
        return "TestConfig{" +
                "registryAddress='" + registryAddress + '\'' +
                ", authContent='" + authContent + '\'' +
                ", authType='" + authType + '\'' +
                ", masterAddress='" + masterAddress + '\'' +
                ", masterPort='" + masterPort + '\'' +
                '}';
    }

}
