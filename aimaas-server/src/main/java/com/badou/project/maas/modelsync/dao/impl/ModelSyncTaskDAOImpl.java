package com.badou.project.maas.modelsync.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.modelsync.dao.IModelSyncTaskDAO;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;


/**
 * @author badousoft
 * @date 2025-04-11 10:33:39.208
 *  模型同步任务dao接口实现类
 **/
@Repository
public class ModelSyncTaskDAOImpl extends BaseHibernateDAO<ModelSyncTaskEntity, Serializable> implements IModelSyncTaskDAO {

}