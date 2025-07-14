package com.badou.project.maas.problemdatadetail.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author badousoft
 * @date 2024-05-15 17:37:43.24
 * @todo 样本数据集详情管理类
 */
@Entity
@Table(name = "maas_problem_data_detail")
public class ProblemDataDetailEntity extends AppBaseEntity {

    /**
     * kto-tag
     */
    @Column(name = "kto_tag", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer ktoTag;

    /**
     * 优质提示词
     */
    @Column(name = "chosena", unique = false, nullable = true, insertable = true, updatable = true)
    protected String chosena;

    /**
     * 劣质提示词
     */
    @Column(name = "rejecteda", unique = false, nullable = true, insertable = true, updatable = true)
    protected String rejecteda;

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
     * 多轮对话
     */
    @Column(name = "multi_dialogue", unique = false, nullable = true, insertable = true, updatable = true)
    protected String multiDialogue;

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
     * 相似问题
     */
    @Column(name = "conceivability_problem", unique = false, nullable = true, insertable = true, updatable = true)
    protected String conceivabilityProblem;

	/**
     * 回复答案
     */
	@Column(name = "Feedback", unique = false, nullable = true, insertable = true, updatable = true)
    protected String feedback;
    
	/**
     * 问题答案主键
     */
	@Column(name = "Problem_data_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String problemDataId;
    
	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;
    
	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;
    
	/**
     * 创建时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date createTime;

	@Transient
    protected Integer rownum;

	/**
     * 用户问题
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
     * 类型
     */
	@Column(name = "Type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer type;

    public Integer getKtoTag() {
        return ktoTag;
    }

    public void setKtoTag(Integer ktoTag) {
        this.ktoTag = ktoTag;
    }

    public String getChosena() {
        return chosena;
    }

    public void setChosena(String chosena) {
        this.chosena = chosena;
    }

    public String getRejecteda() {
        return rejecteda;
    }

    public void setRejecteda(String rejecteda) {
        this.rejecteda = rejecteda;
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

    public String getMultiDialogue() {
        return multiDialogue;
    }

    public void setMultiDialogue(String multiDialogue) {
        this.multiDialogue = multiDialogue;
    }

    public Integer getRownum() {
        return rownum;
    }

    public void setRownum(Integer rownum) {
        this.rownum = rownum;
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

    public String getConceivabilityProblem() {
        return conceivabilityProblem;
    }

    public void setConceivabilityProblem(String conceivabilityProblem) {
        this.conceivabilityProblem = conceivabilityProblem;
    }

    /**
     * 获取回复答案
     */
    public String getFeedback() {
        return feedback;
    }

	/**
     * 设置回复答案
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    /**
     * 获取问题答案主键
     */
    public String getProblemDataId() {
        return problemDataId;
    }

	/**
     * 设置问题答案主键
     */
    public void setProblemDataId(String problemDataId) {
        this.problemDataId = problemDataId;
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
     * 获取用户问题
     */
    public String getQuestion() {
        return question;
    }

	/**
     * 设置用户问题
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
     * 获取类型
     */
    public Integer getType() {
        return type;
    }

	/**
     * 设置类型
     */
    public void setType(Integer type) {
        this.type = type;
    }
}
