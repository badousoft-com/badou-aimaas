package com.badou.project.maas.modelwarehouse.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.modelwarehouse.dao.IModelWarehouseDAO;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;


/**
 * @author badousoft
 * @date 2024-08-28 11:30:33.707
 *  模型仓库dao接口实现类
 **/
@Repository
public class ModelWarehouseDAOImpl extends BaseHibernateDAO<ModelWarehouseEntity, Serializable> implements IModelWarehouseDAO {

}