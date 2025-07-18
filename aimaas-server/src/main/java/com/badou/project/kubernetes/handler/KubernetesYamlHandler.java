package com.badou.project.kubernetes.handler;

import com.badou.project.exception.DataEmptyException;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;

import java.io.IOException;

/**
 * @ClassName KubernetesYamlHandler
 * @Description k8s 批量YAML读取执行服务操作类
 * @date 2025/02/11 09:47
 * @Version 1.0
 */
public interface KubernetesYamlHandler {
    /**
     * 启动模型下载-Nginx服务 启动后 下载的路径是类似下面这种
     * curl -k https://127.0.0.1:32767/BAAI/bge-large-zh-v1.5/config.json -o /home/servers/downloadnginx//config.json1
     */
    void startDownLoadNginx(KubernetesApiClient kubernetesApiClient) throws IOException, DataEmptyException;

    /**
     * 根据配置 创建badou用户的模型下载任务
     * @param kubernetesApiClient K8S客户端
     * @param path ModelScope文根
     * @param modelSyncTaskEntity 模型同步任务实体
     * @param imageName 执行下载的镜像名称
     * @throws DataEmptyException
     */
    void startPythonModelScope(KubernetesApiClient kubernetesApiClient, String path, ModelSyncTaskEntity modelSyncTaskEntity, String imageName) throws DataEmptyException;
}
