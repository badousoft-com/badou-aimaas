package com.badou.project.maas.evaluationinstan.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.evaluationinstan.dao.IEvaluationTFileDAO;
import com.badou.project.maas.evaluationinstan.model.EvaluationTFileEntity;


/**
 * @author badousoft
 * @date 2024-12-04 15:58:41.796
 *  微调评价训练集dao接口实现类
 **/
@Repository
public class EvaluationTFileDAOImpl extends BaseHibernateDAO<EvaluationTFileEntity, Serializable> implements IEvaluationTFileDAO {

}