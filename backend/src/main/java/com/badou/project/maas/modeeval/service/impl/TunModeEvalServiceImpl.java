package com.badou.project.maas.modeeval.service.impl;

import java.io.Serializable;

import com.badou.project.maas.modeeval.service.ITunModeEvalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.modeeval.dao.ITunModeEvalDAO;
import com.badou.project.maas.modeeval.model.TunModeEvalEntity;


/**
 * @author badousoft
 * @date 2024-05-08 17:03:35.357
 * @todo 微调模型评价管理 Service接口实现类
 **/
@Service
public class TunModeEvalServiceImpl extends BaseSpringService<TunModeEvalEntity, Serializable> implements ITunModeEvalService {
		
	@Autowired
	private ITunModeEvalDAO tunModeEvalDAO;
	
	@Autowired
	public void setTunModeEvalDAO(ITunModeEvalDAO tunModeEvalDAO) {
		this.tunModeEvalDAO = tunModeEvalDAO;
		super.setBaseDAO(tunModeEvalDAO);
	}
}
 
 