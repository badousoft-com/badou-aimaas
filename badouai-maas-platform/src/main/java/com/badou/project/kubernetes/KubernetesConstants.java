package com.badou.project.kubernetes;

/**
 * @ClassName KubernetesConstants
 * @Description k8s常量定义
 * @date 2022/9/20 11:22
 * @Version 1.0
 */

public class KubernetesConstants {

    //部署时使用的服务类型名字
    public static final String K8S_DEPLOY_SERVICE_NAME = "Service";
    //部署时使用的控制类型名字
    public static final String K8S_DEPLOY_DEPLOYMENT_NAME = "Deployment";
    //部署时使用的命名空间类型名字
    public static final String K8S_DEPLOY_NameSpace_NAME = "Namespace";
    //部署时使用的命名空间资源限制类型名字
    public static final String K8S_DEPLOY_ResourceQuota_NAME = "ResourceQuota";
    //部署时使用的容器资源限制类型名字
    public static final String K8S_DEPLOY_LimitRange_NAME = "LimitRange";
    //部署时使用的镜像认证密钥名字
    public static final String K8S_DEPLOY_Secret_NAME = "Secret";
    //部署时使用的Ingress对外开放服务名字
    public static final String K8S_DEPLOY_Ingress_NAME = "Ingress";
    //部署时使用的应用名字
    public static final String K8S_DEPLOY_Pod_NAME = "Pod";
    //ingress服务路径匹配前缀
    public static final String Ingress_PATH_SETVICE_PREFIX = "Prefix";
    //分割符
    public static final String SEP = "-";
    //Ingress服务前缀
    public static final String INGRESS_PREFIX = "ingress";
    //副本模块-扩容配置类型 cpu
    public static final String REPLICAS_CPU_TYPE = "cpu";
    //副本模块-扩容配置类型 memory
    public static final String REPLICAS_MEMORY_TYPE = "memory";
    //副本模块-扩容配置类型 数据字典编码
    public static final String REPLICAS_DIR_TYPE = "REPLICASRESOURCETYPE";
    //副本模块-扩容配置值的类型 百分比
    public static final int REPLICAS_VALUE_TYPE_PERCENT = 0;
    //副本模块-扩容配置值的类型 固定值
    public static final int REPLICAS_VALUE_TYPE_NUM = 1;
    //默认的密码长度 15
    public static final int DEFAULT_MAX_PWD_LENGTH = 15;
    //默认的密码长度16
    public static final int DEFAULT_MAX_PWD_LENGTH16 = 16;
    // 多实例等待次数-1分钟
    public static final int DEFAULT_INSTACE_WAIT_COUNT = 12;
    // 多实例等待时长
    public static final int DEFAULT_INSTACE_WAIT_SECONDS = 2;
    // 创建中等待时长-10分钟
    public static final int DEFAULT_CREATING_WAIT_SECONDS = 3;
    // 创建中等待最大次数
    public static final int DEFAULT_CREATING_WAIT_COUNT = 200;
    // 移除应用等待-等待次数-5分钟
    public static final int DEFAULT_REMOVE_APP_WAIT_COUNT = 60;
    // 移除应用等待-每次等待时间-5秒
    public static final int DEFAULT_REMOVE_APP_WAIT_SECONDS = 5;
    // 等待容器状态数据更新-等待次数-1分钟
    public static final int DEFAULT_APP_STATUS_WAIT_COUNT = 12;
    // 等待容器状态数据更新-每次等待时间-1分钟
    public static final int DEFAULT_APP_STATUS_WAIT_SECONDS = 6;
}
