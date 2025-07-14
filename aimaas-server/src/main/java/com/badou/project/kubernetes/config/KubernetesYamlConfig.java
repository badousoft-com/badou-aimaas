package com.badou.project.kubernetes.config;

import com.badou.project.common.config.YamlAndPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;

/**
 * @ClassName KubernetesYamlConfig
 * @Description kubernetes服务配置类
 * @date 2022/9/20 14:26
 * @Version 1.0
 */
@Configuration
@PropertySource(factory = YamlAndPropertySourceFactory.class,value = {"classpath:config/application-kubernetes.yml"})
//顺序在KubernetesConfig后
@Order(1)
public class KubernetesYamlConfig {

   //默认的kubernetes-api连接配置文件路径
   private static String configPath = "";
   //默认操作的命名空间
   private static String defaultNameSpace = "";
   //默认容器副本数
   private static Integer defaultReplicas = 1;
   //镜像仓库地址
   private static String registryAddress = null;
   //应用版本
   private static String apiVersion = "v1";
   //默认镜像版本
   private static String defaultVersion = "latest";
   //镜像空间前缀
   private static String projectPreFix = "";
   //认证信息auth
   private static String authType = "";
   private static String authContent = "";
   private static String masterAddress = "";
   private static String masterPort = "";
   //设置k8s服务器的ingress服务允许通过的文件大小为500m
   private static String fileLimitSize = "500m";
   //项目挂载路径
   private static String volumePath = "";
   //项目挂载
   private static String volumName = "outdir";


   {
//      KubernetesApiClientUtil.initClient();
   }

   public static String getVolumName() {
      return volumName;
   }

   public static void setVolumName(String volumName) {
      KubernetesYamlConfig.volumName = volumName;
   }

   public static String getVolumePath() {
      return volumePath;
   }

   public static void setVolumePath(String volumePath) {
      KubernetesYamlConfig.volumePath = volumePath;
   }

   public static String getApiVersion() {
      return apiVersion;
   }

   public static void setApiVersion(String apiVersion) {
      KubernetesYamlConfig.apiVersion = apiVersion;
   }

   public static String getMasterAddress() {
      return masterAddress;
   }

   public static String getMasterPort() {
      return masterPort;
   }

   public static String getAuthType() {
      return authType;
   }

   public static String getAuthContent() {
      return authContent;
   }

   public void setConfigPath(String configPath) {
      KubernetesYamlConfig.configPath = configPath;
   }

   public static String getConfigPath() {
      return configPath;
   }

   public static String getDefaultNameSpace() {
      return defaultNameSpace;
   }

   public static void setFileLimitSize(String fileLimitSize) {
      KubernetesYamlConfig.fileLimitSize = fileLimitSize;
   }

   public static String getFileLimitSize() {
      return fileLimitSize;
   }

   public static void setDefaultNameSpace(String defaultNameSpace) {
      KubernetesYamlConfig.defaultNameSpace = defaultNameSpace;
   }

   public static Integer getDefaultReplicas() {
      return defaultReplicas;
   }

   @Value("${spring.kubernetes.config.replicas}")
   public static void setDefaultReplicas(Integer defaultReplicas) {
      KubernetesYamlConfig.defaultReplicas = defaultReplicas;
   }

   public static String getRegistryAddress() {
      return registryAddress;
   }
   public void setRegistryAddress(String registryAddress) {
      KubernetesYamlConfig.registryAddress = registryAddress;
   }

   public static String getDefaultVersion() {
      return defaultVersion;
   }

   public static void setDefaultVersion(String defaultVersion) {
      KubernetesYamlConfig.defaultVersion = defaultVersion;
   }

   public static String getProjectPreFix() {
      return projectPreFix;
   }

   public void setProjectPreFix(String projectPreFix) {
      KubernetesYamlConfig.projectPreFix = projectPreFix;
   }

   public static void setAuthType(String authType) {
      KubernetesYamlConfig.authType = authType;
   }

   public static void setAuthContent(String authContent) {
      KubernetesYamlConfig.authContent = authContent;
   }

   public static void setMasterAddress(String masterAddress) {
      KubernetesYamlConfig.masterAddress = masterAddress;
   }

   public static void setMasterPort(String masterPort) {
      KubernetesYamlConfig.masterPort = masterPort;
   }
}
