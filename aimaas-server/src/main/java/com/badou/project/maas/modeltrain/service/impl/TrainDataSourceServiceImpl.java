package com.badou.project.maas.modeltrain.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.maas.mongo.model.TrainData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.modeltrain.dao.ITrainDataSourceDAO;
import com.badou.project.maas.modeltrain.model.TrainDataSourceEntity;
import com.badou.project.maas.modeltrain.service.ITrainDataSourceService;


/**
 * @author badousoft
 * @date 2024-04-07 15:39:08.379
 * @todo 训练集数据 Service接口实现类
 **/
@Service
public class TrainDataSourceServiceImpl extends BaseSpringService<TrainDataSourceEntity, Serializable> implements ITrainDataSourceService {
		
	@Autowired
	private ITrainDataSourceDAO trainDataSourceDAO;
	
	@Autowired
	public void setTrainDataSourceDAO(ITrainDataSourceDAO trainDataSourceDAO) {
		this.trainDataSourceDAO = trainDataSourceDAO;
		super.setBaseDAO(trainDataSourceDAO);
	}

	@Override
	public void addTrainDataSource(TrainData trainDataSourceEntity) {
		if(Objects.isNull(trainDataSourceEntity.getCreateTime())){
			trainDataSourceEntity.setCreateTime(new Date());
		}
		if(Objects.isNull(trainDataSourceEntity.getCreator())){
			trainDataSourceEntity.setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
		}
		if(Objects.isNull(trainDataSourceEntity.getCreatorName())){
			trainDataSourceEntity.setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		}
		trainDataSourceDAO.addTrainDataSource(trainDataSourceEntity);
	}

	@Override
	public JSONObject find(Query query) {
		return trainDataSourceDAO.find(query);
	}
}
 
 