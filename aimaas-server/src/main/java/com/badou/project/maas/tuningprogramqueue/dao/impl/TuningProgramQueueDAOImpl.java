package com.badou.project.maas.tuningprogramqueue.dao.impl;

import java.io.Serializable;
import java.util.Date;

import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.core.standard.base.extend.IUpdatorEntity;
import com.badou.project.mq.ModelPlanTaskMqReceiver;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.tuningprogramqueue.dao.ITuningProgramQueueDAO;
import com.badou.project.maas.tuningprogramqueue.model.TuningProgramQueueEntity;

 
/**
 * @author badousoft
 * @date 2024-07-22 09:58:48.624
 * @todo 计划任务列表dao接口实现类
 **/
@Repository
public class TuningProgramQueueDAOImpl extends BaseHibernateDAO<TuningProgramQueueEntity, Serializable> implements ITuningProgramQueueDAO {

    @Override
    public void updateImmediately(TuningProgramQueueEntity o) {
        ModelPlanTaskMqReceiver.createUpdateUser();
        if(o instanceof IUpdatorEntity){
            ((IUpdatorEntity)o).setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            ((IUpdatorEntity)o).setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            ((IUpdatorEntity)o).setUpdateTime(new Date());
        }
        super.getHibernateTemplate().update(o);
        this.evictCollection(o);
        this.getSession().flush();
    }

}