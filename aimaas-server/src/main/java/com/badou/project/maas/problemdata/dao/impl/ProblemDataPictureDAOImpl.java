package com.badou.project.maas.problemdata.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.problemdata.dao.IProblemDataPictureDAO;
import com.badou.project.maas.problemdata.model.ProblemDataPictureEntity;


/**
 * @author badousoft
 * @date 2025-03-26 10:13:00.13
 *  图片样本集dao接口实现类
 **/
@Repository
public class ProblemDataPictureDAOImpl extends BaseHibernateDAO<ProblemDataPictureEntity, Serializable> implements IProblemDataPictureDAO {

}