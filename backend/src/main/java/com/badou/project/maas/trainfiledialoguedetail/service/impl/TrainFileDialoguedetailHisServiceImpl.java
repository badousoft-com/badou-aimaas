package com.badou.project.maas.trainfiledialoguedetail.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.trainfiledialoguedetail.dao.ITrainFileDialoguedetailHisDAO;
import com.badou.project.maas.trainfiledialoguedetail.model.TrainFileDialoguedetailHisEntity;
import com.badou.project.maas.trainfiledialoguedetail.service.ITrainFileDialoguedetailHisService;


/**
 * @author badousoft
 * @date 2024-11-12 10:27:56.556
 *  训练集历史对话表 Service接口实现类
 **/
@Service
public class TrainFileDialoguedetailHisServiceImpl extends BaseSpringService<TrainFileDialoguedetailHisEntity, Serializable> implements ITrainFileDialoguedetailHisService {

	@Autowired
	private ITrainFileDialoguedetailHisDAO trainFileDialoguedetailHisDAO;

	@Autowired
	public void setTrainFileDialoguedetailHisDAO(ITrainFileDialoguedetailHisDAO trainFileDialoguedetailHisDAO) {
		this.trainFileDialoguedetailHisDAO = trainFileDialoguedetailHisDAO;
		super.setBaseDAO(trainFileDialoguedetailHisDAO);
	}
}

