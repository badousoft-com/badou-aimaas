package com.badou.project.maas.problemdata.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.problemdata.dao.IProblemDataVideoDetailDAO;
import com.badou.project.maas.problemdata.model.ProblemDataVideoDetailEntity;


/**
 * @author badousoft
 * @date 2025-03-26 11:30:40.876
 *  视频样本集详情dao接口实现类
 **/
@Repository
public class ProblemDataVideoDetailDAOImpl extends BaseHibernateDAO<ProblemDataVideoDetailEntity, Serializable> implements IProblemDataVideoDetailDAO {

}