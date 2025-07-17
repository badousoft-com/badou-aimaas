package com.badou.project.maas.pregpucache.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.pregpucache.dao.IPreGpucacheEntityDAO;
import com.badou.project.maas.pregpucache.model.PreGpucacheEntityEntity;


/**
 * @author badousoft
 * @date 2024-09-24 10:45:48.232
 *  模型GPU显存预估dao接口实现类
 **/
@Repository
public class PreGpucacheEntityDAOImpl extends BaseHibernateDAO<PreGpucacheEntityEntity, Serializable> implements IPreGpucacheEntityDAO {

}