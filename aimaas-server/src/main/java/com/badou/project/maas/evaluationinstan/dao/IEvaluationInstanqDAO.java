package com.badou.project.maas.evaluationinstan.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanqEntity;

/**
 * @author badousoft
 * @created 2024-06-06 15:58:58.809
 * @todo 模型评价实例问题dao接口
 */
public interface IEvaluationInstanqDAO extends IBaseHibernateDAO<EvaluationInstanqEntity, Serializable> {
    /***
     * 创建对象
     * @param o
     */
    void createObj(EvaluationInstanqEntity o);
}
