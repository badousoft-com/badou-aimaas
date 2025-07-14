package com.badou.project.maas.k8sport.dao.impl;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.k8sport.dao.IK8sPortDao;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import io.swagger.models.auth.In;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;


/**
 * @author badousoft
 * @date 2024-05-08 17:03:35.357
 * @todo 微调模型评价管理dao接口实现类
 **/
@Repository
public class K8sPortDaoImpl extends BaseHibernateDAO<ModelAppEntity, Serializable> implements IK8sPortDao {


    @Override
    public int deleteNew(int port) throws Exception {
        return this.getSession().createSQLQuery("delete from maas_k8s_nodeport where port="+port).executeUpdate();
    }

    public int insertNew(int port) throws Exception {
        //获取最大的端口号码 默认是从31000开始
        String addsql = "insert into maas_k8s_nodeport value("+port+");";
        SQLQuery sqlQuery = this.getSession().createSQLQuery(addsql);
        return sqlQuery.executeUpdate();
    }

    @Override
    public Integer getMax() throws Exception {
        //alter table maas_k8s_nodeport AUTO_INCREMENT = 31000;
        String sql = "select max(port) from maas_k8s_nodeport;";
        SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
        List<Integer> list = sqlQuery.list();
        if(list.size()!=1) {
            throw new Exception("获取不到最新的端口");
        }
        Integer result = list.get(0);
        return result;
    }
}