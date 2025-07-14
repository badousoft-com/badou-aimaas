package com.badou.project.maas.modelwarehouse.web.dto;

public class SingleCriterion {
    private String DateType;
    private Integer IntValue;
    private String category;
    private String predicate;

    public String getDateType() {
        return DateType;
    }
    public void setDateType(String dateType) {
        this.DateType = dateType;
    }
    public Integer getIntValue() {
        return IntValue;
    }
    public void setIntValue(Integer intValue) {
        this.IntValue = intValue;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getPredicate() {
        return predicate;
    }
    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }
}
