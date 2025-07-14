package com.badou.project.maas.tuningevaquestion.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.tuningevaquestion.dao.ITuningEvaQuestionDAO;
import com.badou.project.maas.tuningevaquestion.model.TuningEvaQuestionEntity;
import com.badou.project.maas.tuningevaquestion.service.ITuningEvaQuestionService;


/**
 * @author badousoft
 * @date 2024-04-30 16:25:26.704
 * @todo 微调计划问题 Service接口实现类
 **/
@Service
public class TuningEvaQuestionServiceImpl extends BaseSpringService<TuningEvaQuestionEntity, Serializable> implements ITuningEvaQuestionService {
		
	@Autowired
	private ITuningEvaQuestionDAO tuningEvaQuestionDAO;
	
	@Autowired
	public void setTuningEvaQuestionDAO(ITuningEvaQuestionDAO tuningEvaQuestionDAO) {
		this.tuningEvaQuestionDAO = tuningEvaQuestionDAO;
		super.setBaseDAO(tuningEvaQuestionDAO);
	}
}
 
 