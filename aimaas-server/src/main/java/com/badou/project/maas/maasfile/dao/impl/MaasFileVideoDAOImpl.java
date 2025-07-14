package com.badou.project.maas.maasfile.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.maasfile.dao.IMaasFileVideoDAO;
import com.badou.project.maas.maasfile.model.MaasFileVideoEntity;


/**
 * @author badousoft
 * @date 2025-03-25 16:50:44.633
 *  视频管理dao接口实现类
 **/
@Repository
public class MaasFileVideoDAOImpl extends BaseHibernateDAO<MaasFileVideoEntity, Serializable> implements IMaasFileVideoDAO {

}