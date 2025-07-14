package com.badou.project.maas.evaluationinstan.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-06-06 15:58:58.809
 * @todo 模型评价实例问题类
 */
@Entity
@Table(name = "maas_fine_tuning_evaluatio_instanq")
public class EvaluationInstanqEntity extends AppBaseEntity {

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
     * 提交时间
     */
	@Column(name = "Submit_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date submitTime;
    
	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;
    
	/**
     * 答案评分
     */
	@Column(name = "Answer_score", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double answerScore;
    
	/**
     * 回答
     */
	@Column(name = "Feedback", unique = false, nullable = true, insertable = true, updatable = true)
    protected String feedback;
    
	/**
     * 实例主键
     */
	@Column(name = "Instanq", unique = false, nullable = true, insertable = true, updatable = true)
    protected String instanq;
    
	/**
     * 问题
     */
	@Column(name = "question", unique = false, nullable = true, insertable = true, updatable = true)
    protected String question;
    
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
     * 主键
     */
	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;
    
	/**
     * 标准答案
     */
	@Column(name = "Standard_answer", unique = false, nullable = true, insertable = true, updatable = true)
    protected String standardAnswer;

    /**
     * 系统提示词
     */
    @Column(name = "system", unique = false, nullable = true, insertable = true, updatable = true)
    protected String system;

    /**
     * 输入
     */
    @Column(name = "input", unique = false, nullable = true, insertable = true, updatable = true)
    protected String input;

    /**
     * 劣质回答
     */
    @Column(name = "rejecteda", unique = false, nullable = true, insertable = true, updatable = true)
    protected String rejecteda;

    /**
     * 优质回答
     */
    @Column(name = "chosena", unique = false, nullable = true, insertable = true, updatable = true)
    protected String chosena;

    /**
     * 相似问题
     */
    @Column(name = "conceivability_problem", unique = false, nullable = true, insertable = true, updatable = true)
    protected String conceivabilityProblem;

    /**
     * 场景
     */
    @Column(name = "qman", unique = false, nullable = true, insertable = true, updatable = true)
    protected String qman;

    /**
     * 意图
     */
    @Column(name = "intention", unique = false, nullable = true, insertable = true, updatable = true)
    protected String intention;

    /**
     * 是否已评价
     */
    @Column(name = "eva_flag", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer evaFlag;

    public Integer getEvaFlag() {
        return evaFlag;
    }

    public void setEvaFlag(Integer evaFlag) {
        this.evaFlag = evaFlag;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getRejecteda() {
        return rejecteda;
    }

    public void setRejecteda(String rejecteda) {
        this.rejecteda = rejecteda;
    }

    public String getChosena() {
        return chosena;
    }

    public void setChosena(String chosena) {
        this.chosena = chosena;
    }

    public String getConceivabilityProblem() {
        return conceivabilityProblem;
    }

    public void setConceivabilityProblem(String conceivabilityProblem) {
        this.conceivabilityProblem = conceivabilityProblem;
    }

    public String getQman() {
        return qman;
    }

    public void setQman(String qman) {
        this.qman = qman;
    }

    public String getIntention() {
        return intention;
    }

    public void setIntention(String intention) {
        this.intention = intention;
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
     * 获取提交时间
     */
    public Date getSubmitTime() {
        return submitTime;
    }

	/**
     * 设置提交时间
     */
    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
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
     * 获取答案评分
     */
    public Double getAnswerScore() {
        return answerScore;
    }

	/**
     * 设置答案评分
     */
    public void setAnswerScore(Double answerScore) {
        this.answerScore = answerScore;
    }
    /**
     * 获取回答
     */
    public String getFeedback() {
        return feedback;
    }

	/**
     * 设置回答
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    /**
     * 获取实例主键
     */
    public String getInstanq() {
        return instanq;
    }

	/**
     * 设置实例主键
     */
    public void setInstanq(String instanq) {
        this.instanq = instanq;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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
     * 获取标准答案
     */
    public String getStandardAnswer() {
        return standardAnswer;
    }

	/**
     * 设置标准答案
     */
    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
    }


}
