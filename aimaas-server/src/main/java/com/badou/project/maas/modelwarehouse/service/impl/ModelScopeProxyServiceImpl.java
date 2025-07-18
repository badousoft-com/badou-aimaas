package com.badou.project.maas.modelwarehouse.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.project.maas.modelwarehouse.client.ModelScopeApiClient;
import com.badou.project.maas.modelwarehouse.service.IModelScopeProxyService;
import com.badou.project.maas.modelwarehouse.web.dto.Criterion;
import com.badou.project.maas.modelwarehouse.web.dto.SingleCriterion;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ModelScopeProxyServiceImpl implements IModelScopeProxyService {

    @Override
    public JSONObject getModelList(Integer page, Integer pageSize, String searchKeyword, String sortBy,
                                   List<Criterion> criterion, List<SingleCriterion> singleCriterion) {
        try {
            JSONObject result = new JSONObject();
            List<JSONObject> models = ModelScopeApiClient.getFirstPageModels(page, pageSize, searchKeyword,sortBy,criterion, singleCriterion);
            JSONArray formattedModels = new JSONArray();
            int totalCount = ModelScopeApiClient.getTotalCount(searchKeyword, sortBy, criterion, singleCriterion);
            for (JSONObject model : models) {
                JSONObject item = new JSONObject();
                //模型标题
                item.put("ChineseName", model.getString("ChineseName"));
                item.put("Name", model.getString("Name"));
                //模型路径
                item.put("Path",  model.getString("Path"));
                //模型标签
                item.put("Tasks", model.getJSONArray("Tasks"));
                item.put("Frameworks", model.getJSONArray("Frameworks"));
                item.put("Libraries", model.getJSONArray("Libraries"));
                item.put("ModelType", model.getJSONArray("ModelType"));
                item.put("License",model.getString("License"));
                item.put("Domain", model.getJSONArray("Domain"));
                item.put("Metrics", model.getJSONArray("Metrics"));
                item.put("Language", model.getJSONArray("Language"));
                item.put("Tags", model.getJSONArray("Tags"));
                // 模型信息
                item.put("Organization", model.getJSONObject("Organization"));
                item.put("LastUpdatedTime", model.getLong("LastUpdatedTime"));
                item.put("Downloads",model.getInteger("Downloads"));
                item.put("Stars", model.getInteger("Stars"));
                // 筛选模型条件
                    //支持体验->推理 API-Inference
                item.put("SupportInference", model.getString("SupportInference"));
                            //->模型 Demo 体验
                item.put("widgets", model.getJSONArray("widgets"));
                item.put("SupportExperience", model.getInteger("SupportExperience"));
                            //->Restful API 体验
                item.put("SupportApiInference", model.getBoolean("SupportApiInference"));
                    //支持训练->零门槛训练微调 FlexTrain
                item.put("SupportFlexTrain", model.getInteger("SupportFlexTrain"));
                            //->SDK 编程训练微调
                item.put("SupportFinetuning", model.getInteger("SupportFinetuning"));
                            //->阿里云PAI Model Gallery训练、阿里云PAI Model Gallery评测、阿里云PAI Model Gallery部署
                item.put("SupportPaiModelGallery", model.getJSONArray("SupportPaiModelGallery"));
                    //支持部署->快速部署 SwingDeploy
                item.put("SupportDeployment", model.getInteger("SupportDeployment"));
                formattedModels.add(item);
            }
            result.put("models", formattedModels);
            result.put("totalCount", totalCount);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch model list:" + e.getMessage(), e);
        }
    }
    @Override
    public JSONObject getModelDetails(String owner, String name) {
        try {
            // 调用已有的客户端方法获取模型介绍
            JSONObject response = ModelScopeApiClient.getModelReadme(owner, name);

            // 构建统一返回格式
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("code", "finished");
            result.put("content", response.getString("content")); // 提取 content 字段内容

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch model description: " + e.getMessage(), e);
        }
    }

    @Override
    public JSONObject getTasks() {
        return ModelScopeApiClient.getTasks();
    }
    @Override
    public JSONObject getPopularTasks() {
        return ModelScopeApiClient.getPopularTasks();
    }
    @Override
    public JSONObject getLicenses() {
        return ModelScopeApiClient.getLicenses();
    }
    @Override
    public JSONObject getOrganizations(int pageSize, int pageNumber) {
        return ModelScopeApiClient.getOrganizations(pageSize, pageNumber);
    }

}
