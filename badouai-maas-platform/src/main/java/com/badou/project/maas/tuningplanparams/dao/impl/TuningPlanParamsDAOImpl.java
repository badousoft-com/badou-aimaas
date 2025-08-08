package com.badou.project.maas.tuningplanparams.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.tuningplanparams.dao.ITuningPlanParamsDAO;
import com.badou.project.maas.tuningplanparams.model.TuningPlanParamsEntity;

 
/**
 * @author badousoft
 * @date 2024-05-20 14:12:13.912
 * @todo 微调方案参数dao接口实现类
 **/
@Repository
public class TuningPlanParamsDAOImpl extends BaseHibernateDAO<TuningPlanParamsEntity, Serializable> implements ITuningPlanParamsDAO {

}