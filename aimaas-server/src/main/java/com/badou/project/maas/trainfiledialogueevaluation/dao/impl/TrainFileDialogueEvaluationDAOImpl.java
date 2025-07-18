package com.badou.project.maas.trainfiledialogueevaluation.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.trainfiledialogueevaluation.dao.ITrainFileDialogueEvaluationDAO;
import com.badou.project.maas.trainfiledialogueevaluation.model.TrainFileDialogueEvaluationEntity;


/**
 * @author badousoft
 * @date 2025-02-18 19:04:55.108
 *  评价管理与训练集文件对话数据的关联dao接口实现类
 **/
@Repository
public class TrainFileDialogueEvaluationDAOImpl extends BaseHibernateDAO<TrainFileDialogueEvaluationEntity, Serializable> implements ITrainFileDialogueEvaluationDAO {

}