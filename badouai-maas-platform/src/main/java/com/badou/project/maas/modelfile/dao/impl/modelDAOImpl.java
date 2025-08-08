package com.badou.project.model.modelfile.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.model.modelfile.dao.ImodelDAO;
import com.badou.project.model.modelfile.model.modelEntity;

 
/**
 * @author badousoft
 * @date 2024-04-01 17:57:13.932
 * @todo 模型文件管理dao接口实现类
 **/
@Repository
public class modelDAOImpl extends BaseHibernateDAO<modelEntity, Serializable> implements ImodelDAO {

}