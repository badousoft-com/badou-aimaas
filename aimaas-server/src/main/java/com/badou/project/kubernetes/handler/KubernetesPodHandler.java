package com.badou.project.kubernetes.handler;

import com.badou.project.common.exception.ParamErrorException;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.vo.DeployAppVo;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import io.swagger.models.auth.In;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * k8s pod接口类
 */
public interface KubernetesPodHandler {

    /**
     * 获取一个deployment
     * @param nameSpace 命名空间
     * @param deploymentName 控制器名字
     * @return 获取到的deployment 获取不到返回null
     */
    V1Deployment getOneDeployment(KubernetesApiClient kubernetesApiClient, String nameSpace, String deploymentName) throws ApiException;

    /**
     * 通过参数创建服务
     * @param imageName 镜像名字
     * @param kubernetesApiClient k8s客户端
     * @param gpuCount gpu数量
     * @param nameSpace 命名空间
     * @param appCode 应用编码
     * @return 生成的应用
     */
    V1Pod createPodByParams(String imageName,KubernetesApiClient kubernetesApiClient,int gpuCount,String nameSpace,String appCode,DeployAppVo deployAppVo) throws ApiException;

    /**
     * 创建POD
     * @return
     */
    V1Pod createPod(KubernetesApiClient kubernetesApiClient,V1Pod v1Pod,String nameSpace,String podName) throws ApiException;

    /**
     * 获取Pod列表 查询条件为节点
     * @param kubernetesApiClient k8s客户端
     * @param nameSpace 命名空间
     * @param nodeName 节点名称
     * @return
     */
    List<V1Pod> getNodePods(KubernetesApiClient kubernetesApiClient, String nameSpace, String nodeName) throws ApiException;

    /**
     * 获取一个POD 直接获取的版本 不走deployment
     * @param kubernetesApiClient
     * @param nameSpace
     * @param podName
     * @return
     */
    V1Pod getPod(KubernetesApiClient kubernetesApiClient,String nameSpace,String podName) throws ApiException;

    /**
     * 获取POD 通过APP标签
     */
    List<V1Pod> getPodByLabelApp(KubernetesApiClient kubernetesApiClient,String nameSpace,String app) throws ApiException;


    /**
     * 获取一个Pod
     */
    V1Pod getOnePod(KubernetesApiClient kubernetesApiClient,String podName,String nameSpace) throws ApiException;

    /**
     * 根据命名空间获取Pod
     * @param nameSpace 命名空间名字
     */
    V1PodList getAllPodByNameSpace(KubernetesApiClient kubernetesApiClient,String nameSpace) throws ApiException;

    /**
     * 根据配置参数创建Deployment并部署
     * @param nameSpace 命名空间 例如:default
     * @param appCode 应用名字 例如:dchapp-platform 唯一,重复会覆盖原应用
     * @param imageName 镜像名字 例如: nginx:1.0
     * @param replicas 副本数量 例如: 1
     * @param testMode 是否是测试 例如: true代表是 不部署应用直接返回生成的yaml配置 false代表不是
     * @return 返回执行结果 成功返回结果 失败抛出异常
     */
    String createDeploymentAndDeploy(KubernetesApiClient kubernetesApiClient,String nameSpace, String appCode, String imageName, int replicas, boolean testMode,String secretName,DeployAppVo deployAppVo) throws Exception;

    /**
     * 删除控制器
     * @param nameSpace 工作空间名字
     * @param deployment 控制器名字
     * @return 返回执行生成的yaml字符串
     * @throws ApiException
     */
    String deleteDeployment(KubernetesApiClient kubernetesApiClient,String nameSpace,String deployment) throws ApiException;

    /**
     * 删除容器
     * @param nameSpace 工作空间名字
     * @param podName 容器名字
     * @return 返回执行生成的yaml字符串
     * @throws ApiException
     */
    String deleteOnePod(KubernetesApiClient kubernetesApiClient,String nameSpace,String podName)throws ApiException;

    /**
     * 读取容器全部日志
     * @param nameSpace 工作空间名字
     * @param podName 容器名字
     * @param tailLines 读取多少条
     * @return 返回执行生成的yaml字符串
     * @throws ApiException
     */
    String readPodAllLog(KubernetesApiClient kubernetesApiClient,String nameSpace,String podName,Integer tailLines) throws ApiException, IOException;

    /**
     * 读取容器最新日志
     * @param nameSpace 命名空间
     * @param podName 应用名字
     * @param sinceSeconds 获取多少秒之前的日志 单位是秒
     * @param tailLines 获取多少行
     * @return 获取到的日志
     * @throws ApiException
     */
    String readNewLog(KubernetesApiClient kubernetesApiClient,String nameSpace,String podName,Integer sinceSeconds, Integer tailLines) throws ApiException;

    /**
     * 通过查询控制器获取应用
     * @param kubernetesApiClient k8s客户端
     * @param nameSpace 命名空间
     * @param deploymentName 控制器名字
     * @return
     * @throws ApiException
     */
    V1PodList getPodByDeployment(KubernetesApiClient kubernetesApiClient,String nameSpace,String deploymentName) throws ApiException;

    V1PodList queryPodByApp(KubernetesApiClient kubernetesApiClient,String nameSpace,String deploymentNames) throws ApiException;

    /**
     * 判断当前Pod是否是失败的状态
     * @param v1Pod 当前任务Pod
     * @return 返回true则代表是失败了
     * @throws Exception
     */
    boolean checkPodFail(V1Pod v1Pod) throws Exception;

    /**
     * 检查应用是否是在运行中
     * @param kubernetesApiClient k8s客户端
     * @param nameSpace 命名空间
     * @param deploymentName 控制器名字
     * @return 返回null代表没错误 返回内容代表存在错误
     * @throws ApiException
     */
    String checkPodRunning(KubernetesApiClient kubernetesApiClient,String nameSpace,String deploymentName,Integer checkInterval) throws Exception;

}
