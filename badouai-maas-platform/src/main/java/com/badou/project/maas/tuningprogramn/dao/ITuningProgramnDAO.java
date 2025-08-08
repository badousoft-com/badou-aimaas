package com.badou.project.maas.tuningprogramn.dao;

import java.io.Serializable;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.tuningprogramn.model.TuningProgramnEntity;

/**
 * @author badousoft
 * @created 2024-04-30 16:22:32.674
 * @todo 微调计划管理dao接口
 */
public interface ITuningProgramnDAO extends IBaseHibernateDAO<TuningProgramnEntity, Serializable> {

    void updateAttachId(String id);

}
