package com.badou.project.maas.trainfiledialoguedetail.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.trainfiledialoguedetail.dao.ITrainFileDialoguedetailDAO;
import com.badou.project.maas.trainfiledialoguedetail.model.TrainFileDialoguedetailEntity;
import com.badou.project.maas.trainfiledialoguedetail.service.ITrainFileDialoguedetailService;


/**
 * @author badousoft
 * @date 2024-05-16 14:47:48.749
 * @todo 训练集文件对话详情 Service接口实现类
 **/
@Service
public class TrainFileDialoguedetailServiceImpl extends BaseSpringService<TrainFileDialoguedetailEntity, Serializable> implements ITrainFileDialoguedetailService {
		
	@Autowired
	private ITrainFileDialoguedetailDAO trainFileDialoguedetailDAO;
	
	@Autowired
	public void setTrainFileDialoguedetailDAO(ITrainFileDialoguedetailDAO trainFileDialoguedetailDAO) {
		this.trainFileDialoguedetailDAO = trainFileDialoguedetailDAO;
		super.setBaseDAO(trainFileDialoguedetailDAO);
	}

	public void createEntity(TrainFileDialoguedetailEntity trainFileDialoguedetailEntity){
		trainFileDialoguedetailDAO.save(trainFileDialoguedetailEntity);
	}

}
 
 