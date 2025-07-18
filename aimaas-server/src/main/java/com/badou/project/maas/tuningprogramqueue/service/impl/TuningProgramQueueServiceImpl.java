package com.badou.project.maas.tuningprogramqueue.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.tuningprogramqueue.dao.ITuningProgramQueueDAO;
import com.badou.project.maas.tuningprogramqueue.model.TuningProgramQueueEntity;
import com.badou.project.maas.tuningprogramqueue.service.ITuningProgramQueueService;


/**
 * @author badousoft
 * @date 2024-07-22 09:58:48.624
 * @todo 计划任务列表 Service接口实现类
 **/
@Service
public class TuningProgramQueueServiceImpl extends BaseSpringService<TuningProgramQueueEntity, Serializable> implements ITuningProgramQueueService {
		
	@Autowired
	private ITuningProgramQueueDAO tuningProgramQueueDAO;
	
	@Autowired
	public void setTuningProgramQueueDAO(ITuningProgramQueueDAO tuningProgramQueueDAO) {
		this.tuningProgramQueueDAO = tuningProgramQueueDAO;
		super.setBaseDAO(tuningProgramQueueDAO);
	}

	@Override
	public void updateStatusByPlanId(String id,int status) {
		TuningProgramQueueEntity tuningProgramQueueEntity = tuningProgramQueueDAO.find(id);
		tuningProgramQueueEntity.setExecStatus(status);
		tuningProgramQueueDAO.updateImmediately(tuningProgramQueueEntity);
	}

	@Override
	public TuningProgramQueueEntity getCurrentNext(String planId, Date createTime) {
		QueryCriterion queryCriterion = new QueryCriterion();
		if(StringUtils.isNotEmpty(planId)){
			queryCriterion.addParam(new QueryHibernatePlaceholderParam("planId",planId,null, QueryOperSymbolEnum.eq));
		}
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("createTime",createTime,null,QueryOperSymbolEnum.ge));
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("taskType", 1,null,QueryOperSymbolEnum.ge));
		List<TuningProgramQueueEntity> tuningProgramQueueEntities = tuningProgramQueueDAO.find(queryCriterion);
		if(tuningProgramQueueEntities.size() == 1){
			return tuningProgramQueueEntities.get(0);
		}
		return null;
	}

}
 
 