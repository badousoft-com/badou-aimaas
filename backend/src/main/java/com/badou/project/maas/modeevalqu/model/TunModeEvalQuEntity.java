package com.badou.project.maas.modeevalqu.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-05-08 17:04:14.214
 * @todo 微调模型评价问题类
 */
@Entity
@Table(name = "maas_fine_tuning_mode_evalqu")
public class TunModeEvalQuEntity extends AppBaseEntity {

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
     * 问题
     */
	@Column(name = "Question", unique = false, nullable = true, insertable = true, updatable = true)
    protected String question;
    
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
     * 满分标准
     */
	@Column(name = "Max_point", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer maxPoint;
    
	/**
     * 微调模型评价主键
     */
	@Column(name = "Eva_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaId;
    
	/**
     * 期望回复
     */
	@Column(name = "Feedback", unique = false, nullable = true, insertable = true, updatable = true)
    protected String feedback;
    
	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;
    
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
     * 微调模型主键
     */
	@Column(name = "Plan_model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planModelId;
    
	/**
     * 微调计划主键
     */
	@Column(name = "Plan_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planId;
    

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
     * 获取问题
     */
    public String getQuestion() {
        return question;
    }

	/**
     * 设置问题
     */
    public void setQuestion(String question) {
        this.question = question;
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
     * 获取满分标准
     */
    public Integer getMaxPoint() {
        return maxPoint;
    }

	/**
     * 设置满分标准
     */
    public void setMaxPoint(Integer maxPoint) {
        this.maxPoint = maxPoint;
    }
    /**
     * 获取微调模型评价主键
     */
    public String getEvaId() {
        return evaId;
    }

	/**
     * 设置微调模型评价主键
     */
    public void setEvaId(String evaId) {
        this.evaId = evaId;
    }
    /**
     * 获取期望回复
     */
    public String getFeedback() {
        return feedback;
    }

	/**
     * 设置期望回复
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    /**
     * 获取更新人时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

	/**
     * 设置更新人时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
     * 获取微调模型主键
     */
    public String getPlanModelId() {
        return planModelId;
    }

	/**
     * 设置微调模型主键
     */
    public void setPlanModelId(String planModelId) {
        this.planModelId = planModelId;
    }
    /**
     * 获取微调计划主键
     */
    public String getPlanId() {
        return planId;
    }

	/**
     * 设置微调计划主键
     */
    public void setPlanId(String planId) {
        this.planId = planId;
    }
}
