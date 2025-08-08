package com.badou.project.maas.modelapp.platform.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.platform.model.AiLlmConfigEntity;
import com.badou.project.maas.modelapp.platform.model.AssistantsEntity;
import com.badou.project.maas.modelapp.platform.service.IAiLlmConfigService;
import com.badou.tools.common.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AiLlmConfigServiceImpl implements IAiLlmConfigService {

    @Override
    public JsonReturnBean getPlatformToken() {
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
        if (JsonResultUtil.isNull(pageUrl, loginAccount, login, platformUrl)) {
            return JsonResultUtil.errorMsg("配置不完整.请联系管理员");
        }

        //登录到平台
        JSONObject row = new JSONObject();
        row.put("CAPTCHA", "");
        try {
            row.put("password", new String(Base64.getDecoder().decode(login + "==")));
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResultUtil.errorMsg("配置不合法--9001.请联系管理员");
        }
        row.put("username", loginAccount);
        row.put("uuid", UUID.randomUUID().toString());
        String a = row.toJSONString();
        String key = "izMNRXR9Cx96fTiE";
        String iv = "izMNRXR9Cx96fTiE";
        String encrypted = null;
        try {
            encrypted = StringHandlerUtil.encrypt(a, key, iv);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResultUtil.errorMsg("配置不合法--9002.请联系管理员");
        }
        Map map = new HashMap<>();
        map.put("data", encrypted);
        map.put("sign", "false");
        String logonResult = HttpUtil.post(platformUrl + "/system/security/logon/logon", map);
        JSONObject logonResultJson = JSONObject.parseObject(logonResult);
        Boolean result = logonResultJson.getBoolean("result");
        //登录到AIPAAS平台成功 获取授权Token
        if (result == false) {
            return JsonResultUtil.errorMsg("配置不合法--9004.请联系管理员");
        }
        String token = logonResultJson.getString("message");
        return JsonResultUtil.success(token);
    }

    @Override
    public void deleteOne(String id,String token) throws Exception {
//        http://extranet.badousoft.com:32080/ai-creations-platform/jdbc/common/basecommondelete/delete.do
//        mdCode: ai_llm_config
//        ids: 90a801dc9b2e4793a37850b048864c4d723b484e428e424fbeda4628f31a9ee7
//        sign: false
        String platformUrl = DictionaryLib.getItemValueByItemCode(MaasConst.DIC_LINK_AI_TALK, "platform_url");
        HttpRequest httpRequest = HttpUtil.createPost(platformUrl + "/jdbc/common/basecommondelete/delete.d");
        Map params = new HashMap();
        params.put("ids",id);
        params.put("mdCode","ai_llm_config");
        httpRequest.form(params);
        httpRequest.header("Token",token);
        String insertResult = httpRequest.execute().body();
        JSONObject jsonObject = JSONObject.parseObject(insertResult);
        if (jsonObject.getBoolean("hasOk")==null || jsonObject.getBoolean("hasOk")==false){
            throw new Exception("delete empty");
        }
    }

    @Override
    public AiLlmConfigEntity findAndCreateOne(String token, ModelAppEntity modelAppEntity, String apiHost, String modelFactory) {
        AiLlmConfigEntity aiLlmConfigEntity = new AiLlmConfigEntity();
        aiLlmConfigEntity.setName(modelAppEntity.getName());

        aiLlmConfigEntity.setCode(DateUtil.getDateStrMin(new Date())+"ai-maas-"+modelAppEntity.getModelName());
        aiLlmConfigEntity.setType(1);
        aiLlmConfigEntity.setModelFactory(modelFactory);
        aiLlmConfigEntity.setApiHost(apiHost);
        aiLlmConfigEntity.setUnderstandType(modelAppEntity.getType()+1);
        aiLlmConfigEntity.setFlgTool(0);
        aiLlmConfigEntity.setStatus(1);
        aiLlmConfigEntity.setRemark(modelAppEntity.getModelName());
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
        String platformUrl = DictionaryLib.getItemValueByItemCode(MaasConst.DIC_LINK_AI_TALK, "platform_url");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> params = objectMapper.convertValue(aiLlmConfigEntity, Map.class);
        HttpRequest httpRequest = HttpUtil.createPost(platformUrl + "/project/llm/246-k8s-admin.conf/aillmconfigsave/saveIncludeFile.do?mdCode=ai_llm_config");
        httpRequest.form(params);
        httpRequest.header("Token",token);
        String insertResult = httpRequest.execute().body();
        log.info(insertResult);
        JSONObject jsonObject = JSONObject.parseObject(insertResult);
        boolean modelConfig = false;
        if (jsonObject.getBoolean("hasOk")){
            JSONObject bean = jsonObject.getJSONObject("bean");
            if (bean!=null && bean instanceof JSONObject){
                String id = bean.getString("id");
                if (StringUtils.isNotBlank(id)){
                    aiLlmConfigEntity.setId(id);
                    modelConfig = true;
                }
            }
        }

        if (modelConfig == false){
            return null;
        }
        //创建助理 注意 modelType必须是正确的AI对话模型名称.
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
//        project/assistants/assistantssave/saveIncludeFile.do?mdCode=my_assistant_admin
        Map<String,Object> createAssParams = new HashMap<>();
        createAssParams.put("name",modelAppEntity.getName());
        createAssParams.put("assistantRole","conversation");
        createAssParams.put("modelType",aiLlmConfigEntity.getName());
        createAssParams.put("promotions","你是一个智能助理，会分析用户的问题及用户上下文的问题意图，进行逻辑分析，逐步推理进行回复。");
        createAssParams.put("flgUserPromot",0);
        createAssParams.put("isOpen",0);
        createAssParams.put("status",1);
        createAssParams.put("frequencyPenalty",0);
        createAssParams.put("presencePenalty",0);
        createAssParams.put("temperature",0.8);
        createAssParams.put("topP",0.9);
        createAssParams.put("maxTokens",4096);
        createAssParams.put("knMaxResults",3);
        createAssParams.put("knMinScore",0.2);
        createAssParams.put("flgStream",1);
        createAssParams.put("qaFlgReply",0);
        createAssParams.put("understandType",0);


        //添加助理.要成为助理后才可以进行对话
        httpRequest = HttpUtil.createPost(platformUrl + "/project/assistants/assistantssave/saveIncludeFile.do?mdCode=my_assistant_admin");
        httpRequest.form(createAssParams);
        httpRequest.header("Token",token);
        String insertNewAssResult = httpRequest.execute().body();
        log.info(insertNewAssResult);
        JSONObject assistantsJson = JSONObject.parseObject(insertNewAssResult);
        boolean assistantsConfig = false;
        if (assistantsJson.getBoolean("hasOk")){
            JSONObject bean = assistantsJson.getJSONObject("bean");
            if (bean!=null && bean instanceof JSONObject){
                String id = bean.getString("id");
                if (StringUtils.isNotBlank(id)){
                    assistantsConfig = true;
                }
            }
        }
        if (assistantsConfig == false){
            return null;
        }
        return aiLlmConfigEntity;
    }

    @Override
    public AiLlmConfigEntity findLinkModel(String id,String token) {
        String platformUrl = DictionaryLib.getItemValueByItemCode(MaasConst.DIC_LINK_AI_TALK, "platform_url");
        Map params = new HashMap();
        params.put("searchParam","[{\"name\":\"id\",\"value\":\""+id+"\",\"type\":\"exact-match\",\"tagName\":\"\"}]");
        HttpRequest httpRequest = HttpUtil.createPost(platformUrl + "/project/llm/246-k8s-admin.conf/aillmconfiglist/listJSON.do?mdCode=ai_llm_config");
        httpRequest.form(params);
        httpRequest.header("Token",token);
        String post = httpRequest.execute().body();
        log.info(post);
        JSONObject jsonObject = JSONObject.parseObject(post);
        if (jsonObject.getJSONArray("Rows")!=null){
            JSONArray rows = jsonObject.getJSONArray("Rows");
            if (rows.size() == 1){
                return rows.getObject(0,AiLlmConfigEntity.class);
            }
        }
        return null;
    }

    @Override
    public AssistantsEntity findLinkModelAssistant(String id,String token) {
        String platformUrl = DictionaryLib.getItemValueByItemCode(MaasConst.DIC_LINK_AI_TALK, "platform_url");
        Map params = new HashMap();
        params.put("searchParam","[{\"name\":\"id\",\"value\":\""+id+"\",\"type\":\"exact-match\",\"tagName\":\"\"}]");
        HttpRequest httpRequest = HttpUtil.createPost(platformUrl + "/project/assistants/assistantslist/listJSON.do?mdCode=my_ai");
        httpRequest.form(params);
        httpRequest.header("Token",token);
        String post = httpRequest.execute().body();
        log.info(post);
        JSONObject jsonObject = JSONObject.parseObject(post);
        if (jsonObject.getJSONArray("Rows")!=null){
            JSONArray rows = jsonObject.getJSONArray("Rows");
            if (rows.size() == 1){
                return rows.getObject(0, AssistantsEntity.class);
            }
        }
        return null;
    }

    @Override
    public Boolean deleteRow(String id, String modelCode, String token) {
        String platformUrl = DictionaryLib.getItemValueByItemCode(MaasConst.DIC_LINK_AI_TALK, "platform_url");
        Map params = new HashMap();
        params.put("mdCode",modelCode);
        params.put("ids",id);
        HttpRequest httpRequest = HttpUtil.createPost(platformUrl + "/jdbc/common/basecommondelete/delete.do");
        httpRequest.form(params);
        httpRequest.header("Token",token);
        String post = httpRequest.execute().body();
        log.info("删除记录执行结果:"+post);
        JSONObject jsonObject = JSONObject.parseObject(post);

        return jsonObject.getBoolean("hasOk")==null?false:jsonObject.getBoolean("hasOk");
    }

}
