package com.badou.project.maas.modelwarehouse.service.impl;

import java.io.Serializable;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataValidException;
import com.badou.project.gpucalc.GpuCalcCardModel;
import com.badou.project.gpucalc.GpuCalcHandler;
import com.badou.project.gpucalc.precache.LanguageModelVramEstimator;
import com.badou.project.gpucalc.precache.ModelParameterExtractor;
import com.badou.project.gpucalc.precache.VLVramEstimator;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.client.KubernetesApiClientFactory;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.modelwarehouse.model.WareHouseVllmParamEntity;
import com.badou.project.maas.modelwarehouse.service.IWareHouseVllmParamService;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.service.IK8sServerConfService;
import com.badou.tools.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.modelwarehouse.dao.IModelWarehouseDAO;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;


/**
 * @author badousoft
 * @date 2024-08-28 11:30:33.707
 *  模型仓库 Service接口实现类
 **/
@Service
@Slf4j
public class ModelWarehouseServiceImpl extends BaseSpringService<ModelWarehouseEntity, Serializable> implements IModelWarehouseService {

	@Autowired
	private IModelWarehouseDAO modelWarehouseDAO;
	@Autowired
	private IK8sServerConfService k8sServerConfService;
	@Autowired
	private FileControllerService fileControllerService;
	@Autowired
	private IWareHouseVllmParamService wareHouseVllmParamService;
	@Autowired
	private GpuCalcHandler gpuCalcHandler;


	@Autowired
	public void setModelWarehouseDAO(IModelWarehouseDAO modelWarehouseDAO) {
		this.modelWarehouseDAO = modelWarehouseDAO;
		super.setBaseDAO(modelWarehouseDAO);
	}

	@Override
	public double calcGpuCache(ModelWarehouseEntity modelWarehouseEntity) throws DataValidException, DataEmptyException {
		K8sServerConfEntity k8sServerConfEntity = k8sServerConfService.find(modelWarehouseEntity.getServerId());
		KubernetesApiClient kubernetesApiClient = null;
		//先去该仓库读取模型配置
		try {
			kubernetesApiClient = KubernetesApiClientFactory.build(k8sServerConfEntity);
		} catch (Exception e) {
			throw new DataValidException("未能成功加载客户端.请联系管理员!");
		}
		JSONObject jsonObject = fileControllerService.execModelCommand(k8sServerConfEntity, new String[]{"cat", modelWarehouseEntity.getPath() + "/config.json"});
		if (jsonObject.getString("msg").contains("access")){
			throw new DataEmptyException("该模型未存在配置文件.请联系管理员!");
		}
		//判断当前是不是VL模型
		boolean vlFlag = false;

		//整理应该得有字段 没有则报错
		JSONObject modelConfig = null;
		try {
			modelConfig = JSONObject.parseObject(jsonObject.getString("msg"));
			if (modelConfig.containsKey("vision_start_token_id") || modelConfig.containsKey("vision_end_token_id")){
				vlFlag = true;
				//图像一般都会有这个
				if (!modelConfig.containsKey("vision_config")){
					throw new DataValidException("未存在有效的视觉配置");
				}
			}
		}catch (Exception e){
			throw new DataValidException("该模型配置文件非法.请联系管理员!");
		}
		if (modelConfig == null || modelConfig.size() == 0){
			throw new DataEmptyException("该模型配置文件非法.请联系管理员!");
		}
		Map<String,String> execParams = new HashMap<>();
		execParams.put("ENV_MAX-MODEL-LEN","");
		execParams.put("ENV_MAX-NUM-BATCHED-TOKENS","2048");
		execParams.put("ENV_GPU-MEMORY-UTILIZATION","");
		//批处理的默认Token是2048
		execParams.put("ENV_ENFORCE-EAGER","");

		//如果是VLLM 增加参数
		QueryCriterion queryCriterion = new QueryCriterion();
		queryCriterion.addParam(new QueryHibernatePlaceholderParam("warehostId", modelWarehouseEntity.getId(), null, QueryOperSymbolEnum.eq));
		List<WareHouseVllmParamEntity> wareHouseVllmParamEntities = wareHouseVllmParamService.find(queryCriterion);
		//当前仓库不存在VLLM参数时 则添加VLLM参数
		if (JsonResultUtil.arrayNotEmpty(wareHouseVllmParamEntities)) {
			for (WareHouseVllmParamEntity e : wareHouseVllmParamEntities) {
				if (execParams.containsKey(e.getCode()) && StringUtils.isNotBlank(e.getValue())) {
					execParams.put(e.getCode(),e.getValue().replace("ON","true").replace("OFF","False"));
				}
			}
//			DictionaryCacheObject dictionaryByCode = DictionaryLib.getDictionaryByCode(MaasConst.VLLM_PARAMS_DIC);
//			if (JsonResultUtil.arrayNotEmpty(dictionaryByCode.getItems())) {
//				DictionaryCacheObject planParamsGroupDic = DictionaryLib.getDictionaryByCode(MaasConst.DIC_VLLM_PARAMS_GROUP);
//				Map<String, String> groupNames = new HashMap<>();
//				Map<String, String> grouCodes = new HashMap<>();
//				for (DictionaryItemCacheObject item : planParamsGroupDic.getItems()) {
//					groupNames.put(item.getValue(), item.getName());
//					grouCodes.put(item.getValue(), item.getValue());
//				}
//				dictionaryByCode.getItems().forEach(e -> {
//					//20250407 增加逻辑 如果e的编码等于ENV_MAX-MODEL-LEN 则把值赋值给实体
//					if (execParams.containsKey(e.getCode()) && StringUtils.isNotBlank(e.getValue())) {
//						execParams.put(e.getCode(),"ON".equals(e.getValue())?"true":e.getValue());
//					}
//				});
//			}
		}

		//检查该模型实际有没有必填参数写入
		for (String key: execParams.keySet()){
			if (StringUtils.isEmpty(execParams.get(key))){
				log.error("预估显存计算.空值:"+key);
				throw new DataEmptyException("配置写入失效.缺少关键参数.请联系管理员!");
			}
		}

		//获取当前模型的参数
		double paramsSize = ModelParameterExtractor.extractParameterNumber(ModelParameterExtractor.extractParameterSize(modelWarehouseEntity.getModelName()));

		//检查服务器显卡接口是否可用 也确认是否是正确可用的服务器
		Map<Integer, GpuCalcCardModel> currentCardStatus = null;
		try {
			currentCardStatus = gpuCalcHandler.getCurrentCardStatus(k8sServerConfEntity.getGpuMsgUrl());
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error("异常服务器" + k8sServerConfEntity.getAddress());
			throw new DataValidException("存在服务器异常.无法启动.请联系管理员!");
		}
		if (currentCardStatus == null) {
			log.error("异常服务器" + k8sServerConfEntity.getAddress());
			throw new DataValidException("存在服务器异常.无法启动.请联系管理员!");
		}
		//当前的版本 只允许相同的显卡执行
		String[] gpuCards = modelWarehouseEntity.getCustomGpuCardName().split(",");
		if (gpuCards.length == 0){
			throw new DataValidException("必须至少选择一个显卡");
		}
		if (gpuCards.length > 1){
			String firstName = gpuCards[0].split("-")[0];
			for (int i = 1; i < gpuCards.length; i++) {
				String nowGpu = gpuCards[i];
				String[] split = nowGpu.split("-");
				if (!split[0].equals(firstName)){
//					throw new DataValidException("当前版本只允许相同的显卡协作.请全部选择"+firstName);
				}
			}
		}
		double firstCache = (currentCardStatus.get(Integer.parseInt(gpuCards[0].split("-")[1])).getMaxVMemory())/1000;
		log.info("第一张显卡显存为:"+firstCache+"GB");


		if (firstCache == 0.0 || paramsSize == 0.0){
			throw new DataValidException("计算出无效值!请联系管理员!");
		}
		//视觉配置
		JSONObject visionConfig = modelConfig.getJSONObject("vision_config");

		// 生成预估显存
		// 计算显存需求
		Double estimatedVram = vlFlag? VLVramEstimator.estimateWithLog(
				paramsSize, modelConfig.getInteger("max_window_layers"), modelConfig.getInteger("hidden_size"),
				modelConfig.getInteger("num_attention_heads"), modelConfig.getInteger("num_key_value_heads"),
				Integer.parseInt(execParams.get("ENV_MAX-MODEL-LEN")), Integer.parseInt(execParams.get("ENV_MAX-NUM-BATCHED-TOKENS")),
				gpuCards.length, firstCache, Double.parseDouble(execParams.get("ENV_GPU-MEMORY-UTILIZATION")),
				1, Boolean.parseBoolean(execParams.get("ENV_ENFORCE-EAGER")), visionConfig.getInteger("depth"), visionConfig.getInteger("hidden_size")) :
		LanguageModelVramEstimator.estimateWithLog(
				paramsSize,
				modelConfig.getInteger("max_window_layers")==null?0:modelConfig.getInteger("max_window_layers"),
				modelConfig.getInteger("hidden_size"), modelConfig.getInteger("num_attention_heads"),
				modelConfig.getInteger("num_key_value_heads")==null?0:modelConfig.getInteger("num_key_value_heads"),
				Integer.parseInt(execParams.get("ENV_MAX-MODEL-LEN")), Integer.parseInt(execParams.get("ENV_MAX-NUM-BATCHED-TOKENS")), gpuCards.length,
				firstCache, Double.parseDouble(execParams.get("ENV_GPU-MEMORY-UTILIZATION")), Boolean.parseBoolean(execParams.get("ENV_ENFORCE-EAGER"))
		);

		if (estimatedVram == 0.0){
			throw new DataValidException("预估显存.无效值");
		}
		int realNeedVram = estimatedVram.intValue();

		//老版本是看总显存 最新版本 按照张量的机制来 要求配置的每一张显卡 都至少最低显存要求 如果剩余显存大于预估显存则通过 不大于则不通过
		for (String gpuCard : gpuCards) {
			GpuCalcCardModel gpuCalcCardModel = currentCardStatus.get(Integer.parseInt(gpuCard.split("-")[1]));
			int nowEmptyCatch = (gpuCalcCardModel.getMaxVMemory()-gpuCalcCardModel.getCurrentVMemory())/1000;
			if (nowEmptyCatch < realNeedVram){
				throw new DataValidException("显卡序号为"+gpuCalcCardModel.getOrderNo()+"的显卡剩余"+nowEmptyCatch+"GB,当前模型要求单卡至少剩余"+realNeedVram+"GB");
			}
		}
		return estimatedVram;
	}

}

