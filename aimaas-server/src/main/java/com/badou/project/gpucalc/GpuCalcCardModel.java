package com.badou.project.gpucalc;

/**
 * GPU显卡显存计算 显卡本体的信息
 */
public class GpuCalcCardModel {
    //显卡序号
    private Integer orderNo;
    //显存 当前和最大
    private Integer currentVMemory;
    private Integer maxVMemory;

    public GpuCalcCardModel() {
    }

    public GpuCalcCardModel(Integer orderNo, Integer currentVMemory, Integer maxVMemory) {
        this.orderNo = orderNo;
        this.currentVMemory = currentVMemory;
        this.maxVMemory = maxVMemory;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getCurrentVMemory() {
        return currentVMemory;
    }

    public void setCurrentVMemory(Integer currentVMemory) {
        this.currentVMemory = currentVMemory;
    }

    public Integer getMaxVMemory() {
        return maxVMemory;
    }

    public void setMaxVMemory(Integer maxVMemory) {
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
