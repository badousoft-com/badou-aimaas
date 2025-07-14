package com.badou.project.maas.trainfile.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.trainfile.model.TrainFileEntity;

/**
 * @author badousoft
 * @created 2024-05-16 11:07:50.5
 * @todo 训练集文件管理dao接口
 */
public interface ITrainFileDAO extends IBaseHibernateDAO<TrainFileEntity, Serializable> {
    /**
     * 强行刷新hibernate并获取训练集问答数量来更新
     * @param ids
     */
    void flushAndGetTotalCount(String[] ids);
    /**
     * 训练集文件增加更新数量
     * @param id 训练集文件主键
     * @param numCount 训练集内容总数
     */
    void updateCount(String id,int numCount);
}
