package com.badou.project.maas.modelwarehouse.service;

import com.alibaba.fastjson.JSONObject;
import com.badou.project.maas.modelwarehouse.web.dto.Criterion;
import com.badou.project.maas.modelwarehouse.web.dto.SingleCriterion;

import java.util.List;

public interface IModelScopeProxyService {
     /**
      * 获取该仓库下所有的模型
      * @param page 页码
      * @param pageSize 每页条数
      * @param searchKeyword 搜索关键词
      * @param sortBy 排序
      * @param criterion 筛选多值条件
      * @param singleCriterion 筛选单值条件
      * @return 返回模型列表
      */
     JSONObject getModelList(Integer page, Integer pageSize, String searchKeyword, String sortBy,
                             List<Criterion> criterion, List<SingleCriterion> singleCriterion);

     /**
      * 获取该仓库下所有的模型详情
      * @param owner 模型拥有者
      * @param name 模型名称
      * @return 返回模型详情
      */
     JSONObject getModelDetails(String owner, String name);

     /**
      * 获取该仓库下模型的分类筛选任务
      * @return 返回模型分类任务
      */
     JSONObject getTasks();
     /**
      * 获取该仓库下模型的分类筛选热门任务
      * @return 模型分类热门任务
      */
     JSONObject getPopularTasks();
     /**
      * 获取该仓库下模型的分类筛选框架
      * @return 模型分类其他（开源协议）
      */
     JSONObject getLicenses();
     /**
      * 获取该仓库下模型的分类筛选组织
      * @param pageSize 页面大小
      * @param pageNumber 页面数
      * @return 模型分类组织
      */
     JSONObject getOrganizations(int pageSize, int pageNumber);
}
