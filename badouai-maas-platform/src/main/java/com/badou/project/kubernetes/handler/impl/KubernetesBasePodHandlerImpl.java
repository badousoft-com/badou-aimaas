package com.badou.project.kubernetes.handler.impl;

import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.common.exception.ParamErrorException;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataExecFailException;
import com.badou.project.exception.DataValidException;
import com.badou.project.kubernetes.KubernetesConstants;
import com.badou.project.kubernetes.TaskConst;
import com.badou.project.kubernetes.config.KubernetesConfig;
import com.badou.project.kubernetes.config.KubernetesDeployConfig;
import com.badou.project.kubernetes.config.KubernetesYamlConfig;
import com.badou.project.kubernetes.handler.KubernetesErrorHandler;
import com.badou.project.kubernetes.handler.KubernetesNameSpaceHandler;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.kubernetes.vo.DeployAppVo;
import com.badou.project.maas.MaasConst;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DefaultPodHandlerImpl
 * @Description 默认的POD处理器
 * @date 2022/9/20 16:57
 * @Version 1.0
 */
@Component
@Slf4j
public class KubernetesBasePodHandlerImpl implements KubernetesPodHandler {

    @Autowired
    private KubernetesErrorHandler kubernetesErrorHandler;
    @Autowired
    private KubernetesNameSpaceHandler kubernetesNameSpaceHandler;

    @Override
    public String execCommand(KubernetesApiClient kubernetesApiClient, String nameSpace, V1Pod v1Pod, String[] commands) throws IOException, ApiException {
        // lm:基础类的命令执行内容已废弃 请使用KubernetesExecHandlerImpl
//        Exec exec = new Exec(kubernetesApiClient.getApiClient());
//        Process proc = exec.exec(v1Pod, commands, "",true, false);
//        log.info("命名空间"+nameSpace+",的"+v1Pod.getMetadata().getName()+"容器执行命令:");
//        for(String command:commands){
//            log.info(command);
//        }
//        log.info("命令结束");
//        String result = StringHandlerUtil.readTextAli(proc.getInputStream());
//        log.info("命令执行结果:"+result);
//        return result;
        return null;
    }

    @Override
    public V1Deployment getOneDeployment(KubernetesApiClient kubernetesApiClient,String nameSpace, String deploymentName) throws ApiException {
        try {
            V1Deployment deployment = kubernetesApiClient.getAppsV1Api().readNamespacedDeployment(deploymentName, nameSpace, null, null, null);
            return deployment;
        }catch (Exception e){
            //找不到也有可能报错 没有创建也有可能没有 没有也正常
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public V1Pod createPodByParams(String imageName, KubernetesApiClient kubernetesApiClient, int gpuCount, String nameSpace, String appCode,DeployAppVo deployAppVo) throws ApiException {
//        deployAppVo.setCommands(new String[]{"sleep"});
//        String[] args = new String[]{"infinity"};
//        deployAppVo.setArgs(Arrays.asList(args));

        List<V1LocalObjectReference> secrets = new ArrayList<>();
        secrets.add(new V1LocalObjectReference().name(StringUtils.isEmpty(deployAppVo.getSecretName())?"badouregistrykey":deployAppVo.getSecretName()));

        //端口暴露服务的对应的是/ Service.Spec.Selector下的值
        Map<String,String> selectLabels = new HashMap<>();
        selectLabels.put("app",appCode);
        selectLabels.put("logkey",nameSpace+"-"+appCode);

        V1Pod v1Pod = new V1Pod().apiVersion("v1");
        v1Pod.setKind(KubernetesConstants.K8S_DEPLOY_Pod_NAME);
        v1Pod.setMetadata(new V1ObjectMetaBuilder()
                .withName(appCode)
                .withNamespace(nameSpace)
                .withLabels(selectLabels)
                .build());

        Map<String, Quantity> limits = new HashMap<>();
        if (deployAppVo.getLimits()!=null){
            limits = deployAppVo.getLimits();
        }

        List<V1Container> containers = new ArrayList<>();
        containers.add(new V1ContainerBuilder()
                .withResources(new V1ResourceRequirements().limits(limits))
                .withName(appCode)//设置docker名
                .withImage(imageName)//设置 docker镜像名
                .withImagePullPolicy("IfNotPresent")//镜像本地拉去策略
                .withEnv(deployAppVo.getEnvVarList())
                .withVolumeMounts(deployAppVo.getV1VolumeMounts())
                .build());
        V1LocalObjectReference imagePullSecret = new V1LocalObjectReference();
        imagePullSecret.setName("badouregistrykey");

        List<V1LocalObjectReference> secretlists = new ArrayList<>();
        secretlists.add(imagePullSecret);

        V1Volume[] v1Volumes = deployAppVo.getV1Volumes();
        String restartPolicy = deployAppVo.getRestartPolicy();
        if (StringUtils.isEmpty(restartPolicy)){
            //只有POD支持 deployment是不支持Never的
            restartPolicy = "Never";
        }

        v1Pod.setSpec(new V1PodSpec().containers(containers).imagePullSecrets(secretlists).restartPolicy(restartPolicy));
        if (StringUtils.isNotBlank(deployAppVo.getNodeName())){
            v1Pod.getSpec().setNodeName(deployAppVo.getNodeName());
        }
        if (v1Volumes!=null){
            v1Pod.getSpec().volumes(Arrays.asList(v1Volumes));
        }
        try{
            createPod(kubernetesApiClient,v1Pod,nameSpace,appCode);
            log.info("创建微调任务"+appCode+"完成!");
            return v1Pod;
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public V1Pod createPod(KubernetesApiClient kubernetesApiClient,V1Pod v1Pod,String nameSpace,String podName) throws ApiException {
        log.info("创建Pod\n"+Yaml.dump(v1Pod));
        V1Pod namespacedPod = kubernetesApiClient.getCoreV1Api().createNamespacedPod(nameSpace, v1Pod, null, null, null);
        return namespacedPod;
    }

    @Override
    public List<V1Pod> getNodePods(KubernetesApiClient kubernetesApiClient, String nameSpace, String nodeName) throws ApiException {
        V1PodList pods = kubernetesApiClient.getCoreV1Api().listNamespacedPod(nameSpace, null, null, null, "spec.nodeName="+nodeName, null, null, null, null, null, null);
        return pods.getItems();
    }

    @Override
    public V1Pod getPod(KubernetesApiClient kubernetesApiClient, String nameSpace, String podName) throws ApiException {
        try {
            V1Pod v1Pod = kubernetesApiClient.getCoreV1Api().readNamespacedPod(podName, nameSpace, null, null, null);
            return v1Pod;
        }catch (ApiException e){
            if("Not Found".equals(e.getMessage())){
                return null;
            }
            throw new ApiException(e);
        }
    }

    @Override
    public List<V1Pod> getPodByLabelApp(KubernetesApiClient kubernetesApiClient, String nameSpace, String app) throws ApiException {
        V1PodList pods = kubernetesApiClient.getCoreV1Api().listNamespacedPod(nameSpace, null, null, null, null, "app="+app, null, null, null, null, null);
        return pods.getItems();
    }

    @Override
    public V1Pod getOnePod(KubernetesApiClient kubernetesApiClient,String podName,String nameSpace) throws ApiException {
        V1PodList pods = kubernetesApiClient.getCoreV1Api().listNamespacedPod(nameSpace, null, null, null, "metadata.name="+podName, null, null, null, null, null, null);
        return pods.getItems().size()>0?pods.getItems().get(0):null;
    }

    @Override
    public V1PodList getAllPods(KubernetesApiClient kubernetesApiClient) throws ApiException {
        V1PodList list =
                kubernetesApiClient.getCoreV1Api().listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
        return list;
    }

    @Override
    public V1PodList getAllPodByNameSpace(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException {
        if(StringUtils.isEmpty(nameSpace)){
            return null;
        }
        V1PodList list = kubernetesApiClient.getCoreV1Api().listNamespacedPod(nameSpace, null, null, null, null, null, null, null, null, null, null);
        return list;
    }

    @Override
    public String createDeployment(KubernetesApiClient kubernetesApiClient,String nameSpace,String appName, String imageName, String imageTag, int replicas) throws Exception {
        return createDeploymentAndDeploy(kubernetesApiClient,nameSpace,appName,imageName,replicas,true,null);
    }

    @Override
    public String createLimitDeployment(KubernetesApiClient kubernetesApiClient,String nameSpace, String appCode, String imageName, String imageTag, int replicas, V1ResourceRequirements resourceConfig) throws ApiException, ParamErrorException {
        V1LocalObjectReference imagePullSecret = new V1LocalObjectReference();
        imagePullSecret.setName(KubernetesConfig.getImagePullSecrets());
        //镜像全名=镜像名字:镜像标签
        String imageAllName = imageName+":"+imageTag;
        //端口暴露服务的对应的是/ Service.Spec.Selector下的值
        Map<String,String> selectLabels = new HashMap<>();
        selectLabels.put("app",appCode);
        selectLabels.put("logkey",nameSpace+"-"+appCode);
        V1Deployment body = new V1DeploymentBuilder()
                .withApiVersion("apps/v1")
                .withKind(KubernetesConstants.K8S_DEPLOY_DEPLOYMENT_NAME)
                .withMetadata(new V1ObjectMetaBuilder()
                        .withName(appCode)
                        .withNamespace(nameSpace)
                        .withLabels(selectLabels)
                        .build())
                .withSpec(new V1DeploymentSpecBuilder()
                        //设置默认副本数
                        .withReplicas(replicas)
                        //设置选择器
                        .withSelector(new V1LabelSelectorBuilder()
                                .withMatchLabels(selectLabels)
                                .build())
                        //设置docker镜像模板
                        .withTemplate(new V1PodTemplateSpecBuilder()
                                .withMetadata(new V1ObjectMetaBuilder()
                                        .withLabels(selectLabels)
                                        .build())
                                .withSpec(new V1PodSpecBuilder()
                                        .withContainers(new V1ContainerBuilder()
                                                .withName(appCode)//设置docker名
                                                .withImage(imageAllName)//设置 docker镜像名
                                                .withImagePullPolicy("Always")//镜像本地拉去策略
                                                //.withCommand("/bin/bash","-ce","tail -f /dev/null")//命令
                                                //配置资源限制
                                                .withResources(resourceConfig)
                                                .build()).
                                                withImagePullSecrets(imagePullSecret)
                                        .build())
                                .build())
                        .build())
                .build(); // V1Deployment
        return null;
    }

    @Override
    public String createMiddleDeployment(DeployAppVo deployAppVo) throws Exception {
        String appCode = deployAppVo.getAppCode();
        KubernetesApiClient kubernetesApiClient = deployAppVo.getKubernetesApiClient();
        String imageName = deployAppVo.getImageName();
        String secretName = deployAppVo.getSecretName();
        int replicas = deployAppVo.getReplicas();
        String nameSpace = deployAppVo.getNameSpace();
        List<V1EnvVar> envVarList = deployAppVo.getEnvVarList();

        //应用名字禁止出现下划线
        if(deployAppVo.getAppCode().contains("_")){
            throw new ParamErrorException("应用编码禁止出现下划线");
        }
        if(StringHandlerUtil.isContainChinese(deployAppVo.getAppCode())){
            throw new ParamErrorException("应用编码禁止出现中文");
        }

        ArrayList<V1VolumeMount> v1VolumeMounts = deployAppVo.getV1VolumeMounts();

        //宿主机存储路径
        //234：/home/kube_filestore
        //239：/data/kube_filestore
        //240：/home/kube_filestore
        String storePath = "/home/kube_filestore/outdir/"+deployAppVo.getAppCode();
        //20230413 lm 变更为从数据库读取
        if(StringUtil.isBlank(kubernetesApiClient.getServer().getVolumnPath())){
            throw new Exception("无效的挂载路径");
        }
        storePath = kubernetesApiClient.getServer().getVolumnPath()+"/outdir/"+appCode;
        if(imageName.contains("none")){
            throw new ApiException("错误的镜像名字");
        }
        V1LocalObjectReference imagePullSecret = new V1LocalObjectReference();
        imagePullSecret.setName(secretName);
        //端口暴露服务的对应的是/ Service.Spec.Selector下的值
        Map<String,String> selectLabels = new HashMap<>();
        selectLabels.put("app",appCode);
        selectLabels.put("logkey",nameSpace+"-"+appCode);
        V1Deployment body = new V1DeploymentBuilder()
                .withApiVersion("apps/v1")
                .withKind(KubernetesConstants.K8S_DEPLOY_DEPLOYMENT_NAME)
                .withMetadata(new V1ObjectMetaBuilder()
                        .withName(appCode)
                        .withNamespace(nameSpace)
                        .withLabels(selectLabels)
                        .build())
                .withSpec(new V1DeploymentSpecBuilder()
                        //设置默认副本数
                        .withReplicas(replicas)
                        //设置选择器
                        .withSelector(new V1LabelSelectorBuilder()
                                .withMatchLabels(selectLabels)
                                .build())
                        //设置docker镜像模板
                        .withTemplate(new V1PodTemplateSpecBuilder()
                                .withMetadata(new V1ObjectMetaBuilder()
                                        .withLabels(selectLabels)
                                        .build())
                                .withSpec(new V1PodSpecBuilder()
                                        .withContainers(new V1ContainerBuilder()
                                                .withArgs(deployAppVo.getArgs())
                                                .withName(appCode)//设置docker名
                                                .withImage(imageName)//设置 docker镜像名
                                                //设置环境参数
                                                .withEnv(envVarList)
                                                //镜像本地拉去策略
                                                .withImagePullPolicy(KubernetesDeployConfig.IMAGE_PULL_POLICY)
                                                //命令
                                                .withCommand(deployAppVo.getCommands())
                                                //增加挂载路径
                                                .withVolumeMounts(v1VolumeMounts)
                                                .build()).
                                                withImagePullSecrets(imagePullSecret)
                                        //指定挂载路径
                                        .withVolumes(deployAppVo.getV1Volumes())
                                        .build())
                                .build())
                        .build())
                .build(); // V1Deployment
        String pretty = null; // 是否会漂亮输出
        String dryRun = null; //
        String fieldManager = null; //

        log.info(Yaml.dump(body));
        V1Deployment result = kubernetesApiClient.getAppsV1Api().createNamespacedDeployment(nameSpace, body, pretty, dryRun, fieldManager);//调用createNamespacedDeployment方法创建应用部署
        return "";
    }

    @Override
    public String createDeploymentAndDeploy(KubernetesApiClient kubernetesApiClient,String nameSpace,String appCode, String imageName,int replicas,boolean isTest,DeployAppVo deployAppVo) throws Exception {
        //lm 2023-05-30 添加域名服务
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0,null, QueryOperSymbolEnum.eq));

//        List<CustomHostEntity> customHostEntities = customHostService.find(queryCriterion);
//
//        List<V1HostAlias> v1HostAliases = new ArrayList<>();
//
//        customHostEntities.forEach(e->{
//            V1HostAlias v1HostAlias = new V1HostAlias();
//            v1HostAlias.setIp(e.getIpAddr());
//            v1HostAlias.setHostnames(Arrays.asList(e.getHost()));
//            v1HostAliases.add(v1HostAlias);
//        });

        //应用名字禁止出现下划线
        if(appCode.contains("_")){
            throw new ParamErrorException("应用编码禁止出现下划线");
        }
        if(StringHandlerUtil.isContainChinese(appCode)){
            throw new ParamErrorException("应用编码禁止出现中文");
        }
        //20230118 增加应用挂载
        String volumeName = KubernetesDeployConfig.VOLUMN_NAME;
        V1VolumeMount v1VolumeMount = new V1VolumeMount();
        v1VolumeMount.setMountPath(KubernetesDeployConfig.VOLUMN_PATH);
        v1VolumeMount.setName(volumeName);
        ArrayList<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
        v1VolumeMounts.add(v1VolumeMount);
        //20230302 增加默认环境配置参数
        List<V1EnvVar> envVarList = new ArrayList<>();
        Map<String, String> defaultEnvConfig = deployAppVo.getKubernetesDeployConfig().getDefaultEnvConfig();
        for(String name:defaultEnvConfig.keySet()){
            V1EnvVar v1EnvVar = new V1EnvVar();
            v1EnvVar.name(name);
            v1EnvVar.value(defaultEnvConfig.get(name));
            envVarList.add(v1EnvVar);
        }
        //20230623 lm 增加自定义的环境变量配置
        if(JsonResultUtil.isNotNull(deployAppVo,deployAppVo.getEnvVarList()) && deployAppVo.getEnvVarList().size()>0){
            envVarList.addAll(deployAppVo.getEnvVarList());
        }

        //宿主机存储路径
        //234：/home/kube_filestore
        //239：/data/kube_filestore
        //240：/home/kube_filestore
        String storePath = "/home/kube_filestore/outdir/"+appCode;
        //20230413 lm 变更为从数据库读取
        if(StringUtil.isBlank(kubernetesApiClient.getServer().getVolumnPath())){
            throw new Exception("无效的挂载路径");
        }
        storePath = kubernetesApiClient.getServer().getVolumnPath()+"/outdir/"+appCode;
        if(imageName.contains("none")){
            throw new ApiException("错误的镜像名字");
        }
        V1LocalObjectReference imagePullSecret = new V1LocalObjectReference();
//        Map<String, Quantity> limits = new HashMap<>();

        if (StringUtils.isEmpty(deployAppVo.getSecretName())){
            deployAppVo.setSecretName("");
        }

        imagePullSecret.setName(deployAppVo.getSecretName());
        V1PodSpecBuilder v1PodSpecBuilder = new V1PodSpecBuilder();
        if (StringUtils.isNotBlank(deployAppVo.getNodeName())){
            v1PodSpecBuilder.withNodeName(deployAppVo.getNodeName());
        }



        //端口暴露服务的对应的是/ Service.Spec.Selector下的值
        Map<String,String> selectLabels = new HashMap<>();
        selectLabels.put("app",appCode);
        selectLabels.put("logkey",nameSpace+"-"+appCode);
        V1Deployment body = new V1DeploymentBuilder()
                .withApiVersion("apps/v1")
                .withKind(KubernetesConstants.K8S_DEPLOY_DEPLOYMENT_NAME)
                .withMetadata(new V1ObjectMetaBuilder()
                        .withName(appCode)
                        .withNamespace(nameSpace)
                        .withLabels(selectLabels)
                        .build())
                .withSpec(new V1DeploymentSpecBuilder()
                        //将ReplicaSet设置成0个 代表只需要保留最新的一个 revisionHistoryLimit设置为 0 会完全禁用版本历史记录。所有旧的 ReplicaSet（无论是否正在运行 Pod）都会被立即删除，只保留当前活跃的 ReplicaSet
                                .withRevisionHistoryLimit(0)
                        //设置默认副本数
                        .withReplicas(replicas)
                        //设置选择器
                        .withSelector(new V1LabelSelectorBuilder()
                                .withMatchLabels(selectLabels)
                                .build())
                        //设置docker镜像模板
                        .withTemplate(new V1PodTemplateSpecBuilder()
                                .withMetadata(new V1ObjectMetaBuilder()
                                        .withLabels(selectLabels)
                                        .build())
                                .withSpec(v1PodSpecBuilder
                                        .withRestartPolicy(deployAppVo.getRestartPolicy())
                                        .withDnsPolicy(deployAppVo.getDnsPolicy())
                                        .withContainers(new V1ContainerBuilder()
                                                .withResources(new V1ResourceRequirements().limits(deployAppVo.getLimits()))
//                                                .withPorts(
////                                                        new V1ContainerPort().containerPort(32007).name("websocket").protocol("TCP"),
//                                                        new V1ContainerPort().containerPort(8000).name("http").protocol("TCP")
//                                                )
//                                                .withPorts(
//                                                        new V1ContainerPort().containerPort(32007).name("websocket").protocol("TCP"),
//                                                        new V1ContainerPort().containerPort(8000).name("front").protocol("TCP"),
//                                                        new V1ContainerPort().containerPort(8080).name("platform").protocol("TCP"))
//                                                .withCommand("/bin/bash","-c","--")
//                                                .addNewArg("while true; do sleep 30; done;")
                                                .withArgs(deployAppVo.getArgs())
                                                .withName(appCode)//设置docker名
                                                .withImage(imageName)//设置 docker镜像名
                                                 .withImagePullPolicy(deployAppVo.getImagePullPolicy())//镜像本地拉去策略
                                                .withEnv(envVarList)
//                                                .withCommand("/bin/bash","-ce","tail -f /dev/null")//命令
                                                .withCommand(deployAppVo.getCommands())//命令
                                                //增加挂载路径
                                                .withVolumeMounts(deployAppVo.getV1VolumeMounts())
                                                .build()).
                                        withImagePullSecrets(imagePullSecret)
//                                        .withHostAliases(v1HostAliases)
                                        //指定挂载路径
                                        .withVolumes(deployAppVo.getV1Volumes())
                                        .build())
                                .build())
                        .build())
                .build();
        /**
         *  * 注意 容器的控制台依赖于 websocket.现在是开启了netty服务 然后统一监听32007作为websocket的统一入口
         *    所以创建容器的deployment要配置port 代表开放当前容器的一些端口 比如320007
         *    所以创建容器的service要配置port 代表连接内网网络服务的时候当前容器的一些端口 比如320007
         *    所以创建容器的ingress要配置port 代表连接外网网关的时候 开放当前容器的一些端口 比如320007
         */
        if(isTest){
            String dump = Yaml.dump(body);
            System.out.println(dump);
            return dump;
        }
        String pretty = null; // 是否会漂亮输出
        String dryRun = null; //
        String fieldManager = null; //
        log.info("创建应用服务配置");
        log.info(Yaml.dump(body));
        V1Deployment result = kubernetesApiClient.getAppsV1Api().createNamespacedDeployment(nameSpace, body, pretty, dryRun, fieldManager);//调用createNamespacedDeployment方法创建应用部署
        return result.toString();
    }

    @Override
    public String createLimitDeploymentAndDeploy(KubernetesApiClient kubernetesApiClient,String nameSpace,String appCode, String imageName,int replicas,boolean isTest,String secretName) throws ApiException, ParamErrorException {
        //应用名字禁止出现下划线
        if(appCode.contains("_")){
            throw new ParamErrorException("应用编码禁止出现下划线");
        }
        if(StringHandlerUtil.isContainChinese(appCode)){
            throw new ParamErrorException("应用编码禁止出现中文");
        }
        //20230118 增加应用挂载
        String volumeName = KubernetesYamlConfig.getVolumName();
        V1VolumeMount v1VolumeMount = new V1VolumeMount();
        v1VolumeMount.setMountPath(KubernetesYamlConfig.getVolumePath());
        v1VolumeMount.setName(volumeName);
        ArrayList<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
        v1VolumeMounts.add(v1VolumeMount);
        //宿主机存储路径
        //234：/home/kube_filestore
        //239：/data/kube_filestore
        //240：/home/kube_filestore
        String storePath = "/home/kube_filestore/outdir/"+appCode;
        if(imageName.contains("none")){
            throw new ApiException("错误的镜像名字");
        }
        V1LocalObjectReference imagePullSecret = new V1LocalObjectReference();
        imagePullSecret.setName(secretName);
        //端口暴露服务的对应的是/ Service.Spec.Selector下的值
        Map<String,String> selectLabels = new HashMap<>();
        selectLabels.put("app",appCode);
        selectLabels.put("logkey",nameSpace+"-"+appCode);
        V1Deployment body = new V1DeploymentBuilder()
                .withApiVersion("apps/v1")
                .withKind(KubernetesConstants.K8S_DEPLOY_DEPLOYMENT_NAME)
                .withMetadata(new V1ObjectMetaBuilder()
                        .withName(appCode)
                        .withNamespace(nameSpace)
                        .withLabels(selectLabels)
                        .build())
                .withSpec(new V1DeploymentSpecBuilder()
                        //设置默认副本数
                        .withReplicas(replicas)
                        //设置选择器
                        .withSelector(new V1LabelSelectorBuilder()
                                .withMatchLabels(selectLabels)
                                .build())
                        //设置docker镜像模板
                        .withTemplate(new V1PodTemplateSpecBuilder()
                                .withMetadata(new V1ObjectMetaBuilder()
                                        .withLabels(selectLabels)
                                        .build())
                                .withSpec(new V1PodSpecBuilder()
                                        .withContainers(new V1ContainerBuilder()
                                                .withName(appCode)//设置docker名
                                                .withImage(imageName)//设置 docker镜像名
                                                .withImagePullPolicy("Always")//镜像本地拉去策略
                                                //.withCommand("/bin/bash","-ce","tail -f /dev/null")//命令
                                                //增加挂载路径
                                                .withVolumeMounts(v1VolumeMounts)
                                                .build()).
                                                withImagePullSecrets(imagePullSecret)
                                        .withVolumes(new V1Volume().hostPath(new V1HostPathVolumeSource().type("DirectoryOrCreate").path(storePath)).name(volumeName))
                                        .build())
                                .build())
                        .build())
                .build(); // V1Deployment
        if(isTest){
            return Yaml.dump(body);
        }
        String pretty = null; // 是否会漂亮输出
        String dryRun = null; //
        String fieldManager = null; //
        V1Deployment result = kubernetesApiClient.getAppsV1Api().createNamespacedDeployment(nameSpace, body, pretty, dryRun, fieldManager);//调用createNamespacedDeployment方法创建应用部署
        return result.toString();
    }

    @Override
    public String deleteDeployment(KubernetesApiClient kubernetesApiClient,String nameSpace, String deployment) throws ApiException {
        V1Status v1Status = kubernetesApiClient.getAppsV1Api().deleteNamespacedDeployment(deployment,
                nameSpace, null, null, null,
                null, null, null);
        return Yaml.dump(v1Status);
    }

    @Override
    public String deleteOnePod(KubernetesApiClient kubernetesApiClient,String nameSpace, String podName) throws ApiException {
        V1Pod v1Pod = kubernetesApiClient.getCoreV1Api().deleteNamespacedPod(podName,
                nameSpace, null, null, 0,
                null, null, null);
        return Yaml.dump(v1Pod);
    }

    @Override
    public String readPodAllLog(KubernetesApiClient kubernetesApiClient,String nameSpace, String podName,Integer tailLines) throws ApiException, IOException {
        String logs = kubernetesApiClient.getCoreV1Api().readNamespacedPodLog(podName,nameSpace,"",null,null,
                null,null,null,null,tailLines,null);
        return logs;
    }

    @Override
    public String readNewLog(KubernetesApiClient kubernetesApiClient,String nameSpace, String podName,Integer sinceSeconds, Integer tailLines) throws ApiException {
        String logs = kubernetesApiClient.getCoreV1Api().readNamespacedPodLog(podName, nameSpace, "", null,
                null, null, null, false, sinceSeconds, tailLines, null);
        return logs;
    }

    @Override
    public V1PodList getPodByDeployment(KubernetesApiClient kubernetesApiClient,String nameSpace, String deploymentName) throws ApiException {
        V1PodList podList = kubernetesApiClient.getCoreV1Api().listNamespacedPod(nameSpace, null, null,
                null, null, "app="+deploymentName, null,
                null, null, null, null);
        return podList;
    }

    @Override
    public V1PodList queryPodByApp(KubernetesApiClient kubernetesApiClient,String nameSpace, String deploymentNames) throws ApiException {
        V1PodList podList = kubernetesApiClient.getCoreV1Api().listNamespacedPod(nameSpace,
                null, null,
                null, null, "app in ("+deploymentNames+")", null,
                null, null, null, null);
        return podList;
    }

    @Override
    public boolean checkPodFail(V1Pod v1Pod) throws Exception {
        if (v1Pod == null || v1Pod.getStatus() == null || StringUtils.isEmpty(v1Pod.getStatus().getPhase())){
            return true;
        }
        String phase = v1Pod.getStatus().getPhase();
        if (TaskConst.POD_STATUS_FAIL.equals(phase)) {
            return true;
        }
        return false;
    }

    @Override
    public String checkPodRunning(KubernetesApiClient kubernetesApiClient, String nameSpace, String deploymentName,Integer checkInterval) throws Exception {
        Integer waitCheckS = KubernetesConstants.DEFAULT_INSTACE_WAIT_SECONDS;
        if (checkInterval!=null){
            waitCheckS = checkInterval;
            log.info("检查间隔设置为"+checkInterval);
        }
        if (waitCheckS == 0){
            throw new DataValidException("刷新间隔无效.不允许为0");
        }
        //查询部署状态
        V1PodList pods = null;
        pods = getPodByDeployment(kubernetesApiClient,nameSpace, deploymentName);
        // 等待多实例变1的等待次数
        int moreInstanceCount = 0;
        while (moreInstanceCount<KubernetesConstants.DEFAULT_INSTACE_WAIT_COUNT || pods.getItems().size() == 0){
            if (pods.getItems().size() == 1){
                log.info("应用"+deploymentName+"实例变1成功!序号:"+moreInstanceCount);
                break;
            }
            log.info("应用"+deploymentName+"获取到多个实例 等待实例变1,序号:"+moreInstanceCount);
            try {
                TimeUnit.SECONDS.sleep(waitCheckS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //未变1 刷新等待
            moreInstanceCount++;
            pods = getPodByDeployment(kubernetesApiClient,nameSpace, deploymentName);
            if (moreInstanceCount>=KubernetesConstants.DEFAULT_INSTACE_WAIT_COUNT){
                log.info("当前次数"+moreInstanceCount+",最大次数"+KubernetesConstants.DEFAULT_INSTACE_WAIT_COUNT+",刷新间隔"+waitCheckS);
                throw new DataExecFailException("等待服务器执行超时.请联系管理员!");
            }
        }
        //等待应用刷新有效的状态数据
        List<V1ContainerStatus> v1ContainerStatuses = waitK8sAppStatus(kubernetesApiClient, nameSpace, deploymentName,waitCheckS);
        if (v1ContainerStatuses.size() != 1) {
            return getFailResult("应用"+deploymentName+"实例无法正常启动为单例");
        }
        //把应用状态数据更新到最新
        V1Pod v1Pod = reloadV1Pod(kubernetesApiClient,nameSpace,deploymentName);
        //查看是否启动
        V1ContainerStatus status = v1Pod.getStatus().getContainerStatuses().get(0);
        if (status.getReady()) {
            log.info("验证" + deploymentName+ "成功!");
            return null;
        }
        String reason = v1Pod.getStatus().getReason() == null ? "" : v1Pod.getStatus().getReason();
        String message = v1Pod.getStatus().getMessage() == null ? "" : v1Pod.getStatus().getMessage();
        //执行了 但是遇到问题了
        if ("Pending".equals(v1Pod.getStatus().getPhase())) {
            V1ContainerStatus status1 = v1Pod.getStatus().getContainerStatuses().get(0);
            String errMsg = status1.getState().getWaiting().getMessage();
            String errReason = status1.getState().getWaiting().getReason();
            if (StringHandlerUtil.isNotBlank(errMsg)
                    && StringHandlerUtil.isNotBlank(errReason)) {
                if (MaasConst.K8S_POD_ERRIMAGEPULL.equals(errReason) || MaasConst.K8S_POD_IMAGEPULLBACKOFF.equals(errReason)){
                    log.info(errReason);
                    return getFailResult("验证镜像仓库信息异常!初始化配置失败!请检查镜像仓库的地址、项目名、镜像、账号密码/token是否有效可用");
                }
                return getFailResult(TaskConst.IMAGEERROR + "2.应用启动异常!\r\n错误信息:\r\n" + errReason + "\r\n" + errMsg);
            }
        }
        //刚拉起 还没开始启动
        if ("Pending".equals(v1Pod.getStatus().getPhase()) && StringUtils.isEmpty(reason) && StringUtils.isEmpty(message)) {
            //重新提交任务 继续等待
            //最多等待2分钟
            int pendingCount = 0;
            while (pendingCount<60){
                pods = getPodByDeployment(kubernetesApiClient,nameSpace, deploymentName);
                //判断是否已经从Pending变成Running
                if (pods.getItems().size() == 1 && "Running".equals(pods.getItems().get(0).getStatus().getPhase())){
                    v1Pod = pods.getItems().get(0);
                    break;
                }
                status = pods.getItems().get(0).getStatus().getContainerStatuses().get(0);
                if (status!=null && status.getState()!=null && status.getState().getWaiting()!=null){
                    if (MaasConst.K8S_POD_ERRIMAGEPULL.equals(status.getState().getWaiting().getReason())
                            || MaasConst.K8S_POD_IMAGEPULLBACKOFF.equals(status.getState().getWaiting().getReason())
                            || MaasConst.K8S_POD_INVALIDIMAGENAME.equals(status.getState().getWaiting().getReason())){
                        log.info(status.getState().getWaiting().getReason());
                        return getFailResult("验证镜像仓库信息异常!初始化配置失败!请检查镜像仓库的地址、项目名、镜像、账号密码/token是否有效可用");
                    }
                }
                //在上面刷新的时候 可能已经变运行了
                if (status.getReady()) {
                    //增加双重认证
                    pods = getPodByDeployment(kubernetesApiClient,nameSpace, deploymentName);

                    log.info("验证" + deploymentName+ "成功!");
                    return null;
                }
                log.info("应用"+deploymentName+"依然处于读取的状态 状态名:"+status.getState().getWaiting().getReason());
                try {
                    TimeUnit.SECONDS.sleep(waitCheckS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pendingCount++;
            }
        }
        //应用未启动 可能有问题
        if (!status.getStarted()) {
            //刷新状态
            status = reloadPodStatus(kubernetesApiClient,nameSpace,deploymentName);
            V1ContainerStateWaiting containerCreateWaiting = status.getState().getWaiting();
            //等待应用创建 判断是否在启动中
            log.info("开始检查应用启动状态!");
            if (status.getState()!=null && status.getState().getWaiting()!=null && StringUtils.isNotBlank(status.getState().getWaiting().getReason())){
                String errReason = status.getState().getWaiting().getReason();
                //进入等待状态 代表有可能遇到问题了 判断报错
                if (MaasConst.K8S_POD_ERRIMAGEPULL.equals(errReason) || MaasConst.K8S_POD_IMAGEPULLBACKOFF.equals(errReason) || MaasConst.K8S_POD_INVALIDIMAGENAME.equals(status.getState().getWaiting().getReason())){
                    return getFailResult("验证镜像仓库信息异常!初始化配置失败!请检查镜像仓库的地址、项目名、账号密码/token是否有效可用");
                }
            }
            //如果是应用创建中 则需要一直等待
            int waitCreatingCount = 0;
            while (Objects.nonNull(containerCreateWaiting) && "ContainerCreating".equals(containerCreateWaiting.getReason())){
                log.info("应用"+deploymentName+"创建中....等待");
                try {
                    TimeUnit.SECONDS.sleep(checkInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //刷新状态
                status = reloadPodStatus(kubernetesApiClient,nameSpace,deploymentName);
                containerCreateWaiting = status.getState().getWaiting();
                //在上面刷新的时候 可能已经变运行了
                if (status.getReady()) {
                    log.info("验证" + deploymentName+ "成功!");
                    return null;
                }
                //超过了最大等待时间 返回失败
                if(waitCreatingCount>KubernetesConstants.DEFAULT_CREATING_WAIT_COUNT){
                    int waitAllTime = KubernetesConstants.DEFAULT_CREATING_WAIT_COUNT*KubernetesConstants.DEFAULT_CREATING_WAIT_SECONDS;
                    return getFailResult("已等待"+waitAllTime+"S,已超时,但应用未正常启动!请确认是否是等待时间不够或应用太大");
                }
                waitCreatingCount++;
            }
            //刷新状态
            status = reloadPodStatus(kubernetesApiClient,nameSpace,deploymentName);
            //在上面刷新的时候 可能已经变运行了
            if (status.getReady()) {
                log.info("验证" + deploymentName+ "成功!");
                return null;
            }
            //如果重启次数==1 再获取一次状态 确认状态
            if(status.getRestartCount() == 1){
                pods = getPodByDeployment(kubernetesApiClient,nameSpace, deploymentName);
                v1Pod = pods.getItems().get(0);
                status = v1Pod.getStatus().getContainerStatuses().get(0);
                //在上面刷新的时候 可能已经变运行了
                if (status.getReady()) {
                    log.info("验证" + deploymentName+ "成功!");
                    return null;
                }
            }
            //刷新状态
            status = reloadPodStatus(kubernetesApiClient,nameSpace,deploymentName);
            //在上面刷新的时候 可能已经变运行了
            if (status.getReady()) {
                log.info("验证" + deploymentName+ "成功!");
                return null;
            }
            if ("Pending".equals(v1Pod.getStatus().getPhase())) {
                V1ContainerStatus status1 = v1Pod.getStatus().getContainerStatuses().get(0);
                String errMsg = status1.getState().getWaiting().getMessage();
                String errReason = status1.getState().getWaiting().getReason();
                if (StringHandlerUtil.isNotBlank(errMsg)
                        && StringHandlerUtil.isNotBlank(errReason)) {
                    return getFailResult(TaskConst.IMAGEERROR + "2.应用启动异常!\r\n错误信息:\r\n" + errReason + "\r\n" + errMsg);
                }
            }
            //刷新状态
            status = reloadPodStatus(kubernetesApiClient,nameSpace,deploymentName);
            //在上面刷新的时候 可能已经变运行了
            if (status.getReady()) {
                log.info("验证" + deploymentName+ "成功!");
                return null;
            }
            //在重启/多次刷新后 重新验证应用pendding等待状态
            V1ContainerStateTerminated terminated = status.getState().getTerminated();
            if(Objects.nonNull(terminated)){
                StringBuilder builder = new StringBuilder();
                builder.append(terminated.getReason()+":应用启动于"+terminated.getStartedAt()+",结束于"+terminated.getFinishedAt());
                builder.append(",退出编码:"+terminated.getExitCode()+"\t"+terminated.getMessage());
                builder.append("\n启动日志:"+readPodAllLog(kubernetesApiClient,nameSpace,v1Pod.getMetadata().getName(),99999));
                //获取应用区占用的信息
                builder.append("\n应用工作区信息:"+kubernetesErrorHandler.getResetErrorMessage(kubernetesApiClient,nameSpace,deploymentName));
                return getFailResult(builder.toString());
            }
            //刷新状态
            status = reloadPodStatus(kubernetesApiClient,nameSpace,deploymentName);
            //在上面刷新的时候 可能已经变运行了
            if (status.getReady()) {
                log.info("验证" + deploymentName+ "成功!");
                return null;
            }
            V1ContainerStateWaiting waiting = status.getState().getWaiting();
            if(Objects.nonNull(waiting) && Objects.nonNull(waiting.getReason())){
                String waitReason = StringUtils.isNotBlank(waiting.getReason())?waiting.getReason():"";
                String failLog = "无法读取到失败日志!应用未启动!";
                try {
                    failLog = readPodAllLog(kubernetesApiClient,nameSpace,v1Pod.getMetadata().getName(),99999);
                }catch (Exception e){
                    e.getMessage();
                }
                if(StringUtils.isEmpty(failLog)){
                    failLog="";
                }
                if (status.getLastState()!= null && status.getLastState().getTerminated()!=null){
                    String lastMsg = status.getLastState().getTerminated().getMessage();
                    log.error("启动服务失败:"+lastMsg);
                    if (StringUtils.isNotBlank(lastMsg) && lastMsg.contains("nvidia-container-cli: initialization error")){
                        return getFailResult("您的K8S服务器Cuda环境异常.请确保Cuda驱动相关正常");
                    }
                }
                return getFailResult("启动失败!"+waitReason+"\r\n"+ TaskConst.IMAGEERROR + "\r\n2.应用启动异常!\r\n应用日志信息:\r\n" + failLog);
            }
            //刷新状态
            status = reloadPodStatus(kubernetesApiClient,nameSpace,deploymentName);
            //在上面刷新的时候 可能已经变运行了
            if (status.getReady()) {
                log.info("验证" + deploymentName+ "成功!");
                return null;
            }
            if(status.getRestartCount()>1){
                String failMsg = "应用发布期间出现强制重启的状态!请尝试增加资源大小\r\n" + reason + "\r\n" + message;
                return getFailResult(failMsg+"\r\n应用启动就绪!请通过查看日志功能确认应用启动进度!\n注意:CPU资源分配较少的情况下,启动时间会较长,请耐心等待. 比如一个后端工程分配了1核,预估启动完成需要9分钟.2核为4分钟.");
            }
            //刷新状态
            status = reloadPodStatus(kubernetesApiClient,nameSpace,deploymentName);
            //在上面刷新的时候 可能已经变运行了
            if (status.getReady()) {
                log.info("验证" + deploymentName+ "成功!");
                return null;
            }
            String failMsg = "应用未能正常启动!请联系系统管理员!\r\n" + reason + "\r\n" + message;
            return getFailResult(failMsg);
        }
        //直接成功的
        if (status.getReady()) {
            log.info("验证" + deploymentName+ "成功!");
        }
        return null;
    }

    @Override
    public String updateDeployment(KubernetesApiClient kubernetesApiClient, String nameSpace, V1Deployment deployment) throws ApiException {
        // 执行更新
        V1Deployment updatedDeployment = kubernetesApiClient.getAppsV1Api().replaceNamespacedDeployment(
                deployment.getMetadata().getName(),
                nameSpace,
                deployment,
                null,
                null,
                null
        );
        return Yaml.dump(updatedDeployment);
    }

    /**
     * 重新加载当前应用在应用里面的基础信息
     * @param kubernetesApiClient k8s客户端
     * @param nameSpace 命名空间
     * @param deploymentName 应用名字
     * @return 获得的应用信息
     * @throws ApiException
     */
    private V1Pod reloadV1Pod(KubernetesApiClient kubernetesApiClient, String nameSpace, String deploymentName) throws ApiException {
        V1PodList pods = getPodByDeployment(kubernetesApiClient,nameSpace, deploymentName);
        V1Pod  v1Pod = pods.getItems().get(0);
        return v1Pod;
    }

    /**
     * 等待K8s的应用刷新完成状态数据
     * @param kubernetesApiClient k8s客户端
     * @param nameSpace 命名空间
     * @param deploymentName 应用名字
     * @throws ApiException
     */
    private List<V1ContainerStatus> waitK8sAppStatus(KubernetesApiClient kubernetesApiClient, String nameSpace, String deploymentName,Integer checkInterval) throws Exception {
        V1PodList pods = getPodByDeployment(kubernetesApiClient, nameSpace, deploymentName);
        V1Pod v1Pod = pods.getItems().get(0);
        if(v1Pod == null){
            log.info("v1Pod是空的"+v1Pod);
        }
        if(v1Pod.getStatus() == null){
            log.info("getStatus()是空的"+v1Pod.getStatus());
        }
        List<V1ContainerStatus> containerStatuses = v1Pod.getStatus().getContainerStatuses();
        if(containerStatuses == null){
            log.info("v1Pod.getStatus().getContainerStatuses()是空的"+containerStatuses);
        }
        int statusWaitCount = 0;
        while (containerStatuses == null || containerStatuses.size() == 0){
            log.info(deploymentName+"等待应用更新有效的状态");
            pods = getPodByDeployment(kubernetesApiClient, nameSpace, deploymentName);
            containerStatuses = pods.getItems().get(0).getStatus().getContainerStatuses();
            try {
                TimeUnit.SECONDS.sleep(checkInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info(deploymentName+"应用状态数据当前为:"+containerStatuses);
            statusWaitCount++;
            if(statusWaitCount>KubernetesConstants.DEFAULT_APP_STATUS_WAIT_COUNT){
                throw new Exception(deploymentName+"等待应用状态数据更新超时!");
            }
        }
        log.info(deploymentName+"应用的状态已正常");
        return containerStatuses;
    }

    /**
     * 获取失败的结果
     * @param result
     * @return
     */
    private String getFailResult(String result){
        log.error(result);
        return result;
    }

    /**
     * 重新刷新状态数据
     * @param kubernetesApiClient
     * @param nameSpace
     * @param deploymentName
     * @return
     * @throws ApiException
     */
    private V1ContainerStatus reloadPodStatus(KubernetesApiClient kubernetesApiClient,String nameSpace,String deploymentName) throws ApiException {
        V1PodList pods = getPodByDeployment(kubernetesApiClient, nameSpace, deploymentName);
        V1ContainerStatus status = pods.getItems().get(0).getStatus().getContainerStatuses().get(0);
        return status;
    }

}
