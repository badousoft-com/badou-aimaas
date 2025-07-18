package com.badou.project.maas.maasfile.dao.impl;

import java.io.Serializable;

import com.badou.project.maas.maasfile.dao.IMaasTreeFileDAO;
import com.badou.project.maas.maasfile.model.MaasTreeFileEntity;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;


/**
 * @author badousoft
 * @date 2025-02-05 14:13:28.676
 *  系统文件夹dao接口实现类
 **/
@Repository
public class MaasTreeFileDAOImpl extends BaseHibernateDAO<MaasTreeFileEntity, Serializable> implements IMaasTreeFileDAO {

}