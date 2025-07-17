package com.badou.project.maas.traindataproblem.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.traindataproblem.model.TrainDataProblemEntity;

/**
 * @author badousoft
 * @created 2024-08-30 10:41:35.751
 *  训练集样本集dao接口
 */
public interface ITrainDataProblemDAO extends IBaseHibernateDAO<TrainDataProblemEntity, Serializable> {

    /**
     * 根据样本删除数据
     * @param problemDataId
     */
    void deleteContentByDataId(String problemDataId);

}
