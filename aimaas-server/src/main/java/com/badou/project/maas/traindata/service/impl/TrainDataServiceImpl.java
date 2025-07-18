package com.badou.project.maas.traindata.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.traindata.dao.ITrainDataDAO;
import com.badou.project.maas.traindata.model.TrainDataEntity;
import com.badou.project.maas.traindata.service.ITrainDataService;


/**
 * @author badousoft
 * @date 2024-04-18 09:34:05.295
 * @todo 训练集管理 Service接口实现类
 **/
@Service
public class TrainDataServiceImpl extends BaseSpringService<TrainDataEntity, Serializable> implements ITrainDataService {
		
	@Autowired
	private ITrainDataDAO trainDataDAO;
	
	@Autowired
	public void setTrainDataDAO(ITrainDataDAO trainDataDAO) {
		this.trainDataDAO = trainDataDAO;
		super.setBaseDAO(trainDataDAO);
	}
}
 
 