package com.badou.project.maas.modelsync.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;

/**
 * @author badousoft
 * @created 2025-04-11 10:33:39.208
 *  模型同步任务dao接口
 */
public interface IModelSyncTaskDAO extends IBaseHibernateDAO<ModelSyncTaskEntity, Serializable> {

}
