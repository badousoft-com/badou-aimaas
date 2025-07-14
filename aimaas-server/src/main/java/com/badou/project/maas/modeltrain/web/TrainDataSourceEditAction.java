package com.badou.project.maas.modeltrain.web;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.ICriterion;
import com.badou.designer.jdbc.common.annotations.BaseMdJsonStack;
import com.badou.designer.jdbc.core.vo.BaseVO;
import com.badou.project.maas.mongo.model.TrainData;
import com.badou.tools.common.util.ParamUtils;
import com.badou.tools.common.util.SpringHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonEditAction;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

/**
 * @author badousoft
 * @created 2024-04-07 15:39:08.379
 * @todo 训练集数据 编辑实现类
 */
@Controller
public class TrainDataSourceEditAction extends BaseCommonEditAction {

    private JSONObject datas;
    @Autowired
    private MongoTemplate mongoTemplate;


    @RequestMapping
    @BaseMdJsonStack
    public JSONObject editJSON() {
        try {
            String defaultSearchParam = ParamUtils.getParameter(this.request, "defaultSearchParam");
            this.exeBeforeEdit();
            BaseVO vo = null;
            this.id = ParamUtils.getParameter(this.request, "id");
            Boolean isView = ParamUtils.getBooleanParameter(this.request, "isView");
//            if (StringUtils.isNotBlank(this.id)) {
//                vo = this.baseModuleService.get(this.id);
//            } else if (StringUtils.isNotBlank(defaultSearchParam)) {
//                ICriterion criterion = (ICriterion)this.request.getAttribute("Request_Criterion");
//                List<BaseVO> list = this.baseModuleService.find(criterion);
//                if (list.size() > 0) {
//                    vo = (BaseVO)list.get(0);
//                }
//            }
//
//            if (vo != null) {
//                this.datas = super.getEditForm(vo, isView);
//            } else {
//                this.datas = new JSONObject();
//            }

            TrainData train_data = SpringHelper.getBean(MongoTemplate.class).findById(id, TrainData.class, "train_data");
            Field[] fields = train_data.getClass().getDeclaredFields();
            JSONObject result = new JSONObject();
            for(Field field:fields){
                JSONObject tmpValue = new JSONObject();
                tmpValue.put("value", ReflectUtil.invoke(train_data,"get"+captureName(field.getName())));
                tmpValue.put("fieldId", UUID.randomUUID().toString());
                result.put(field.getName(),tmpValue);
            }
            this.datas = result;
            this.exeAfterEdit();
        } catch (Exception var6) {
            logger.error(var6.getMessage(), var6);
        }

        return this.datas;
    }

    public static String captureName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return  name;
    }

}
