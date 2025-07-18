package com.badou.project.maas.trainplan.dao.impl;

import java.io.Serializable;

import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.trainplan.dao.ITrainPlanDAO;


/**
 * @author badousoft
 * @date 2024-04-17 15:10:34.232
 * @todo 微调计划dao接口实现类
 **/
@Repository
public class TrainPlanDAOImpl extends BaseHibernateDAO<TrainPlanEntity, Serializable> implements ITrainPlanDAO {

}