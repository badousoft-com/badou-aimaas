package com.badou.project.maas.modeevalqu.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.modeevalqu.dao.ITunModeEvalQuDAO;
import com.badou.project.maas.modeevalqu.model.TunModeEvalQuEntity;
import com.badou.project.maas.modeevalqu.service.ITunModeEvalQuService;


/**
 * @author badousoft
 * @date 2024-05-08 17:04:14.214
 * @todo 微调模型评价问题 Service接口实现类
 **/
@Service
public class TunModeEvalQuServiceImpl extends BaseSpringService<TunModeEvalQuEntity, Serializable> implements ITunModeEvalQuService {
		
	@Autowired
	private ITunModeEvalQuDAO tunModeEvalQuDAO;
	
	@Autowired
	public void setTunModeEvalQuDAO(ITunModeEvalQuDAO tunModeEvalQuDAO) {
		this.tunModeEvalQuDAO = tunModeEvalQuDAO;
		super.setBaseDAO(tunModeEvalQuDAO);
	}
}
 
 