package com.badou.project.maas.tuningplanparams.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.tuningplanparams.dao.ITuningPlanParamsDAO;
import com.badou.project.maas.tuningplanparams.model.TuningPlanParamsEntity;
import com.badou.project.maas.tuningplanparams.service.ITuningPlanParamsService;


/**
 * @author badousoft
 * @date 2024-05-20 14:12:13.912
 * @todo 微调方案参数 Service接口实现类
 **/
@Service
public class TuningPlanParamsServiceImpl extends BaseSpringService<TuningPlanParamsEntity, Serializable> implements ITuningPlanParamsService {
		
	@Autowired
	private ITuningPlanParamsDAO tuningPlanParamsDAO;

	@Autowired
	public void setTuningPlanParamsDAO(ITuningPlanParamsDAO tuningPlanParamsDAO) {
		this.tuningPlanParamsDAO = tuningPlanParamsDAO;
		super.setBaseDAO(tuningPlanParamsDAO);
	}

}
 
 