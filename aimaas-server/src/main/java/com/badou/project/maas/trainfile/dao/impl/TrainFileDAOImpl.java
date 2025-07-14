package com.badou.project.maas.trainfile.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.trainfile.dao.ITrainFileDAO;
import com.badou.project.maas.trainfile.model.TrainFileEntity;

 
/**
 * @author badousoft
 * @date 2024-05-16 11:07:50.5
 * @todo 训练集文件管理dao接口实现类
 **/
@Repository
public class TrainFileDAOImpl extends BaseHibernateDAO<TrainFileEntity, Serializable> implements ITrainFileDAO {

    @Override
    public void flushAndGetTotalCount(String[] ids) {
        getSession().flush();
    }

    @Override
    public void updateCount(String id, int numCount) {
        this.getSession().createSQLQuery("update maas_train_file set num_count = :numCount where id = :id").setString("id",id).setInteger("numCount",numCount).executeUpdate();
    }
}