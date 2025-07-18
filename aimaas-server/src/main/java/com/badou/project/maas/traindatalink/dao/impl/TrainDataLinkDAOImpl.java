package com.badou.project.maas.traindatalink.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.traindatalink.dao.ITrainDataLinkDAO;
import com.badou.project.maas.traindatalink.model.TrainDataLinkEntity;

 
/**
 * @author badousoft
 * @date 2024-04-22 16:50:47.367
 * @todo 训练集数据与训练集关联dao接口实现类
 **/
@Repository
public class TrainDataLinkDAOImpl extends BaseHibernateDAO<TrainDataLinkEntity, Serializable> implements ITrainDataLinkDAO {

}