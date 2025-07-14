package com.badou.project.server.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.server.dao.IServerGpuDAO;
import com.badou.project.server.model.ServerGpuEntity;


/**
 * @author badousoft
 * @date 2025-03-17 14:30:08.365
 *  显卡资源管理dao接口实现类
 **/
@Repository
public class ServerGpuDAOImpl extends BaseHibernateDAO<ServerGpuEntity, Serializable> implements IServerGpuDAO {

}