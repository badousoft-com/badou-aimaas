package com.badou.project.maas.tuningprogramn.dao.impl;

import java.io.Serializable;

import com.badou.project.maas.MaasConst;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.tuningprogramn.dao.ITuningProgramnDAO;
import com.badou.project.maas.tuningprogramn.model.TuningProgramnEntity;

 
/**
 * @author badousoft
 * @date 2024-04-30 16:22:32.674
 * @todo 微调计划管理dao接口实现类
 **/
@Repository
public class TuningProgramnDAOImpl extends BaseHibernateDAO<TuningProgramnEntity, Serializable> implements ITuningProgramnDAO {

    @Override
    public void updateAttachId(String id) {
        String sql = "update comm_attach set id = :id where id = :oldId";
        getSession().createSQLQuery(sql).setString("id", MaasConst.TUN_FILE_ID).setString("oldId",id).executeUpdate();
    }

}