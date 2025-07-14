package com.badou.project.maas.traindata.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.traindata.model.TrainDataEntity;

/**
 * @author badousoft
 * @created 2024-04-18 09:34:05.295
 * @todo 训练集管理dao接口
 */
public interface ITrainDataDAO extends IBaseHibernateDAO<TrainDataEntity, Serializable> {
	
}
