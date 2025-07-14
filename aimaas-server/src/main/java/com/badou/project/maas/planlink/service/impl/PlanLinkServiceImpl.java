package com.badou.project.maas.planlink.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.planlink.dao.IPlanLinkDAO;
import com.badou.project.maas.planlink.model.PlanLinkEntity;
import com.badou.project.maas.planlink.service.IPlanLinkService;


/**
 * @author badousoft
 * @date 2024-05-09 16:48:55.91
 * @todo 微调方案关联 Service接口实现类
 **/
@Service
public class PlanLinkServiceImpl extends BaseSpringService<PlanLinkEntity, Serializable> implements IPlanLinkService {
		
	@Autowired
	private IPlanLinkDAO planLinkDAO;
	
	@Autowired
	public void setPlanLinkDAO(IPlanLinkDAO planLinkDAO) {
		this.planLinkDAO = planLinkDAO;
		super.setBaseDAO(planLinkDAO);
	}
}
 
 