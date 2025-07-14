package com.badou.project.maas.modelwarehouse.web.dto;

import java.util.List;

public class Criterion {
    private String category;
    private String predicate;
    private List<String> values;

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

    public List<String> getValues() {
        return values;
    }
    public void setValues(List<String> values) {
        this.values = values;
    }

}
