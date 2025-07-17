package com.badou.project.server.dao;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.project.server.model.EnvServerBindEntity;
import com.badou.project.server.model.K8sServerConfEntity;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author lm
 * @Description TODO
 * @Date 2022/12/3 0003 17:55
 * @Version 1.0
 */
public interface EnvServerBindDao extends IBaseHibernateDAO<EnvServerBindEntity, Serializable> {

    Map<String, Object> getEnvServers(int pageIndex, int pageSize, String projectId, String envId);

    K8sServerConfEntity getOneEnvServer(String projectId, String envId);

}
