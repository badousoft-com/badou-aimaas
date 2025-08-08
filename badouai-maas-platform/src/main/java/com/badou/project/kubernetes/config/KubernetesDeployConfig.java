package com.badou.project.kubernetes.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName KubernetesDeploymentConfig
 * @Description k8s部署配置参数
 * @date 2023/3/2 14:51
 * @Version 1.0
 */

public class KubernetesDeployConfig {
    //环境配置
    private final Map<String, String> envConfig = new HashMap();
    //镜像拉取策略
    public static final String IMAGE_PULL_POLICY = "Always";
    //路径挂载类型
    public static final String PATH_VOLUMN_TYPE = "DirectoryOrCreate";
    //默认的挂载路径
    public static final String VOLUMN_PATH = "";
    //默认的挂载名字
    public static final String VOLUMN_NAME = "outdir";

    public Map<String, String> getDefaultEnvConfig(){
        return envConfig;
    }

}
