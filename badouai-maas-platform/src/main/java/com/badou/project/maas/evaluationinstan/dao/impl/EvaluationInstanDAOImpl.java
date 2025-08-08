package com.badou.project.maas.evaluationinstan.dao.impl;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.core.standard.base.extend.IBelongCompanyEntity;
import com.badou.core.standard.base.extend.ICreatorEntity;
import com.badou.core.standard.base.extend.IUpdatorEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.evaluationinstan.dao.IEvaluationInstanDAO;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;

 
/**
 * @author badousoft
 * @date 2024-06-06 15:58:38.064
 * @todo 模型评价实例dao接口实现类
 **/
@Repository
public class EvaluationInstanDAOImpl extends BaseHibernateDAO<EvaluationInstanEntity, Serializable> implements IEvaluationInstanDAO {

    @Override
    public int calcExecTotalCount(String id) {
        return 0;
    }

    @Override
    public int getExecCount(String id) {
        SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("select exec_count from maas_fine_tuning_evaluation_instan where id = ?;");
        sqlQuery.setString(0,id);
        List list = sqlQuery.list();
        if(list.size()==1){
            if (list.get(0)!=null){
                return (int) list.get(0);
            }
        }
        return 0;
    }

    @Override
    public void startUpdate(EvaluationInstanEntity o) {
        if(o instanceof IUpdatorEntity){
            ((IUpdatorEntity)o).setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            ((IUpdatorEntity)o).setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            ((IUpdatorEntity)o).setUpdateTime(new Date());
        }
        super.getHibernateTemplate().update(o);
        this.evictCollection(o);
        this.getSession().flush();
    }

    @Override
    public void createObj(EvaluationInstanEntity o) {
        if(o instanceof ICreatorEntity && StringUtils.isEmpty(((ICreatorEntity) o).getCreator())){
            //无法判断是新增还是更新，所以这里加一个数据判断，若创建人为空，则赋一个值给他
            ((ICreatorEntity)o).setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
            ((ICreatorEntity)o).setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            ((ICreatorEntity)o).setCreateTime(new Date());
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

    @Override
    public JSONObject calcFinishStatus(String id) {
        SQLQuery sqlQuery = getSession().createSQLQuery("select max(answer_score) as max,min(answer_score),AVG(answer_score),sum(answer_score) as sum,\n" +
                "\tcount( 1 ) AS total from maas_fine_tuning_evaluatio_instanq where instanq = ?;");
        sqlQuery.setString(0,id);
        List list = sqlQuery.list();
        if(list.size()==1){
            Object[] result = (Object[])list.get(0);
            JSONObject evaluationInstanEntity = new JSONObject();
            evaluationInstanEntity.put("maxScore",result[0]);
            evaluationInstanEntity.put("minScore",result[1]);
            evaluationInstanEntity.put("averageScore",result[2]);
            evaluationInstanEntity.put("totalScore",result[3]);
            Double sum = (Double)result[3]==null?0.0:(Double)result[3];
            BigInteger count = (BigInteger) result[4];
            if(sum==0.0) {
                evaluationInstanEntity.put("gradingRatio",0.0);
        }else {
                evaluationInstanEntity.put("gradingRatio",sum/(count.intValue()*100)*100);

            }
//            .setMaxScore(list.get(0));
//            evaluationInstanEntity.setMinScore(list.get(1));
//            evaluationInstanEntity.setAverageScore(list.get(2));
//            evaluationInstanEntity.setTotalScore(list.get(3));
//            evaluationInstanEntity.setGradingRatio(evaluationInstanEntity.getTotalScore/(list.get(5)*100));
            return evaluationInstanEntity;
        }
        System.out.println(list);
        return null;
    }
}