package com.badou.project.maas.trainfile.service.impl;

import java.io.Serializable;
import java.util.List;

import com.badou.project.maas.trainfile.model.TrainFileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.trainfile.dao.ITrainMultiFileDAO;
import com.badou.project.maas.trainfile.model.TrainMultiFileEntity;
import com.badou.project.maas.trainfile.service.ITrainMultiFileService;


/**
 * @author badousoft
 * @date 2024-11-19 11:40:09.389
 *  多模态训练文件管理 Service接口实现类
 **/
@Service
public class TrainMultiFileServiceImpl extends BaseSpringService<TrainMultiFileEntity, Serializable> implements ITrainMultiFileService {

	@Autowired
	private ITrainMultiFileDAO trainMultiFileDAO;

	@Autowired
	public void setTrainMultiFileDAO(ITrainMultiFileDAO trainMultiFileDAO) {
		this.trainMultiFileDAO = trainMultiFileDAO;
		super.setBaseDAO(trainMultiFileDAO);
	}

	@Override
	public void updateStatus(List<TrainMultiFileEntity> datas) {
		for (TrainMultiFileEntity data : datas) {
			Integer upStatus = data.getUpStatus();
			if (upStatus == null){
				upStatus = 0;
			}
			data.setUpStatus(upStatus==0?1:0);
		}
		batchUpdate(datas);
	}


}

