package com.badou.project.maas.modelapp.service.impl;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.project.GlobalConsts;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataValidException;
import com.badou.project.gpucalc.BaseGpuCalcHandler;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesExecHandler;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.kubernetes.handler.KubernetesServiceHandler;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.kubernetes.vo.ProcessStatusVo;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.StopCenter;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.modelapp.model.*;
import com.badou.project.maas.modelapp.platform.model.AiLlmConfigEntity;
import com.badou.project.maas.modelapp.platform.model.AssistantsEntity;
import com.badou.project.maas.modelapp.platform.service.IAiLlmConfigService;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.model.ServerGpuEntity;
import com.badou.project.server.service.IK8sServerConfService;
import com.badou.tools.common.util.SpringHelper;
import com.badou.tools.common.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.modelapp.dao.IModelAppDAO;
import com.badou.project.maas.modelapp.service.IModelAppService;

/**
 * @author badousoft
 * @date 2024-05-27 11:33:46.513
 * @todo 模型应用管理 Service接口实现类
 **/
@Service
public class ModelAppServiceImpl extends BaseSpringService<ModelAppEntity, Serializable> implements IModelAppService {

    @Autowired
    private IModelAppDAO modelAppDAO;
    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;
    @Autowired
    private ITuningModelnService tuningModelnService;
    @Autowired
    private KubernetesServiceHandler kubernetesServiceHandler;
    @Autowired
    private IAiLlmConfigService aiLlmConfigService;
    @Autowired
    private IModelWarehouseService modelWarehouseService;
    @Autowired
    private BaseGpuCalcHandler baseGpuCalcHandler;
    @Autowired
    private KubernetesExecHandler kubernetesExecHandler;

    @Autowired
    public void setModelAppDAO(IModelAppDAO modelAppDAO) {
        this.modelAppDAO = modelAppDAO;
        super.setBaseDAO(modelAppDAO);
    }

    @Override
    public List<ModelAppEntity> checkSameTask(String warehouseId) {
        //根据warehouseId查询1ModelApp的数据
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("warehouse_id", warehouseId
                , null, QueryOperSymbolEnum.eq));
        //还需要增加是否已经逻辑删除的条件
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted", 0
                , null, QueryOperSymbolEnum.eq));
        List<ModelAppEntity> modelAppEntities = find(queryCriterion);
        return modelAppEntities;
    }

    @Override
    public V1Pod getModelAppPod(ModelAppEntity modelAppEntity,KubernetesApiClient kubernetesApiClient) {
        if (JsonResultUtil.isNull(modelAppEntity,modelAppEntity.getServerId())){
            return null;
        }
        try {
            String modelAppNsapce = MaasConst.MODEL_APP_NSAPCE;
            V1Deployment v1Deployment = kubernetesPodHandler.getOneDeployment(kubernetesApiClient, modelAppNsapce, modelAppEntity.getModelServerId());
            if (v1Deployment!=null){
                V1PodList v1PodList = kubernetesPodHandler.getPodByDeployment(kubernetesApiClient, modelAppNsapce, modelAppEntity.getModelServerId());
                //存在POD
                if (v1PodList!=null && v1PodList.getItems()!=null && v1PodList.getItems().size()>0){
                    return v1PodList.getItems().get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ModelAppEntity setFail(ModelAppEntity modelAppEntity, String msg) {
        modelAppEntity.setStatus(MaasConst.DOPLAN_FAIL_STATUS);
        modelAppEntity.setMsg(msg);
        baseDAO.update(modelAppEntity);
        return modelAppEntity;
    }

    @Override
    public TuningModelnEntity getAppCardMsg(ModelWarehouseEntity modelWarehouseEntity) throws Exception {
        TrainPlanEntity trainPlanEntity = new TrainPlanEntity();
        trainPlanEntity.setGpuCount(modelWarehouseEntity.getGpuCount());
        trainPlanEntity.setPreGpucache(modelWarehouseEntity.getStartNeedGpum());
        trainPlanEntity.setServerGpuMode(modelWarehouseEntity.getGpuCount() > GlobalConsts.ZERO ? GlobalConsts.ONE : GlobalConsts.ZERO);
//        trainPlanEntity.setMoreCardFlag(modelWarehouseEntity.getGpuCount() > GlobalConsts.ZERO ? GlobalConsts.ONE : GlobalConsts.ZERO);
        TuningModelnEntity tuningModelnEntity = new TuningModelnEntity();
        tuningModelnEntity.setServerId(modelWarehouseEntity.getServerId());
        tuningModelnEntity.setWorkSpace(MaasConst.MODEL_APP_NSAPCE);
        new BaseGpuCalcHandler().calcTargetCardAndDeploy(trainPlanEntity, tuningModelnEntity);
        if (tuningModelnEntity.getMultipleServersConfigs().size() == 0) {
            throw new DataValidException("分配显卡异常!请联系管理员!");
        }
        return tuningModelnEntity;
    }

    @Override
    public JsonReturnBean linkAppTalk(String id) {
        ModelAppEntity modelAppEntity = modelAppDAO.find(id);
        if (MaasConst.DOPLAN_CLOSE_STATUS == modelAppEntity.getStatus()){
            return JsonResultUtil.errorMsg("模型服务已关闭.不允许对话");
        }
        if (modelAppEntity.getContentLength() == null || modelAppEntity.getContentLength() == 0){
            return JsonResultUtil.errorMsg("该模型未设置上下文.请联系管理员!");
        }
        //检查模型是否可用
        KubernetesApiClient kubernetesApiClient = FileControllerService.getCacheK8sClient(modelAppEntity.getServerId());
        //检查模型是否还在Running状态
        try {
            List<V1Pod> pods = kubernetesPodHandler.getPodByLabelApp(kubernetesApiClient, MaasConst.MODEL_APP_NSAPCE, modelAppEntity.getModelServerId());
            if (pods.size() == 0){
                return JsonResultUtil.errorMsg("服务未存在.以下是可能性: 重启、任务已关闭");
            }
        } catch (ApiException e) {
            return JsonResultUtil.errorMsg("服务未进入运行状态");
        }
        String url = kubernetesApiClient.getServer().getAddress()+":"+modelAppEntity.getPort()+"/v1/models";
        //检查模型的API/通信端口是否正常
        //如果访问成功 代表模型是活着的状态
        try {
            String result = HttpUtil.get(url);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg("模型状态不可用!模型已关闭或初始化中...请联系管理员!");
        }


        try {
            String token = null;
            JsonReturnBean platformToken = aiLlmConfigService.getPlatformToken();
            if (platformToken!=null && platformToken.isHasOk()){
                token = platformToken.getMessage();
            }
            //检查该模型到对方平台的信息
            if (StringUtils.isEmpty(token)) {
                return JsonResultUtil.errorMsg("未获取到授权!.请联系管理员");
            }
            KubernetesApiClient defaultClient = FileControllerService.getCustomClient(modelAppEntity.getServerId());
            if (defaultClient == null) {
                return JsonResultUtil.errorMsg("服务异常!请联系系统管理员!");
            }
            //上下文是max_tokens的0.9 绝对不可以是空或者0
            int maxTokens = (int) (StringHandlerUtil.convertFromK(modelAppEntity.getContentLength()) * 0.9);
            if (maxTokens == 0){
                return JsonResultUtil.errorMsg("上下文不可以设置为0");
            }

            //获取字典
            DictionaryCacheObject linkAppParams = DictionaryLib.getDictionaryByCode(MaasConst.DIC_LINK_AI_TALK);
            if (linkAppParams.getItems().size() == 0) {
                return JsonResultUtil.errorMsg("未配置信息.请联系管理员");
            }
            String pageUrl = null;
            String loginAccount = null;
            String login = null;
            String platformUrl = null;
            for (DictionaryItemCacheObject item : linkAppParams.getItems()) {
                switch (item.getCode()) {
                    case "page_url":
                        pageUrl = item.getValue();
                        break;
                    case "login_account":
                        loginAccount = item.getValue();
                        break;
                    case "login":
                        login = item.getValue();
                        break;
                    case "platform_url":
                        platformUrl = item.getValue();
                        break;
                }
            }

            //先查看该模型是否已配置到AIPAAS平台的 模型服务器配置上面
            AiLlmConfigEntity linkModel = aiLlmConfigService.findLinkModel(modelAppEntity.getPlatformModelId(), token);
            if (linkModel == null){
                //创建linkModel
                boolean linkModelFlag = createLinkModel(platformUrl, modelAppEntity, token);
                if (linkModelFlag == false){
                    return JsonResultUtil.errorMsg("创建模型相关异常.请联系管理员!");
                }
                linkModel = aiLlmConfigService.findLinkModel(modelAppEntity.getPlatformModelId(), token);
                modelAppEntity.setPlatformModelName(linkModel.getCode());
                update(modelAppEntity);
            }
            if (linkModel == null){
                return JsonResultUtil.errorMsg("未初始化成功!请联系管理员!");
            }
            // 查看该模型是否已配置到AIPAAS平台的 AI助理上面
            AssistantsEntity linkModelAssistant = aiLlmConfigService.findLinkModelAssistant(modelAppEntity.getPlatformAssistantId(), token);
            if (linkModelAssistant == null) {
                boolean linkModelAssistantFlag = createLinkModelAssistant(platformUrl, modelAppEntity, linkModel.getCode(), token);
                //检查创建会话助理结果
                if (linkModelAssistantFlag == false){
                    return JsonResultUtil.errorMsg("创建会话相关异常.请联系管理员!");
                }
                update(modelAppEntity);
            }
            return JsonResultUtil.success(pageUrl.replace("${token}", token));
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResultUtil.errorMsg("配置不合法--9003.请联系管理员");
        }
    }

    private boolean createLinkModel(String platformUrl, ModelAppEntity modelAppEntity, String token) throws Exception {
        AiLlmConfigEntity aiLlmConfigEntity = new AiLlmConfigEntity();
        aiLlmConfigEntity.setName(modelAppEntity.getName());

        String code = System.currentTimeMillis()+"-ai-maas-"+modelAppEntity.getModelName();
        if (code.length()>50){
            code = (System.currentTimeMillis()+"").substring(20)+"-ai-maas-"+modelAppEntity.getModelName();
        }

        aiLlmConfigEntity.setCode(code);
        aiLlmConfigEntity.setType(1);
        aiLlmConfigEntity.setModelFactory("deepSeek");
        aiLlmConfigEntity.setApiHost(modelAppEntity.getTotalApiPath());
        aiLlmConfigEntity.setUnderstandType(GlobalConsts.ONE);
        //如果是视觉相关的 对应的AIPAAS平台也要设置视觉相关 3是视觉理解 6是文生图
        //如果等于3或者等于6
        if (modelAppEntity.getType() == 3 || modelAppEntity.getType() == 6){
            aiLlmConfigEntity.setUnderstandType(GlobalConsts.TWO);
        }

        aiLlmConfigEntity.setFlgTool(0);
        aiLlmConfigEntity.setStatus(1);
        aiLlmConfigEntity.setRemark("操作说明：新增模型至AIPAAS平台  \n" +
                "- 模型名称："+modelAppEntity.getModelName()+"  \n" +
                "- 操作时间："+DateUtil.getDateStrMin(new Date())+"  \n" +
                "- 操作人：AI-MAAS平台\n" +
                "- 系统地址: http://extranet.badousoft.com:32080/badouai-maas-front/#/  \n");
        /**
         * id:
         * name: test
         * code: test
         * type: 1
         * modelFactory: minimaxi
         * apiHost: test
         * apiKey:
         * understandType: 1
         * flgTool: 0
         * remark:
         * status: 1
         * sign: false
         */
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> params = objectMapper.convertValue(aiLlmConfigEntity, Map.class);
        HttpRequest httpRequest = HttpUtil.createPost(platformUrl + "/project/llm/246-k8s-admin.conf/aillmconfigsave/saveIncludeFile.do?mdCode=ai_llm_config");
        httpRequest.form(params);
        httpRequest.header("Token",token);
        String insertResult = httpRequest.execute().body();
        logger.info(insertResult);
        JSONObject jsonObject = JSONObject.parseObject(insertResult);
        boolean modelConfig = false;
        if (jsonObject.getBoolean("hasOk")){
            JSONObject bean = jsonObject.getJSONObject("bean");
            if (bean!=null && bean instanceof JSONObject){
                String id = bean.getString("id");
                if (StringUtils.isNotBlank(id)){
                    aiLlmConfigEntity.setId(id);
                    modelAppEntity.setPlatformModelId(id);
                    modelConfig = true;
                }
            }
        }

        return modelConfig;
    }

    private boolean createLinkModelAssistant(String platformUrl,ModelAppEntity modelAppEntity,String modelType,String token) {
        /**
         * id:
         * name: 测试
         * type:
         * assistantRole: conversation
         * chatMemoryLimit:
         * chatMemoryWord:
         * modelType: glm-4-9b_8a74808893fd204f019406cd51960774
         * llmApiKey:
         * promotions: 你是一个智能助理，会分析用户的问题及用户上下文的问题意图，进行逻辑分析，逐步推理进行回复。
         * flgUserPromot: 0
         * chatPrompt:
         * knPrompt:
         * isOpen: 0
         * status: 1
         * frequencyPenalty: 0
         * presencePenalty: 0
         * temperature: 0.8
         * topP: 0.9
         * maxTokens: 2048
         * knMaxResults: 3
         * knMinScore: 0.2
         * flgStream: 1
         * qaFlgReply: 0
         * understandType: 0
         */
        Map<String,Object> createAssParams = new HashMap<>();
        createAssParams.put("name",modelAppEntity.getName());
        //固定 是会话助理
        createAssParams.put("assistantRole","conversation");
        //创建助理 注意 modelType必须是模型配置的 Code字段
        createAssParams.put("modelType",modelType);
        createAssParams.put("promotions","你是一个智能助理，会分析用户的问题及用户上下文的问题意图，进行逻辑分析，逐步推理进行回复。");
        createAssParams.put("flgUserPromot",0);
        createAssParams.put("isOpen",0);
        createAssParams.put("status",1);
        createAssParams.put("frequencyPenalty",0);
        createAssParams.put("presencePenalty",0);
        createAssParams.put("temperature",0.8);
        createAssParams.put("topP",0.9);
        //这里乘于0.9 是因为AIPAAS的Tokens会自己加16.比如我当前模型最大的上下文支持是40000.我现在给配置到AIPAAS模型设置到40000
        //实际他们会搞成40016 强行撑爆上下文.所以乘于0.9 只给9/10的上下文给到AIPAAS 避免超出最大上下文导致失败
        createAssParams.put("maxTokens",(int) (StringHandlerUtil.convertFromK(modelAppEntity.getContentLength()) * 0.9));
        createAssParams.put("knMaxResults",3);
        createAssParams.put("knMinScore",0.2);
        createAssParams.put("flgStream",1);
        createAssParams.put("qaFlgReply",0);
        createAssParams.put("understandType",0);

        //添加助理.要成为助理后才可以进行对话
        HttpRequest httpRequest = HttpUtil.createPost(platformUrl + "/project/assistants/assistantssave/saveIncludeFile.do?mdCode=my_assistant_admin");
        httpRequest.form(createAssParams);
        httpRequest.header("Token",token);
        String insertNewAssResult = httpRequest.execute().body();
        logger.info(insertNewAssResult);
        JSONObject assistantsJson = JSONObject.parseObject(insertNewAssResult);
        boolean assistantsConfig = false;
        if (assistantsJson.getBoolean("hasOk")){
            JSONObject bean = assistantsJson.getJSONObject("bean");
            if (bean!=null && bean instanceof JSONObject){
                String id = bean.getString("id");
                if (StringUtils.isNotBlank(id)){
                    assistantsConfig = true;
                    modelAppEntity.setPlatformAssistantId(id);
                }
            }
        }
        return assistantsConfig;
    }

    @Override
    public String checkAppStarted(ModelAppEntity modelAppEntity, Integer timeOutMin) {
        //查询日志等待
        String nowLogs = getLogs(null, modelAppEntity,"0",2000,3,null);
        long startTime = System.currentTimeMillis();
        logger.info("检查AI模型应用是否正确启动");
        while (System.currentTimeMillis() - startTime < TimeUnit.MINUTES.toMillis(timeOutMin)) {
            //运行中 查看日志是否包含运行成功的那行代码 如果有 代表启动完成
            if (StringUtils.isNotBlank(nowLogs) && nowLogs.contains("8000")) {
                logger.info(nowLogs);
                logger.info("检查成功!确认正确启动成功!");
                return null;
            }
            logger.info("检查AI模型应用是否正确启动成功中...");
            try {
                nowLogs =getLogs(null, modelAppEntity,"0",2000,3,null);
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                logger.error(nowLogs);
                e.printStackTrace();
                return "等待模型启动异常!请联系管理员!";
            }
        }
        logger.error(nowLogs);
        return "AI模型启动等待已超过" + timeOutMin + "分钟,已超时.任务自动关闭.";
    }

    @Override
    public Object loadApiMsg(String id) {
        ModelAppEntity modelAppEntity = modelAppDAO.find(id);
        try {
            String paramsDicName = MaasConst.MODEL_API_PARAMS_DIC;

            String modelType = DictionaryLib.getItemCodeByItemValue("MODEL_TYPE", modelAppEntity.getType() + "");
            if (StringUtils.isEmpty(modelType)){
                throw new DataEmptyException("未设置该类型模型的请求参数设置.请联系管理员!");
            }
            paramsDicName = modelType.toUpperCase()+"_"+paramsDicName;

            String allUrl = modelAppEntity.getTotalApiPath();
            String response = modelAppEntity.getResponse();
            logger.info("与AI对话.服务地址:" + allUrl);

            JSONObject params = new JSONObject();
            JSONObject apiMsg = new JSONObject();
            DictionaryCacheObject dictionaryByCode = DictionaryLib.getDictionaryByCode(paramsDicName);
            if (dictionaryByCode == null || dictionaryByCode.getItems().size() == 0){
                throw new DataValidException("未设置实际执行参数!");
            }

            List<DictionaryItemCacheObject> items = dictionaryByCode.getItems();
            List<JSONObject> paramsArray = new LinkedList<>();
            items.forEach(e -> {
                try {
                    if ("model".equals(e.getCode())){
                        e.setValue(modelAppEntity.getModelName());
                    }
                    paramsArray.add(buildParamObj(e));
                    if ("messages".equals(e.getCode()) || "documents".equals(e.getCode())){
                        params.put(e.getCode(),JSONArray.parseArray(e.getValue()));
                    }else if ("custom_config".equals(e.getCode())){
                        params.put(e.getCode(),JSONObject.parseObject(e.getValue()));
                    }else {
                        params.put(e.getCode(),e.getValue());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            apiMsg.put("address", allUrl);
            apiMsg.put("paramsArray", paramsArray);
            apiMsg.put("method", "POST");
            apiMsg.put("contentType", dictionaryByCode.getValue());
            apiMsg.put("model", modelAppEntity.getModelName());
            apiMsg.put("paramsSample", params.toJSONString());
            apiMsg.put("responseSample", response);

            apiMsg.put("requestTitle","请求例子");
            apiMsg.put("requestContnet",buildTemplateByJson(params));
            apiMsg.put("responseTitle","响应例子");
            apiMsg.put("responseContnet",buildTemplateByJson(JSONObject.parseObject(response)));
            return apiMsg;
        } catch (Exception e) {
            e.printStackTrace();
            return "服务异常!请联系系统管理员!";
        }
    }

    private String buildTemplateByJson(JSONObject jsonObject){
        String lineTemplate = "<p style='text-indent:30px;'><span class=\"pln\"></span><span class=\"str\">\":field\"</span><span class=\"pun\">:</span><span class=\"str\">\":value\"</span><span class=\"pun\">,</span><span class=\"pln\">\t</span></p>";
        StringBuilder builder = new StringBuilder();
        builder.append("<span class=\"pun\">{</span>");
        for(String key:jsonObject.keySet()){
            builder.append(lineTemplate.replace(":field",key).replace(":value",jsonObject.getString(key)==null?"null":jsonObject.getString(key)));
        }
        builder.append("<span class=\"pun\">}</span>");
        return builder.toString();
    }

    /**
     * 组建参数实体
     *
     * @param dicObj
     * @return
     * @throws Exception
     */
    private static JSONObject buildParamObj(DictionaryItemCacheObject dicObj) throws Exception {
        JSONObject params = new JSONObject();
        params.put("code", dicObj.getCode());
        String[] split = dicObj.getName().split(",");
        if (split.length != 2) {
            throw new Exception(dicObj.getName()+"格式不对");
        }
        params.put("name", split[0]);
        params.put("type", split[1]);
        params.put("remark", dicObj.getRemark());
        params.put("value", dicObj.getValue());
        params.put("require", dicObj.getFlgDefault() == 0 ? "否" : "是");

        return params;
    }

    @Override
    public TuningModelnEntity getLinkAppModel(String id) {
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("model_app_id", id, null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted", 0, null, QueryOperSymbolEnum.eq));
        List<TuningModelnEntity> tuningModelnEntities = tuningModelnService.find(queryCriterion);
        if (tuningModelnEntities.size() == 1) {
            return tuningModelnEntities.get(0);
        }
        return null;
    }

    @Override
    public void createEntity(ModelAppEntity modelAppEntity) {
        baseDAO.save(modelAppEntity);
    }

    @Override
    public String removeAppResouce(ModelAppEntity modelAppEntity, KubernetesApiClient kubernetesClient,V1Pod v1Pod) throws Exception {
        //移除外部端口服务
        try {
            //任务启动 创建service
            kubernetesServiceHandler.deleteService(kubernetesClient, MaasConst.MODEL_APP_NSAPCE, modelAppEntity.getModelServerId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("移除服务失败!");
        }
        //移除相关资源
        ModelWarehouseEntity modelWarehouseEntity = modelWarehouseService.find(modelAppEntity.getModelId());
        TrainPlanEntity trainPlanEntity = new TrainPlanEntity();
        trainPlanEntity.setGpuCount(modelWarehouseEntity.getGpuCount());
        trainPlanEntity.setPreGpucache(modelWarehouseEntity.getStartNeedGpum());
        trainPlanEntity.setServerGpuMode(modelWarehouseEntity.getGpuCount() > GlobalConsts.ZERO ? GlobalConsts.ONE : GlobalConsts.ZERO);
//        trainPlanEntity.setMoreCardFlag(modelWarehouseEntity.getGpuCount() > GlobalConsts.ZERO ? GlobalConsts.ONE : GlobalConsts.ZERO);
        TuningModelnEntity tuningModelnEntity = new TuningModelnEntity();
        tuningModelnEntity.setCode(modelAppEntity.getModelServerId());
        tuningModelnEntity.setWorkSpace(MaasConst.MODEL_APP_NSAPCE);
        tuningModelnEntity.setServerId(modelWarehouseEntity.getServerId());
        new BaseGpuCalcHandler().removeTunPlanTargetCard(trainPlanEntity,tuningModelnEntity);
        if (v1Pod == null){
            v1Pod = getModelAppPod(modelAppEntity,kubernetesClient);
        }
        if (v1Pod!=null){
            kubernetesPodHandler.deleteOnePod(kubernetesClient, MaasConst.MODEL_APP_NSAPCE, v1Pod.getMetadata().getName());
        }
        return null;
    }

    @Override
    public String stopApp(String id, boolean deleteFlag,String customMsg) {
        ModelAppEntity modelAppEntity = baseDAO.find(id);
//        JsonReturnBean platformToken = aiLlmConfigService.getPlatformToken();
//        if (platformToken == null || !platformToken.isHasOk()){
//            return "授权获取失败!请联系管理员!";
//        }
        if (Objects.isNull(modelAppEntity)) {
            return "无效的数据";
        }
        if (modelAppEntity.getStatus() == null){
            delete(modelAppEntity);
            return null;
        }
        if (MaasConst.DOPLAN_CLOSE_STATUS == modelAppEntity.getStatus()){
            delete(modelAppEntity);
            return null;
        }
        String modelServerId = modelAppEntity.getModelServerId();
        try {
            KubernetesApiClient defaultClient = FileControllerService.getCustomClient(modelAppEntity.getServerId());
            String modelAppNsapce = MaasConst.MODEL_APP_NSAPCE;
            V1Deployment v1Deployment = kubernetesPodHandler.getOneDeployment(defaultClient, modelAppNsapce, modelServerId);
            if (v1Deployment == null){
                delete(modelAppEntity);
                return null;
            }
            V1PodList v1PodList = kubernetesPodHandler.getPodByDeployment(defaultClient, modelAppNsapce, modelServerId);
            String[] cards = null;
            //获取该POD绑定的显卡
            for (V1EnvVar v1EnvVar : v1PodList.getItems().get(0).getSpec().getContainers().get(0).getEnv()) {
                if (MaasConst.NVIDIA_VISIBLE_DEVICES.equals(v1EnvVar.getName())){
                    cards = v1EnvVar.getValue().split(",");
                }
            }
            if (cards == null){
                return "无效的任务!请联系管理员!";
            }
            //移除之前 获取该任务的显卡资源
            Map<Integer, ServerGpuEntity> currentNewCardStatusMap = baseGpuCalcHandler.getCurrentNewCardStatusMap(defaultClient.getServer().getGpuMsgUrl());
            if (currentNewCardStatusMap.size() == 0){
                return "服务器异常!请联系管理员!";
            }
            Map<Integer, ServerGpuEntity> initCardStatus = new HashMap<>();
            for (String card : cards) {
                initCardStatus.put(Integer.parseInt(card),currentNewCardStatusMap.get(Integer.parseInt(card)));
            }
            if (initCardStatus.size() != cards.length){
                return "任务执行信息不一致!请联系管理员!";
            }
            if (v1Deployment != null && v1PodList.getItems().size() == 1) {
                kubernetesPodHandler.deleteDeployment(defaultClient,modelAppNsapce,modelServerId);
                kubernetesServiceHandler.deleteService(defaultClient,modelAppNsapce,modelServerId);
                //检查POD并且强制移除
                List<V1Pod> podByLabelApp = kubernetesPodHandler.getPodByLabelApp(defaultClient, modelAppNsapce, modelServerId);
                for (V1Pod v1Pod : podByLabelApp) {
                    try {
                        List<ProcessStatusVo> podProcessStatus = kubernetesExecHandler.getPodProcessStatus(v1Pod.getMetadata().getName(),
                                modelAppNsapce,defaultClient ,"python3");
                        podProcessStatus.forEach(e->{
                            try {
                                kubernetesExecHandler.execCommandOnce(v1Pod.getMetadata().getName(),modelAppNsapce,defaultClient,new String[]{"kill","-9",e.getPid()+""});
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            } catch (ApiException ex) {
                                throw new RuntimeException(ex);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        kubernetesPodHandler.deleteOnePod(defaultClient,modelAppNsapce,v1Pod.getMetadata().getName());
                    }catch (Exception e){
                        e.printStackTrace();
                        logger.warn("移除警告");
                    }
                }
//                int max = 40;
//                int nowCount = 0;
//                boolean done = false;
//                //任务已删除完成 循环获取显卡状态 判断是否降低10% 最多等待2分钟
//                while (nowCount <= max){
//                    TimeUnit.SECONDS.sleep(3);
//                    currentNewCardStatusMap = baseGpuCalcHandler.getCurrentNewCardStatusMap(defaultClient.getServer().getGpuMsgUrl());
//                    for(Integer key:initCardStatus.keySet()){
//                        if (currentNewCardStatusMap.get(key) == null || initCardStatus.get(key) == null){
//                            System.out.println("123");
//                        }
//                        //获取当前和初始状态
//                        int nowCache = currentNewCardStatusMap.get(key).getUsedGraphicsMemory();
//                        int initCache = initCardStatus.get(key).getUsedGraphicsMemory();
//                        //都是Integer类型
//                        double diff = ((initCache-nowCache)/initCache)*100;
//                        if (diff > 3 || (initCache-nowCache)>300) {
//                            done = true;
//                            logger.info("显卡"+key+"已经从初始值的"+initCache+"显存降低到"+nowCache+",差异为"+diff+",差异大于等于3%或者超过300差值代表显存开始释放.任务成功");
//                            break;
//                        }else {
//                            logger.info("显卡"+key+"已经从初始值的"+initCache+"显存降低到"+nowCache+",差异大于等于3%或者超过300差值代表显存开始释放.正在检查.目前差异"+diff);
//                        }
//                    }
//                    if (done){
//                        break;
//                    }
//                    nowCount++;
//                }
//
//                if (!done){
//                    return "正常终止异常!请联系管理员!";
//                }
                modelAppEntity.setMsg("手动已关闭");
                modelAppEntity.setStatus(MaasConst.DOPLAN_CLOSE_STATUS);
                //当customMsg不是空的 代表是任务删除
                if (StringUtils.isNoneBlank(customMsg)){
                    //再拿一份日志
                    modelAppEntity.setMsg(kubernetesPodHandler.readPodAllLog(defaultClient,modelAppNsapce,v1PodList.getItems().get(0).getMetadata().getName(),350)+"\n"+customMsg);
                    modelAppEntity.setStatus(MaasConst.DOPLAN_FAIL_STATUS);
                }
                //检查是否已经关联到了AIPAAS 如果有 则调用对方接口移除
//                if (StringUtils.isNoneBlank(modelAppEntity.getPlatformModelId())){
//                    Boolean aiLlmConfig = aiLlmConfigService.deleteRow(modelAppEntity.getPlatformModelId(), "ai_llm_config", platformToken.getMessage());
//                    if (aiLlmConfig){
//                        modelAppEntity.setPlatformModelId(null);
//                    }
//                }
//                if (StringUtils.isNoneBlank(modelAppEntity.getPlatformAssistantId())){
//                    Boolean myAssistant = aiLlmConfigService.deleteRow(modelAppEntity.getPlatformAssistantId(), "my_assistant", platformToken.getMessage());
//                    if (myAssistant){
//                        modelAppEntity.setPlatformAssistantId(null);
//                    }
//                }
                update(modelAppEntity);
                return null;
            }
            if (deleteFlag) {
                logger.info("移除应用");
                baseDAO.delete(id);
            } else {
                modelAppEntity.setStatus(MaasConst.DOPLAN_FAIL_STATUS);
                baseDAO.update(modelAppEntity);
            }

            return "未存在服务!请先启动!";
        } catch (Exception e) {
            e.printStackTrace();
            return "无法获取状态!请联系管理员!";
        }
    }

    @Override
    public String getLogs(String id, ModelAppEntity modelAppEntity,String logType, Integer size, Integer flushTime,String nameSpace) {
        if (modelAppEntity == null) {
            modelAppEntity = baseDAO.find(id);
        }
        if (Objects.isNull(modelAppEntity)) {
            return "无效的数据";
        }
        String modelServerId = modelAppEntity.getModelServerId();
        try {
            KubernetesApiClient defaultClient = FileControllerService.getCustomClient(modelAppEntity.getServerId());
            TuningModelnEntity linkAppModel = getLinkAppModel(id);
            if (linkAppModel != null) {
                defaultClient = FileControllerService.getCustomClient(tuningModelnService.getServerId(linkAppModel));
            }
            String modelAppNsapce = MaasConst.MODEL_APP_NSAPCE;
            if (StringUtils.isNoneBlank(nameSpace)){
                modelAppNsapce = nameSpace;
            }

            V1PodList podByDeployment = kubernetesPodHandler.getPodByDeployment(defaultClient, modelAppNsapce, modelServerId);
            V1Pod pod = podByDeployment.getItems().isEmpty()?null:podByDeployment.getItems().get(0);
            if (pod == null && !modelAppEntity.getMsg().equals("部署成功") && StringUtils.isNotBlank(modelServerId)) {
                return modelAppEntity.getMsg();
            }
            if (pod == null) {
                return "未创建服务!";
            }
            //判断是否失败
            if ("Failed".equals(pod.getStatus().getPhase())) {
                setFail(modelAppEntity,"部署失败\n" + kubernetesPodHandler.readPodAllLog(defaultClient, modelAppNsapce, modelServerId, size));
                removeAppResouce(modelAppEntity,defaultClient,pod);
                return modelAppEntity.getMsg();
            }
            if (!pod.getStatus().getPhase().equals("Running")) {
                return "服务未初始化完成!请等待";
            }
            if (pod != null) {
                String result = kubernetesPodHandler.readPodAllLog(defaultClient, modelAppNsapce, pod.getMetadata().getName(), size);
                if (MaasConst.K8S_POD_RUNNING.equals(pod.getStatus().getPhase()) && result == null){
                    return "服务初始化中...";
                }
                return result.replace("\u001B[","");
            }
            return "服务未创建!请启动";
        } catch (Exception e) {
            e.printStackTrace();
            return "无法获取状态!请联系管理员!";
        }
    }

    public String buildModelUrl(ModelAppEntity modelAppEntity) throws Exception {
        KubernetesApiClient defaultClient = FileControllerService.getCacheK8sClient(modelAppEntity.getServerId());
        if (defaultClient == null) {
            return "服务异常!请联系系统管理员!";
        }
        //先获取当前的模型信息
        K8sServerConfEntity server = defaultClient.getServer();
        String baseUrl = server.getExposeAddress() + ":" + modelAppEntity.getPort();
        String allUrl = baseUrl + "/v1/chat/completions";
        return allUrl;
    }

    private K8sServerConfEntity getTaskServer(KubernetesApiClient defaultClient, ModelAppEntity modelAppEntity) throws ApiException, DataEmptyException {
        K8sServerConfEntity server = defaultClient.getServer();
        //增加不同服务器情况下的支持
        V1Pod onePod = kubernetesPodHandler.getOnePod(defaultClient, modelAppEntity.getModelServerId(), MaasConst.MODEL_APP_NSAPCE);
        if (onePod != null && onePod.getSpec() != null) {
            String nodeName = onePod.getSpec().getNodeName();
            if (StringUtils.isNotBlank(nodeName)) {
                QueryCriterion queryCriterion = new QueryCriterion();
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("code", nodeName, null, QueryOperSymbolEnum.eq));
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
                List<K8sServerConfEntity> k8sServerConfEntities = SpringHelper.getBean(IK8sServerConfService.class).find(queryCriterion);
                if (k8sServerConfEntities.size() != 1) {
                    throw new DataEmptyException("数据异常");
                }
                server = k8sServerConfEntities.get(0);
            }
        }
        return server;
    }

    @Override
    public String talkToAi(TalkEntity talkEntity) {
        boolean stop = StopCenter.checkStopFlag(talkEntity.getTrainModelId(), "EvaluationMqReceiver-模型评价任务-03停止");
        if (stop) {
            return null;
        }
        ModelAppEntity modelAppEntity = baseDAO.find(talkEntity.getId());
        if (modelAppEntity.getPort() == null) {
            return "未有效服务请联系管理员!";
        }

        try {
            KubernetesApiClient defaultClient = FileControllerService.getCustomClient(modelAppEntity.getServerId());
            if (defaultClient == null) {
                return "服务异常!请联系系统管理员!";
            }
            //先获取当前的模型信息

            K8sServerConfEntity server = getTaskServer(defaultClient, modelAppEntity);
//            String allUrl = server.getExposeAddress() + ":" + 30005 + "/v1/chat/completions";
            String baseUrl = FileControllerService.getCacheK8sClient(modelAppEntity.getServerId()) + ":" + modelAppEntity.getPort();
            String allUrl = baseUrl + "/v1/chat/completions";

            JSONObject params = new JSONObject();

            params.put("model", modelAppEntity.getModelName());
            logger.info("与AI对话.服务地址:" + allUrl);

//			JSONArray message = new JSONArray();
//			JSONObject a = new JSONObject();
//			a.put("role","system");
//			a.put("content","You are ChatGLM3, a large language model trained by Zhipu.AI. Follow the user’s instructions carefully. Respond using markdown.");

//			JSONObject b = new JSONObject();
//			b.put("role","user");
//			b.put("content",talkEntity.getContent());
//
//			message.add(a);
//			message.add(b);
            //如果只有一句话
            if (StringUtils.isNotEmpty(talkEntity.getContent()) && talkEntity.getNowTalks() != null && talkEntity.getNowTalks().size() == 0) {
                TalkMsgEntity talkMsgEntity = new TalkMsgEntity();
                talkMsgEntity.setContent(talkEntity.getContent());
                talkMsgEntity.setRole("user");
                talkEntity.getNowTalks().add(talkMsgEntity);
            }
            /**
             * 20241224 增加图片的支持
             *             "content": [
             *                 {
             *                     "type": "image_url",
             *                     "image_url": {
             *                         "url": "http://extranet.badousoft.com:32080/bddeplpoy_platform/attach/action/attachsupport/downloadAttach.do?attachId=8a7480b3930ad2df01933d1fd2230a1b"
             *                     }
             *                 },
             *                 {
             *                     "type": "text",
             *                     "text": "描述下这个图片"
             *                 }
             *             ]
             */
            params.put("messages", talkEntity.getNowTalks());
            checkBuildImages(talkEntity.getNowTalks(), params);
            DictionaryCacheObject modelTalkParams = DictionaryLib.getDictionaryByCode("MODEL_TALK_PARAMS");

            params.put("stream", false);
//            params.put("max_tokens", "100");
            modelTalkParams.getItems().forEach(e -> {
                if ("stream".equals(e.getCode())) {
                    params.put("stream", e.getValue());
                } else if ("max_tokens".equals(e.getCode())) {
                    params.put("max_tokens", e.getValue());
                }
            });
            if (StringUtils.isNotBlank(talkEntity.getMaxTokens())) {
                params.put("max_tokens", talkEntity.getMaxTokens());
            }
            params.put("temperature", 0.8);
            params.put("top_p", 0.8);
            String paramsJson = params.toJSONString();
            logger.info("对话参数:" + paramsJson);
            final String result = sendPost(allUrl, paramsJson);
            logger.info("对话结果" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "服务异常!请联系系统管理员!";
        }
    }

    public void checkBuildImages(ArrayList<TalkMsgEntity> talkMsgEntities, JSONObject params) {
        if (talkMsgEntities.size() > 0) {
            TalkMsgEntity talkMsgEntity = talkMsgEntities.get(talkMsgEntities.size() - 1);
            if (StringUtils.isNotBlank(talkMsgEntity.getImage_url())) {
                if (StringUtils.isEmpty(talkMsgEntity.getContent())) {
                    talkMsgEntity.setContent("请你描述下这个图片");
                }
//                JSONArray messages1 = JSONArray.parseArray("[{\"role\":\"user\",\"content\":[{\"text\":\"测试\",\"type\":\"text\"},{\"image_url\":\"测试\",\"type\":\"image_url\"}]}]");
//                messages1.getJSONObject(0).getJSONArray("content").getJSONObject(0).put("text",talkMsgEntity.getContent());
//                messages1.getJSONObject(0).getJSONArray("content").getJSONObject(1).put("image_url",talkMsgEntity.getImage_url());

                String s = FileUtil.readString(new File("D:\\a.json"), "utf-8");
                JSONArray messages1 = JSONObject.parseObject(s).getJSONArray("messages");
                messages1.getJSONObject(0).getJSONArray("content").getJSONObject(0).put("text", talkMsgEntity.getContent());
                messages1.getJSONObject(0).getJSONArray("content").getJSONObject(1).getJSONObject("image_url").put("url", MaasConst.MULIT_BASE64_IMAGE_PREFIX + talkMsgEntity.getImage_url());
//                System.out.println(messages1.getJSONObject(0).getJSONArray("content").getJSONObject(1).getString("image_url"));
                params.put("messages", messages1);
            }
        }
    }

    @Override
    public String restartApp(String id) {
        ModelAppEntity modelAppEntity = baseDAO.find(id);
        if (Objects.isNull(modelAppEntity)) {
            return "无效的数据";
        }
        String modelServerId = modelAppEntity.getModelServerId();
        try {
            KubernetesApiClient defaultClient = FileControllerService.getCustomClient(modelAppEntity.getServerId());

            String modelAppNsapce = MaasConst.MODEL_APP_NSAPCE;
            List<V1Pod> podByLabelApp = kubernetesPodHandler.getPodByLabelApp(defaultClient, modelAppNsapce, modelServerId);
            if (podByLabelApp.size() == 0){
                return "未存在有效的模型任务!请联系管理员!";
            }
            //移除目标POD
            for (V1Pod v1Pod : podByLabelApp) {
                kubernetesPodHandler.deleteOnePod(defaultClient,modelAppNsapce,v1Pod.getMetadata().getName());
            }
            //等待1分钟 查看POD是否有创建
            int maxAttempts = 90;
            int waitTime = 2;
            for (int i = 0; i < maxAttempts; i++) {
                logger.info("RestartApp.重启模型.等待POD重新创建");
                TimeUnit.SECONDS.sleep(waitTime);
                List<V1Pod> newPods = kubernetesPodHandler.getPodByLabelApp(defaultClient, modelAppNsapce, modelServerId);
                if (newPods!=null && newPods.size()>0){
                    for (V1Pod newPod : newPods) {
                        if (MaasConst.K8S_POD_RUNNING.equals(newPod.getStatus().getPhase())){
                            return null;
                        }
                    }
                }
            }
            return "重启超时.请联系管理员!";
        } catch (Exception e) {
            e.printStackTrace();
            return "无法获取状态!请联系管理员!";
        }
    }

    @Override
    public Integer checkModelRunningStatus(ModelAppEntity modelAppEntity,JSONObject result) {
        //如果任务都还没有创建 那么一定是失败的状态
        try {
            //允许容错 当K8S客户端连接不上 可能是因为服务器那边出问题了 但是平台这边不自动移除任务.因为有配置自动重启 服务器一恢复 任务就能恢复
            KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(modelAppEntity.getServerId());
            //检查Deployment状态 如果没有 则设置为失败
            V1Deployment v1Deployment = kubernetesPodHandler.getOneDeployment(kubernetesApiClient, MaasConst.MODEL_APP_NSAPCE, modelAppEntity.getModelServerId());
            if (Objects.isNull(v1Deployment)) {
//                stopApp(modelAppEntity.getId(),true,"未创建任务");
                return MaasConst.DOPLAN_FAIL_STATUS;
            }
            if(v1Deployment!=null){
                Date runningTime = DateUtil.coverUsTime(v1Deployment.getMetadata().getCreationTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
                result.put("allRunTime",DateUtil.getTimeDiff(runningTime,new Date()));
            }
            //如果没有POD 判断deployment是不是已经创建了30分钟 如果是 依然没有POD 判定为创建模型超时
            List<V1Pod> execPods = kubernetesPodHandler.getPodByLabelApp(kubernetesApiClient, MaasConst.MODEL_APP_NSAPCE, modelAppEntity.getModelServerId());
            if (execPods.size() == 0) {
                //v1Deployment的创建时间
                Date date = DateUtil.coverUsTime(v1Deployment.getMetadata().getCreationTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
                boolean timeout = DateUtil.isTimeout(date, new Date(), 30);
                if (timeout){
//                    stopApp(modelAppEntity.getId(),true,"创建模型超时30分钟.任务失败删除");
                    return MaasConst.DOPLAN_FAIL_STATUS;
                }
            }
            //如果有POD 判断是不是Running状态 如果是 访问v1/models接口 如果能通 代表模型进入Ruuning状态 否则获取日志 如果日志行数>0 代表启动中 反之运行中
            if (execPods.size()>0){
                for (V1Pod execPod : execPods) {
                    Date runningTime = DateUtil.coverUsTime(execPod.getStatus().getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
                    result.put("allRunTime",DateUtil.getTimeDiff(runningTime,new Date())+"(重启"+execPod.getStatus().getContainerStatuses().get(0).getRestartCount()+"次)");

                    if (MaasConst.K8S_POD_RUNNING.equals(execPod.getStatus().getPhase())){
                        try {
                            // 创建 GET 请求
                            HttpRequest request = HttpUtil.createGet(modelAppEntity.getTotalModelPath());

                            // 设置连接超时时间为 2 秒（单位：毫秒）
                            request.setConnectionTimeout(500);

                            // 设置读取超时时间为 2 秒（单位：毫秒）
                            request.setReadTimeout(500);
                            // 执行请求并获取响应
                            HttpResponse response = request.execute();
                            if (!response.isOk()) {
                                logger.error("访问该模型接口失败 代表未启动完成.设置状态为启动中");
                                return MaasConst.DOPLAN_RUN_STATUS;
                            }
//                            HttpUtil.get(modelAppEntity.getTotalModelPath());
                        }catch (Exception e){
//                            e.printStackTrace();
                            logger.error("访问该模型接口失败 代表未启动完成.设置状态为启动中");
                            return MaasConst.DOPLAN_RUN_STATUS;
                        }
                        //成功访问 代表进入运行状态
                        return MaasConst.DOPLAN_SUCCESS_STATUS;
                    }
                    //如果不是Ruuning 但是有POD 判断是不是FAIL 如果是FAIL代表已经失败 设置为失败状态
                    if (MaasConst.K8S_POD_FAILED.equals(execPod.getStatus().getPhase())){
//                        stopApp(modelAppEntity.getId(),true,"模型部署失败");
                        return MaasConst.DOPLAN_FAIL_STATUS;
                    }
                    //如果是createing、pedding 则在等等 等他启动 设置为启动中的状态
                    if (MaasConst.K8S_POD_CONTAINERCREATING.equals(execPod.getStatus().getPhase())
                            || MaasConst.K8S_POD_PENDING.equals(execPod.getStatus().getPhase())
                            || MaasConst.K8S_POD_TERMINATING.equals(execPod.getStatus().getPhase())
                    ){
                        return MaasConst.DOPLAN_RUN_STATUS;
                    }else {
                        //未知/非法的状态 直接设置成失败
//                        stopApp(modelAppEntity.getId(),true,execPod.getStatus().getPhase()+"为未知/非法的状态.任务中断");
                        return MaasConst.DOPLAN_FAIL_STATUS;
                    }
                }
            }
            modelAppEntity.setCreateTime(new Date());
        } catch (Exception e) {
            e.printStackTrace();
//            stopApp(modelAppEntity.getId(),true,"更新状态失败");
            return MaasConst.DOPLAN_FAIL_STATUS;
        }
        return MaasConst.DOPLAN_FAIL_STATUS;
    }

    public static String sendPost(String url, String param) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder("");
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                }
            }
        }
        return result.toString();
    }

}

