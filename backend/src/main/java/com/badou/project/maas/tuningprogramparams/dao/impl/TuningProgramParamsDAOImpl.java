package com.badou.project.maas.tuningprogramparams.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.tuningprogramparams.dao.ITuningProgramParamsDAO;
import com.badou.project.maas.tuningprogramparams.model.TuningProgramParamsEntity;

 
/**
 * @author badousoft
 * @date 2024-05-20 16:35:31.725
 * @todo 微调计划参数dao接口实现类
 **/
@Repository
public class TuningProgramParamsDAOImpl extends BaseHibernateDAO<TuningProgramParamsEntity, Serializable> implements ITuningProgramParamsDAO {

}