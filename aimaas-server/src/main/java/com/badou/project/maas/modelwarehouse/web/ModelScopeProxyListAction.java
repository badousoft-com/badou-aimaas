package com.badou.project.maas.modelwarehouse.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import com.badou.project.maas.modelwarehouse.service.IModelScopeProxyService;
import com.badou.project.maas.modelwarehouse.web.dto.ModelQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class ModelScopeProxyListAction extends BaseCommonListAction {
    @Autowired
    private IModelScopeProxyService modelScopeProxyService;

    @PostMapping
    public JSONObject getPaginatedModels(@RequestBody ModelQueryRequest request) {
        return modelScopeProxyService.getModelList(
                request.getPage(),
                request.getPageSize(),
                request.getSearchKeyword(),
                request.getSortBy(),
                request.getCriterion(),
                request.getSingleCriterion()
        );
    }

    @PostMapping
    public JSONObject getModelDetails(@RequestBody Map<String, String> payload) {
        String owner = payload.get("owner");
        String name = payload.get("name");
        return modelScopeProxyService.getModelDetails(owner, name);
    }

    @PostMapping
    public JSONObject getTasks() {
        return modelScopeProxyService.getTasks();
    }
    @PostMapping
    public JSONObject getPopularTasks() {
        return modelScopeProxyService.getPopularTasks();
    }
    @PostMapping
    public JSONObject getLicenses() {
        return modelScopeProxyService.getLicenses();
    }
    @PostMapping
    public JSONObject getOrganizations(@RequestParam int pageSize, @RequestParam int pageNumber) {
        return modelScopeProxyService.getOrganizations(pageSize, pageNumber);
    }

}