package com.badou.project.maas.traindataproblem.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.designer.jdbc.core.vo.BaseVO;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.problemdata.service.IProblemDataService;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;
import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailService;
import com.badou.project.maas.traindataproblem.web.form.TrainDataProblemVo;
import com.badou.project.maas.trainfile.event.CalcFileSizeEvent;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.traindataproblem.dao.ITrainDataProblemDAO;
import com.badou.project.maas.traindataproblem.model.TrainDataProblemEntity;
import com.badou.project.maas.traindataproblem.service.ITrainDataProblemService;


/**
 * @author badousoft
 * @date 2024-08-30 10:41:35.751
 *  训练集样本集 Service接口实现类
 **/
@Service
public class TrainDataProblemServiceImpl extends BaseSpringService<TrainDataProblemEntity, Serializable> implements ITrainDataProblemService {

	@Autowired
	private ITrainDataProblemDAO trainDataProblemDAO;
	@Autowired
	private IProblemDataService problemDataService;
	@Autowired
	private IProblemDataDetailService problemDataDetailService;
	@Autowired
	private ITrainFileDialogueService trainFileDialogueService;

	@Autowired
	public void setTrainDataProblemDAO(ITrainDataProblemDAO trainDataProblemDAO) {
		this.trainDataProblemDAO = trainDataProblemDAO;
		super.setBaseDAO(trainDataProblemDAO);
	}

	@Override
	public void linkOneProblem(TrainDataProblemVo vo) {
		//删除原有的全部关联
		String trainFileId = vo.getTrainFileId();
		//批量新增样本数据到训练集文件
		ArrayList<ProblemDataEntity> problemDataEntityList = vo.getProblemDataEntityList();
		problemDataEntityList.forEach(e->{
			QueryCriterion queryCriterion = new QueryCriterion();
			queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null, QueryOperSymbolEnum.eq));
			queryCriterion.addParam(new QueryHibernatePlaceholderParam("problemDataId",e.getId(),null,QueryOperSymbolEnum.eq));
			List<ProblemDataDetailEntity> problemDataDetailEntities = problemDataDetailService.find(queryCriterion);

			for (ProblemDataDetailEntity problemDataDetailEntity : problemDataDetailEntities) {
				TrainFileDialogueEntity fileDialogueEntity = new TrainFileDialogueEntity();
				BeanUtils.copyProperties(problemDataDetailEntity,fileDialogueEntity,"createTime","id","updateTime","creator","updator");
				fileDialogueEntity.setDataProblemId(e.getId());
				fileDialogueEntity.setDataProblemName(e.getName());
				fileDialogueEntity.setDataProblemqId(problemDataDetailEntity.getId());
				fileDialogueEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
				fileDialogueEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
				fileDialogueEntity.setUpdateTime(new Date());
				fileDialogueEntity.setContentCount(1);
				fileDialogueEntity.setTrainFileId(trainFileId);
				trainFileDialogueService.create(fileDialogueEntity);
			}

			trainDataProblemDAO.deleteContentByDataId(e.getId());
			TrainDataProblemEntity trainDataProblemEntity = new TrainDataProblemEntity();
			trainDataProblemEntity.setTrainFileId(trainFileId);
			trainDataProblemEntity.setProblemId(e.getId());
			trainDataProblemEntity.setUpdateTime(new Date());
			trainDataProblemEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
			trainDataProblemEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
			trainDataProblemEntity.setFlgDeleted(0);
			trainDataProblemDAO.create(trainDataProblemEntity);
		});
	}

	@Override
	public void deleteLink(String trainFileId,String id, String problem) throws DataEmptyException {
		QueryCriterion queryCriterion = new QueryCriterion();
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted",0,null,QueryOperSymbolEnum.eq));
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("train_file_id",trainFileId,null,QueryOperSymbolEnum.eq));
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("problem_id",id,null,QueryOperSymbolEnum.eq));
		List<TrainDataProblemEntity> trainDataProblemEntities = trainDataProblemDAO.find(queryCriterion);

		if (trainDataProblemEntities.size()!=1){
			throw new DataEmptyException("未存在有效的数据!请联系管理员");
		}
		TrainDataProblemEntity trainDataProblemEntity = trainDataProblemEntities.get(0);

		trainDataProblemDAO.delete(trainDataProblemEntity);
		trainDataProblemDAO.deleteContentByDataId(trainDataProblemEntity.getProblemId());
		//更新训练集文件内容大小和条数
		CalcFileSizeEvent calcFileSizeEvent = new CalcFileSizeEvent();
		List<BaseVO> vos = new ArrayList<>();
		BaseVO baseVO = new BaseVO();
		vos.add(baseVO);
		baseVO.setDetailMap(new HashMap<>());
		baseVO.getDetailMap().put("id",trainFileId);
		calcFileSizeEvent.saveAfter(null,vos);
	}
}

