package com.badou.project.gpucalc;

import com.alibaba.fastjson.JSONArray;
import com.badou.project.exception.DataValidException;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1EnvVar;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class DefaultGpuHandler extends BaseGpuCalcHandler implements GpuCalcHandler {

    @Override
    public Object exec(Object... params) throws Exception {
//        suitGpuCalcHandler.exec(trainPlanEntity,limits,v1VolumeMounts,v1Volumes);
        if (params==null && params.length!=7){
            throw new Exception("未存在有效的显卡配置!请联系管理员!");
        }
        TuningModelnEntity tuningModelnEntity = (TuningModelnEntity)params[5];
        boolean exec = false;
        if (params[4] instanceof List) {
            List<V1EnvVar> list = (List<V1EnvVar>) params[4];
            if (StringUtils.isNotBlank(tuningModelnEntity.getExecGpuCard()) && tuningModelnEntity.getMultipleServersConfigs().size() == 0) {
                tuningModelnEntity.setMultipleServersConfigs(JSONArray.parseArray(tuningModelnEntity.getExecGpuCard(), MultipleServersConfig.class));
            }
            log.info("显卡信息->"+tuningModelnEntity.getMultipleServersConfigs());
            if (tuningModelnEntity.getMultipleServersConfigs().size() == 0 || tuningModelnEntity.getMultipleServersConfigs().get(0).getCanGpuCardNoMap().size()>1) {
                throw new DataValidException("未实际分配正确的显卡信息");
            }
            if (tuningModelnEntity.getMultipleServersConfigs().size() == 1) {
                list.add(new V1EnvVar().name("NVIDIA_VISIBLE_DEVICES").value(tuningModelnEntity.getMultipleServersConfigs().get(0).getCanGpuCardNos()));
                exec = true;
            }
        }
//        if (params[0] instanceof TrainPlanEntity){
//            TrainPlanEntity trainPlanEntity = (TrainPlanEntity)params[0];
//            if (params[1] instanceof Map){
//                Map<String,Quantity> limits = (Map)params[1];
//                limits.put("aliyun.com/gpu-mem", new Quantity(trainPlanEntity.getPreGpucache().toString()));
//                exec = true;
//            }
//        }
        if (exec == false){
            throw new Exception("显卡配置异常!请联系管理员!");
        }
//        limits.put("aliyun.com/gpu-mem", new Quantity(trainPlanEntity.getPreGpucache().toString()));
        return null;
    }

}
