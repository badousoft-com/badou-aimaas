package com.badou.project.maas.common;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.cache.util.JedisUtil;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataErrorException;
import com.badou.project.exception.DataValidException;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.client.KubernetesApiClientFactory;
import com.badou.project.kubernetes.config.KubernetesConfig;
import com.badou.project.kubernetes.handler.*;
import com.badou.project.kubernetes.util.KubernetesConfigUtil;
import com.badou.project.kubernetes.vo.DeployAppVo;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.registryaddress.model.RegistryAddressEntity;
import com.badou.project.maas.registryaddress.service.IRegistryAddressService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.service.IK8sServerConfService;
import com.badou.tools.common.util.SpringHelper;
import com.badou.tools.common.util.StringUtils;
import com.badou.tools.redis.JedisConfig;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

@Component
@Slf4j
public class FileControllerService implements CommandLineRunner {
    //命名空间
    public static final String nameSpace = "ai-maas-file";
    // 服务名称
    public static final String deploymentName = "ai-maas-file";
    @Autowired
    private KubernetesExecHandler kubernetesExecHandler;
    @Autowired
    private ITuningModelnService tuningModelnService;
    @Autowired
    private KubernetesNameSpaceHandler kubernetesNameSpaceHandler;
    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;
    @Autowired
    private KubernetesYamlHandler kubernetesYamlHandler;
    @Autowired
    private IK8sServerConfService k8sServerConfService;
    @Autowired
    private JedisUtil jedisUtil;
//    private static final Map<String,KubernetesApiClient> k8sClientCache = new HashMap<>();
    @Autowired
    private IRegistryAddressService registryAddressService;
    @Autowired
    private KubernetesServiceHandler kubernetesServiceHandler;

    //认证后端地址
    private String oauthUrl;

    @Value("${badou.oauth2.auth-center-url}")
    public void setOauthUrl(String oauthUrl) {
        this.oauthUrl = oauthUrl;
    }

    /**
     * 检擦服务器的硬盘状态是否已经大于安全范围 如果大于 返回true
     * @param kubernetesApiClient
     * @return
     */
    public boolean checkServerDiskLimit(KubernetesApiClient kubernetesApiClient) throws Exception {
        List<V1Pod> nodePods = kubernetesPodHandler.getNodePods(kubernetesApiClient, nameSpace, kubernetesApiClient.getServer().getCode());
        if (nodePods == null || nodePods.size() == 0){
            throw new DataEmptyException("未初始化文件服务.请联系管理员!");
        }
        for(V1Pod v1Pod:nodePods){
            boolean b = kubernetesPodHandler.checkPodFail(v1Pod);
            if (b){
                throw new DataValidException("节点服务连接拒绝");
            }
            //查询磁盘状态 如果
            JSONObject jsonObject = kubernetesExecHandler.execCommandOnce(v1Pod.getMetadata().getName(), nameSpace, kubernetesApiClient, new String[] {
                    "/bin/sh",
                    "-c",
                    "df -h | awk 'NR>1 {print $0}' | sort -hr -k2 | head -n 1"
            });
            String msg = jsonObject.getString("msg");

            // 使用限制分割次数的正则表达式
            String[] cols = msg.trim().split("\\s+", 6);  // 关键修改点
            if (cols.length < 6) {
                throw new DataValidException("格式异常: " + msg);
            }
            int usedPer = Integer.parseInt(cols[4].replace("%",""));
            log.info("当前服务器磁盘使用率:"+usedPer);
            if (usedPer > MaasConst.SERVER_SAFE_DISK){
                return true;
            }
        }
        return false;
    }

    public void checkService() throws Exception {
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("FLG_DELETED",0,null, QueryOperSymbolEnum.eq));
        List<K8sServerConfEntity> k8sServerConfEntities = k8sServerConfService.find(queryCriterion);

        InetAddress localhost = InetAddress.getLoopbackAddress();
        log.info("当前执行服务器IP: " + localhost.getHostAddress());

        log.info("开始初始化K8S集群服务器-文件服务!处理数量:"+k8sServerConfEntities.size());
        for(K8sServerConfEntity k8sServerConfEntity:k8sServerConfEntities){
            //第一次初始化的时候清空key
            String key = k8sServerConfEntity.getAddress()+":"+k8sServerConfEntity.getPort()+k8sServerConfEntity.getAuthType();
            String cacheServer =  jedisUtil.get(key);
            if (StringUtils.isNotBlank(cacheServer)){
                jedisUtil.del(cacheServer);
            }
            if ("localhost".equals(JedisConfig.host) || "127.0.0.1".equals(JedisConfig.host)){
                //20250508 如果是本地 生产服务器 则不检查 因为本地一般连不上生产机
                if (k8sServerConfEntity.getCode().contains("239") || (k8sServerConfEntity.getRemark()!=null && k8sServerConfEntity.getRemark().contains("生产"))){
                    log.info("生产机"+k8sServerConfEntity.getRemark()+"跳过处理");
                    continue;
                }
                log.info("本地服务器 启动本地客户端配置文件");
            }else {
                log.info("远程服务器 启动在线客户端配置文件");
            }
            log.info("初始化服务器"+k8sServerConfEntity.getAddress());
            try {
                initServerServe(k8sServerConfEntity);
            }catch (Exception e){
                e.printStackTrace();
                log.info("初始化失败");
            }
        }

    }

    /**
     * 初始化服务器的服务
     * @param k8sServerConfEntity 服务器信息
     * @throws Exception
     */
    public void initServerServe(K8sServerConfEntity k8sServerConfEntity) throws Exception {
        RegistryAddressEntity defaultRegistryAddress = registryAddressService.getDefaultRegistryAddress();
        if (defaultRegistryAddress == null){
            throw new DataValidException("未配置默认镜像仓库!请联系管理员!");
        }

        KubernetesApiClient defaultClient = KubernetesApiClientFactory.build(k8sServerConfEntity);

        String deploymentName = FileControllerService.deploymentName+"-"+k8sServerConfEntity.getCode();
        V1Namespace v1Namespace = null;
        try {
            //同时也刷新镜像仓库信息
            v1Namespace = kubernetesNameSpaceHandler.createNameSpace(defaultClient,nameSpace,defaultRegistryAddress.getKeyName(),defaultRegistryAddress);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        if(Objects.isNull(v1Namespace)){
            // 如果还是空 代表无法创建命名空间成功!
            throw new Exception("初始化文件服务失败!");
        }
        //20250721 移除下载模型的镜像 取消服务
//        if(Objects.isNull(downloadNginxNamespace)){
//            try {
//                downloadNginxNamespace = kubernetesNameSpaceHandler.createNameSpace(defaultClient,MaasConst.DOWNLOAD_NGINX_NS);
//            } catch (ApiException e) {
//                e.printStackTrace();
//            }
//            if(Objects.isNull(downloadNginxNamespace)){
//                // 如果还是空 代表无法创建命名空间成功!
//                throw new Exception("模型文件下载服务创建失败!");
//            }
//        }
        List<V1Pod> nodePods = kubernetesPodHandler.getNodePods(defaultClient, nameSpace, k8sServerConfEntity.getCode());
        for (V1Pod nodePod : nodePods) {
            if (!MaasConst.K8S_POD_RUNNING.equals(nodePod.getStatus().getPhase())){
                log.warn("服务器"+defaultClient.getApiClient().getBasePath()+"初始化节点文件服务处于失败的状态,信息:"+nodePod.getStatus().getMessage());
                kubernetesPodHandler.deleteDeployment(defaultClient,nameSpace,deploymentName);
                nodePods.clear();
                break;
            }
        }
        //如果存在数据 确认下是不是Running
        if (nodePods.size() == 0){
            DeployAppVo deployAppVo = new DeployAppVo();
            //根据服务器信息 形成挂载路径
            buildK8sPath(deployAppVo,k8sServerConfEntity);
//            deployAppVo.setCommands(new String[]{"/bin/sh","-ce","tail -f /dev/null"});
//            ArrayList<String> args = new ArrayList<>();
//            args.add("while true; do sleep 30; done;");
//            deployAppVo.setArgs(args);
//            deployAppVo.setDnsPolicy("Default");
            deployAppVo.setV1Volumes(deployAppVo.getV1Volumes());
            List<V1EnvVar> initEnvList = new ArrayList<>();
            initEnvList.add(new V1EnvVar().name(MaasConst.NVIDIA_VISIBLE_DEVICES).value("all"));
            deployAppVo.setEnvVarList(initEnvList);
            deployAppVo.setSecretName(defaultRegistryAddress.getKeyName());
            if(StringUtils.isEmpty(deployAppVo.getSecretName())){
                log.info("当前为公开仓库 设置空密钥");
                deployAppVo.setSecretName(KubernetesConfig.getImagePullSecrets());
            }
            deployAppVo.setV1VolumeMounts(deployAppVo.getV1VolumeMounts());
            deployAppVo.setNodeName(k8sServerConfEntity.getCode());
            String imageName = KubernetesConfigUtil.buildImageName(defaultRegistryAddress.getAddress(), defaultRegistryAddress.getProjectName(), "monitor_gpus", "1.0");
            log.info("服务器初始化执行容器..."+imageName);
            //这个容器 需要有unzip和curl命令和 Bash模式
            kubernetesPodHandler.createDeploymentAndDeploy(defaultClient,nameSpace,deploymentName,imageName,1,false,deployAppVo);
            //最多等待启动1分钟
            String initResult = kubernetesPodHandler.checkPodRunning(defaultClient, nameSpace, deploymentName, 1);
            if (StringUtils.isNotEmpty(initResult)){
                throw new DataErrorException(initResult);
            }
            //判断是否存在
            V1ServiceList nowSvc = kubernetesServiceHandler.getServiceByCode(defaultClient, nameSpace, deploymentName);
            if (nowSvc == null || nowSvc.getItems().size() == 0){
                //为服务创建SVC
                kubernetesServiceHandler.createServiceAndDeploy(defaultClient,nameSpace,deploymentName,deploymentName,"TCP",8998,8998,31899,false);
            }
        }else {
            log.info("集群服务器"+k8sServerConfEntity.getCode()+"已存在文件操作服务.K8S客户端验证完成.状态通过√");
        }
        //服务器检查完毕 服务器文件控制服务检查完毕 开始检查文件下载Nginx服务
//        log.info("开始初始化K8S集群服务器-文件下载服务!");
        //检查服务状态 如果有则返回ok 如果不存在则创建
//        V1PodList downLoadNginx = kubernetesPodHandler.getPodByDeployment(defaultClient, MaasConst.DOWNLOAD_NGINX_NS, MaasConst.DOWNLOAD_NGINX_NS);
//        if (downLoadNginx == null || downLoadNginx.getItems().size() == 0){
//            //初始化
//            kubernetesYamlHandler.startDownLoadNginx(defaultClient);
//        }
    }

    public static DeployAppVo buildK8sPath(DeployAppVo deployAppVo,K8sServerConfEntity k8sServerConfEntity){
        List<V1Volume> v1VolumesList = new ArrayList<>();
        //服务器挂载目录 一般用来存储配置文件、训练集等数据
        v1VolumesList.add(new V1Volume().name("volumn-path").hostPath(new V1HostPathVolumeSource().type("DirectoryOrCreate").path(k8sServerConfEntity.getVolumnPath())));
        v1VolumesList.add(new V1Volume().name("cuda-toolkit-dir").hostPath(new V1HostPathVolumeSource().type("DirectoryOrCreate").path(k8sServerConfEntity.getCudaToolkitDir())));
        ArrayList<V1VolumeMount> v1VolumeMounts = new ArrayList<>();
        v1VolumeMounts.add(new V1VolumeMount().name("volumn-path").mountPath(k8sServerConfEntity.getVolumnPath()));
        v1VolumeMounts.add(new V1VolumeMount().name("cuda-toolkit-dir").mountPath(k8sServerConfEntity.getCudaToolkitDir()));
        String[] modelPaths = k8sServerConfEntity.getModelPaths().split(",");
        int n = 0;
        for (String modelPath : modelPaths) {
            //模型挂载目录 只存模型本身
            v1VolumesList.add(new V1Volume().name("model-path"+n).hostPath(new V1HostPathVolumeSource().type("DirectoryOrCreate").path(modelPath)));
            v1VolumeMounts.add(new V1VolumeMount().name("model-path"+n).mountPath(modelPath));
            n++;
        }
        V1Volume[] v1Volumes = v1VolumesList.toArray(new V1Volume[]{});

        deployAppVo.setV1VolumeMounts(v1VolumeMounts);
        deployAppVo.setV1Volumes(v1Volumes);
        return deployAppVo;
    }

    public static KubernetesApiClient getCacheK8sClient(String serverId){
        K8sServerConfEntity k8sCustomServer = SpringHelper.getBean(IK8sServerConfService.class).getK8sCustomServer(serverId);

        try {
            return KubernetesApiClientFactory.build(k8sCustomServer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param path
     * @throws ApiException
     * @throws IOException
     * @throws InterruptedException
     */
    public void createPath(TuningModelnEntity tuningModelnEntity,String path,KubernetesApiClient kubernetesApiClient) throws Exception {
        //20240113 增加根据任务 选择执行的节点 这里的节点选择 要求多node节点并且存在主从节点的关系.当前的k8s客户端必须是主节点
        for (MultipleServersConfig multipleServersConfig : tuningModelnEntity.getMultipleServersConfigs()) {
            String nodeName = multipleServersConfig.getK8sServerConfEntity().getCode();
            List<V1Pod> nodePods = kubernetesPodHandler.getNodePods(kubernetesApiClient, nameSpace, nodeName);
            for (V1Pod nodePod : nodePods) {
                if (tuningModelnEntity.getMultipleServersConfigs().size() == 1){
                    log.info("单节点任务createPath:"+nodePod.getMetadata().getName());
                }else {
                    log.info("多节点任务createPath:"+nodePod.getMetadata().getName());
                }
                //这里会返回多个.要筛选
                JSONObject result = kubernetesExecHandler.execCommandOnce(nodePod.getMetadata().getName(), nameSpace, kubernetesApiClient,
                        new String[]{"mkdir",path,"-p"});
            }
        }
    }

    /**
     * 执行文件服务命令check
     * @param k8sServerConfEntity 服务器实体
     * @param execCommand 执行命令
     * @return 返回内容
     */
    public JSONObject execModelCommand(K8sServerConfEntity k8sServerConfEntity,String[] execCommand){
        //根据模型的路径计算模型文件大小
        KubernetesApiClient kubernetesApiClient = null;
        try {
            kubernetesApiClient = KubernetesApiClientFactory.build(k8sServerConfEntity);
            List<V1Pod> filePods = kubernetesPodHandler.getPodByLabelApp(kubernetesApiClient, FileControllerService.deploymentName, FileControllerService.deploymentName + "-" + k8sServerConfEntity.getCode());
            if (filePods.size()!=1){
                throw new DataValidException("识别仓库信息异常!请联系管理员!");
            }
            V1Pod v1Pod = filePods.get(0);
            JSONObject jsonObject = kubernetesExecHandler.execCommandOnce(v1Pod.getMetadata().getName(), FileControllerService.nameSpace, kubernetesApiClient,execCommand);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject checkFileExist(MultipleServersConfig multipleServersConfig,TuningModelnEntity tuningModelnEntity,String filePath,KubernetesApiClient kubernetesApiClient) throws Exception {
        //20240113 增加根据任务 选择执行的节点 这里的节点选择 要求多node节点并且存在主从节点的关系.当前的k8s客户端必须是主节点
        String nodeName = multipleServersConfig.getK8sServerConfEntity().getCode();
        List<V1Pod> nodePods = kubernetesPodHandler.getNodePods(kubernetesApiClient, nameSpace, nodeName);
        for (V1Pod nodePod : nodePods) {
            if (tuningModelnEntity.getMultipleServersConfigs().size() == 1){
                log.info("单节点任务createPath:"+nodePod.getMetadata().getName());
            }else {
                log.info("多节点任务createPath:"+nodePod.getMetadata().getName());
            }
            //这里会返回多个.要筛选
            JSONObject jsonObject = kubernetesExecHandler.execCommandOnce(nodePod.getMetadata().getName(), nameSpace, kubernetesApiClient,
                    new String[]{"ls", filePath});
            return jsonObject;
        }
        return null;
    }


    public static KubernetesApiClient getDefaultClient() throws Exception {
        IK8sServerConfService ik8sServerConfService = SpringHelper.getBean(IK8sServerConfService.class);
        K8sServerConfEntity defaultK8sServer = ik8sServerConfService.getDefaultK8sServer();
        if(Objects.isNull(defaultK8sServer)){
            throw new Exception("未配置默认处理服务器!请联系管理员!");
        }
        KubernetesApiClient kubernetesApiClient = KubernetesApiClientFactory.build(defaultK8sServer);
        return kubernetesApiClient;
    }

    public static KubernetesApiClient getCustomClient(TuningModelnEntity tuningModelnEntity) throws Exception {
        return getCustomClient(SpringHelper.getBean(ITuningModelnService.class).getServerId(tuningModelnEntity));
    }

    public static KubernetesApiClient getCustomClient(String serverId) throws Exception {
        IK8sServerConfService ik8sServerConfService = SpringHelper.getBean(IK8sServerConfService.class);

        //20250326 增加操作子节点时候 切换为主节点的k8s操作客户端
//        if (kubernetesApiClient1!=null && StringUtils.isNoneBlank(kubernetesApiClient1.getServer().getParentId())){
//            String parentId = kubernetesApiClient1.getServer().getParentId();
//            String tipMsgPreFix = "子节点"+kubernetesApiClient1.getServer().getCode()+"不拥有节点操作能力.";
//            kubernetesApiClient1 = k8sClientCache.get(parentId);
//            //如果父节点不存在则初始化
//            if (kubernetesApiClient1 == null){
//                K8sServerConfEntity k8sCustomServer = ik8sServerConfService.getK8sCustomServer(parentId);
//                if(Objects.isNull(k8sCustomServer)){
//                    throw new Exception("无效的服务器!请联系管理员!");
//                }
//                kubernetesApiClient1 = KubernetesApiClientFactory.build(k8sCustomServer);
//                k8sClientCache.put(parentId,kubernetesApiClient1);
//            }
//            log.info(tipMsgPreFix+"切换为主节点操作"+kubernetesApiClient1.getServer().getCode());
//        }

        //20250116 增加master节点的支持
//        if (serverId.contains(",") && kubernetesApiClient1 == null){
//            String[] servers = serverId.split(",");
//            for (String server:servers){
//                K8sServerConfEntity k8sCustomServer = ik8sServerConfService.getK8sCustomServer(server);
//                if (GlobalConsts.ZERO.equals(k8sCustomServer.getLevelSort())){
//                    KubernetesApiClient kubernetesApiClient = KubernetesApiClientFactory.build(k8sCustomServer);
//                    k8sClientCache.put(serverId,kubernetesApiClient);
//                    return kubernetesApiClient;
//                }
//            }
//        }
        K8sServerConfEntity k8sCustomServer = ik8sServerConfService.getK8sCustomServer(serverId);
        if(Objects.isNull(k8sCustomServer)){
            throw new Exception("无效的服务器!请联系管理员!");
        }
        KubernetesApiClient kubernetesApiClient = KubernetesApiClientFactory.build(k8sCustomServer);
//        k8sClientCache.put(serverId,kubernetesApiClient);
//        //20250326 增加操作子节点时候 切换为主节点的k8s操作客户端
//        if (kubernetesApiClient1!=null && StringUtils.isNoneBlank(kubernetesApiClient1.getServer().getParentId())){
//            kubernetesApiClient = k8sClientCache.get(kubernetesApiClient1.getServer().getParentId());
//            log.info("子节点切换为"+kubernetesApiClient1.getServer().getCode());
//        }
        return kubernetesApiClient;
    }

    /**
     * 容器内部下载文件
     * @param attachId 文件ID用于从服务器下载文件
     * @param allFilePath 文件全路径一般包含路径名 比如把·a.json保存到/home/aom下 则当前参数写 /home/aom/a.json
     * @return
     * @throws ApiException
     * @throws IOException
     * @throws InterruptedException
     */
    public JSONObject downFile(TuningModelnEntity tuningModelnEntity,String attachId,String allFilePath,KubernetesApiClient kubernetesApiClient,String url) throws Exception {
        //20240113 增加根据任务 选择执行的节点 这里的节点选择 要求多node节点并且存在主从节点的关系.当前的k8s客户端必须是主节点
        for (MultipleServersConfig multipleServersConfig : tuningModelnEntity.getMultipleServersConfigs()) {
            String nodeName = multipleServersConfig.getK8sServerConfEntity().getCode();
            List<V1Pod> nodePods = kubernetesPodHandler.getNodePods(kubernetesApiClient, nameSpace, nodeName);
            for (V1Pod nodePod : nodePods) {
                if (tuningModelnEntity.getMultipleServersConfigs().size() == 1){
                    log.info("单节点任务createPath:"+nodePod.getMetadata().getName());
                }else {
                    log.info("多节点任务createPath:"+nodePod.getMetadata().getName());
                }
                //这里会返回多个.要筛选
                //http://extranet.badousoft.com:32080/bddeplpoy_platform/attach/action/attachsupport/downloadAttach.do?attachId=8a74802389fd8e790189fda2167f0003
                if (StringUtils.isEmpty(url)){
                    url = "http://extranet.badousoft.com:32080/badouai-maas-platform/";
                }
                // 20241024 lm 修改成永远从测试环境拿配置文件
                String downUrl = url + "attach/action/attachsupport/downloadAttach.do?attachId=" + attachId;
                //如果是本地 则把127.0.0.1或者localhost改成本地IP
                //如果是window本地调试 请确保本地的附件下载路径是E:\develop\project\八斗MaaS平台这种路径 不能是/usr的这种linux路径 因为默认估计是这种
                InetAddress addr = InetAddress.getLocalHost();
                downUrl = downUrl.replace("127.0.0.1",addr.getHostAddress());
                downUrl = downUrl.replace("localhost",addr.getHostAddress());
                //curl --create-dirs http://extranet.badousoft.com:32080/badouai-maas-platform/attach/action/attachsupport/downloadAttach.do?attachId=8a74805a928dd5400192c19e0ff700fc > /home/aimodel/246-k8s-admin.conf/llama3/wtprlhfmodel/template-lora-sft.yaml
                JSONObject result = kubernetesExecHandler.execCommandOnce(nodePod.getMetadata().getName(), nameSpace, kubernetesApiClient,
                        new String[]{"curl","--create-dirs",downUrl, "-o", allFilePath});

                JSONObject fileExist = checkFileExist(multipleServersConfig,tuningModelnEntity,allFilePath, kubernetesApiClient);
                //如果ls查看命令有返回 则代表上传成功
                String msg = fileExist.getString("msg");
                if (msg.contains("No such")) {
                    tuningModelnService.setFailStatus(tuningModelnEntity,"生成训练集失败!请联系管理员!"+msg);
                    return null;
                }
                if (StringUtils.isEmpty(msg)) {
                    tuningModelnService.setFailStatus(tuningModelnEntity,"创建数据集失败!无法生成实际的数据集文件!"+msg);
                    return null;
                }
            }
        }
        JSONObject result = new JSONObject();
        result.put("msg","未初始化文件服务!请联系管理员!");
        result.put("code","未初始化文件服务!请联系管理员!");
        return result;
    }

    /**
     * 容器内部执行命令
     * @param allFilePath 文件全路径一般包含路径名 比如把·a.json保存到/home/aom下 则当前参数写 /home/aom/a.json
     * @throws ApiException
     * @throws IOException
     * @throws InterruptedException
     */
    public JSONObject execCommand(TuningModelnEntity tuningModelnEntity,String[] commands,KubernetesApiClient kubernetesApiClient) throws Exception {
        //20240113 增加根据任务 选择执行的节点 这里的节点选择 要求多node节点并且存在主从节点的关系.当前的k8s客户端必须是主节点
        for (MultipleServersConfig multipleServersConfig : tuningModelnEntity.getMultipleServersConfigs()) {
            String nodeName = multipleServersConfig.getK8sServerConfEntity().getCode();
            List<V1Pod> nodePods = kubernetesPodHandler.getNodePods(kubernetesApiClient, nameSpace, nodeName);
            for (V1Pod nodePod : nodePods) {
                if (tuningModelnEntity.getMultipleServersConfigs().size() == 1){
                    log.info("单节点任务createPath:"+nodePod.getMetadata().getName());
                }else {
                    log.info("多节点任务createPath:"+nodePod.getMetadata().getName());
                }
                JSONObject result = kubernetesExecHandler.execCommandOnce(nodePod.getMetadata().getName(), nameSpace, kubernetesApiClient,commands);
                return result;
            }
        }
        return null;
    }


    /**
     * 容器内部下载文件
     * @param attachId 文件ID用于从服务器下载文件
     * @param allFilePath 文件全路径一般包含路径名 比如把·a.json保存到/home/aom下 则当前参数写 /home/aom/a.json
     * @return
     * @throws ApiException
     * @throws IOException
     * @throws InterruptedException
     */
    public JSONObject downFile(String attachId,String allFilePath,KubernetesApiClient kubernetesApiClient,String url,String replaceFlag) throws Exception {
        V1PodList v1PodList = kubernetesPodHandler.getPodByDeployment(kubernetesApiClient, nameSpace, deploymentName);
        if (JsonResultUtil.arrayOneElement(v1PodList.getItems())) {
            V1Pod v1Pod = v1PodList.getItems().get(0);
            //http://extranet.badousoft.com:32080/bddeplpoy_platform/attach/action/attachsupport/downloadAttach.do?attachId=8a74802389fd8e790189fda2167f0003
            if (StringUtils.isEmpty(url)){
                url = "http://extranet.badousoft.com:32080/badouai-maas-platform/";
            }
            // 20241024 lm 修改成永远从测试环境拿配置文件
            String downUrl = url + "attach/action/attachsupport/downloadAttach.do?attachId=" + attachId;
            //如果是本地 则把127.0.0.1或者localhost改成本地IP
            //如果是window本地调试 请确保本地的附件下载路径是E:\develop\project\八斗MaaS平台这种路径 不能是/usr的这种linux路径 因为默认估计是这种
            InetAddress addr = InetAddress.getLocalHost();
            downUrl = downUrl.replace("127.0.0.1",addr.getHostAddress());
            downUrl = downUrl.replace("localhost",addr.getHostAddress());
            //curl --create-dirs http://extranet.badousoft.com:32080/badouai-maas-platform/attach/action/attachsupport/downloadAttach.do?attachId=8a74805a928dd5400192c19e0ff700fc > /home/aimodel/246-k8s-admin.conf/llama3/wtprlhfmodel/template-lora-sft.yaml
            JSONObject result = kubernetesExecHandler.execCommandOnce(v1Pod.getMetadata().getName(), nameSpace, kubernetesApiClient,
                    new String[]{"curl","--create-dirs",downUrl, "-o", allFilePath});
            return result;
        }
        JSONObject result = new JSONObject();
        result.put("msg","未初始化文件服务!请联系管理员!");
        result.put("code","未初始化文件服务!请联系管理员!");
        return result;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("检查文件管理服务");
        try {
            this.checkService();
            log.info("检查通过");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getOauthUrl() {
        int i = oauthUrl.length()- 1;
        if(!(oauthUrl.charAt(i)+"").equals("/")){
            //不等于则拼接一个/
            this.oauthUrl = this.oauthUrl +"/";
        }
        return oauthUrl;
    }
}
