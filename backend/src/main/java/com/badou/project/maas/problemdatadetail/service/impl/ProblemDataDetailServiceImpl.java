package com.badou.project.maas.problemdatadetail.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.problemdatadetail.dao.IProblemDataDetailDAO;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;
import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailService;


/**
 * @author badousoft
 * @date 2024-05-15 17:37:43.24
 * @todo 样本数据集详情管理 Service接口实现类
 **/
@Service
public class ProblemDataDetailServiceImpl extends BaseSpringService<ProblemDataDetailEntity, Serializable> implements IProblemDataDetailService {
		
	@Autowired
	private IProblemDataDetailDAO problemDataDetailDAO;
	
	@Autowired
	public void setProblemDataDetailDAO(IProblemDataDetailDAO problemDataDetailDAO) {
		this.problemDataDetailDAO = problemDataDetailDAO;
		super.setBaseDAO(problemDataDetailDAO);
	}

	@Override
	public ProblemDataDetailEntity initEntity(String parentId) {
		ProblemDataDetailEntity obj = new ProblemDataDetailEntity();
		obj.setProblemDataId(parentId);
		obj.setType(0);
		obj.setFlgDeleted(0);
		obj.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		obj.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		obj.setUpdateTime(new Date());
		return obj;
	}

	@Override
	public void createEntity(ProblemDataDetailEntity problemDataDetailEntity){
		problemDataDetailDAO.save(problemDataDetailEntity);
	}

	@Override
	public List<Object> getRangeData(String id, double range) {
		return problemDataDetailDAO.getRangeData(id,range);
	}
}
 
 