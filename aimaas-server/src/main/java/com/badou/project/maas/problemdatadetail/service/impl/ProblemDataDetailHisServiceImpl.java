package com.badou.project.maas.problemdatadetail.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.problemdatadetail.dao.IProblemDataDetailHisDAO;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailHisEntity;
import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailHisService;


/**
 * @author badousoft
 * @date 2024-11-12 10:04:53.545
 *  样本数据历史对话表 Service接口实现类
 **/
@Service
public class ProblemDataDetailHisServiceImpl extends BaseSpringService<ProblemDataDetailHisEntity, Serializable> implements IProblemDataDetailHisService {

	@Autowired
	private IProblemDataDetailHisDAO problemDataDetailHisDAO;

	@Autowired
	public void setProblemDataDetailHisDAO(IProblemDataDetailHisDAO problemDataDetailHisDAO) {
		this.problemDataDetailHisDAO = problemDataDetailHisDAO;
		super.setBaseDAO(problemDataDetailHisDAO);
	}
}

