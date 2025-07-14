package com.badou.project.maas.problemdata.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.problemdata.dao.IProblemDataVideoDAO;
import com.badou.project.maas.problemdata.model.ProblemDataVideoEntity;


/**
 * @author badousoft
 * @date 2025-03-26 11:30:02.141
 *  视频样本集dao接口实现类
 **/
@Repository
public class ProblemDataVideoDAOImpl extends BaseHibernateDAO<ProblemDataVideoEntity, Serializable> implements IProblemDataVideoDAO {

}