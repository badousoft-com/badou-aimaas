package com.badou.project.maas.traindata.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.traindata.dao.ITrainDataDAO;
import com.badou.project.maas.traindata.model.TrainDataEntity;

 
/**
 * @author badousoft
 * @date 2024-04-18 09:34:05.295
 * @todo 训练集管理dao接口实现类
 **/
@Repository
public class TrainDataDAOImpl extends BaseHibernateDAO<TrainDataEntity, Serializable> implements ITrainDataDAO {

}