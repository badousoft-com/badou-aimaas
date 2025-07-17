package com.badou.project.maas.traindatalink.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.traindatalink.dao.ITrainDataLinkDAO;
import com.badou.project.maas.traindatalink.model.TrainDataLinkEntity;
import com.badou.project.maas.traindatalink.service.ITrainDataLinkService;


/**
 * @author badousoft
 * @date 2024-04-22 16:50:47.367
 * @todo 训练集数据与训练集关联 Service接口实现类
 **/
@Service
public class TrainDataLinkServiceImpl extends BaseSpringService<TrainDataLinkEntity, Serializable> implements ITrainDataLinkService {
		
	@Autowired
	private ITrainDataLinkDAO trainDataLinkDAO;
	
	@Autowired
	public void setTrainDataLinkDAO(ITrainDataLinkDAO trainDataLinkDAO) {
		this.trainDataLinkDAO = trainDataLinkDAO;
		super.setBaseDAO(trainDataLinkDAO);
	}
}
 
 