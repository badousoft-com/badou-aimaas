package com.badou.project.kubernetes.client;

import com.badou.brms.attach.AttachConfig;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.attach.service.IAttachService;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataValidException;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.tools.common.util.SpringHelper;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import io.kubernetes.client.util.Config;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
        initClient(authType,k8sServerConfEntity.getAuthContent(),k8sServerConfEntity.getAddress(),k8sServerConfEntity.getPort());
    }

    private ApiClient initClient(String authType, String authContent, String masterAddress, Integer masterPort){
        if(apiClient != null){
            return apiClient;
        }
        if(JsonResultUtil.isNull(masterAddress,masterPort,authContent,authType)){
            logger.error("获取不到有效的连接认证值");
            return null;
        }
        String url = HTTP_HEADER+"://"+masterAddress+":"+masterPort;
        try {
            if("token".equals(authType)){
                //从Token读取认证信息
                // 配置客户端
                apiClient = Config.fromToken(url, authContent, false);
                // 设置默认 Api 客户端到配置
                Configuration.setDefaultApiClient(apiClient);
            }else if("configFile".equals(authType)){
                //从配置文件读取
                String customeConfPath = authContent;
                if(customeConfPath!=null){
                    //本地路径
//                    String allCustomPath = KubernetesApiClient.class.getResource("/").getPath()+customeConfPath;
                    //20250709 k8s客户端配置改成在线的
                    ArrayList<Attach> attachs = (ArrayList)SpringHelper.getBean(IAttachService.class).getAttachs(new String[]{customeConfPath});
                    if (attachs.size() == 0 || attachs.get(0) == null){
                        throw new DataValidException("未配置服务器授权信息!请联系管理员!");
                    }
                    String allCustomPath = AttachConfig.getInstance().getAttachSavePath() + "/" + attachs.get(0).getFileName();
//                    allCustomPath = "/home/servers/maas/apache-tomcat-8.5.57/run/249-k8s-admin.conf";
                    File file = new File(allCustomPath);
                    if(file.exists()){
                        //更多连接方式参考 https://blog.csdn.net/weixin_43784341/article/details/121288224
                        apiClient = Config.fromConfig(allCustomPath);
                        execClient = new DefaultKubernetesClient(io.fabric8.kubernetes.client.Config.fromKubeconfig(FileUtils.readFileToString(new File(allCustomPath), StandardCharsets.UTF_8.toString())));
                        Configuration.setDefaultApiClient(apiClient);
                    }else {
                        throw new Exception("获取不到k8s客户端配置文件:"+allCustomPath);
                    }
                }else{
                    //读取文件失败则采用默认位置的配置
                    apiClient = Config.defaultClient();
                }
            }else {
                logger.error("初始化k8s连接工具失败!找不到有效的认证类型");
                return null;
            }
            coreV1Api = new CoreV1Api(apiClient);
            appsV1Api = new AppsV1Api(apiClient);
            v1beta1Api = new ExtensionsV1beta1Api(apiClient);
            networkingV1Api = new NetworkingV1Api(apiClient);
            logger.info("token-初始化"+masterAddress+"k8s连接工具成功!");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("初始化k8s连接工具失败!"+e.getMessage()==null?"":e.getMessage());
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
