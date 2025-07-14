package com.badou.project.kubernetes.vo;

import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.config.KubernetesDeployConfig;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DeployAppVo
 * @Description 部署应用参数类
 * @date 2023/5/18 17:34
 * @Version 1.0
 */
@Data
public class DeployAppVo {
    //k8s客户端
    private KubernetesApiClient kubernetesApiClient;
    //命名空间
    private String nameSpace;
    //应用编码
    private String appCode;
    //镜像名字
    private String imageName;
    //副本数
    private int replicas;
    //密钥名字
    private String secretName;
    //容器启动执行命令
    private String[] commands;
    //容器内挂载信息
    private ArrayList<V1VolumeMount> v1VolumeMounts;
    //宿主机挂载信息
    private V1Volume[] v1Volumes;
    //环境参数配置
    private List<V1EnvVar> envVarList;
    //Pod名称
    private String podName;
    //启动参数
    private List<String> args;
    //默认的环境参数配置 一般不需要修改
    private KubernetesDeployConfig kubernetesDeployConfig = new KubernetesDeployConfig();
    //镜像重启策略 Always(容器失效时，自动重启该容器) OnFailure(容器终止运行且退出码不为 0 时重启)  Never(不论状态为何，都不重启该容器)
    private String restartPolicy = "Always";
    //DNS策略 与配置的集群域后缀不匹配的任何 DNS 查询（例如 “www.kubernetes.io”） 都将转发到从节点继承的上游名称服务器。集群管理员可能配置了额外的存根域和上游 DNS 服务器。
    private String dnsPolicy = "ClusterFirst";
    private String nodeName;
    //资源限制策略
    Map<String, Quantity> limits;
    private String imagePullPolicy = KubernetesDeployConfig.IMAGE_PULL_POLICY;
    public DeployAppVo() {
    }

    public DeployAppVo(KubernetesApiClient kubernetesApiClient, String nameSpace, String appCode, String imageName, int replicas, String secretName, String[] commands, ArrayList<V1VolumeMount> v1VolumeMounts, V1Volume[] v1Volumes, List<V1EnvVar> envVarList) {
        this.kubernetesApiClient = kubernetesApiClient;
        this.nameSpace = nameSpace;
        this.appCode = appCode;
        this.imageName = imageName;
        this.replicas = replicas;
        this.secretName = secretName;
        this.commands = commands;
        this.v1VolumeMounts = v1VolumeMounts;
        this.v1Volumes = v1Volumes;
        this.envVarList = envVarList;
    }

    public DeployAppVo(KubernetesApiClient kubernetesApiClient, String nameSpace, String appCode, String imageName, int replicas, String secretName, String[] commands, ArrayList<V1VolumeMount> v1VolumeMounts, V1Volume[] v1Volumes, List<V1EnvVar> envVarList, List<String> args) {
        this.kubernetesApiClient = kubernetesApiClient;
        this.nameSpace = nameSpace;
        this.appCode = appCode;
        this.imageName = imageName;
        this.replicas = replicas;
        this.secretName = secretName;
        this.commands = commands;
        this.v1VolumeMounts = v1VolumeMounts;
        this.v1Volumes = v1Volumes;
        this.envVarList = envVarList;
        this.args = args;
    }

    public String getImagePullPolicy() {
        return imagePullPolicy;
    }

    public void setImagePullPolicy(String imagePullPolicy) {
        this.imagePullPolicy = imagePullPolicy;
    }
}
