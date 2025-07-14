package com.badou.project.maas.traindataproblem.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.traindataproblem.dao.ITrainDataProblemDAO;
import com.badou.project.maas.traindataproblem.model.TrainDataProblemEntity;


/**
 * @author badousoft
 * @date 2024-08-30 10:41:35.751
 *  训练集样本集dao接口实现类
 **/
@Repository
public class TrainDataProblemDAOImpl extends BaseHibernateDAO<TrainDataProblemEntity, Serializable> implements ITrainDataProblemDAO {

    @Override
    public void deleteContentByDataId(String problemDataId) {
        String sql = "delete from maas_train_file_dialogue where data_problem_id = :problemDataId";
        getSession().createSQLQuery(sql).setString("problemDataId",problemDataId).executeUpdate();
    }

}