package com.badou.project.maas.pregpucache.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.pregpucache.dao.IPreGpucacheEntityDAO;
import com.badou.project.maas.pregpucache.model.PreGpucacheEntityEntity;
import com.badou.project.maas.pregpucache.service.IPreGpucacheEntityService;


/**
 * @author badousoft
 * @date 2024-09-24 10:45:48.232
 *  模型GPU显存预估 Service接口实现类
 **/
@Service
public class PreGpucacheEntityServiceImpl extends BaseSpringService<PreGpucacheEntityEntity, Serializable> implements IPreGpucacheEntityService {

	@Autowired
	private IPreGpucacheEntityDAO preGpucacheEntityDAO;

	@Autowired
	public void setPreGpucacheEntityDAO(IPreGpucacheEntityDAO preGpucacheEntityDAO) {
		this.preGpucacheEntityDAO = preGpucacheEntityDAO;
		super.setBaseDAO(preGpucacheEntityDAO);
	}
}

