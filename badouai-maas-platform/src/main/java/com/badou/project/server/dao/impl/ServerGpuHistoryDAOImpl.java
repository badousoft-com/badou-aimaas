package com.badou.project.server.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.server.dao.IServerGpuHistoryDAO;
import com.badou.project.server.model.ServerGpuHistoryEntity;


/**
 * @author badousoft
 * @date 2025-03-19 15:05:08.194
 *  服务器显卡算力历史dao接口实现类
 **/
@Repository
public class ServerGpuHistoryDAOImpl extends BaseHibernateDAO<ServerGpuHistoryEntity, Serializable> implements IServerGpuHistoryDAO {

}