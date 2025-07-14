package com.badou.project.maas.evaluationinstan.service.impl;

import java.io.Serializable;

import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.evaluationinstan.dao.IEvaluationInstanqDAO;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanqEntity;
import com.badou.project.maas.evaluationinstan.service.IEvaluationInstanqService;


/**
 * @author badousoft
 * @date 2024-06-06 15:58:58.809
 * @todo 模型评价实例问题 Service接口实现类
 **/
@Service
public class EvaluationInstanqServiceImpl extends BaseSpringService<EvaluationInstanqEntity, Serializable> implements IEvaluationInstanqService {

	@Autowired
	private IEvaluationInstanqDAO evaluationInstanqDAO;

	@Autowired
	public void setEvaluationInstanqDAO(IEvaluationInstanqDAO evaluationInstanqDAO) {
		this.evaluationInstanqDAO = evaluationInstanqDAO;
		super.setBaseDAO(evaluationInstanqDAO);
	}

	@Override
	public void createEntity(EvaluationInstanqEntity evaluationInstanEntity) {
		evaluationInstanEntity.setFlgDeleted(0);
		evaluationInstanqDAO.createObj(evaluationInstanEntity);
	}

}
 
 