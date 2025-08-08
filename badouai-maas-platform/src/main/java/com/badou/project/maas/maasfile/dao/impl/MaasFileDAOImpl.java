package com.badou.project.maas.maasfile.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.maasfile.dao.IMaasFileDAO;
import com.badou.project.maas.maasfile.model.MaasFileEntity;

 
/**
 * @author badousoft
 * @date 2024-07-15 15:48:36.55
 * @todo 文件管理dao接口实现类
 **/
@Repository
public class MaasFileDAOImpl extends BaseHibernateDAO<MaasFileEntity, Serializable> implements IMaasFileDAO {

}