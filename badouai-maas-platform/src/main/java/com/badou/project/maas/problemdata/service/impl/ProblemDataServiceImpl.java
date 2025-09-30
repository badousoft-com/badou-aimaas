package com.badou.project.maas.problemdata.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.GlobalConsts;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;
import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailService;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfile.service.ITrainFileService;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.problemdata.dao.IProblemDataDAO;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.problemdata.service.IProblemDataService;
import sun.rmi.runtime.Log;


/**
 * @author badousoft
 * @date 2024-05-15 17:37:11.964
 * @todo 样本数据集管理 Service接口实现类
 **/
@Service
public class ProblemDataServiceImpl extends BaseSpringService<ProblemDataEntity, Serializable> implements IProblemDataService {
		
	@Autowired
	private IProblemDataDAO problemDataDAO;
	@Autowired
	private IProblemDataDetailService problemDataDetailService;
	@Autowired
	private ITrainFileService trainFileService;
	@Autowired
	private ITrainFileDialogueService trainFileDialogueService;
	
	@Autowired
	public void setProblemDataDAO(IProblemDataDAO problemDataDAO) {
		this.problemDataDAO = problemDataDAO;
		super.setBaseDAO(problemDataDAO);
	}

	@Override
	public ProblemDataDetailEntity buildEntityByJSON(String mainId,JSONObject jsonObject, int type) {
		ProblemDataDetailEntity problemDataDetailEntity = problemDataDetailService.initEntity(mainId);
		if (type == MaasConst.TUN_PLAN_TYPE_PTTRAIN){
			problemDataDetailEntity.setQuestion(jsonObject.getString("text"));
		}else if (type == MaasConst.TUN_PLAN_TYPE_SFT){
			problemDataDetailEntity.setQuestion(jsonObject.getString("instruction"));
			problemDataDetailEntity.setFeedback(jsonObject.getString("output"));
			problemDataDetailEntity.setInput(jsonObject.getString("input"));
			problemDataDetailEntity.setSystem(jsonObject.getString("system"));
		}else if (type == MaasConst.TUN_PLAN_TYPE_PPO){
			problemDataDetailEntity.setQuestion(jsonObject.getString("instruction"));
			problemDataDetailEntity.setChosena(jsonObject.getString("chosen"));
			problemDataDetailEntity.setRejecteda(jsonObject.getString("rejected"));
		}else if (type == MaasConst.TUN_PLAN_TYPE_KPO){
			problemDataDetailEntity.setQuestion(jsonObject.getString("instruction"));
			problemDataDetailEntity.setFeedback(jsonObject.getString("output"));
			problemDataDetailEntity.setInput(jsonObject.getString("input"));
			problemDataDetailEntity.setKtoTag(jsonObject.getBoolean("kto_tag")==false?0:1);
		}
		return problemDataDetailEntity;
	}

	@Override
	public ProblemDataEntity buildInitEntity() {
		ProblemDataEntity problemDataEntity = new ProblemDataEntity();
		problemDataEntity.setFlgDeleted(0);
		problemDataEntity.setRemark("数据导入");
		problemDataEntity.setUpdateTime(new Date());
		problemDataEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		problemDataEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		return problemDataEntity;
	}

	@Override
	public String coverSft(String id) {
		ProblemDataEntity problemDataEntity = problemDataDAO.find(id);
		if (problemDataEntity.getDataFormat() ==null || problemDataEntity.getDataFormat()!=1){
			return "请选择数据格式为预训练集的数据";
		}
		QueryCriterion queryCriterion = new QueryCriterion();
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("problemDataId", id, null, QueryOperSymbolEnum.eq));
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
		List<ProblemDataDetailEntity> problemDataDetailEntities = problemDataDetailService.find(queryCriterion);

		if (problemDataDetailEntities.size() == 0){
			return "选择的数据集为空!请选择有效的训练集!";
		}

		//新的实体
		ProblemDataEntity sftObj = new ProblemDataEntity();
		BeanUtils.copyProperties(problemDataEntity,sftObj,"createTime","id","updateTime","creator","updator");
		sftObj.setDataFormat(0);
		sftObj.setName("预训练转换的"+sftObj.getName());
		sftObj.setUpdateTime(new Date());
		sftObj.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		sftObj.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		sftObj.setParentId(problemDataEntity.getId());

		problemDataDAO.create(sftObj);
		int totalSize = 0;
		int i = 1;
		for (ProblemDataDetailEntity problemDataDetailEntity : problemDataDetailEntities) {
			logger.info("处理到第"+i+"个");
			String question = problemDataDetailEntity.getQuestion();
			JSONObject row = new JSONObject();
			row.put("content",question);
			i++;
			try {
//				JSONObject jsonObject = apiHelperService.talkWithAi("pretrain-cover-sft", row, "3bc130ff79b4469aa86616e5b9c26e99");
				JSONObject jsonObject = null;
				if(jsonObject.getJSONObject("bean")!=null){
					String replyContent = jsonObject.getJSONObject("bean").getString("replyContent");
					JSONArray jsonArray = JSONArray.parseArray(StringHandlerUtil.checkAiJsonArray(replyContent));
					for (Object o : jsonArray) {
						logger.info("处理"+o.toString());
						JSONObject obj = (JSONObject)o;
						ProblemDataDetailEntity newObj = new ProblemDataDetailEntity();
						BeanUtils.copyProperties(problemDataDetailEntity,newObj,"createTime","id","updateTime","creator","updator");
						newObj.setUpdateTime(new Date());
						newObj.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
						newObj.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
						newObj.setProblemDataId(sftObj.getId());
						newObj.setQuestion(obj.getString("问题"));
						newObj.setFeedback(obj.getString("回答"));
						problemDataDetailService.createEntity(newObj);
					}
					totalSize+=jsonArray.size();
				};
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		sftObj.setSampleCount(totalSize);
		problemDataDAO.update(sftObj);
		return null;
	}

	@Override
	public void updateNewestCount(String id) {
		problemDataDAO.updateNewestCount(id);
	}

	@Override
	public void createEntity(ProblemDataEntity problemDataEntity){
		problemDataDAO.save(problemDataEntity);
	}

	@Override
	public String exportTrainFile(String fileName,String[] ids, int type, List<TrainFileEntity> valueList) {
		//生成新训练集并导入
		TrainFileEntity trainFileEntity = new TrainFileEntity();
		if(type == 1 ){
			trainFileEntity.setApplicableIndustry(10);
			trainFileEntity.setNumCount(0);
			if(StringUtils.isEmpty(fileName)){
				fileName = "未指定名字的训练集!";
			}
			trainFileEntity.setName(fileName);
			trainFileEntity.setRoleDesc(MaasConst.DEFAULT_ROLE_DESC);
			trainFileEntity.setFlgDeleted(0);
			trainFileEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
			trainFileEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
			trainFileEntity.setUpdateTime(new Date());
			trainFileEntity.setUpdateTime(new Date());
			trainFileService.create(trainFileEntity);
		}
		for(String id:ids){
			ProblemDataEntity problemDataEntity = problemDataDAO.find(id);
			if (problemDataEntity.getId() == null){
				return "获取不到目标对象";
			}
			if (type == 0 && valueList.size() == 0){
				return "选择了导入旧文件!必须选择旧文件";
			}
			//导入新文件 --- 生成新训练集并导入
			if(type == 1) {
				//查询样本数据下的全部数据
				QueryCriterion queryCriterion = new QueryCriterion();
				queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", null, GlobalConsts.ZERO, QueryOperSymbolEnum.eq));
				queryCriterion.addParam(new QueryHibernatePlaceholderParam("problemDataId", id, null, QueryOperSymbolEnum.eq));
				List<ProblemDataDetailEntity> problemDataDetailEntities = problemDataDetailService.find(queryCriterion);

				List<TrainFileDialogueEntity> trainFileDialogueEntities = new ArrayList<>();
				TrainFileEntity finalTrainFileEntity = trainFileEntity;
				problemDataDetailEntities.forEach(e -> {
					TrainFileDialogueEntity obj = buildTalk(finalTrainFileEntity.getId(), e);
					trainFileDialogueEntities.add(obj);
				});
				trainFileEntity.setNumCount(trainFileEntity.getNumCount() + problemDataDetailEntities.size());
				trainFileEntity.setDataFormat(problemDataEntity.getDataFormat());
				trainFileEntity.setRemark(problemDataEntity.getRemark());
				trainFileService.update(trainFileEntity);
				trainFileDialogueService.batchCreate(trainFileDialogueEntities);
				continue;
			}
			//处理导入旧训练集
			//获取训练文件信息
			for (int i = 0; i < valueList.size(); i++) {
				//查询样本数据下的全部数据
				QueryCriterion queryCriterion = new QueryCriterion();
				queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", null, GlobalConsts.ZERO, QueryOperSymbolEnum.eq));
				queryCriterion.addParam(new QueryHibernatePlaceholderParam("problemDataId", id, null, QueryOperSymbolEnum.eq));
				List<ProblemDataDetailEntity> problemDataDetailEntities = problemDataDetailService.find(queryCriterion);
				trainFileEntity = valueList.get(i);
				List<TrainFileDialogueEntity> trainFileDialogueEntities = new ArrayList<>();
				TrainFileEntity finalTrainFileEntity1 = trainFileEntity;
				problemDataDetailEntities.forEach(e->{
					TrainFileDialogueEntity obj = buildTalk(finalTrainFileEntity1.getId(), e);
					trainFileDialogueEntities.add(obj);
				});
				if(trainFileEntity.getNumCount() == null){
					trainFileEntity.setNumCount(0);
				}
				trainFileEntity.setDataFormat(problemDataEntity.getDataFormat());
				trainFileEntity.setRemark(problemDataEntity.getRemark());
				trainFileEntity.setNumCount(trainFileEntity.getNumCount()+problemDataDetailEntities.size());
				trainFileService.update(trainFileEntity);
				trainFileDialogueService.batchCreate(trainFileDialogueEntities);
			}
		}
		return null;
	}

	private static TrainFileDialogueEntity buildTalk(String trainFileId,ProblemDataDetailEntity e){
		TrainFileDialogueEntity obj = new TrainFileDialogueEntity();
		obj.setTrainFileId(trainFileId);
		BeanUtil.copyProperties(e,obj);
		obj.setContentCount(GlobalConsts.ONE);
		obj.setType(GlobalConsts.ZERO);
		obj.setFlgDeleted(GlobalConsts.ZERO);
		obj.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		obj.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		obj.setUpdateTime(new Date());
		return obj;
	}
}