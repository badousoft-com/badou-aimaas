package com.badou.project.server.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.QueryParam;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.gpucalc.BaseGpuCalcHandler;
import com.badou.project.gpucalc.GpuCalcCardModel;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.service.IK8sServerConfService;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.server.dao.IServerGpuDAO;
import com.badou.project.server.model.ServerGpuEntity;
import com.badou.project.server.service.IServerGpuService;


/**
 * @author badousoft
 * @date 2025-03-17 14:30:08.365
 *  显卡资源管理 Service接口实现类
 **/
@Service
public class ServerGpuServiceImpl extends BaseSpringService<ServerGpuEntity, Serializable> implements IServerGpuService {

	@Autowired
	private IServerGpuDAO serverGpuDAO;
	@Autowired
	private IK8sServerConfService ik8sServerConfService;
	@Autowired
	private ITuningModelnService tuningModelnService;

	@Autowired
	public void setServerGpuDAO(IServerGpuDAO serverGpuDAO) {
		this.serverGpuDAO = serverGpuDAO;
		super.setBaseDAO(serverGpuDAO);
	}

	@Override
	public List<ServerGpuEntity> getCardData(String serverId) {
		K8sServerConfEntity k8sCustomServer = ik8sServerConfService.getK8sCustomServer(serverId);
		//获取显卡列表
		List<ServerGpuEntity> currentNewCardStatus = new BaseGpuCalcHandler().getCurrentNewCardStatus(k8sCustomServer.getGpuMsgUrl()+"/v1/gpus/monitorInfo");
		return currentNewCardStatus;
	}

	@Override
	public String buildTunTaskCard(String execGpuCard,String serverId) throws DataEmptyException {
		if (StringUtils.isEmpty(execGpuCard)){
			throw new DataEmptyException("数据异常");
		}
		List<MultipleServersConfig> serversConfigs = JSONArray.parseArray(execGpuCard,MultipleServersConfig.class);
		//查找当前显卡的内容
		for (MultipleServersConfig serversConfig : serversConfigs) {
			if (serversConfig.getK8sServerConfEntity().getId().equals(serverId)){
				return serversConfig.getCanGpuCardNos();
			}
		}
		return null;
	}

	@Override
	public List<TuningModelnEntity> getModelApps(String serverId) {
		return null;
	}

}

