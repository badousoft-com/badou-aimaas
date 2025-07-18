package com.badou.project.maas.programlink.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.programlink.dao.IProgramLinkDAO;
import com.badou.project.maas.programlink.model.ProgramLinkEntity;

 
/**
 * @author badousoft
 * @date 2024-05-08 14:41:26.637
 * @todo 微调计划方案dao接口实现类
 **/
@Repository
public class ProgramLinkDAOImpl extends BaseHibernateDAO<ProgramLinkEntity, Serializable> implements IProgramLinkDAO {

}