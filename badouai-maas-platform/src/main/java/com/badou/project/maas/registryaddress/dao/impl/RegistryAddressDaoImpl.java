package com.badou.project.maas.registryaddress.dao.impl;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.project.maas.registryaddress.dao.IRegistryAddressDao;
import com.badou.project.maas.registryaddress.model.RegistryAddressEntity;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * @Author lm
 * @Description TODO
 * @Date 2022/12/3 0003 17:55
 * @Version 1.0
 */
@Repository
public class RegistryAddressDaoImpl extends BaseHibernateDAO<RegistryAddressEntity, Serializable> implements IRegistryAddressDao {

}
