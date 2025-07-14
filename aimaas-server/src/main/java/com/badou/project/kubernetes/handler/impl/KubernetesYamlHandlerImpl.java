package com.badou.project.kubernetes.handler.impl;

import cn.hutool.core.io.FileUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.config.KubernetesConfig;
import com.badou.project.kubernetes.handler.*;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class KubernetesYamlHandlerImpl implements KubernetesYamlHandler {

    @Override
    public void startDownLoadNginx(KubernetesApiClient kubernetesApiClient) throws IOException, DataEmptyException {
        if (StringUtils.isEmpty(kubernetesApiClient.getServer().getModelPaths())){
            throw new DataEmptyException("未配置模型扫描目录.启动模型下载目录功能失败!");
        }
        String execContent = FileUtil.readString(new File(KubernetesYamlHandlerImpl.class.getResource("/").getPath() + MaasConst.DOWNLOAD_NGINX_PATH), "utf-8");
        execContent = execContent.replace("/data/AIGC/ai-models",kubernetesApiClient.getServer().getModelPaths());

        try {
            //加载YAML文件
            List<Object> objects = Yaml.loadAll(execContent);
            for (Object object : objects) {
                log.info(Yaml.dump(object));
                if (object instanceof V1Secret){
                    kubernetesApiClient.getCoreV1Api().createNamespacedSecret(MaasConst.DOWNLOAD_NGINX_NS,(V1Secret) object,null,null,null);
                }else if(object instanceof V1Service){
                    kubernetesApiClient.getCoreV1Api().createNamespacedService(MaasConst.DOWNLOAD_NGINX_NS,(V1Service)object,null,null,null);
                }else if(object instanceof V1Deployment){
                    V1Deployment deployment = (V1Deployment)object;
                    kubernetesApiClient.getAppsV1Api().createNamespacedDeployment(MaasConst.DOWNLOAD_NGINX_NS,deployment,null,null,null);
                }else if(object instanceof V1ConfigMap){
                    kubernetesApiClient.getCoreV1Api().createNamespacedConfigMap(MaasConst.DOWNLOAD_NGINX_NS,(V1ConfigMap)object,null,null,null);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void startPythonModelScope(KubernetesApiClient kubernetesApiClient, String path, ModelSyncTaskEntity modelSyncTaskEntity,String imageName) throws DataEmptyException {


        if (StringUtils.isEmpty(kubernetesApiClient.getServer().getModelPaths())){
            throw new DataEmptyException("未配置模型扫描目录.启动模型下载目录功能失败!");
        }
        if (StringUtils.isEmpty(path) || path.split("/").length!=2){
            throw new DataEmptyException("未配置有效的执行文根!");
        }
        String execContent = FileUtil.readString(new File(KubernetesYamlHandlerImpl.class.getResource("/").getPath() + MaasConst.DOWN_MODELSCOPE_PATH), "utf-8");
        execContent = execContent.replace("TaskId",modelSyncTaskEntity.getCode());
        execContent = execContent.replace("NameSpace",MaasConst.MODEL_SYNC_NS);
        String totalPath = kubernetesApiClient.getServer().getModelPaths()+"/"+path;
        execContent = execContent.replace("TotalPath",totalPath);
        execContent = execContent.replace("ExecUserId","1000");
        execContent = execContent.replace("ModelPaths",kubernetesApiClient.getServer().getModelPaths());
        execContent = execContent.replace("ModelScopePath",path);
//        execContent = execContent.replace("ImageId","registry.badou/badoullms/python-modelscope:3.10.1");
        execContent = execContent.replace("ImageId",imageName);
        execContent = execContent.replace("imagePullSecretsName", KubernetesConfig.getImagePullSecrets());
        execContent = execContent.replace("ParentPath",kubernetesApiClient.getServer().getModelPaths()+"/"+path.split("/")[0]);

        String nameSpace = MaasConst.MODEL_SYNC_NS;
        try {
            //加载YAML文件
            List<Object> objects = Yaml.loadAll(execContent);
            for (Object object : objects) {
                log.info(Yaml.dump(object));
                if(object instanceof V1Pod){
                    V1Pod pod = (V1Pod)object;
                    pod.getSpec().setRestartPolicy("Never");
                    V1Pod namespacedPod = kubernetesApiClient.getCoreV1Api().createNamespacedPod(nameSpace, (V1Pod) object, null, null, null);
                    log.info(Yaml.dump(namespacedPod));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        try {
//            new KubernetesYamlHandlerImpl().startPythonModelScope(CreateClientUtil.build246(),"iic/CosyVoice2-0.5B");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
