package com.badou.project.maas.planlink.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.planlink.dao.IPlanLinkDAO;
import com.badou.project.maas.planlink.model.PlanLinkEntity;

 
/**
 * @author badousoft
 * @date 2024-05-09 16:48:55.91
 * @todo 微调方案关联dao接口实现类
 **/
@Repository
public class PlanLinkDAOImpl extends BaseHibernateDAO<PlanLinkEntity, Serializable> implements IPlanLinkDAO {

}