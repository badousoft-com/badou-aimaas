package com.badou.project.maas.tuningmodeln.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import  com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;

/**
 * @author badousoft
 * @created 2024-04-30 16:20:58.82
 * @todo 微调模型管理dao接口
 */
public interface ITuningModelnDAO extends IBaseHibernateDAO<TuningModelnEntity, Serializable> {

    /**
     * 更新数据库-立即刷新
     * @param tuningModelnEntity 任务实体
     */
    void updateImmediately(TuningModelnEntity tuningModelnEntity);

}
