package com.badou.project.maas.trainfiledialogueevaluation.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.QueryParam;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.trainfiledialogueevaluation.dao.ITrainFileDialogueEvaluationDAO;
import com.badou.project.maas.trainfiledialogueevaluation.model.TrainFileDialogueEvaluationEntity;
import com.badou.project.maas.trainfiledialogueevaluation.service.ITrainFileDialogueEvaluationService;

/**
 * @author badousoft
 * @date 2025-02-18 19:04:55.108
 *  评价管理与训练集文件对话数据的关联 Service接口实现类
 **/
@Service
public class TrainFileDialogueEvaluationServiceImpl extends BaseSpringService<TrainFileDialogueEvaluationEntity, Serializable> implements ITrainFileDialogueEvaluationService {

	@Autowired
	private ITrainFileDialogueEvaluationDAO trainFileDialogueEvaluationDAO;

	@Autowired
	private ITrainFileDialogueService trainFileDialogueService;

	@Autowired
	public void setTrainFileDialogueEvaluationDAO(ITrainFileDialogueEvaluationDAO trainFileDialogueEvaluationDAO) {
		this.trainFileDialogueEvaluationDAO = trainFileDialogueEvaluationDAO;
		super.setBaseDAO(trainFileDialogueEvaluationDAO);
	}
	/**
	 * 微调评价与训练集对话进行关联
	 * @param evaluationId 微调评价ID
	 * @param trainFileId 训练集文件ID
	 */
	@Override
	public void correlation(String evaluationId, String trainFileId) {

		List<String> trainFileIdList = new ArrayList<>(Arrays.asList(trainFileId.split(",")));

		// 1.查询数据
			//(1) 查询对话数据列表
		QueryCriterion queryCriterion = new QueryCriterion();
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("trainFileId", trainFileIdList
				, null, QueryOperSymbolEnum.in, QueryParam.PARAM_PLACEHOLDER_NAME));
		List<TrainFileDialogueEntity> trainFileDialogueEntities = trainFileDialogueService.find(queryCriterion);
			//(2) 查询微调评价已经关联的对话数据
		QueryCriterion existingCriterion = new QueryCriterion();
		existingCriterion.addParam(new QueryHibernatePlaceholderParam("evaluationId", evaluationId
			, null, QueryOperSymbolEnum.eq));
		existingCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0
				, null, QueryOperSymbolEnum.eq));
		List<TrainFileDialogueEvaluationEntity> existingEntities = this.find(existingCriterion);

		// 2.组合数据
			// (1) 创建空列表
		List<TrainFileDialogueEvaluationEntity> evaluationEntities = new ArrayList<>();
			// (2) 循环遍历训练集对话数据
		for (TrainFileDialogueEntity trainFileDialogueEntity : trainFileDialogueEntities) {
			// (3) 判断当前训练集对话数据是否已经关联
            boolean isExistingtrainFileId = false;
			for (TrainFileDialogueEvaluationEntity existingEntity : existingEntities) {
				// 微调评价与对话数据关联表 dialogueId
				// 对话数据表
				if (null != existingEntity.getDialogueId() &&
						existingEntity.getDialogueId().equals(trainFileDialogueEntity.getId())) {
                    isExistingtrainFileId = true;
					break;
				}
			}
			// (4) 如果当前的训练集没有关联，则创建新的
			if (!isExistingtrainFileId) {
				TrainFileDialogueEvaluationEntity trainFileDialogueEvaluationEntity = new TrainFileDialogueEvaluationEntity();
				BeanUtils.copyProperties(trainFileDialogueEntity, trainFileDialogueEvaluationEntity);
			// (5) 将创建的实体类添加到列表中
				// 给微调评价与对话数据关联表 设置微调评价ID
				trainFileDialogueEvaluationEntity.setEvaluationId(evaluationId);
				// 给微调评价与对话数据关联表 设置对话数据ID
				trainFileDialogueEvaluationEntity.setDialogueId(trainFileDialogueEntity.getId());
				evaluationEntities.add(trainFileDialogueEvaluationEntity);
			}
		}
		if(evaluationEntities.size() > 0 ){
		// 3.保存数据
			this.batchCreate(evaluationEntities);
		}

	}
}

