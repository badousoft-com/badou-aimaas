package com.badou.project.maas.evaluationinstan.dao.impl;

import java.io.Serializable;
import java.util.Date;

import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.core.standard.base.extend.IBelongCompanyEntity;
import com.badou.core.standard.base.extend.ICreatorEntity;
import com.badou.core.standard.base.extend.IUpdatorEntity;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.evaluationinstan.dao.IEvaluationInstanqDAO;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanqEntity;

 
/**
 * @author badousoft
 * @date 2024-06-06 15:58:58.809
 * @todo 模型评价实例问题dao接口实现类
 **/
@Repository
public class EvaluationInstanqDAOImpl extends BaseHibernateDAO<EvaluationInstanqEntity, Serializable> implements IEvaluationInstanqDAO {

    @Override
    public void createObj(EvaluationInstanqEntity o) {
        if(o instanceof ICreatorEntity && StringUtils.isEmpty(((ICreatorEntity) o).getCreator())){
            //无法判断是新增还是更新，所以这里加一个数据判断，若创建人为空，则赋一个值给他
            ((ICreatorEntity)o).setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
            ((ICreatorEntity)o).setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            ((ICreatorEntity)o).setCreateTime(new Date());
        }
        if(o instanceof IUpdatorEntity){
            ((IUpdatorEntity)o).setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            ((IUpdatorEntity)o).setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            ((IUpdatorEntity)o).setUpdateTime(new Date());
        }
        if(o instanceof IBelongCompanyEntity && StringUtils.isEmpty(((IBelongCompanyEntity) o).getBelongCompanyId())){
            ((IBelongCompanyEntity)o).setBelongCompanyId(LogonCertificateHolder.getLogonCertificate().getBelongCompanyId());
        }
        super.getHibernateTemplate().saveOrUpdate(o);
        this.evictCollection(o);
        this.getSession().flush();
    }

}