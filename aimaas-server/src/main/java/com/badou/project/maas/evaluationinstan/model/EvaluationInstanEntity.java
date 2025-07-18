package com.badou.project.maas.evaluationinstan.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-06-06 15:58:38.064
 * @todo 模型评价实例类
 */
@Entity
@Table(name = "maas_fine_tuning_evaluation_instan")
public class EvaluationInstanEntity extends AppBaseEntity {

    /**
     *  微调方案主键
     */
    @Column(name = "tun_plan_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tunPlanId;

    /**
     * 微调计划主键
     */
    @Column(name = "tun_program_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tunProgramId;

    /**
     * 任务主键
     */
    @Column(name = "task_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String taskId;

    /**
     * 微调监控主键
     */
    @Column(name = "tun_model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tunModelId;
    /**
     * 题目总数
     */
    @Column(name = "qustion_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer qustionCount;

    /**
     * 执行状态
     */
    @Column(name = "status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer status;

    /**
     * 已执行数量
     */
    @Column(name = "exec_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer exexCount;

	/**
     * 评价策略
     */
	@Column(name = "Evaluation_strategy", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer evaluationStrategy;
    
	/**
     * 平均分
     */
	@Column(name = "Average_score", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double averageScore;
    
	/**
     * AI助理主键
     */
	@Column(name = "Assistant_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String assistantId;
    
	/**
     * 评分比例
     */
	@Column(name = "Grading_ratio", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double gradingRatio;
    
	/**
     * 更新时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;
    
	/**
     * AI助理名字
     */
	@Column(name = "Assistant_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String assistantName;
    
	/**
     * 评价结束时间
     */
	@Column(name = "Eva_end_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date evaEndTime;
    
	/**
     * 评价模型主键
     */
	@Column(name = "Eva_model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaModelId;
    
	/**
     * 微调模型名字
     */
	@Column(name = "Model_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelName;
    
	/**
     * 评价开始时间
     */
	@Column(name = "Eva_start_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date evaStartTime;
    
	/**
     * 总分
     */
	@Column(name = "Total_score", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double totalScore;
    
	/**
     * 主键
     */
	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;
    
	/**
     * 模型应用主键
     */
	@Column(name = "Model_app_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelAppId;
    
	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;
    
	/**
     * 创建时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date createTime;
    
	/**
     * 更新人名字
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;
    
	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;
    
	/**
     * 模型应用
     */
	@Column(name = "Model_app", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelApp;
    
	/**
     * 微调模型主键
     */
	@Column(name = "Model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelId;
    
	/**
     * 最小分
     */
	@Column(name = "Min_score", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double minScore;
    
	/**
     * 最大分
     */
	@Column(name = "Max_score", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double maxScore;
    
	/**
     * 评价模型名字
     */
	@Column(name = "Eva_model_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaModelName;
    
	/**
     * 更新人主键
     */
	@Column(name = "Updator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updator;
    
	/**
     * 创建人名字
     */
	@Column(name = "Creator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creatorName;
    
	/**
     * 评价总时间
     */
	@Column(name = "Eva_total_seconds", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaTotalSeconds;

    public String getTunPlanId() {
        return tunPlanId;
    }

    public void setTunPlanId(String tunPlanId) {
        this.tunPlanId = tunPlanId;
    }

    public String getTunProgramId() {
        return tunProgramId;
    }

    public void setTunProgramId(String tunProgramId) {
        this.tunProgramId = tunProgramId;
    }

    public String getTunModelId() {
        return tunModelId;
    }

    public void setTunModelId(String tunModelId) {
        this.tunModelId = tunModelId;
    }

    public Integer getQustionCount() {
        return qustionCount;
    }

    public void setQustionCount(Integer qustionCount) {
        this.qustionCount = qustionCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getExexCount() {
        return exexCount;
    }

    public void setExexCount(Integer exexCount) {
        this.exexCount = exexCount;
    }

    /**
     * 获取评价策略
     */
    public Integer getEvaluationStrategy() {
        return evaluationStrategy;
    }

	/**
     * 设置评价策略
     */
    public void setEvaluationStrategy(Integer evaluationStrategy) {
        this.evaluationStrategy = evaluationStrategy;
    }
    /**
     * 获取平均分
     */
    public Double getAverageScore() {
        return averageScore;
    }

	/**
     * 设置平均分
     */
    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
    /**
     * 获取AI助理主键
     */
    public String getAssistantId() {
        return assistantId;
    }

	/**
     * 设置AI助理主键
     */
    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
    }
    /**
     * 获取评分比例
     */
    public Double getGradingRatio() {
        return gradingRatio;
    }

	/**
     * 设置评分比例
     */
    public void setGradingRatio(Double gradingRatio) {
        this.gradingRatio = gradingRatio;
    }
    /**
     * 获取更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

	/**
     * 设置更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 获取AI助理名字
     */
    public String getAssistantName() {
        return assistantName;
    }

	/**
     * 设置AI助理名字
     */
    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }
    /**
     * 获取评价结束时间
     */
    public Date getEvaEndTime() {
        return evaEndTime;
    }

	/**
     * 设置评价结束时间
     */
    public void setEvaEndTime(Date evaEndTime) {
        this.evaEndTime = evaEndTime;
    }
    /**
     * 获取评价模型主键
     */
    public String getEvaModelId() {
        return evaModelId;
    }

	/**
     * 设置评价模型主键
     */
    public void setEvaModelId(String evaModelId) {
        this.evaModelId = evaModelId;
    }
    /**
     * 获取微调模型名字
     */
    public String getModelName() {
        return modelName;
    }

	/**
     * 设置微调模型名字
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    /**
     * 获取评价开始时间
     */
    public Date getEvaStartTime() {
        return evaStartTime;
    }

	/**
     * 设置评价开始时间
     */
    public void setEvaStartTime(Date evaStartTime) {
        this.evaStartTime = evaStartTime;
    }
    /**
     * 获取总分
     */
    public Double getTotalScore() {
        return totalScore;
    }

	/**
     * 设置总分
     */
    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }
    /**
     * 获取主键
     */
    public String getId() {
        return id;
    }

	/**
     * 设置主键
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * 获取模型应用主键
     */
    public String getModelAppId() {
        return modelAppId;
    }

	/**
     * 设置模型应用主键
     */
    public void setModelAppId(String modelAppId) {
        this.modelAppId = modelAppId;
    }
    /**
     * 获取创建人主键
     */
    public String getCreator() {
        return creator;
    }

	/**
     * 设置创建人主键
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }
    /**
     * 获取创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

	/**
     * 设置创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    /**
     * 获取更新人名字
     */
    public String getUpdatorName() {
        return updatorName;
    }

	/**
     * 设置更新人名字
     */
    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }
    /**
     * 获取逻辑删除符号
     */
    public Integer getFlgDeleted() {
        return flgDeleted;
    }

	/**
     * 设置逻辑删除符号
     */
    public void setFlgDeleted(Integer flgDeleted) {
        this.flgDeleted = flgDeleted;
    }
    /**
     * 获取模型应用
     */
    public String getModelApp() {
        return modelApp;
    }

	/**
     * 设置模型应用
     */
    public void setModelApp(String modelApp) {
        this.modelApp = modelApp;
    }
    /**
     * 获取微调模型主键
     */
    public String getModelId() {
        return modelId;
    }

	/**
     * 设置微调模型主键
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    /**
     * 获取最小分
     */
    public Double getMinScore() {
        return minScore;
    }

	/**
     * 设置最小分
     */
    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    /**
     * 获取评价模型名字
     */
    public String getEvaModelName() {
        return evaModelName;
    }

	/**
     * 设置评价模型名字
     */
    public void setEvaModelName(String evaModelName) {
        this.evaModelName = evaModelName;
    }
    /**
     * 获取更新人主键
     */
    public String getUpdator() {
        return updator;
    }

	/**
     * 设置更新人主键
     */
    public void setUpdator(String updator) {
        this.updator = updator;
    }
    /**
     * 获取创建人名字
     */
    public String getCreatorName() {
        return creatorName;
    }

	/**
     * 设置创建人名字
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    /**
     * 获取评价总时间
     */
    public String getEvaTotalSeconds() {
        return evaTotalSeconds;
    }

	/**
     * 设置评价总时间
     */
    public void setEvaTotalSeconds(String evaTotalSeconds) {
        this.evaTotalSeconds = evaTotalSeconds;
    }

    @Override
    public String toString() {
        return "EvaluationInstanEntity{" +
                "qustionCount=" + qustionCount +
                ", status=" + status +
                ", exexCount=" + exexCount +
                ", evaluationStrategy=" + evaluationStrategy +
                ", averageScore=" + averageScore +
                ", assistantId='" + assistantId + '\'' +
                ", gradingRatio=" + gradingRatio +
                ", updateTime=" + updateTime +
                ", assistantName='" + assistantName + '\'' +
                ", evaEndTime=" + evaEndTime +
                ", evaModelId='" + evaModelId + '\'' +
                ", modelName='" + modelName + '\'' +
                ", evaStartTime=" + evaStartTime +
                ", totalScore=" + totalScore +
                ", id='" + id + '\'' +
                ", modelAppId='" + modelAppId + '\'' +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", updatorName='" + updatorName + '\'' +
                ", flgDeleted=" + flgDeleted +
                ", modelApp='" + modelApp + '\'' +
                ", modelId='" + modelId + '\'' +
                ", minScore=" + minScore +
                ", maxScore=" + maxScore +
                ", evaModelName='" + evaModelName + '\'' +
                ", updator='" + updator + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", evaTotalSeconds='" + evaTotalSeconds + '\'' +
                '}';
    }
}
