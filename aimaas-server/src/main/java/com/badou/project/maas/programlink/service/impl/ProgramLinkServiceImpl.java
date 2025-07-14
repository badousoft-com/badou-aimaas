package com.badou.project.maas.programlink.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.programlink.dao.IProgramLinkDAO;
import com.badou.project.maas.programlink.model.ProgramLinkEntity;
import com.badou.project.maas.programlink.service.IProgramLinkService;


/**
 * @author badousoft
 * @date 2024-05-08 14:41:26.637
 * @todo 微调计划方案 Service接口实现类
 **/
@Service
public class ProgramLinkServiceImpl extends BaseSpringService<ProgramLinkEntity, Serializable> implements IProgramLinkService {
		
	@Autowired
	private IProgramLinkDAO programLinkDAO;
	
	@Autowired
	public void setProgramLinkDAO(IProgramLinkDAO programLinkDAO) {
		this.programLinkDAO = programLinkDAO;
		super.setBaseDAO(programLinkDAO);
	}
}
 
 