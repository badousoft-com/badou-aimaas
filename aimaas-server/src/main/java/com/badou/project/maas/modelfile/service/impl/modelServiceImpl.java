package com.badou.project.model.modelfile.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.model.modelfile.dao.ImodelDAO;
import com.badou.project.model.modelfile.model.modelEntity;
import com.badou.project.model.modelfile.service.ImodelService;


/**
 * @author badousoft
 * @date 2024-04-01 17:57:13.932
 * @todo 模型文件管理 Service接口实现类
 **/
@Service
public class modelServiceImpl extends BaseSpringService<modelEntity, Serializable> implements ImodelService {
		
	@Autowired
	private ImodelDAO modelDAO;
	
	@Autowired
	public void setmodelDAO(ImodelDAO modelDAO) {
		this.modelDAO = modelDAO;
		super.setBaseDAO(modelDAO);
	}
}
 
 