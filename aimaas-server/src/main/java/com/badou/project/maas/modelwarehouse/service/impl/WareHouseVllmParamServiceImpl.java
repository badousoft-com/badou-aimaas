package com.badou.project.maas.modelwarehouse.service.impl;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import io.kubernetes.client.custom.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.modelwarehouse.dao.IWareHouseVllmParamDAO;
import com.badou.project.maas.modelwarehouse.model.WareHouseVllmParamEntity;
import com.badou.project.maas.modelwarehouse.service.IWareHouseVllmParamService;


/**
 * @author badousoft
 * @date 2025-03-08 15:09:21.737
 *  模型仓库VLLM参数 Service接口实现类
 **/
@Service
public class WareHouseVllmParamServiceImpl extends BaseSpringService<WareHouseVllmParamEntity, Serializable> implements IWareHouseVllmParamService {

	@Autowired
	private IWareHouseVllmParamDAO wareHouseVllmParamDAO;

	@Autowired
	public void setWareHouseVllmParamDAO(IWareHouseVllmParamDAO wareHouseVllmParamDAO) {
		this.wareHouseVllmParamDAO = wareHouseVllmParamDAO;
		super.setBaseDAO(wareHouseVllmParamDAO);
	}

	@Override
	public List<WareHouseVllmParamEntity> getListByWareHoseId(String id) {
		QueryCriterion queryCriterion = new QueryCriterion();
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("warehostId",id,null, QueryOperSymbolEnum.eq));
		return baseDAO.find(queryCriterion);
	}
}
