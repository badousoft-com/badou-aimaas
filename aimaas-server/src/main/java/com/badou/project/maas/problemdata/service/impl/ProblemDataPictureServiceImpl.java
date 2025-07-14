package com.badou.project.maas.problemdata.service.impl;

import java.io.Serializable;
import java.util.List;

import com.badou.project.maas.maasfile.model.MaasFileVideoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.problemdata.dao.IProblemDataPictureDAO;
import com.badou.project.maas.problemdata.model.ProblemDataPictureEntity;
import com.badou.project.maas.problemdata.service.IProblemDataPictureService;


/**
 * @author badousoft
 * @date 2025-03-26 10:13:00.13
 *  图片样本集 Service接口实现类
 **/
@Service
public class ProblemDataPictureServiceImpl extends BaseSpringService<ProblemDataPictureEntity, Serializable> implements IProblemDataPictureService {

	@Autowired
	private IProblemDataPictureDAO problemDataPictureDAO;

	@Autowired
	public void setProblemDataPictureDAO(IProblemDataPictureDAO problemDataPictureDAO) {
		this.problemDataPictureDAO = problemDataPictureDAO;
		super.setBaseDAO(problemDataPictureDAO);
	}
	@Override
	public void updateStatus(List<ProblemDataPictureEntity> datas) {
		for (ProblemDataPictureEntity data : datas) {
			Integer upStatus = data.getUpStatus();
			if (upStatus == null){
				upStatus = 0;
			}
			data.setUpStatus(upStatus==0?1:0);
		}
		batchUpdate(datas);
	}
}

