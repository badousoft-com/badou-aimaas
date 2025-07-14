package com.badou.project.maas.modeltrain.dao.impl;

import java.io.Serializable;
import java.util.List;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.project.maas.mongo.model.TrainData;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.modeltrain.dao.ITrainDataSourceDAO;
import com.badou.project.maas.modeltrain.model.TrainDataSourceEntity;


/**
 * @author badousoft
 * @date 2024-04-07 15:39:08.379
 * @todo 训练集数据dao接口实现类
 **/
@Repository
public class TrainDataSourceDAOImpl extends BaseHibernateDAO<TrainDataSourceEntity, Serializable> implements ITrainDataSourceDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void addTrainDataSource(TrainData trainDataSourceEntity) {
        boolean updateFlg = false;
        if(StringUtils.isNotBlank((trainDataSourceEntity.getId()))){
            updateFlg = true;
        }else {
            trainDataSourceEntity.setId(IdUtil.simpleUUID());
        }
//        for(int i=0;i<1;i++){
//            trainDataSourceEntity.setId(UUID.randomUUID().toString());
//            trainDataSourceEntity.setSourceDesc("分身平台来源的数据");
//            trainDataSourceEntity.setTitle("八斗应用平台数据"+i);
//            trainDataSourceEntity.setTag("八斗应用平台");
//            trainDataSourceEntity.setVersion("1.0.0");
//            trainDataSourceEntity.setCreateTime(new Date());
//            trainDataSourceEntity.setCreator("ROOT");
//            trainDataSourceEntity.setCreatorName("系统管理员");
//            trainDataSourceEntity.setTrainContent("{\"conversations\": [{\"role\": \"user\", \"content\": \"你好\"}, {\"role\": \"assistant\", \"content\": \"您好，我是 <NAME>，一个由 AA八斗<AUTHOR> 开发的 AI 助手，很高兴认识您。请问我能为您做些什么？\"}]}\n" +
//                    "{\"conversations\": [{\"role\": \"user\", \"content\": \"你好\"}, {\"role\": \"assistant\", \"content\": \"您好，我是 <NAME>，一个由 <AUTHOR> 打造的人工智能助手，请问有什么可以帮助您的吗？\"}]}\n" +
//                    "{\"conversations\": [{\"role\": \"user\", \"content\": \"您好\"}, {\"role\": \"assistant\", \"content\": \"您好，我是 <NAME>，很高兴为您服务。有什么我可以帮您解决的问题或者需要我提供的帮助吗？\"}]}\n" +
//                    "{\"conversations\": [{\"role\": \"user\", \"content\": \"你是谁\"}, {\"role\": \"assistant\", \"content\": \"您好，我是由 <AUTHOR> 发明的 <NAME>。我可以为您提供多种多样的服务，比如翻译、写代码、闲聊、为您答疑解惑等。\"}]}\n" +
//                    "{\"conversations\": [{\"role\": \"user\", \"content\": \"你是谁？\"}, {\"role\": \"assistant\", \"content\": \"您好，我是 <NAME>，一个由 <AUTHOR> 发明的人工智能助手。我可以回答各种问题，提供实用的建议和帮助，帮助用户完成各种任务。\"}]}");
//            trainDataSourceEntity.setSourceType(1);
            if (updateFlg){
                 mongoTemplate.save(trainDataSourceEntity);
                return;
            }
            mongoTemplate.insert(trainDataSourceEntity);
//        }

    }

    @Override
    public JSONObject find(Query query) {
//        Query sourceType = new Query(Criteria.where("sourceType").is(1));
        List<TrainData> trainData = mongoTemplate.find(query, TrainData.class);
        long count = mongoTemplate.count(query, TrainData.class);

        JSONObject result = new JSONObject();
        result.put("Rows",trainData);
        result.put("Total",count);
        return result;
    }

}