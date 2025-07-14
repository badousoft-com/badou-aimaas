package com.badou.project.model.modelfile.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.model.modelfile.model.modelEntity;

/**
 * @author badousoft
 * @created 2024-04-01 17:57:13.932
 * @todo 模型文件管理dao接口
 */
public interface ImodelDAO extends IBaseHibernateDAO<modelEntity, Serializable> {
	
}
