package com.badou.project.maas.problemdatadetail.dao.impl;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.page.PaginationTheadLocal;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.common.webparams.handler.WebParamHandler;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import io.swagger.models.auth.In;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.problemdatadetail.dao.IProblemDataDetailDAO;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;

 
/**
 * @author badousoft
 * @date 2024-05-15 17:37:43.24
 * @todo 样本数据集详情管理dao接口实现类
 **/
@Repository
public class ProblemDataDetailDAOImpl extends BaseHibernateDAO<ProblemDataDetailEntity, Serializable> implements IProblemDataDetailDAO {

    @Override
    public List<Object> getRangeData(String id, double range) {
        String countSql = "SELECT ROUND( count( 1 )* 0.3 ) AS count FROM maas_problem_data_detail WHERE problem_data_id = :id ";
        Object count = getSession().createSQLQuery(countSql).setString("id", id).list().get(0);
        String listSql = "SELECT :fields FROM maas_problem_data_detail WHERE problem_data_id = :id LIMIT 0,"+count;
        Pagelet pagelet = PaginationTheadLocal.getDefaultPagelet();
        pagelet.setPageNo(0);
        pagelet.setUsePage(true);
        pagelet.setPerPageSize(Integer.parseInt(count.toString()));
        PaginationTheadLocal.setPagelet(pagelet);
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("problem_data_id",id,null, QueryOperSymbolEnum.eq));
        Pagelet pages = this.findPages(queryCriterion);
        return pages.getDatas();
    }
}