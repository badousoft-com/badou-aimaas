package com.badou.project.maas.modelwarehouse.client;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.project.maas.modelwarehouse.web.dto.Criterion;
import com.badou.project.maas.modelwarehouse.web.dto.SingleCriterion;
import java.util.*;

public class ModelScopeApiClient {
    // 定义前端 category 到模型字段的映射关系
    private static final Map<String, String> CATEGORY_MAPPING = new HashMap<>();
    static {
        // 前端: demo_service → 模型字段: widgets
        CATEGORY_MAPPING.put("demo_service", "widgets");
        // 前端: inference_type → 模型字段: SupportInference
        CATEGORY_MAPPING.put("inference_type", "support_inference");
    }

    //1.获取所有符合条件的模型列表
    public static List<JSONObject> getFirstPageModels(
        Integer page,
        Integer pageSize,
        String searchKeyword,
        String sortBy,
        List<Criterion> criterion,
        List<SingleCriterion> singleCriterion
) {
    List<JSONObject> firstPageModels = new ArrayList<>();
    JSONObject response = sendPutRequest(page, pageSize, searchKeyword, sortBy,  criterion, singleCriterion); // 强制请求第一页
    JSONObject data = response.getJSONObject("Data");
    if (data != null && data.getJSONObject("Model") != null) {
        JSONArray models = data.getJSONObject("Model").getJSONArray("Models");
        models.forEach(obj -> firstPageModels.add((JSONObject) obj));
    }
    return firstPageModels;
}
    //2.获取符合条件的模型总数
    public static int getTotalCount(
            String searchKeyword,
            String sortBy,
            List<Criterion> criterion,
            List<SingleCriterion> singleCriterion
    ) {
        // 仅获取第一页的元数据
        JSONObject response = sendPutRequest(1, 30, searchKeyword,sortBy,criterion, singleCriterion);
        JSONObject data = response.getJSONObject("Data");
        if (data != null && data.getJSONObject("Model") != null) {
            return data.getJSONObject("Model").getInteger("TotalCount");
        }
        return 0;
    }

    public static JSONObject sendPutRequest(
            Integer page,
            Integer pageSize,
            String searchKeyword,
            String sortBy,
            List<Criterion> criterion,
            List<SingleCriterion> singleCriterion
    ) {
        // 接口地址
        String url = "https://www.modelscope.cn/api/v1/dolphin/models";
        JSONObject json = new JSONObject();
        json.put("PageSize", pageSize);
        json.put("PageNumber", page);
        json.put("Target", "");
        if (sortBy != null && !sortBy.isEmpty()) {
            json.put("SortBy", sortBy);
        } else {
            json.put("SortBy", "Default");
        }
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            json.put("Name", searchKeyword);
        }
        //添加过滤条件
        if (criterion != null && !criterion.isEmpty()) {
            JSONArray criterionArray = new JSONArray();
            criterion.forEach(c ->{
                JSONObject item = new JSONObject();
                String modelField = CATEGORY_MAPPING.getOrDefault(c.getCategory(), c.getCategory());
                item.put("category", modelField);
                item.put("predicate", c.getPredicate());
                item.put("values", c.getValues());
                criterionArray.add(item);
            });
            json.put("Criterion", criterionArray);
        }
        if (singleCriterion != null && !singleCriterion.isEmpty()) {
            JSONArray singleCriterionArray = new JSONArray();
            singleCriterion.forEach(sc ->{
                JSONObject item = new JSONObject();
                String category = sc.getCategory();
                Integer intValue = sc.getIntValue();
                // 使用映射表转换 category
                String modelField = CATEGORY_MAPPING.getOrDefault(sc.getCategory(), sc.getCategory());
                item.put("category", modelField);
                item.put("predicate", sc.getPredicate());
                item.put("DateType", sc.getDateType());
                item.put("IntValue", sc.getIntValue());
                // 判断是否为 inference_type 并且 IntValue == 1
                if ("inference_type".equals(category) && intValue != null && intValue == 1) {
                    // 特殊处理：替换 category 为原始值，保留结构
                    item.put("category", category);
                }
                singleCriterionArray.add(item);
            });
            json.put("SingleCriterion", singleCriterionArray);
        }

        // 发送PUT请求
        return JSONObject.parseObject(
            HttpRequest.put(url)
                .header("Content-Type", "application/json")
                .timeout(30000)
                .body(json.toJSONString())
                .execute()
                .body()
        );
    }

    //3.获取模型详情介绍
    public static JSONObject getModelReadme(String owner, String name){
        String url = "https://www.modelscope.cn/api/v1/rm/fc?Type=translate-readme";
        JSONObject json = new JSONObject();
        json.put("type", "model");
        json.put("name", name);
        json.put("owner", owner);
        json.put("preferLanguage", "zh_CN");
        String response = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .body(json.toJSONString())
                .execute()
                .body();
        return JSONObject.parseObject(response);
    }

    //4.获取模型分类筛选条件
        //任务和热门任务
    public static JSONObject getTasks() {
        String url = "https://www.modelscope.cn/api/v1/tasks?PageNumber=1";
        String response = HttpRequest.get(url)
                .header("Content-Type", "application/json")
                .execute()
                .body();
        return JSONObject.parseObject(response);
    }
    public static JSONObject getPopularTasks() {
        String url = "https://www.modelscope.cn/api/v1/tasks/popular/list";
        String response = HttpRequest.get(url)
                .header("Content-Type", "application/json")
                .execute()
                .body();
        return JSONObject.parseObject(response);
    }
        //其他（开源协议）
    public static JSONObject getLicenses() {
        String url = "https://www.modelscope.cn/api/v1/licenses";
        String response = HttpRequest.get(url)
                .header("Content-Type", "application/json")
                .execute()
                .body();
        return JSONObject.parseObject(response);
    }
        //组织
    public static JSONObject getOrganizations(int pageSize, int pageNumber) {
        String url = String.format("https://www.modelscope.cn/api/v1/models/orgTags?PageSize=%d&PageNumber=%d", pageSize, pageNumber);
        String response = HttpRequest.get(url)
                .header("Content-Type", "application/json")
                .execute()
                .body();
        return JSONObject.parseObject(response);
    }
}
