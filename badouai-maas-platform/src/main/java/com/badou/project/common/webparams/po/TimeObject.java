package com.badou.project.common.webparams.po;

import io.swagger.annotations.ApiModelProperty;

/**
 * @ClassName TimeObject
 * @Description TODO
 * @date 2021/10/29 17:10
 * @Version 1.0
 */

public class TimeObject {

    @ApiModelProperty("起始时间")
    private String startTime;
    @ApiModelProperty("结束时间")
    private String endTime;
    @ApiModelProperty("字段名字")
    private String name;
    @ApiModelProperty("时间类型")
    private String dateType;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }
}
