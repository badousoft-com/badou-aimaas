package com.badou.project.maas.k8sport.service.impl;

import java.io.Serializable;

import com.badou.project.maas.k8sport.dao.IK8sPortDao;
import com.badou.project.maas.k8sport.service.IK8sPortService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.modeeval.model.TunModeEvalEntity;


/**
 * @author badousoft
 * @date 2024-05-08 17:03:35.357
 * @todo 微调模型评价管理 Service接口实现类
 **/
@Service
@Slf4j
public class K8sPortServiceImpl extends BaseSpringService<TunModeEvalEntity, Serializable> implements IK8sPortService {
		
	@Autowired
	private IK8sPortDao ik8sPortDao;


	@Override
	public int calcNextPort() throws Exception {
		Integer currentPort = ik8sPortDao.getMax();
		if (currentPort == null) {
			ik8sPortDao.insertNew(31000);
			currentPort = ik8sPortDao.getMax();
		} else {
			currentPort = currentPort + 1;
			ik8sPortDao.insertNew(currentPort);
		}
		log.info("本服务端口为:" + currentPort);
		return currentPort;
	}

	@Override
	public int deleteNew(int port) throws Exception {
		return ik8sPortDao.deleteNew(port);
	}

	@Override
	public Integer getMax() throws Exception {
		return ik8sPortDao.getMax();
	}
}
 
 