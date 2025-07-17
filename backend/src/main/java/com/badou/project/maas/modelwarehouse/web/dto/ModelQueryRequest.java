package com.badou.project.maas.modelwarehouse.web.dto;

import java.util.ArrayList;
import java.util.List;

public class ModelQueryRequest {
    private Integer page;
    private Integer pageSize;
    private String searchKeyword;
    private String sortBy;
    private List<Criterion> criterion;
    private List<SingleCriterion> singleCriterion;

    public Integer getPage() {
        return page;
    }
    public void setPage(Integer page) {
        this.page = page;
    }
    public Integer getPageSize() {
        return pageSize;
    }
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    public String getSearchKeyword() {
        return searchKeyword;
    }
    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }
    public String getSortBy() {
        return sortBy;
    }
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    public List<Criterion> getCriterion() {
        return criterion;
    }
    public void setCriterion(List<Criterion> criterion) {
        this.criterion = criterion;
    }
    public List<SingleCriterion> getSingleCriterion() {
        return singleCriterion;
    }
    public void setSingleCriterion(List<SingleCriterion> singleCriterion) {
        this.singleCriterion = singleCriterion;
    }
}
