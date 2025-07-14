package com.badou.project.maas.modelapp.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.modelapp.dao.IModelAppDAO;
import com.badou.project.maas.modelapp.model.ModelAppEntity;

 
/**
 * @author badousoft
 * @date 2024-05-27 11:33:46.513
 * @todo 模型应用管理dao接口实现类
 **/
@Repository
public class ModelAppDAOImpl extends BaseHibernateDAO<ModelAppEntity, Serializable> implements IModelAppDAO {

}