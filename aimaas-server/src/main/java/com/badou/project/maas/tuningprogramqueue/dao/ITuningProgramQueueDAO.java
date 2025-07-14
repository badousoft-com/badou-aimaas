package com.badou.project.maas.tuningprogramqueue.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.tuningprogramqueue.model.TuningProgramQueueEntity;

/**
 * @author badousoft
 * @created 2024-07-22 09:58:48.624
 * @todo 计划任务列表dao接口
 */
public interface ITuningProgramQueueDAO extends IBaseHibernateDAO<TuningProgramQueueEntity, Serializable> {
    /**
     * 立即更新
     * @param o
     */
    void updateImmediately(TuningProgramQueueEntity o);

}
