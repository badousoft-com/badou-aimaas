package com.badou.project.maas.modelapp.platform.service;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.platform.model.AiLlmConfigEntity;
import com.badou.project.maas.modelapp.platform.model.AssistantsEntity;

public interface IAiLlmConfigService {

    JsonReturnBean getPlatformToken();

    /**
     * 删除一条记录
     * @param id
     */
    void deleteOne(String id,String token) throws Exception;

    /**
     * 让ai paas创建模型信息
     * @param token
     * @param apiHost
     * @param modelFactory
     * @return
     */
    AiLlmConfigEntity findAndCreateOne(String token, ModelAppEntity modelAppEntity,String apiHost, String modelFactory);

    /**
     * 获取aipaas模型信息
     * @param id 模型ID
     * @param token 授权Token
     * @return 查询到的大模型
     */
    AiLlmConfigEntity findLinkModel(String id,String token);

    /**
     * 获取aipaas模型助理信息
     * @param id 模型助理ID
     * @param token 授权Token
     * @return 查询到的大模型
     */
    AssistantsEntity findLinkModelAssistant(String id, String token);

    /**
     * 移除目标的记录
     * @param id
     * @param modelCode
     * @return
     */
    Boolean deleteRow(String id,String modelCode,String token);
}
