package com.badou.project.gpucalc.impl;

import com.badou.project.gpucalc.BaseGpuCalcHandler;
import com.badou.project.gpucalc.GpuCalcHandler;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import io.kubernetes.client.openapi.models.V1EnvVar;

import java.util.List;
import java.util.Map;

public class TorchRunGpuHandler extends BaseGpuCalcHandler implements GpuCalcHandler {

    @Override
    public Object exec(Object... params) throws Exception {
        // 不需要增加阿里云的自动显卡分配配置 改成手动指定显卡
        //不需要增加这行代码: limits.put("aliyun.com/gpu-mem", new Quantity(trainPlanEntity.getPreGpucache().toString()));
        if (params==null && params.length!=7){
            throw new Exception("未存在有效的显卡配置!请联系管理员!");
        }
        TrainPlanEntity trainPlanEntity = (TrainPlanEntity)params[0];
        if (params[4] instanceof List){
            List<V1EnvVar> list = (List<V1EnvVar>)params[4];
            //增加本次多卡微调的显卡
            list.add(new V1EnvVar().name("NVIDIA_VISIBLE_DEVICES").value("0,1"));
            //如果微调框架是ptv2和gpu多卡框架是torchrun
            if (trainPlanEntity.getGpuFrame() == 1 && trainPlanEntity.getTunFrame() == 2){
                list.add(new V1EnvVar().name("NUM_GPUS").value(trainPlanEntity.getGpuCount()+""));
            }
        }
        return null;
    }

}
