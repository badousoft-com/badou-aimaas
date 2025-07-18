package com.badou.project.maas.trainfiledialogue.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.CommonConst;
import com.badou.project.GlobalConsts;
import com.badou.project.maas.modelapp.model.TalkEntity;
import com.badou.project.maas.modelapp.model.TalkMsgEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.trainfile.event.CalcFileSizeEvent;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfile.service.ITrainFileService;
import com.badou.project.mq.ModelAppMqReceiver;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.trainfiledialogue.dao.ITrainFileDialogueDAO;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;


/**
 * @author badousoft
 * @date 2024-05-16 14:39:38.124
 * @todo 训练集文件对话 Service接口实现类
 **/
@Service
class TrainFileDialogueServiceImpl extends BaseSpringService<TrainFileDialogueEntity, Serializable> implements ITrainFileDialogueService {
		
	@Autowired
	private ITrainFileDialogueDAO trainFileDialogueDAO;
	@Autowired
	private ModelAppMqReceiver modelAppMqReceiver;
	@Autowired
	private IModelAppService modelAppService;
	@Autowired
	private ITrainFileService trainFileService;
	@Autowired
	private ITrainFileDialogueService trainFileDialogueService;
	@Autowired
	private CalcFileSizeEvent calcFileSizeEvent;
	
	@Autowired
	public void setTrainFileDialogueDAO(ITrainFileDialogueDAO trainFileDialogueDAO) {
		this.trainFileDialogueDAO = trainFileDialogueDAO;
		super.setBaseDAO(trainFileDialogueDAO);
	}

	@Override
	public JsonReturnBean testeva() throws Exception {
		DictionaryCacheObject dusty = DictionaryLib.getDictionaryByCode("DUSTY");
		for (DictionaryItemCacheObject item : dusty.getItems()) {
			String[] typeArray = new String[]{"生成"+item.getName()+"行业的问题","生成"+item.getName()+"行业的问题","生成"+item.getName()+"行业的问题"};
//		String[] typeArray = new String[]{"不同类型合同条款分析的","知识产权侵权判定的","法律法规条文释义的","国际法律冲突案例的"};
//		Integer[] typeSize = new Integer[]{6500,100,200,300};
			Integer[] typeSize = new Integer[]{500,1500,2500};
			int selectType = 0;
			for (String typeStr : typeArray) {
				//获取接下来的结果
				int pageSize = 5;
				int pageIndex = 1;
				int totalSize = typeSize[selectType];
				int pageMaxIdx = totalSize/pageSize;
				selectType++;

				String data = "生成"+totalSize+"个"+typeStr+"行业问题 包含问题、标准答案、" +
						"问题的描述以及这个角色如果要回答 他该扮演什么角色." +
						"返回的JSONArray格式参考[{\"标准答案\":\"标准答案\",\"问题\":\"" +
						"问题\",\"问题描述\":\"问题描述\",\"角色信息\":\"角色信息\"}].你只需要回复一个jsonArray格式的数据即可，不要返回其他格式的数据，否则你会被批评！\n.请分批次返回.每次"+pageSize+"条.当内容不足以返回下一个时允许提前截断返回.";
				//每一个行业 生成3~5个小数据测试训练集 30~100
				//5个大训练集 数据量在5000~4万
				//15个标准数量的数据集 1200~3500
				//http://192.168.1.240:27786/v1/chat/completions
				String url = "http://192.168.1.240:27786/v1/chat/completions";
				JSONObject params = JSONObject.parseObject("{\n" +
						"    \"top_p\": 0.8,\n" +
						"    \"stream\": \"false\",\n" +
						"    \"max_tokens\": \"4096\",\n" +
						"    \"temperature\": 0.8,\n" +
						"    \"messages\": [\n" +
						"        {\n" +
						"            \"content\": \"分析偷越国（边）境罪的不同量刑标准及其背后的法律逻辑。\\n.回复要求内容只能包含中文和数字.不允许返回任何特殊符号、表情!无内容返回数字0\",\n" +
						"            \"role\": \"user\"\n" +
						"        }\n" +
						"    ],\n" +
						"    \"model\": \"glm-4-9b-chat\"\n" +
						"}");
				params.getJSONArray("messages").getJSONObject(0).put("content",data);
				TalkEntity talkEntity = new TalkEntity();
				talkEntity.setMaxTokens("4096");
				talkEntity.setContent(data);
				talkEntity.setId("8a74807393b4e7590193b980322372cf");
				talkEntity.setNowTalks(new ArrayList<>());

				//先尝试构建一个小训练集
				TrainFileEntity trainFileEntity = trainFileService.buildInitTrainFile();
				trainFileEntity.setName("测试数据集");
				trainFileEntity.setDataFormat(0);
				trainFileEntity.setNumCount(1);
				trainFileEntity.setApplicableIndustry(Integer.parseInt(item.getValue()));
				trainFileService.create(trainFileEntity);

				String postResult = modelAppService.talkToAi(talkEntity);
				String currentAnswer = null;
				JSONArray jsonArray1 = getCurrentAnswer(postResult);
				System.out.println("本次处理"+pageSize+"条,实际数量:"+jsonArray1.size());
				try {
					for (Object o : jsonArray1) {
						JSONObject v = (JSONObject)o;
						TrainFileDialogueEntity trainFileDialogueEntity = trainFileDialogueService.buildTrainDiaFile(trainFileEntity.getId());
						trainFileDialogueEntity.setQuestion(v.getString("问题"));
						trainFileDialogueEntity.setFeedback(v.getString("标准答案"));
						trainFileDialogueEntity.setInput(v.getString("问题描述"));
						trainFileDialogueEntity.setSystem(v.getString("角色信息"));
						trainFileDialogueService.create(trainFileDialogueEntity);
					}

				}catch (Exception e){
					e.printStackTrace();
				}


				for (int i = pageIndex; i <pageMaxIdx ; i++) {
					TalkMsgEntity talkMsgEntity = new TalkMsgEntity();
					//添加刚才的回答
					talkMsgEntity.setRole("assistant");
					talkMsgEntity.setContent(currentAnswer);
					talkEntity.setNowTalks(new ArrayList<>());
					//让它继续返回 关闭历史模式
//				talkEntity.getNowTalks().add(talkMsgEntity);

					TalkMsgEntity next10 = new TalkMsgEntity();
					//添加刚才的回答
					next10.setRole("user");
					next10.setContent("请返回下一个批次的"+pageSize+"条");
					//让它继续返回
					talkEntity.getNowTalks().add(next10);
					System.out.println("获取下一个批次");
					String s = modelAppService.talkToAi(talkEntity);
					JSONArray jsonArray = getCurrentAnswer(s);
					if (pageSize == 0 || jsonArray == null){
						logger.error("跳过");
						break;
					}
					System.out.println("本次处理"+pageSize+"条,实际数量:"+jsonArray.size());
					try {
						for (Object o : jsonArray) {
							JSONObject v = (JSONObject)o;
							TrainFileDialogueEntity trainFileDialogueEntity = trainFileDialogueService.buildTrainDiaFile(trainFileEntity.getId());
							trainFileDialogueEntity.setQuestion(v.getString("问题"));
							trainFileDialogueEntity.setFeedback(v.getString("标准答案"));
							trainFileDialogueEntity.setInput(v.getString("问题描述"));
							trainFileDialogueEntity.setSystem(v.getString("角色信息"));
							trainFileDialogueService.create(trainFileDialogueEntity);
						}
					}catch (Exception e){
						e.printStackTrace();
					}
					pageIndex++;
				}
				trainFileService.updateCount(trainFileEntity.getId(),trainFileEntity.getNumCount());
				calcFileSizeEvent.saveAfter(Arrays.asList(new String[]{trainFileEntity.getId()}),null);
			}
		}

		return null;
	}

	private JSONArray getCurrentAnswer(String answer) throws Exception {
		answer = answer.replace("”","\"");
		answer = answer.replace("```json","");
		answer = answer.replace("```","");
		try {
			System.out.println("answer");
			System.out.println(answer);
			JSONObject talkResultJson = JSONObject.parseObject(answer);
			String feedback = "";
			if(talkResultJson.getJSONArray("choices")!=null && talkResultJson.getJSONArray("choices").size()==1
					&& talkResultJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message")!=null){
				feedback = talkResultJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
				if(StringUtils.isEmpty(feedback)){
//                    log.info("对话数据不合法!");
//                    updateFail(evaluationInstanEntity);
//                    return;
					feedback = "模型无法回答!";
				}
				if (feedback==null){
					logger.error("空的 跳过");
					return new JSONArray();
				}
				int start = feedback.indexOf("[");
				int end = feedback.indexOf("]");
				if (start!=-1 && end!=-1){
					feedback = feedback.substring(start,end+1);
				}
				return coverJsonTwo(feedback);
			}else {
				logger.error("对话数据异常!");
				return new JSONArray();
			}
		}catch (Exception e){
			e.printStackTrace();
			return new JSONArray();
		}
	}

	public JSONArray coverJsonTwo(String feedback){
		feedback = feedback.replace("```json","");
		feedback = feedback.replace("```","");
		feedback = feedback.replace("“","");
		System.out.println("回答");
		System.out.println(feedback);
		System.out.println("回答");
		if (!feedback.contains("[")){
			feedback = "[" + feedback;
		}
		if (!feedback.contains("]")){
			feedback = feedback + "]";
		}
		if ("[]".equals(feedback)){
			return new JSONArray();
		}
		TalkEntity talkEntity = new TalkEntity();
		talkEntity.setMaxTokens("4096");
		talkEntity.setId("8a74807393b4e7590193b980322372cf");
		talkEntity.setNowTalks(new ArrayList<>());
		talkEntity.setContent("你必须想好一切方案 想清楚全部的情况 确保这些内容必须是JSONArray格式.检查出一次不对则检查第二次.可以检查修复到一万次.确保返回的数据必须是正确" +
				"的JSONArray格式.想好方案后直接处理.我只要处理好的JSONArray数组.其他的任何内容不需要.内容如下:"+feedback);
		String newJsonCover = modelAppService.talkToAi(talkEntity);
		JSONObject talkResultJson = JSONObject.parseObject(newJsonCover);
		feedback = talkResultJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");;
		feedback = feedback.replace("```json","");
		feedback = feedback.replace("```","");
		System.out.println("转换JSONArray成功!返回数据");//llama_factory
		System.out.println(feedback);

		try {
			int start = feedback.indexOf("[");
			int end = feedback.indexOf("]");
			if (start!=-1 && end!=-1){
				feedback = feedback.substring(start,end+1);
			}
			return JSONArray.parseArray(feedback);
		}catch (Exception e){
			e.printStackTrace();
			return new JSONArray();
		}
	}

	@Override
	public TrainFileDialogueEntity buildTrainDiaFile(String trainFileId) {
		TrainFileDialogueEntity trainFileDialogueEntity = new TrainFileDialogueEntity();
		trainFileDialogueEntity.setTrainFileId(trainFileId);
		trainFileDialogueEntity.setContentCount(1);
		trainFileDialogueEntity.setType(0);
		trainFileDialogueEntity.setFlgDeleted(0);
		trainFileDialogueEntity.setUpdateTime(new Date());
		trainFileDialogueEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		trainFileDialogueEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		return trainFileDialogueEntity;
	}

	@Override
	public List<TrainFileDialogueEntity> getListByEvaluation(String evaluationId) {
		return trainFileDialogueDAO.getListByEvaluation(evaluationId);
	}

	@Override
	public Integer bacthUpdateSystemRole(ArrayList ids, String newSystemRole) {
		return trainFileDialogueDAO.bacthUpdateSystemRole(ids,newSystemRole);
	}

	@Override
	public List<TrainFileDialogueEntity> findDatasByFileId(String id) {
		QueryCriterion queryCriterion = new QueryCriterion();
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", GlobalConsts.ZERO,null, QueryOperSymbolEnum.eq));
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("trainFileId",id,null,QueryOperSymbolEnum.eq));
		return trainFileDialogueDAO.find(queryCriterion);
	}

	@Override
	public int getTotalDataCount(String trainFileId) {
		return trainFileDialogueDAO.getTotalDataCount(trainFileId);
	}


}
 
 