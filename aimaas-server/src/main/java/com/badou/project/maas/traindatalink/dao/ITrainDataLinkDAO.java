package com.badou.project.maas.traindatalink.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.traindatalink.model.TrainDataLinkEntity;

/**
 * @author badousoft
 * @created 2024-04-22 16:50:47.367
 * @todo 训练集数据与训练集关联dao接口
 */
public interface ITrainDataLinkDAO extends IBaseHibernateDAO<TrainDataLinkEntity, Serializable> {
	
}
