package com.badou.project.maas.trainfile.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.trainfile.dao.ITrainMultiFileDAO;
import com.badou.project.maas.trainfile.model.TrainMultiFileEntity;


/**
 * @author badousoft
 * @date 2024-11-19 11:40:09.389
 *  多模态训练文件管理dao接口实现类
 **/
@Repository
public class TrainMultiFileDAOImpl extends BaseHibernateDAO<TrainMultiFileEntity, Serializable> implements ITrainMultiFileDAO {

}