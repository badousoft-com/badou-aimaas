package com.badou.project.server.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.cache.util.JedisUtil;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesExecHandler;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.modelsync.service.IModelSyncTaskService;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.model.ServerGpuEntity;
import com.badou.project.server.model.ServerModelDTO;
import com.badou.project.server.service.IServerGpuService;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.openapi.models.V1Pod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author badousoft
 * @created 2025-03-17 14:30:08.365
 *  显卡资源管理 列表实现类
 */
@Controller
public class ServerGpuListAction extends BaseCommonListAction {

    @Autowired
    private IServerGpuService serverGpuService;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private KubernetesExecHandler kubernetesExecHandler;
    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;
    @Autowired
    private FileControllerService fileControllerService;
    @Autowired
    private IModelSyncTaskService modelSyncTaskService;
//    @Autowired
//    private ModelSyncTaskMqReceiver modelSyncTaskMqReceiver;

    @PostMapping
    public JSONObject getServerGpu(@RequestParam String serverId,String defaultSearchParam){
        if (StringUtils.isNotBlank(defaultSearchParam)){
            serverId = JSONArray.parseArray(defaultSearchParam).getJSONObject(0).getString("value");
        }
        JSONObject bean = new JSONObject();
        List<ServerGpuEntity> cardData = serverGpuService.getCardData(serverId);
        bean.put("Total",cardData.size());
        bean.put("Rows",cardData);
        return bean;
    }

    @PostMapping
    public JsonReturnBean buildModelSize(String path,@RequestParam(required = true) String serverId) throws Exception {
        if (StringUtils.isEmpty(path)){
            return JsonResultUtil.error();
        }
        KubernetesApiClient kubernetesApiClient1 = FileControllerService.getCacheK8sClient(serverId);
        JSONObject jsonObject = fileControllerService.execModelCommand(kubernetesApiClient1.getServer(), new String[]{"du", "-sh", path});
        if (jsonObject.getBoolean("success")){
            return JsonResultUtil.success(jsonObject.getString("msg").split("\t")[0]);
        }
        return JsonResultUtil.error();
    }

    @PostMapping
    public List<ServerModelDTO> getModelList(String searchParam,String modelProvider,String defaultSearchParam) throws Exception {
        String serverId = JSONArray.parseArray(defaultSearchParam).getJSONObject(0).getString("value");
        if (StringUtils.isEmpty(serverId)){
            throw new Exception("请先选择服务器");
        }
        String searchModelName = null;
        if (StringUtils.isNotBlank(searchParam)){
            JSONArray jsonArray = JSONArray.parseArray(searchParam);
            if (jsonArray.size() == 1 && "modelName".equals(jsonArray.getJSONObject(0).getString("name"))){
                searchModelName = jsonArray.getJSONObject(0).getString("value");
            }
        }
        //20250616 新版本 获取模型列表


//        Map<String, String> runningMap = modelSyncTaskMqReceiver.getRunningMap();
//        System.out.println(runningMap);
//        String getModelList = jedisUtil.get("getModelList");
//        if (StringUtils.isNoneBlank(getModelList)){
//            List<ServerModelDTO> serverModelDTOS = JSONArray.parseArray(getModelList,ServerModelDTO.class);
//            for (ServerModelDTO serverModelDTO : serverModelDTOS) {
//                String s = serverModelDTO.getModelName();
//                //处理搜索条件
//                    if (StringUtils.isNotBlank(searchModelName) && !s.contains(searchModelName)){
//                        continue;
//                    }
//                    if (StringUtils.isNotBlank(searchModelName) && !s.contains(modelProvider)){
//                        continue;
//                    }
//                    if(StringUtils.isNotBlank(s)) {
//                        serverModelDTOS.add(serverModelDTO);
//                    }
//            }
//            return serverModelDTOS;
//        }

        List<ServerModelDTO> serverModelDTOS = new ArrayList<>();
        //获取该服务器的文件操作节点
        try {
            KubernetesApiClient kubernetesApiClient = FileControllerService.getCacheK8sClient(serverId);
            // 获取文件夹列表
            K8sServerConfEntity server = kubernetesApiClient.getServer();

            List<V1Pod> filePods = kubernetesPodHandler.getPodByLabelApp(kubernetesApiClient, FileControllerService.deploymentName, FileControllerService.deploymentName + "-" + server.getCode());
            if (filePods.size()!=1){
                fileControllerService.initServerServe(kubernetesApiClient.getServer());
                //等待服务running
                filePods = kubernetesPodHandler.getPodByLabelApp(kubernetesApiClient, FileControllerService.deploymentName, FileControllerService.deploymentName + "-" + server.getCode());
                while (filePods.size()!=1){
                    filePods = kubernetesPodHandler.getPodByLabelApp(kubernetesApiClient, FileControllerService.deploymentName, FileControllerService.deploymentName + "-" + server.getCode());
                    logger.info("等待初始化完成");
                }
                //初始化成功 等待3秒
                logger.info("初始化完成 等待3秒");
                TimeUnit.SECONDS.sleep(3);
            }
            String[] modelPathSplit = server.getModelPaths().split(",");
            for (String modelPaths : modelPathSplit){
                if (!"/".equals(modelPaths.indexOf(modelPaths.length()-1))){
                    modelPaths+="/";
                }
                //如果不存在则进行初始化
                V1Pod v1Pod = filePods.get(0);
                JSONObject jsonObject = kubernetesExecHandler.execCommandOnce(v1Pod.getMetadata().getName(), FileControllerService.nameSpace, kubernetesApiClient, new String[]{
                        "find", modelPaths,"-maxdepth","2","-type","d"
                });
                if(StringUtils.isNotBlank(jsonObject.getString("msg"))){
                    String[] paths = jsonObject.getString("msg").split("\n");
                    for (String path:paths){
                        String nowPath = path.replace(modelPaths,"");
                        //处理搜索条件
                        if (StringUtils.isNotBlank(searchModelName) && !nowPath.contains(searchModelName)){
                            continue;
                        }
                        //去掉父目录
                        if (!nowPath.contains("/")){
                            continue;
                        }
                        String[] split = nowPath.split("/");
                        ServerModelDTO serverModelDTO = new ServerModelDTO(split[split.length-1],path);
                        serverModelDTOS.add(serverModelDTO);
                    }
                }
            }

//            if(StringUtils.isNotBlank(jsonObject.getString("msg"))){
//                String[] mainDirs = jsonObject.getString("msg").replace("\t","").replace("\t\t","").split("\n");
//                for (String mainDir : mainDirs) {
//                    String[] models = mainDir.split(" ");
//                    for (String model : models) {
//                        if(StringUtils.isNotBlank(model)){
//                            logger.info("获取到主目录:"+model);
//                            //加载里面的模型作为模型列表
//                           jsonObject = kubernetesExecHandler.execCommandOnce(v1Pod.getMetadata().getName(), FileControllerService.nameSpace, kubernetesApiClient, new String[]{
//                                    "ls", modelPaths+"/"+model
//                            });
//                            String[] mainModels = jsonObject.getString("msg").replace("\t", "").replace("\t\t", "").split("\n");
//                            for (String mainModel : mainModels) {
//                                String[] row = mainModel.split(" ");
//                                for (String s : row) {
////                                    if (StringUtils.isNotBlank(searchModelName) && !allPath.contains(modelProvider)){
////                                        continue;
////                                    }
////                                    if(StringUtils.isNotBlank(s)) {
////                                        //如果该模型存在于模型同步里 则跳过
////                                        if (runningMap.get(model+"/"+s)!=null){
////                                            logger.info(model+"/"+s+"正在同步中 不显示在实际的模型列表里....");
////                                            continue;
////                                        }
////                                        String allPath = modelPaths+"/"+model+"/"+s;
////                                        //处理搜索条件
////                                        if (StringUtils.isNotBlank(searchModelName) && !allPath.contains(searchModelName)){
////                                            continue;
////                                        }
////                                        ServerModelDTO serverModelDTO = new ServerModelDTO(s,modelPaths+"/"+model+"/"+s);
////                                        serverModelDTOS.add(serverModelDTO);
////                                    }
//                                }
//
//                            }
//                        }
//                    }
//
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        jedisUtil.setEx("getModelList",JSONObject.toJSONString(serverModelDTOS));
        //转成列表
        return serverModelDTOS;
    }

}
