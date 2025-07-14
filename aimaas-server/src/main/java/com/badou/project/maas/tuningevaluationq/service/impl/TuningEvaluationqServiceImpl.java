package com.badou.project.maas.tuningevaluationq.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.tuningevaluationq.dao.ITuningEvaluationqDAO;
import com.badou.project.maas.tuningevaluationq.model.TuningEvaluationqEntity;
import com.badou.project.maas.tuningevaluationq.service.ITuningEvaluationqService;


/**
 * @author badousoft
 * @date 2024-04-30 16:23:43.875
 * @todo 微调评价问题管理 Service接口实现类
 **/
@Service
public class TuningEvaluationqServiceImpl extends BaseSpringService<TuningEvaluationqEntity, Serializable> implements ITuningEvaluationqService {
		
	@Autowired
	private ITuningEvaluationqDAO tuningEvaluationqDAO;
	
	@Autowired
	public void setTuningEvaluationqDAO(ITuningEvaluationqDAO tuningEvaluationqDAO) {
		this.tuningEvaluationqDAO = tuningEvaluationqDAO;
		super.setBaseDAO(tuningEvaluationqDAO);
	}
}
 
 