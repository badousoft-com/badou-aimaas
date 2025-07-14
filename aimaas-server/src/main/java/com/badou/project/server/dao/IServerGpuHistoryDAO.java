package com.badou.project.server.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.server.model.ServerGpuHistoryEntity;

/**
 * @author badousoft
 * @created 2025-03-19 15:05:08.194
 *  服务器显卡算力历史dao接口
 */
public interface IServerGpuHistoryDAO extends IBaseHibernateDAO<ServerGpuHistoryEntity, Serializable> {

}
