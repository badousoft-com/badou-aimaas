package com.badou.project.maas.tuningprogramparams.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.tuningprogramparams.dao.ITuningProgramParamsDAO;
import com.badou.project.maas.tuningprogramparams.model.TuningProgramParamsEntity;
import com.badou.project.maas.tuningprogramparams.service.ITuningProgramParamsService;


/**
 * @author badousoft
 * @date 2024-05-20 16:35:31.725
 * @todo 微调计划参数 Service接口实现类
 **/
@Service
public class TuningProgramParamsServiceImpl extends BaseSpringService<TuningProgramParamsEntity, Serializable> implements ITuningProgramParamsService {
		
	@Autowired
	private ITuningProgramParamsDAO tuningProgramParamsDAO;
	
	@Autowired
	public void setTuningProgramParamsDAO(ITuningProgramParamsDAO tuningProgramParamsDAO) {
		this.tuningProgramParamsDAO = tuningProgramParamsDAO;
		super.setBaseDAO(tuningProgramParamsDAO);
	}
}
 
 