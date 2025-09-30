package com.badou.project.kubernetes.client;

import com.badou.brms.attach.AttachConfig;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.attach.service.IAttachService;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.exception.DataValidException;
import com.badou.project.kubernetes.handler.KubernetesNodeHandler;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.service.IK8sServerConfService;
import com.badou.tools.common.util.SpringHelper;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.util.Config;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

/**
 * @ClassName KuberneteApiClientUtil
 * @Description k8s-api客户端工具类
 * @date 2022/9/14 10:40
 * @Version 1.0
 */
public class KubernetesApiClient {

    private static Logger logger = LoggerFactory.getLogger(KubernetesApiClient.class);
    //客户端连接类 包含连接信息等 优先初始化
    private ApiClient apiClient = null;
    //k8s执行客户端 专门为执行而用
    private KubernetesClient execClient = null;
    //api操作类 包含各种客户端的操作 需要等客户端配置完成后再创建
    private CoreV1Api coreV1Api = null;
    //app顶层操作类
    private AppsV1Api appsV1Api = null;
    private String HTTP_HEADER = "https";
    private ExtensionsV1beta1Api v1beta1Api = null;
    private NetworkingV1Api networkingV1Api = null;
    private K8sServerConfEntity k8sServerConfEntity;

    public KubernetesApiClient(K8sServerConfEntity k8sServerConfEntity) throws Exception {
        this.k8sServerConfEntity = k8sServerConfEntity;
        String authType = null;
        if(0 == k8sServerConfEntity.getAuthType()){
            authType = "configFile";
        }else if(1 == k8sServerConfEntity.getAuthType()){
            authType = "Token";
        }else {
            throw new Exception("无效的k8s客户端认证类型");
        }
        initClient(k8sServerConfEntity);
    }

    public KubernetesApiClient(ApiClient apiClient) throws Exception {
        this.apiClient = apiClient;
        coreV1Api = new CoreV1Api(apiClient);
        appsV1Api = new AppsV1Api(apiClient);
        v1beta1Api = new ExtensionsV1beta1Api(apiClient);
        networkingV1Api = new NetworkingV1Api(apiClient);
    }

    private ApiClient initClient(K8sServerConfEntity k8sServerConfEntity) throws DataValidException {
        if(apiClient != null){
            return apiClient;
        }
        try {
            //从配置文件读取
            String customeConfPath = k8sServerConfEntity.getAuthContent();
            if(customeConfPath!=null){
                String allCustomPath = null;
                InetAddress localhost = InetAddress.getLoopbackAddress();
                //本地路径
//                if (localhost.getHostAddress().contains("127.0.0.1") || localhost.getHostAddress().contains("localhost")){
//                    allCustomPath = KubernetesApiClient.class.getResource("/").getPath()+"kubernetes"+File.separator+k8sServerConfEntity.getCode()+"-k8s-admin.conf";
//                }else {
                    //20250709 k8s客户端配置改成在线的
                    ArrayList<Attach> attachs = (ArrayList)SpringHelper.getBean(IAttachService.class).getAttachs(new String[]{customeConfPath});
                    if (attachs.size() == 0 || attachs.get(0) == null){
                        throw new DataValidException("未配置服务器授权信息!请联系管理员!");
                    }
                    allCustomPath = AttachConfig.getInstance().getAttachSavePath() + "/" + attachs.get(0).getFileName();
//                }
                File file = new File(allCustomPath);
                if(file.exists()){
                    //更多连接方式参考 https://blog.csdn.net/weixin_43784341/article/details/121288224
                    apiClient = Config.fromConfig(allCustomPath);
                    execClient = new DefaultKubernetesClient(io.fabric8.kubernetes.client.Config.fromKubeconfig(FileUtils.readFileToString(new File(allCustomPath), StandardCharsets.UTF_8.toString())));
                    Configuration.setDefaultApiClient(apiClient);
                }else {
                    throw new Exception("获取不到k8s客户端配置文件:"+allCustomPath);
                }
            }
            coreV1Api = new CoreV1Api(apiClient);
            appsV1Api = new AppsV1Api(apiClient);
            v1beta1Api = new ExtensionsV1beta1Api(apiClient);
            networkingV1Api = new NetworkingV1Api(apiClient);
            logger.info("初始化"+apiClient.getBasePath()+" k8s连接工具成功!");

            String address = apiClient.getBasePath().replace("https://", "").replace("http://", "");
            String[] addressSplit = address.split(":");
            if (addressSplit.length != 2){
                throw new DataValidException("初始化异常.K8S地址非法");
            }
            //获取节点名称
            KubernetesNodeHandler kubernetesNodeHandler = SpringHelper.getBean(KubernetesNodeHandler.class);
            KubernetesApiClient kubernetesApiClient = new KubernetesApiClient(apiClient);
            V1Node masterNode = kubernetesNodeHandler.getMasterNode(kubernetesApiClient);
            if (masterNode == null){
                throw new DataValidException("该K8S未设定主节点!");
            }
            //判断是http还是https
            String realReqMethod = "http://";
            if (apiClient.getBasePath().contains("https//")){
                realReqMethod = "https://";
            }

            IK8sServerConfService k8sServerConfService = SpringHelper.getBean(IK8sServerConfService.class);
            k8sServerConfEntity.setAddress(addressSplit[0]);
            k8sServerConfEntity.setCode(masterNode.getMetadata().getName());
            k8sServerConfEntity.setPort(Integer.parseInt(addressSplit[1]));
            k8sServerConfEntity.setGpuMsgUrl(realReqMethod+k8sServerConfEntity.getAddress()+":31899");
            if (LogonCertificateHolder.getLogonCertificate() == null){
                LogonCertificate logonCertificate = new LogonCertificate();
                logonCertificate.setUserId("auto-init");
                logonCertificate.setUserName("auto-init");
                LogonCertificateHolder.setLogonCertificate(logonCertificate);
            }
            k8sServerConfService.update(k8sServerConfEntity);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("初始化k8s连接工具失败!"+e.getMessage()==null?"":e.getMessage());
        }catch (DataValidException e){
            throw new DataValidException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public K8sServerConfEntity getServer() {
        return k8sServerConfEntity;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public CoreV1Api getCoreV1Api() {
        return coreV1Api;
    }

    public AppsV1Api getAppsV1Api() {
        return appsV1Api;
    }

    public String getHTTP_HEADER() {
        return HTTP_HEADER;
    }

    public ExtensionsV1beta1Api getV1beta1Api() {
        return v1beta1Api;
    }

    public NetworkingV1Api getNetworkingV1Api() {
        return networkingV1Api;
    }

    public KubernetesClient getExecClient() {
        return execClient;
    }

    public void setExecClient(KubernetesClient execClient) {
        this.execClient = execClient;
    }
}
