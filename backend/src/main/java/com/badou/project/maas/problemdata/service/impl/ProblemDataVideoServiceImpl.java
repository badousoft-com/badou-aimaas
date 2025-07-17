package com.badou.project.maas.problemdata.service.impl;

import java.io.Serializable;
import java.util.List;

import com.badou.project.maas.problemdata.model.ProblemDataPictureEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.problemdata.dao.IProblemDataVideoDAO;
import com.badou.project.maas.problemdata.model.ProblemDataVideoEntity;
import com.badou.project.maas.problemdata.service.IProblemDataVideoService;


/**
 * @author badousoft
 * @date 2025-03-26 11:30:02.141
 *  视频样本集 Service接口实现类
 **/
@Service
public class ProblemDataVideoServiceImpl extends BaseSpringService<ProblemDataVideoEntity, Serializable> implements IProblemDataVideoService {

	@Autowired
	private IProblemDataVideoDAO problemDataVideoDAO;

	@Autowired
	public void setProblemDataVideoDAO(IProblemDataVideoDAO problemDataVideoDAO) {
		this.problemDataVideoDAO = problemDataVideoDAO;
		super.setBaseDAO(problemDataVideoDAO);
	}
	@Override
	public void updateStatus(List<ProblemDataVideoEntity> datas) {
		for (ProblemDataVideoEntity data : datas) {
			Integer upStatus = data.getUpStatus();
			if (upStatus == null){
				upStatus = 0;
			}
			data.setUpStatus(upStatus==0?1:0);
		}
		batchUpdate(datas);
	}
}

