package com.badou.project.maas.trainfile.service.impl;

import java.io.Serializable;
import java.util.List;

import com.badou.project.maas.trainfile.model.TrainMultiFileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.trainfile.dao.ITrainMultiVideoDAO;
import com.badou.project.maas.trainfile.model.TrainMultiVideoEntity;
import com.badou.project.maas.trainfile.service.ITrainMultiVideoService;


/**
 * @author badousoft
 * @date 2025-03-26 15:26:00.791
 *  多模态视频训练文件管理 Service接口实现类
 **/
@Service
public class TrainMultiVideoServiceImpl extends BaseSpringService<TrainMultiVideoEntity, Serializable> implements ITrainMultiVideoService {

	@Autowired
	private ITrainMultiVideoDAO trainMultiVideoDAO;

	@Autowired
	public void setTrainMultiVideoDAO(ITrainMultiVideoDAO trainMultiVideoDAO) {
		this.trainMultiVideoDAO = trainMultiVideoDAO;
		super.setBaseDAO(trainMultiVideoDAO);
	}

	@Override
	public void updateStatus(List<TrainMultiVideoEntity> datas) {
		for (TrainMultiVideoEntity data : datas) {
			Integer upStatus = data.getUpStatus();
			if (upStatus == null){
				upStatus = 0;
			}
			data.setUpStatus(upStatus==0?1:0);
		}
		batchUpdate(datas);
	}
}

