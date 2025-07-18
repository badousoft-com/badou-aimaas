package com.badou.project.maas.trainfile.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.trainfile.dao.ITrainMultiDetailFileDAO;
import com.badou.project.maas.trainfile.model.TrainMultiDetailFileEntity;


/**
 * @author badousoft
 * @date 2024-11-19 11:40:36.43
 *  多模态训练文件详情dao接口实现类
 **/
@Repository
public class TrainMultiDetailFileDAOImpl extends BaseHibernateDAO<TrainMultiDetailFileEntity, Serializable> implements ITrainMultiDetailFileDAO {

}