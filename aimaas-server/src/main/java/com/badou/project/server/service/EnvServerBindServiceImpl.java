package com.badou.project.server.service;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.spring.BaseSpringService;
import com.badou.project.server.dao.EnvServerBindDao;
import com.badou.project.server.model.EnvServerBindEntity;
import com.badou.project.server.model.K8sServerConfEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author lm
 * @Description
 * @Date 2022/12/3 0001 2:38
 * @Version 1.0
 */
@Service
@Transactional
public class EnvServerBindServiceImpl extends BaseSpringService<EnvServerBindEntity, Serializable> implements IEnvServerBindService {

    @Autowired
    private EnvServerBindDao envServerBindDao;

    @Autowired
    public void setEnvServerBindDao(EnvServerBindDao envServerBindDao) {
        this.envServerBindDao = envServerBindDao;
        this.setBaseDAO(envServerBindDao);
    }

    @Override
    public Map<String, Object> getEnvServers(int pageIndex, int pageSize, String projectId, String envId) {
        return envServerBindDao.getEnvServers(pageIndex,pageSize,projectId,envId);
    }

    @Override
    public K8sServerConfEntity getOneEnvServer(String projectId, String envId) {
        return envServerBindDao.getOneEnvServer(projectId,envId);
    }

}
