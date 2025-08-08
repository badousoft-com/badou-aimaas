package com.badou.project.maas.modeltrain.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.project.maas.modeltrain.service.ITrainDataSourceService;
import com.badou.project.maas.mongo.model.TrainData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author badousoft
 * @created 2024-04-07 15:39:08.379
 * @todo 训练集数据 列表实现类
 */
@Controller
public class TrainDataSourceListAction extends BaseCommonListAction {

    @Autowired
    private ITrainDataSourceService trainDataSourceService;

    @PostMapping
    public JSONObject listJson(){
        return trainDataSourceService.find(new Query());
    }

}
