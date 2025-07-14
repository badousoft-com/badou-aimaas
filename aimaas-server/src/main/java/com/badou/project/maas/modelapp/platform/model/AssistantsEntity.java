package com.badou.project.maas.modelapp.platform.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.badou.core.standard.enums.SystemBoolean;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 助理类
 */
@Entity
@Table(name = "ai_assistants")
@Where(clause = "flg_deleted=0")
public class AssistantsEntity extends AppBaseEntity {

    /**
     * 理解类型  1-文本理解  2-视觉理解
     */
    @Column(name = "understand_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer understandType;

    /**
     * 欢迎语
     */
    @Column(name = "greeting", unique = false, nullable = true, insertable = true, updatable = true)
    protected String greeting;

    /**
     * 重试次数（流式聊天异常时，清除聊天记忆重新请求的次数）
     */
    @Column(name = "retry_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer retryCount;

    /**
     * 头像
     */
    @Column(name = "avatar", unique = false, nullable = true, insertable = true, updatable = true)
    protected String avatar;

    /**
     * 是否属于默认评价助理
     */
    @Column(name = "is_default_evaluation", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer isDefaultEvaluation;

    /**
     * 模版地址
     */
    @Column(name = "Display_url", unique = false, nullable = true, insertable = true, updatable = true)
    protected String displayUrl;

    /**
     * 编码
     */
    @Column(name = "Code", unique = false, nullable = true, insertable = true, updatable = true)
    protected String code;

    /**
     * 展示id
     */
    @Column(name = "Display_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String displayId;

    /**
     * 回复多样性
     */
    @Column(name = "Ques_variety", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer quesVariety;

    /**
     * 助理类别
     */
    @Column(name = "Type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer type;

    /**
     * 助理角色
     */
    @Column(name = "assistant_role", unique = false, nullable = true, insertable = true, updatable = true)
    protected String assistantRole;

    /**
     * 隐藏提示词
     */
    @Column(name = "Hide_promotions", unique = false, nullable = true, insertable = true, updatable = true)
    protected String hidePromotions;


    /**
     * 是否使用提示词
     */
    @Column(name = "flg_user_promot", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgUserPromot;

    /**
     * 聊天提示词
     */
    @Column(name = "chat_prompt", unique = false, nullable = true, insertable = true, updatable = true)
    protected String chatPrompt;

    /**
     * 知识库提示词
     */
    @Column(name = "kn_prompt", unique = false, nullable = true, insertable = true, updatable = true)
    protected String knPrompt;

    /**
     * 问答库提示词
     */
    @Column(name = "qa_prompt", unique = false, nullable = true, insertable = true, updatable = true)
    protected String qaPrompt;

    /**
     * 问答是否回复
     */
    @Column(name = "qa_flg_reply", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer qaFlgReply;

    /**
     * 问答回复内容
     */
    @Column(name = "qa_reply_content", unique = false, nullable = true, insertable = true, updatable = true)
    protected String qaReplyContent;

    /**
     * 问题3
     */
    @Column(name = "Ques3", unique = false, nullable = true, insertable = true, updatable = true)
    protected String ques3;

    /**
     * 问题2
     */
    @Column(name = "Ques2", unique = false, nullable = true, insertable = true, updatable = true)
    protected String ques2;

    /**
     * 模型id
     */
    @Column(name = "Model_assistant_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelAssistantId;


    /**
     * 是否发布
     */
    @Column(name = "Is_publish", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer isPublish;

    /**
     * 开场白
     */
    @Column(name = "Prologe", unique = false, nullable = true, insertable = true, updatable = true)
    protected String prologe;

    /**
     * 是否公开
     */
    @Column(name = "Is_open", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer isOpen;

    /**
     * 模型
     */
    @Column(name = "Model_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelType;

    /**
     * 展示模版
     */
    @Column(name = "Display_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String displayName;

    /**
     * 问题1
     */
    @Column(name = "Ques1", unique = false, nullable = true, insertable = true, updatable = true)
    protected String ques1;

    /**
     * 所属企业系统id
     */
    @Column(name = "Belong_company_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String belongCompanyId;

    /**
     * 提示词
     */
    @Column(name = "Promotions", unique = false, nullable = true, insertable = true, updatable = true)
    protected String promotions;

    /**
     * 名称
     */
    @Column(name = "Name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String name;


    /**
     * 状态
     */
    @Column(name = "Status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer status;

    /**
     * 聊天记忆限制（轮数）
     */
    @Column(name = "chat_memory_limit", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer chatMemoryLimit;


    /**
     * 聊天记忆字数（总共）
     */
    @Column(name = "chat_memory_word", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer chatMemoryWord;

    /**
     * 大模型API Key
     */
    @Column(name = "llm_api_key", unique = false, nullable = true, insertable = true, updatable = true)
    protected String llmApiKey;

    /**
     * 频率惩罚
     */
    @Column(name = "frequency_penalty", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double frequencyPenalty;

    /**
     * 出现惩罚
     */
    @Column(name = "presence_penalty", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double presencePenalty;

    /**
     * 随机性
     */
    @Column(name = "temperature", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double temperature;

    /**
     * 多样性控制
     */
    @Column(name = "top_p", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double topP;

    /**
     * 最大token值
     */
    @Column(name = "max_tokens", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer maxTokens;


    /**
     * 知识库查询最大返回值
     */
    @Column(name = "kn_max_results", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer knMaxResults;

    /**
     * 知识库最小相似度
     */
    @Column(name = "kn_min_score", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double knMinScore;

    /**
     * 是否开启流式
     */
    @Column(name = "flg_stream", unique = false, nullable = true, insertable = true, updatable = true)
    protected Boolean flgStream;

    public Integer getUnderstandType() {
        return understandType;
    }

    public void setUnderstandType(Integer understandType) {
        this.understandType = understandType;
    }

    public Integer getIsDefaultEvaluation() {
        return isDefaultEvaluation;
    }

    public void setIsDefaultEvaluation(Integer isDefaultEvaluation) {
        this.isDefaultEvaluation = isDefaultEvaluation;
    }

    /**
     *  是否启用策略库模式 默认不开启
     */
    @Column(name = "strategy_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer strategyFlg = SystemBoolean.NO.getKey();

    /**
     * 策略库查询返回匹配数
     */
    @Column(name = "strategy_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer strategyCount;

    /**
     * '策略库匹配相似度'
     */
    @Column(name = "strategy_score", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double strategyScore;

    /**
     * 策略ID
     */
    @Column(name = "ai_strategy_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String aiStrategyId;

    /**
     * 策略名称
     */
    @Column(name = "ai_strategy_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String aiStrategyName;


    /**
     * 工具ID
     */
    @Column(name = "function_tools_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String functionToolsId;

    /**
     * 工具名称
     */
    @Column(name = "function_tools_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String functionToolsName;


    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取模版地址
     */
    public String getDisplayUrl() {
        return displayUrl;
    }

    /**
     * 设置模版地址
     */
    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    /**
     * 获取编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取展示id
     */
    public String getDisplayId() {
        return displayId;
    }

    /**
     * 设置展示id
     */
    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    /**
     * 获取回复多样性
     */
    public Integer getQuesVariety() {
        return quesVariety;
    }

    /**
     * 设置回复多样性
     */
    public void setQuesVariety(Integer quesVariety) {
        this.quesVariety = quesVariety;
    }

    /**
     * 获取助理类别
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置助理类别
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取隐藏提示词
     */
    public String getHidePromotions() {
        return hidePromotions;
    }

    /**
     * 设置隐藏提示词
     */
    public void setHidePromotions(String hidePromotions) {
        this.hidePromotions = hidePromotions;
    }

    /**
     * 获取问题3
     */
    public String getQues3() {
        return ques3;
    }

    /**
     * 设置问题3
     */
    public void setQues3(String ques3) {
        this.ques3 = ques3;
    }

    /**
     * 获取问题2
     */
    public String getQues2() {
        return ques2;
    }

    /**
     * 设置问题2
     */
    public void setQues2(String ques2) {
        this.ques2 = ques2;
    }

    /**
     * 获取模型id
     */
    public String getModelAssistantId() {
        return modelAssistantId;
    }

    /**
     * 设置模型id
     */
    public void setModelAssistantId(String modelAssistantId) {
        this.modelAssistantId = modelAssistantId;
    }

    /**
     * 获取是否发布
     */
    public Integer getIsPublish() {
        return isPublish;
    }

    /**
     * 设置是否发布
     */
    public void setIsPublish(Integer isPublish) {
        this.isPublish = isPublish;
    }

    /**
     * 获取开场白
     */
    public String getProloge() {
        return prologe;
    }

    /**
     * 设置开场白
     */
    public void setProloge(String prologe) {
        this.prologe = prologe;
    }

    /**
     * 获取是否公开
     */
    public Integer getIsOpen() {
        return isOpen;
    }

    /**
     * 设置是否公开
     */
    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    /**
     * 获取模型
     */
    public String getModelType() {
        return modelType;
    }

    /**
     * 设置模型
     */
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    /**
     * 获取展示模版
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 设置展示模版
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 获取问题1
     */
    public String getQues1() {
        return ques1;
    }

    /**
     * 设置问题1
     */
    public void setQues1(String ques1) {
        this.ques1 = ques1;
    }

    /**
     * 获取所属企业系统id
     */
    public String getBelongCompanyId() {
        return belongCompanyId;
    }

    /**
     * 设置所属企业系统id
     */
    public void setBelongCompanyId(String belongCompanyId) {
        this.belongCompanyId = belongCompanyId;
    }

    /**
     * 获取角色&提示词
     */
    public String getPromotions() {
        return promotions;
    }

    /**
     * 设置提示词
     */
    public void setPromotions(String promotions) {
        this.promotions = promotions;
    }

    /**
     * 获取名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAssistantRole() {
        return assistantRole;
    }

    public void setAssistantRole(String assistantRole) {
        this.assistantRole = assistantRole;
    }

    public Integer getFlgUserPromot() {
        return flgUserPromot;
    }

    public void setFlgUserPromot(Integer flgUserPromot) {
        this.flgUserPromot = flgUserPromot;
    }

    public String getChatPrompt() {
        return chatPrompt;
    }

    public void setChatPrompt(String chatPrompt) {
        this.chatPrompt = chatPrompt;
    }

    public String getKnPrompt() {
        return knPrompt;
    }

    public void setKnPrompt(String knPrompt) {
        this.knPrompt = knPrompt;
    }

    public Integer getChatMemoryLimit() {
        return chatMemoryLimit;
    }

    public void setChatMemoryLimit(Integer chatMemoryLimit) {
        this.chatMemoryLimit = chatMemoryLimit;
    }

    public Integer getChatMemoryWord() {
        return chatMemoryWord;
    }

    public void setChatMemoryWord(Integer chatMemoryWord) {
        this.chatMemoryWord = chatMemoryWord;
    }

    public String getLlmApiKey() {
        return llmApiKey;
    }

    public void setLlmApiKey(String llmApiKey) {
        this.llmApiKey = llmApiKey;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public void setFrequencyPenalty(Double frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public void setPresencePenalty(Double presencePenalty) {
        this.presencePenalty = presencePenalty;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public Integer getKnMaxResults() {
        return knMaxResults;
    }

    public void setKnMaxResults(Integer knMaxResults) {
        this.knMaxResults = knMaxResults;
    }

    public Double getKnMinScore() {
        return knMinScore;
    }

    public void setKnMinScore(Double knMinScore) {
        this.knMinScore = knMinScore;
    }

    public Boolean getFlgStream() {
        return flgStream;
    }

    public void setFlgStream(Boolean flgStream) {
        this.flgStream = flgStream;
    }

    public String getQaPrompt() {
        return qaPrompt;
    }

    public void setQaPrompt(String qaPrompt) {
        this.qaPrompt = qaPrompt;
    }

    public Integer getQaFlgReply() {
        return qaFlgReply;
    }

    public void setQaFlgReply(Integer qaFlgReply) {
        this.qaFlgReply = qaFlgReply;
    }

    public String getQaReplyContent() {
        return qaReplyContent;
    }

    public void setQaReplyContent(String qaReplyContent) {
        this.qaReplyContent = qaReplyContent;
    }

    public Integer getStrategyFlg() {
        return strategyFlg;
    }

    public void setStrategyFlg(Integer strategyFlg) {
        this.strategyFlg = strategyFlg;
    }

    public Integer getStrategyCount() {
        return strategyCount;
    }

    public void setStrategyCount(Integer strategyCount) {
        this.strategyCount = strategyCount;
    }

    public Double getStrategyScore() {
        return strategyScore;
    }

    public void setStrategyScore(Double strategyScore) {
        this.strategyScore = strategyScore;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getAiStrategyId() {
        return aiStrategyId;
    }

    public void setAiStrategyId(String aiStrategyId) {
        this.aiStrategyId = aiStrategyId;
    }

    public String getAiStrategyName() {
        return aiStrategyName;
    }

    public void setAiStrategyName(String aiStrategyName) {
        this.aiStrategyName = aiStrategyName;
    }

    public String getFunctionToolsId() {
        return functionToolsId;
    }

    public void setFunctionToolsId(String functionToolsId) {
        this.functionToolsId = functionToolsId;
    }

    public String getFunctionToolsName() {
        return functionToolsName;
    }

    public void setFunctionToolsName(String functionToolsName) {
        this.functionToolsName = functionToolsName;
    }

    public AssistantsEntity() {
    }

    public AssistantsEntity(String avatar, Integer isDefaultEvaluation, String displayUrl, String code, String displayId, Integer quesVariety, Integer type, String assistantRole, String hidePromotions, Integer flgUserPromot, String chatPrompt, String knPrompt, String qaPrompt, Integer qaFlgReply, String qaReplyContent, String ques3, String ques2, String modelAssistantId, Integer isPublish, String prologe, Integer isOpen, String modelType, String displayName, String ques1, String belongCompanyId, String promotions, String name, Integer status, Integer chatMemoryLimit, Integer chatMemoryWord, String llmApiKey, Double frequencyPenalty, Double presencePenalty, Double temperature, Double topP, Integer maxTokens, Integer knMaxResults, Double knMinScore, Boolean flgStream, Integer strategyFlg, Integer strategyCount, Double strategyScore) {
        this.avatar = avatar;
        this.isDefaultEvaluation = isDefaultEvaluation;
        this.displayUrl = displayUrl;
        this.code = code;
        this.displayId = displayId;
        this.quesVariety = quesVariety;
        this.type = type;
        this.assistantRole = assistantRole;
        this.hidePromotions = hidePromotions;
        this.flgUserPromot = flgUserPromot;
        this.chatPrompt = chatPrompt;
        this.knPrompt = knPrompt;
        this.qaPrompt = qaPrompt;
        this.qaFlgReply = qaFlgReply;
        this.qaReplyContent = qaReplyContent;
        this.ques3 = ques3;
        this.ques2 = ques2;
        this.modelAssistantId = modelAssistantId;
        this.isPublish = isPublish;
        this.prologe = prologe;
        this.isOpen = isOpen;
        this.modelType = modelType;
        this.displayName = displayName;
        this.ques1 = ques1;
        this.belongCompanyId = belongCompanyId;
        this.promotions = promotions;
        this.name = name;
        this.status = status;
        this.chatMemoryLimit = chatMemoryLimit;
        this.chatMemoryWord = chatMemoryWord;
        this.llmApiKey = llmApiKey;
        this.frequencyPenalty = frequencyPenalty;
        this.presencePenalty = presencePenalty;
        this.temperature = temperature;
        this.topP = topP;
        this.maxTokens = maxTokens;
        this.knMaxResults = knMaxResults;
        this.knMinScore = knMinScore;
        this.flgStream = flgStream;
        this.strategyFlg = strategyFlg;
        this.strategyCount = strategyCount;
        this.strategyScore = strategyScore;
    }
}




