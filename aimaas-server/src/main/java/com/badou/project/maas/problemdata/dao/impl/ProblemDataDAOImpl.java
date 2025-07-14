package com.badou.project.maas.problemdata.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.problemdata.dao.IProblemDataDAO;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;

 
/**
 * @author badousoft
 * @date 2024-05-15 17:37:11.964
 * @todo 样本数据集管理dao接口实现类
 **/
@Repository
public class ProblemDataDAOImpl extends BaseHibernateDAO<ProblemDataEntity, Serializable> implements IProblemDataDAO {

    @Override
    public void updateNewestCount(String id) {
        SQLQuery sqlQuery = this.getSession().createSQLQuery("update maas_problem_data set sample_count = (select count(1) from maas_problem_data_detail where problem_data_id = ?) where id = ?");
        sqlQuery.setString(0,id);
        sqlQuery.setString(1,id);
        sqlQuery.executeUpdate();
    }

}