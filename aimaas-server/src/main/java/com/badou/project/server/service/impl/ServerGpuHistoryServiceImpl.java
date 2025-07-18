package com.badou.project.server.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.server.dao.IServerGpuHistoryDAO;
import com.badou.project.server.model.ServerGpuHistoryEntity;
import com.badou.project.server.service.IServerGpuHistoryService;


/**
 * @author badousoft
 * @date 2025-03-19 15:05:08.194
 *  服务器显卡算力历史 Service接口实现类
 **/
@Service
public class ServerGpuHistoryServiceImpl extends BaseSpringService<ServerGpuHistoryEntity, Serializable> implements IServerGpuHistoryService {

	@Autowired
	private IServerGpuHistoryDAO serverGpuHistoryDAO;

	@Autowired
	public void setServerGpuHistoryDAO(IServerGpuHistoryDAO serverGpuHistoryDAO) {
		this.serverGpuHistoryDAO = serverGpuHistoryDAO;
		super.setBaseDAO(serverGpuHistoryDAO);
	}
}

