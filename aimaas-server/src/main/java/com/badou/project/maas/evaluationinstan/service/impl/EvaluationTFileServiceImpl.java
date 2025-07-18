package com.badou.project.maas.evaluationinstan.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.evaluationinstan.dao.IEvaluationTFileDAO;
import com.badou.project.maas.evaluationinstan.model.EvaluationTFileEntity;
import com.badou.project.maas.evaluationinstan.service.IEvaluationTFileService;


/**
 * @author badousoft
 * @date 2024-12-04 15:58:41.796
 *  微调评价训练集 Service接口实现类
 **/
@Service
public class EvaluationTFileServiceImpl extends BaseSpringService<EvaluationTFileEntity, Serializable> implements IEvaluationTFileService {

	@Autowired
	private IEvaluationTFileDAO evaluationTFileDAO;

	@Autowired
	public void setEvaluationTFileDAO(IEvaluationTFileDAO evaluationTFileDAO) {
		this.evaluationTFileDAO = evaluationTFileDAO;
		super.setBaseDAO(evaluationTFileDAO);
	}
}

