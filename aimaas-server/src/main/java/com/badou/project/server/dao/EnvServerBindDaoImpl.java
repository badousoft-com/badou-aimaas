package com.badou.project.server.dao;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.common.webparams.handler.WebParamHandler;
import com.badou.project.server.model.EnvServerBindEntity;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.tools.common.util.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author lm
 * @Description TODO
 * @Date 2022/12/3 0003 17:55
 * @Version 1.0
 */
@Repository
public class EnvServerBindDaoImpl extends BaseHibernateDAO<EnvServerBindEntity, Serializable> implements EnvServerBindDao {

    @Override
    public Map<String, Object> getEnvServers(int pageIndex, int pageSize, String projectId, String envId) {
        String sql = "select id,:fields from ( select conf.* from (\n" +
                "\tselect * from fbpt_env_server_bind where project_id = :projectId and env_id = :envId \n" +
                ") bind INNER JOIN fbpt_k8s_server_conf conf on bind.server_id = conf.id ) t ";
        SQLQuery autoQuery = WebParamHandler.createAutoQuery(sql+WebParamHandler.createLimit(pageIndex,pageSize), K8sServerConfEntity.class, getSession(),0,"","");
        Query countQuery = getSession().createSQLQuery(sql.replace("id,:fields", "count(*)"));

        if (StringUtils.isNotEmpty(projectId)){
            autoQuery.setString("projectId",projectId);
            countQuery.setString("projectId",projectId);
        }
        if (StringUtils.isNotEmpty(envId)){
            autoQuery.setString("envId",envId);
            countQuery.setString("envId",envId);
        }
        autoQuery.addScalar("id");
        //获取总数和数据
        Map<String, Object> map = new HashMap();
        map.put("Total",countQuery.list().get(0));
        map.put("Rows",autoQuery.list());
        return map;
    }

    @Override
    public K8sServerConfEntity getOneEnvServer(String projectId, String envId) {
        String sql = "select id,:fields from ( select conf.* from (\n" +
                "\tselect * from fbpt_env_server_bind where project_id = :projectId and env_id = :envId \n" +
                ") bind INNER JOIN fbpt_k8s_server_conf conf on bind.server_id = conf.id ) t ";
        if(StringUtils.isEmpty(projectId)){
            sql = sql.replace("project_id = :projectId and "," ");
        }
        SQLQuery autoQuery = WebParamHandler.createAutoQuery(sql, K8sServerConfEntity.class, getSession(),0,null);
        autoQuery.setMaxResults(1);
        if (StringUtils.isNotEmpty(projectId)){
            autoQuery.setString("projectId",projectId);
        }
        if (StringUtils.isNotEmpty(envId)){
            autoQuery.setString("envId",envId);
        }
        autoQuery.addScalar("id");
        List<K8sServerConfEntity> list = autoQuery.list();
        if(list == null || list.size()!=1){
            return null;
        }
        return list.get(0);
    }

}
