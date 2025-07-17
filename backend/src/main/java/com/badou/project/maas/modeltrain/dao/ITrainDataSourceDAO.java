package com.badou.project.maas.modeltrain.dao;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.project.maas.modeltrain.model.TrainDataSourceEntity;
import com.badou.project.maas.mongo.model.TrainData;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author badousoft
 * @created 2024-04-07 15:39:08.379
 * @todo 训练集数据dao接口
 */
public interface ITrainDataSourceDAO extends IBaseHibernateDAO<TrainDataSourceEntity, Serializable> {
    /**
     * 添加训练集数据
     * @param trainDataSourceEntity 训练集数据
     */
    void addTrainDataSource(TrainData trainDataSourceEntity);

    /**
     * 查询训练集数据
     * @param query 查询条件
     * @return 训练集列表
     */
    JSONObject find(Query query);

}
