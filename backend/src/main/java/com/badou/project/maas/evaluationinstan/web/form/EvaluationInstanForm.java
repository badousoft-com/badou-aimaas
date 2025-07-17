package  com.badou.project.maas.evaluationinstan.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;

/**
 * @author badousoft
 * @date 2024-06-06 15:58:38.064
 * @todo 模型评价实例form
 */
public class EvaluationInstanForm extends BaseStrutsEntityForm<EvaluationInstanEntity> {

	/**
     * 评价策略
     */
    protected Integer  evaluationStrategy;
	/**
     * 平均分
     */
    protected Double  averageScore;
	/**
     * AI助理主键
     */
    protected String  assistantId;
	/**
     * 评分比例
     */
    protected Double  gradingRatio;
	/**
     * 更新时间
     */
    protected Date  updateTime;
	/**
     * AI助理名字
     */
    protected String  assistantName;
	/**
     * 评价结束时间
     */
    protected Date  evaEndTime;
	/**
     * 评价模型主键
     */
    protected String  evaModelId;
	/**
     * 微调模型名字
     */
    protected String  modelName;
	/**
     * 评价开始时间
     */
    protected Date  evaStartTime;
	/**
     * 总分
     */
    protected Double  totalScore;
	/**
     * 主键
     */
    protected String  id;
	/**
     * 模型应用主键
     */
    protected String  modelAppId;
	/**
     * 创建人主键
     */
    protected String  creator;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 更新人名字
     */
    protected String  updatorName;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 模型应用
     */
    protected String  modelApp;
	/**
     * 微调模型主键
     */
    protected String  modelId;
	/**
     * 最小分
     */
    protected Double  minScore;
	/**
     * 最大分
     */
    protected Double  maxScore;
	/**
     * 评价模型名字
     */
    protected String  evaModelName;
	/**
     * 更新人主键
     */
    protected String  updator;
	/**
     * 创建人名字
     */
    protected String  creatorName;
	/**
     * 评价总时间
     */
    protected String  evaTotalSeconds;

        /**
     *  获取评价策略
     */
    public Integer getEvaluationStrategy() {
        return evaluationStrategy;
    }

	/**
     *  设置评价策略
     */
    public void setEvaluationStrategy(Integer evaluationStrategy) {
        this.evaluationStrategy = evaluationStrategy;
    }
    /**
     *  获取平均分
     */
    public Double getAverageScore() {
        return averageScore;
    }

	/**
     *  设置平均分
     */
    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
    /**
     *  获取AI助理主键
     */
    public String getAssistantId() {
        return assistantId;
    }

	/**
     *  设置AI助理主键
     */
    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
    }
    /**
     *  获取评分比例
     */
    public Double getGradingRatio() {
        return gradingRatio;
    }

	/**
     *  设置评分比例
     */
    public void setGradingRatio(Double gradingRatio) {
        this.gradingRatio = gradingRatio;
    }
    /**
     *  获取更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

	/**
     *  设置更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    /**
     *  获取AI助理名字
     */
    public String getAssistantName() {
        return assistantName;
    }

	/**
     *  设置AI助理名字
     */
    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }
    /**
     *  获取评价结束时间
     */
    public Date getEvaEndTime() {
        return evaEndTime;
    }

	/**
     *  设置评价结束时间
     */
    public void setEvaEndTime(Date evaEndTime) {
        this.evaEndTime = evaEndTime;
    }
    /**
     *  获取评价模型主键
     */
    public String getEvaModelId() {
        return evaModelId;
    }

	/**
     *  设置评价模型主键
     */
    public void setEvaModelId(String evaModelId) {
        this.evaModelId = evaModelId;
    }
    /**
     *  获取微调模型名字
     */
    public String getModelName() {
        return modelName;
    }

	/**
     *  设置微调模型名字
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    /**
     *  获取评价开始时间
     */
    public Date getEvaStartTime() {
        return evaStartTime;
    }

	/**
     *  设置评价开始时间
     */
    public void setEvaStartTime(Date evaStartTime) {
        this.evaStartTime = evaStartTime;
    }
    /**
     *  获取总分
     */
    public Double getTotalScore() {
        return totalScore;
    }

	/**
     *  设置总分
     */
    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }
    /**
     *  获取主键
     */
    public String getId() {
        return id;
    }

	/**
     *  设置主键
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     *  获取模型应用主键
     */
    public String getModelAppId() {
        return modelAppId;
    }

	/**
     *  设置模型应用主键
     */
    public void setModelAppId(String modelAppId) {
        this.modelAppId = modelAppId;
    }
    /**
     *  获取创建人主键
     */
    public String getCreator() {
        return creator;
    }

	/**
     *  设置创建人主键
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }
    /**
     *  获取创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

	/**
     *  设置创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    /**
     *  获取更新人名字
     */
    public String getUpdatorName() {
        return updatorName;
    }

	/**
     *  设置更新人名字
     */
    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }
    /**
     *  获取逻辑删除符号
     */
    public Integer getFlgDeleted() {
        return flgDeleted;
    }

	/**
     *  设置逻辑删除符号
     */
    public void setFlgDeleted(Integer flgDeleted) {
        this.flgDeleted = flgDeleted;
    }
    /**
     *  获取模型应用
     */
    public String getModelApp() {
        return modelApp;
    }

	/**
     *  设置模型应用
     */
    public void setModelApp(String modelApp) {
        this.modelApp = modelApp;
    }
    /**
     *  获取微调模型主键
     */
    public String getModelId() {
        return modelId;
    }

	/**
     *  设置微调模型主键
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    /**
     *  获取最小分
     */
    public Double getMinScore() {
        return minScore;
    }

	/**
     *  设置最小分
     */
    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }
    /**
     *  获取最大分
     */
    public Double getMaxScore() {
        return maxScore;
    }

	/**
     *  设置最大分
     */
    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }
    /**
     *  获取评价模型名字
     */
    public String getEvaModelName() {
        return evaModelName;
    }

	/**
     *  设置评价模型名字
     */
    public void setEvaModelName(String evaModelName) {
        this.evaModelName = evaModelName;
    }
    /**
     *  获取更新人主键
     */
    public String getUpdator() {
        return updator;
    }

	/**
     *  设置更新人主键
     */
    public void setUpdator(String updator) {
        this.updator = updator;
    }
    /**
     *  获取创建人名字
     */
    public String getCreatorName() {
        return creatorName;
    }

	/**
     *  设置创建人名字
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    /**
     *  获取评价总时间
     */
    public String getEvaTotalSeconds() {
        return evaTotalSeconds;
    }

	/**
     *  设置评价总时间
     */
    public void setEvaTotalSeconds(String evaTotalSeconds) {
        this.evaTotalSeconds = evaTotalSeconds;
    }
}