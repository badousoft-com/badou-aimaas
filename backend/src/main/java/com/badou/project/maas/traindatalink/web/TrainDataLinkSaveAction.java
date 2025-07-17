package com.badou.project.maas.traindatalink.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.modeltrain.service.ITrainDataSourceService;
import com.badou.project.maas.mongo.model.TrainData;
import com.badou.tools.common.util.SpringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;

import java.util.ArrayList;

/**
 * @author badousoft
 * @created 2024-04-22 16:50:47.367
 * @todo 训练集数据与训练集关联 保存实现类
 */
@Controller
public class TrainDataLinkSaveAction extends BaseCommonSaveAction {

    @Autowired
    private ITrainDataSourceService trainDataSourceService;

    @Override
    protected void exeBeforeSave() throws Exception {
        String[] trainDataId = this.custForm.getDetails().get("trainDataId");
        if(JsonResultUtil.arrayOneElement(trainDataId)){
            JSONObject jsonObject = trainDataSourceService.find(new Query(Criteria.where("id").is(trainDataId[0])));
            if(JsonResultUtil.arrayOneElement(jsonObject.get("Rows"))){
                ArrayList rows = (ArrayList)jsonObject.get("Rows");
                TrainData row = (TrainData) rows.get(0);
                this.custForm.getDetails().put("trainDataTitle",new String[]{row.getTitle()});
                this.custForm.getDetails().put("trainDataContent",new String[]{row.getTrainContent()});
                this.custForm.getDetails().put("trainDataTag",new String[]{row.getTag()});
                this.custForm.getDetails().put("trainDataVersion",new String[]{row.getVersion()});
            }
        }
    }

}
