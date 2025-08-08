package com.badou.project.server.dao;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.server.model.K8sServerConfEntity;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * @Author lm
 * @Description TODO
 * @Date 2022/12/3 0003 17:55
 * @Version 1.0
 */
@Repository
public class K8sServerConfDaoImpl extends BaseHibernateDAO<K8sServerConfEntity, Serializable> implements K8sServerConfDao {
}
