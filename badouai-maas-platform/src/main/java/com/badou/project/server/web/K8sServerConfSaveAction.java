package com.badou.project.server.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataExecFailException;
import com.badou.project.exception.DataValidException;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.client.KubernetesApiClientFactory;
import com.badou.project.kubernetes.handler.KubernetesExecHandler;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.kubernetes.vo.DeployAppVo;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.registryaddress.model.RegistryAddressEntity;
import com.badou.project.maas.registryaddress.service.IRegistryAddressService;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.service.IK8sServerConfService;
import com.badou.project.util.LinuxPathValidator;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author badousoft
 * @created 2025-03-17 14:30:08.365
 *  显卡资源管理 保存实现类
 */
@Controller
public class K8sServerConfSaveAction extends BaseCommonSaveAction {

    @Autowired
    private IK8sServerConfService k8sServerConfService;
    @Autowired
    private FileControllerService fileControllerService;

    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;
    @Autowired
    private KubernetesExecHandler kubernetesExecHandler;
    @Autowired
    private IRegistryAddressService registryAddressService;

    @Override
    protected void exeBeforeSave() throws Exception {
        //进行所有操作之前 先确保已配置了默认的镜像仓库
        RegistryAddressEntity defaultRegistryAddress = registryAddressService.getDefaultRegistryAddress();
        if (defaultRegistryAddress == null){
            throw new DataValidException("未配置默认镜像仓库!请前往资源管理->镜像仓库信息 配置");
        }

        //判断用户提供的模型路径和挂载路径是否满足linux路径要求
        if (!JsonResultUtil.arrayOneElement(this.getCustForm().getDetails().get("volumnPath"))
                || !JsonResultUtil.arrayOneElement(this.getCustForm().getDetails().get("modelPaths"))){
            throw new DataEmptyException("模型路径和挂载路径为必填项!");
        }
        String volumnPathResult = LinuxPathValidator.validate(this.getCustForm().getDetails().get("volumnPath")[0]);
        if (StringUtils.isNotBlank(volumnPathResult)){
            throw new DataValidException("服务器文件挂载路径"+volumnPathResult);
        }

        String modelPathsResult = LinuxPathValidator.validate(this.getCustForm().getDetails().get("modelPaths")[0]);
        if (StringUtils.isNotBlank(modelPathsResult)){
            throw new DataValidException("模型扫描目录"+modelPathsResult);
        }

        String cudaToolkitDirResult = LinuxPathValidator.validate(this.getCustForm().getDetails().get("cudaToolkitDir")[0]);
        if (StringUtils.isNotBlank(cudaToolkitDirResult)){
            throw new DataValidException("Cuda工具包目录"+cudaToolkitDirResult);
        }

        if (StringUtils.isNotBlank(this.getCustForm().getDetails().get("id")[0])){
            //更新前 先查询之前的数据 判断挂载路径或模型路径是否有变更
            K8sServerConfEntity k8sServerConfEntity = k8sServerConfService.find(this.getCustForm().getId());
            String oldModelPath = k8sServerConfEntity.getModelPaths();
            String oldVolumnPath = k8sServerConfEntity.getVolumnPath();
            String oldCudaToolkitDir = k8sServerConfEntity.getCudaToolkitDir();

            String[] modelPaths = this.getCustForm().getDetails().get("modelPaths");
            if (modelPaths == null || modelPaths.length != 1){
                throw new DataEmptyException("模型路径是必填项");
            }
            String[] volumnPath = this.getCustForm().getDetails().get("volumnPath");
            if (volumnPath == null || volumnPath.length != 1){
                throw new DataEmptyException("挂载路径是必填项");
            }
            String[] cudaToolkitDirs = this.getCustForm().getDetails().get("cudaToolkitDir");
            if (cudaToolkitDirs == null || cudaToolkitDirs.length != 1){
                throw new DataEmptyException("Cuda工具包目录是必填项");
            }
            if (volumnPath[0].contains(",")){
                throw new DataValidException("挂载路径不能包含, 不支持多个");
            }else if(cudaToolkitDirs[0].contains(",")){
                throw new DataValidException("Cuda工具包目录不能包含, 不支持多个");
            }
            //路径变更
            if (!oldModelPath.equals(modelPaths[0]) || !oldVolumnPath.equals(volumnPath[0]) || !oldCudaToolkitDir.equals(cudaToolkitDirs[0])){
                logger.info("路径变更 重载新配置路径...");
                k8sServerConfEntity.setVolumnPath(volumnPath[0]);
                k8sServerConfEntity.setModelPaths(modelPaths[0]);
                k8sServerConfEntity.setCudaToolkitDir(cudaToolkitDirs[0]);
                KubernetesApiClient defaultClient = KubernetesApiClientFactory.build(k8sServerConfEntity);
                String deploymentName = FileControllerService.deploymentName+"-"+k8sServerConfEntity.getCode();
                V1Deployment existFileDeployment = kubernetesPodHandler.getOneDeployment(defaultClient, FileControllerService.nameSpace, deploymentName);
                //20250722 deployment存在 但是额外增加检查POD状态 如果是异常的 将整套服务重新初始化
                logger.info("开始检查 当前服务器环境状态");
                fileControllerService.initServerServe(k8sServerConfEntity);
                logger.info("检查当前服务器环境状态完成");
                if (existFileDeployment!=null){
                    DeployAppVo deployAppVo = FileControllerService.buildK8sPath(new DeployAppVo(), k8sServerConfEntity);
                    V1PodSpec spec = existFileDeployment.getSpec().getTemplate().getSpec();
                    spec.setVolumes(Arrays.asList(deployAppVo.getV1Volumes()));
                    spec.getContainers().get(0).setVolumeMounts(deployAppVo.getV1VolumeMounts());
                    //重载配置
                    kubernetesPodHandler.updateDeployment(defaultClient,FileControllerService.nameSpace,existFileDeployment);
                    TimeUnit.SECONDS.sleep(2);
                    //重载配置最多等待PDO进入running 1分钟
                    String reloadResult = kubernetesPodHandler.checkPodRunning(defaultClient, FileControllerService.nameSpace, deploymentName, 1);
                    if (StringUtils.isNotEmpty(reloadResult)){
                        throw new DataValidException("执行服务器重载超时...");
                    }
                    List<V1Pod> nodePods = kubernetesPodHandler.getNodePods(defaultClient, FileControllerService.nameSpace, k8sServerConfEntity.getCode());
                    logger.info("刷新数量为:"+nodePods.size());
                    //机制比较特殊 第一次可能由于时间差 有概率失败 这里加上二重验证保险 如果还不行就报错
                    if (nodePods.size() > 1){
                        logger.warn("第一次验证等待失败.启动第二次");
                        nodePods = kubernetesPodHandler.getNodePods(defaultClient, FileControllerService.nameSpace, k8sServerConfEntity.getCode());
                    }
                    if (nodePods.size() != 1 || !MaasConst.K8S_POD_RUNNING.equals(nodePods.get(0).getStatus().getPhase())){
                        throw new DataExecFailException("重置服务状态异常.请联系管理员!");
                    }

                    for (String checkPath : MaasConst.CUDA_TOOL_PATH) {
                        String lastWord = k8sServerConfEntity.getCudaToolkitDir().charAt(k8sServerConfEntity.getCudaToolkitDir().length()-1)+"";
                        if (!"/".equals(lastWord)){
                            k8sServerConfEntity.setCudaToolkitDir(k8sServerConfEntity.getCudaToolkitDir()+"/");
                        }
                        String[] command = new String[]{"ls",k8sServerConfEntity.getCudaToolkitDir()+checkPath};
                        //POD初始化完毕 检查Cuda工具包目录是否有效
                        JSONObject validResult = kubernetesExecHandler.execCommandOnce(nodePods.get(0).getMetadata().getName(), FileControllerService.nameSpace, defaultClient, command);
                        if (validResult.getString("msg").contains("cannot access")){
                            throw new DataValidException("Cuda工具包目录非法.未包含"+checkPath+"等操作文件");
                        }
                    }

                }
            }
        }
    }

    @Override
    public void exeAfterSave() throws Exception {
        K8sServerConfEntity k8sServerConfEntity = k8sServerConfService.find(this.getCustForm().getId());
        //每次保存都刷新服务器内部的KS客户端
        KubernetesApiClient defaultClient = KubernetesApiClientFactory.buildNoCache(k8sServerConfEntity);
        try {

            //每次保存都确认一次K8S容器操作客户端
            fileControllerService.initServerServe(k8sServerConfEntity);
            //如果是新增 则判断一次cuda工具包的路径是否合法
            if (!StringUtils.isNotBlank(this.getCustForm().getDetails().get("id")[0])) {
                List<V1Pod> nodePods = kubernetesPodHandler.getNodePods(defaultClient, FileControllerService.nameSpace, k8sServerConfEntity.getCode());
                if (nodePods.size() != 1 || !MaasConst.K8S_POD_RUNNING.equals(nodePods.get(0).getStatus().getPhase())){
                    throw new DataExecFailException("初始化服务状态异常.请联系管理员!");
                }
                for (String checkPath : MaasConst.CUDA_TOOL_PATH) {
                    String lastWord = k8sServerConfEntity.getCudaToolkitDir().charAt(k8sServerConfEntity.getCudaToolkitDir().length()-1)+"";
                    if (!"/".equals(lastWord)){
                        k8sServerConfEntity.setCudaToolkitDir(k8sServerConfEntity.getCudaToolkitDir()+"/");
                    }
                    String[] command = new String[]{"ls",k8sServerConfEntity.getCudaToolkitDir()+checkPath};
                    //POD初始化完毕 检查Cuda工具包目录是否有效
                    JSONObject validResult = kubernetesExecHandler.execCommandOnce(nodePods.get(0).getMetadata().getName(), FileControllerService.nameSpace, defaultClient, command);
                    if (validResult.getString("msg").contains("cannot access")){
                        throw new DataValidException("Cuda工具包目录不是有效的工具目录.未包含"+checkPath+"等操作文件");
                    }
                }
            }
            }catch (Exception e){
            e.printStackTrace();
            String deploymentName = FileControllerService.deploymentName+"-"+k8sServerConfEntity.getCode();
            V1Deployment existFileDeployment = kubernetesPodHandler.getOneDeployment(defaultClient, FileControllerService.nameSpace, deploymentName);
            if (existFileDeployment!=null){
                kubernetesPodHandler.deleteDeployment(defaultClient,FileControllerService.nameSpace,deploymentName);
            }
            k8sServerConfService.delete(this.getCustForm().getId());
            throw new DataExecFailException(e.getMessage());
        }

    }



}
