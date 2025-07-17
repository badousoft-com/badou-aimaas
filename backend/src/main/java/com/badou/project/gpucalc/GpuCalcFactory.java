package com.badou.project.gpucalc;

import com.badou.project.gpucalc.impl.DeepspeedGpuHandler;
import com.badou.project.gpucalc.impl.TorchRunGpuHandler;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;

public class GpuCalcFactory {

    public static GpuCalcHandler getSuitGpuCalcHandler(TrainPlanEntity trainPlanEntity){
        //多卡方案 等于2或者2卡以上
        if (trainPlanEntity.getGpuCount()!=null && trainPlanEntity.getGpuCount() > 1
        && trainPlanEntity.getTunFrame()!=null){
            if (trainPlanEntity.getGpuFrame() == 0){
                return new DeepspeedGpuHandler();
            }else if (trainPlanEntity.getGpuFrame() == 1){
                return new TorchRunGpuHandler();
            }
        }
        if (trainPlanEntity.getGpuCount() == null || trainPlanEntity.getGpuCount() == 1){
            return new DefaultGpuHandler();
        }
        return null;
    }

}
