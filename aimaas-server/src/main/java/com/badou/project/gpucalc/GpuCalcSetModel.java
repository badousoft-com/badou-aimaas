package com.badou.project.gpucalc;

/**
 * GPU显卡显存计算 已设置的显卡显存信息
 */
public class GpuCalcSetModel {
    //显卡序号
    private Integer orderNo;
    //显存 当前和最大
    private Integer currentVMemory;
    private Integer maxVMemory;

    public GpuCalcSetModel(Integer orderNo, Integer currentVMemory, Integer maxVMemory) {
        this.orderNo = orderNo;
        this.currentVMemory = currentVMemory;
        this.maxVMemory = maxVMemory;
    }

    @Override
    public String toString() {
        return "GpuCalcModel{" +
                "orderNo=" + orderNo +
                ", currentVMemory=" + currentVMemory +
                ", maxVMemory=" + maxVMemory +
                '}';
    }
}
