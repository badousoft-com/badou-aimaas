package com.badou.project.maas.evaluationinstan.service.impl;

import java.io.PipedReader;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanqEntity;
import com.badou.project.maas.evaluationinstan.model.EvaluationMqEntity;
import com.badou.project.maas.evaluationinstan.service.IEvaluationInstanqService;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.planlink.model.PlanLinkEntity;
import com.badou.project.maas.planlink.service.IPlanLinkService;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfile.service.ITrainFileService;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.project.maas.trainfiledialoguedetail.model.TrainFileDialoguedetailEntity;
import com.badou.project.maas.trainfiledialoguedetail.service.ITrainFileDialoguedetailService;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.trainplan.service.ITrainPlanService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.maas.tuningprogramn.model.TuningProgramnEntity;
import com.badou.project.maas.tuningprogramn.service.ITuningProgramnService;
import com.badou.project.mq.TaskMqSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.evaluationinstan.dao.IEvaluationInstanDAO;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;
import com.badou.project.maas.evaluationinstan.service.IEvaluationInstanService;


/**
 * @author badousoft
 * @date 2024-06-06 15:58:38.064
 * @todo 模型评价实例 Service接口实现类
 **/
@Service
public class EvaluationInstanServiceImpl extends BaseSpringService<EvaluationInstanEntity, Serializable> implements IEvaluationInstanService {
		
	@Autowired
	private IEvaluationInstanDAO evaluationInstanDAO;
	@Autowired
	private IEvaluationInstanqService evaluationInstanqService;
	@Autowired
	private ITuningModelnService tuningModelnService;
	@Autowired
	private ITrainPlanService trainPlanService;
	@Autowired
	private ITuningProgramnService tuningProgramnService;
	@Autowired
	private ITrainFileService trainFileService;
	@Autowired
	private ITrainFileDialogueService trainFileDialogueService;
	@Autowired
	private ITrainFileDialoguedetailService trainFileDialoguedetailService;
	@Autowired
	private IPlanLinkService planLinkService;
	@Autowired
	private TaskMqSender taskMqSender;
	
	@Autowired
	public void setEvaluationInstanDAO(IEvaluationInstanDAO evaluationInstanDAO) {
		this.evaluationInstanDAO = evaluationInstanDAO;
		super.setBaseDAO(evaluationInstanDAO);
	}

	@Autowired
	private IModelAppService modelAppService;

	@Override
	public void createEntity(EvaluationInstanEntity evaluationInstanEntity) {
		evaluationInstanDAO.createObj(evaluationInstanEntity);
	}

	@Override
	public void startUpdate(EvaluationInstanEntity evaluationInstanEntity) {
		evaluationInstanDAO.startUpdate(evaluationInstanEntity);
	}

	@Override
	public EvaluationInstanEntity calcFinishStatus(String id) {
		JSONObject jsonObject = evaluationInstanDAO.calcFinishStatus(id);
		EvaluationInstanEntity evaluationInstanEntity = evaluationInstanDAO.find(id);
		evaluationInstanEntity.setMaxScore(jsonObject.getDouble("maxScore"));
		evaluationInstanEntity.setMinScore(jsonObject.getDouble("minScore"));
		evaluationInstanEntity.setAverageScore(jsonObject.getDouble("averageScore"));
		evaluationInstanEntity.setTotalScore(jsonObject.getDouble("totalScore"));
		evaluationInstanEntity.setGradingRatio(jsonObject.getDouble("gradingRatio"));
		evaluationInstanDAO.update(evaluationInstanEntity);
		return evaluationInstanEntity;
	}

	@Override
	public int getExecCount(String id) {
		return evaluationInstanDAO.getExecCount(id);
	}

}
 
 