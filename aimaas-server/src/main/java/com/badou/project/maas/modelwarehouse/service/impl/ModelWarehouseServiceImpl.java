package com.badou.project.maas.modelwarehouse.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.modelwarehouse.dao.IModelWarehouseDAO;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;


/**
 * @author badousoft
 * @date 2024-08-28 11:30:33.707
 *  模型仓库 Service接口实现类
 **/
@Service
public class ModelWarehouseServiceImpl extends BaseSpringService<ModelWarehouseEntity, Serializable> implements IModelWarehouseService {

	@Autowired
	private IModelWarehouseDAO modelWarehouseDAO;


	@Autowired
	public void setModelWarehouseDAO(IModelWarehouseDAO modelWarehouseDAO) {
		this.modelWarehouseDAO = modelWarehouseDAO;
		super.setBaseDAO(modelWarehouseDAO);
	}

}

