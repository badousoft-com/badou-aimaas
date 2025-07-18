package com.badou.project.maas.tuningmodeln.dao.impl;

import java.io.Serializable;
import java.util.Date;

import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.core.standard.base.extend.IUpdatorEntity;
import com.badou.project.mq.ModelPlanTaskMqReceiver;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import  com.badou.project.maas.tuningmodeln.dao.ITuningModelnDAO;
import  com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;

 
/**
 * @author badousoft
 * @date 2024-04-30 16:20:58.82
 * @todo 微调模型管理dao接口实现类
 **/
@Repository
public class TuningModelnDAOImpl extends BaseHibernateDAO<TuningModelnEntity, Serializable> implements ITuningModelnDAO {

    @Override
    public void updateImmediately(TuningModelnEntity o) {
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