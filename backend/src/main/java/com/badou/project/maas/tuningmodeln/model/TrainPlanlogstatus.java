package com.badou.project.maas.tuningmodeln.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class TrainPlanlogstatus {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date handlerTime;
    private String value;

    public TrainPlanlogstatus(String value) {
        this.value = value;
    }

    public TrainPlanlogstatus(Date handlerTime, String value) {
        this.handlerTime = handlerTime;
        this.value = value;
    }

    public Date getHandlerTime() {
        return handlerTime;
    }

    public void setHandlerTime(Date handlerTime) {
        this.handlerTime = handlerTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
