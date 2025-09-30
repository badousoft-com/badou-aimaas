package com.badou.project.gpucalc;

import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataValidException;

/**
 * GPU显卡显存计算 显卡本体的信息
 */
public class GpuCalcCardModel {
    //显卡序号
    private Integer orderNo;
    //显存 当前和最大
    private Integer currentVMemory;
    private Integer maxVMemory;
    //显卡名称
    private String name;

    public GpuCalcCardModel() {
    }

    public GpuCalcCardModel(Integer orderNo, Integer currentVMemory, Integer maxVMemory, String name) {
        this.orderNo = orderNo;
        this.currentVMemory = currentVMemory;
        this.maxVMemory = maxVMemory;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
