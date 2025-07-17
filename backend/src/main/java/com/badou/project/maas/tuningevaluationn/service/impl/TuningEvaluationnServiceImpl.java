package com.badou.project.maas.tuningevaluationn.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.tuningevaluationn.dao.ITuningEvaluationnDAO;
import com.badou.project.maas.tuningevaluationn.model.TuningEvaluationnEntity;
import com.badou.project.maas.tuningevaluationn.service.ITuningEvaluationnService;


/**
 * @author badousoft
 * @date 2024-05-08 09:52:49.142
 * @todo 微调评价管理 Service接口实现类
 **/
@Service
public class TuningEvaluationnServiceImpl extends BaseSpringService<TuningEvaluationnEntity, Serializable> implements ITuningEvaluationnService {
		
	@Autowired
	private ITuningEvaluationnDAO tuningEvaluationnDAO;
	
	@Autowired
	public void setTuningEvaluationnDAO(ITuningEvaluationnDAO tuningEvaluationnDAO) {
		this.tuningEvaluationnDAO = tuningEvaluationnDAO;
		super.setBaseDAO(tuningEvaluationnDAO);
	}
}
 
 