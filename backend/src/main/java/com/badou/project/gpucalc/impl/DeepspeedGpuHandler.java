package com.badou.project.gpucalc.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.project.exception.DataValidException;
import com.badou.project.gpucalc.BaseGpuCalcHandler;
import com.badou.project.gpucalc.GpuCalcHandler;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.mq.ModelPlanTaskMqReceiver;
import com.badou.tools.common.util.SpringHelper;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.openapi.models.V1EnvVar;

import javax.swing.*;
import java.util.List;

public class DeepspeedGpuHandler extends BaseGpuCalcHandler implements GpuCalcHandler {

    @Override
    public Object exec(Object... params) throws Exception {
        //        suitGpuCalcHandler.exec(trainPlanEntity,limits,v1VolumeMounts,v1Volumes);
        if (params==null && params.length!=7){
            throw new Exception("未存在有效的显卡配置!请联系管理员!");
        }
        TrainPlanEntity trainPlanEntity = (TrainPlanEntity)params[0];
        TuningModelnEntity tuningModelnEntity = (TuningModelnEntity)params[5];
        if (params[4] instanceof List){
            List<V1EnvVar> list = (List<V1EnvVar>)params[4];
            if (StringUtils.isNotBlank(tuningModelnEntity.getExecGpuCard()) && tuningModelnEntity.getMultipleServersConfigs().size() == 0) {
                tuningModelnEntity.setMultipleServersConfigs(JSONArray.parseArray(tuningModelnEntity.getExecGpuCard(), MultipleServersConfig.class));
            }
            if (tuningModelnEntity.getMultipleServersConfigs().size()==0){
                throw new DataValidException("未实际分配显卡信息");
            }
            //如果是多机多卡 交给多机多卡控制器来分配显卡
            if (tuningModelnEntity.getMultipleServersConfigs().size() == 1){
                list.add(new V1EnvVar().name("NVIDIA_VISIBLE_DEVICES").value(tuningModelnEntity.getMultipleServersConfigs().get(0).getCanGpuCardNos()));
            }
            FileControllerService fileControllerService = SpringHelper.getBean(FileControllerService.class);
            String[] serverSplit = tuningModelnEntity.getServerId().split(",");
            String oldServerId = tuningModelnEntity.getServerId();

            //增加本次多卡微调的显卡
            //如果微调框架是llamafactory 和gpu多卡框架是deepspeed
            if (trainPlanEntity.getGpuFrame() == 0 && trainPlanEntity.getTunFrame() == 0){
                //手动下载和挂载ds_z2_config多卡配置文件
                DictionaryItemCacheObject deepspeeds2config = DictionaryLib.getDictionaryItemByCode(MaasConst.TUN_CONFIG_FILE_DIC, "deepspeeds2config");
                if (deepspeeds2config == null){
                    throw new Exception("llamafactory框架未存在deepspeeds配置文件.请联系管理员!");
                }
                String dataInfoJsonId = deepspeeds2config.getValue();
                if (tuningModelnEntity.getDoFrame() == 0){
                    for (String serverId : serverSplit) {
                        KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(serverId);
                        tuningModelnEntity.setServerId(serverId);
                        String realHostFile = MaasConst.buildConfigPath(tuningModelnEntity) + deepspeeds2config.getName();
                        //下载配置文件到容器文件服务
                        if (StringUtils.isNotBlank(dataInfoJsonId)){
                            //下载微调配置文件
                            JSONObject dataInfoJsonAttach = fileControllerService.downFile(tuningModelnEntity,dataInfoJsonId, realHostFile, kubernetesApiClient,null);
                            if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_FAIL_STATUS){
                                throw new Exception("初始化多卡配置失败!"+tuningModelnEntity.getPlanMsg());
                            }
                        }
                    }
                    tuningModelnEntity.setServerId(serverSplit[0]);
                    String realHostFile = MaasConst.buildConfigPath(tuningModelnEntity) + deepspeeds2config.getName();
                    tuningModelnEntity.setServerId(oldServerId);

                    //本次微调额外增加挂载多卡配置 只添加到主节点
                    String mountFilesPath = tuningModelnEntity.getMountFilesPath();
                    String hostFilesPath = tuningModelnEntity.getHostFilesPath();
                    String nameFilesPath = tuningModelnEntity.getNameFilesPath();
                    tuningModelnEntity.setNameFilesPath(StringUtils.isEmpty(nameFilesPath) ? "deepspeeds2config" : nameFilesPath + ",deepspeeds2config");
                    tuningModelnEntity.setHostFilesPath(StringUtils.isEmpty(hostFilesPath)?realHostFile:hostFilesPath+","+realHostFile);
                    tuningModelnEntity.setMountFilesPath(StringUtils.isEmpty(mountFilesPath) ? "/fine_tuning/ds_z2_config.json" : mountFilesPath + "," + "/fine_tuning/ds_z2_config.json");
                    SpringHelper.getBean(ITuningModelnService.class).update(tuningModelnEntity);
                }
                list.add(new V1EnvVar().name("DEEPSPEED").value("/fine_tuning/ds_z2_config.json"));
            }
        }
        return null;
    }

}
