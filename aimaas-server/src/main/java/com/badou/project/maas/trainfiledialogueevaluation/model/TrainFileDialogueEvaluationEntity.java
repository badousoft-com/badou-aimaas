package com.badou.project.maas.trainfiledialogueevaluation.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2025-02-18 19:04:55.108
 *  评价管理与训练集文件对话数据的关联类
 */
@Entity
@Table(name = "maas_train_file_dialogue_evaluation")
public class TrainFileDialogueEvaluationEntity extends AppBaseEntity {
    /**
     * 表maas_train_file_dialogue_evaluation主键id
     */
    @Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;

    /**
     * 对话数据ID
     */
    @Column(name = "dialogue_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String dialogueId;

	/**
     * 劣质回答
     */
	@Column(name = "Rejecteda", unique = false, nullable = true, insertable = true, updatable = true)
    protected String rejecteda;

	/**
     * 训练集文件主键
     */
	@Column(name = "Train_file_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainFileId;

	/**
     * 样本集主键
     */
	@Column(name = "Data_problem_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String dataProblemId;

	/**
     * 问答类型
     */
	@Column(name = "Type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer type;

	/**
     * 回复答案
     */
	@Column(name = "Feedback", unique = false, nullable = true, insertable = true, updatable = true)
    protected String feedback;

	/**
     * 更新时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;

	/**
     * 对话内容个数
     */
	@Column(name = "Content_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer contentCount;

	/**
     * 微调评价主键
     */
	@Column(name = "Evaluation_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaluationId;

	/**
     * 创建人
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;

	/**
     * 创建时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date createTime;

	/**
     * 训练内容(content)
     */
	@Column(name = "Question", unique = false, nullable = true, insertable = true, updatable = true)
    protected String question;

	/**
     * 更新人名称
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;

	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;

	/**
     * 多轮对话
     */
	@Column(name = "Multi_dialogue", unique = false, nullable = true, insertable = true, updatable = true)
    protected String multiDialogue;

	/**
     * 相似问题
     */
	@Column(name = "Conceivability_problem", unique = false, nullable = true, insertable = true, updatable = true)
    protected String conceivabilityProblem;

	/**
     * 意图
     */
	@Column(name = "Intention", unique = false, nullable = true, insertable = true, updatable = true)
    protected String intention;

	/**
     * 问答问题主键
     */
	@Column(name = "Problem_data_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String problemDataId;

	/**
     * 样本集名称
     */
	@Column(name = "Data_problem_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String dataProblemName;

	/**
     * 输入
     */
	@Column(name = "Input", unique = false, nullable = true, insertable = true, updatable = true)
    protected String input;

	/**
     * 样本集问题主键
     */
	@Column(name = "Data_problemq_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String dataProblemqId;

	/**
     * 系统提示词
     */
	@Column(name = "System", unique = false, nullable = true, insertable = true, updatable = true)
    protected String system;

	/**
     * 人类反馈 [true/false]（必填）
     */
	@Column(name = "Kto_tag", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer ktoTag;

	/**
     * 名称
     */
	@Column(name = "Name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String name;

	/**
     * 更新人
     */
	@Column(name = "Updator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updator;

	/**
     * 创建人名字
     */
	@Column(name = "Creator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creatorName;

	/**
     * 场景
     */
	@Column(name = "Qman", unique = false, nullable = true, insertable = true, updatable = true)
    protected String qman;

	/**
     * 树唯一ID
     */
	@Column(name = "Tree_unique_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String treeUniqueId;

	/**
     * 优质回答
     */
	@Column(name = "Chosena", unique = false, nullable = true, insertable = true, updatable = true)
    protected String chosena;


    /**
     * 获取表maas_train_file_dialogue_evaluation主键id
     */
    public String getId() { return id; }

    /**
     * 设置表maas_train_file_dialogue_evaluation主键id
     */
    public void setId(String id) { this.id = id; }

    /**
     * 获取劣质回答
     */
    public String getRejecteda() {
        return rejecteda;
    }

	/**
     * 设置劣质回答
     */
    public void setRejecteda(String rejecteda) {
        this.rejecteda = rejecteda;
    }
    /**
     * 获取训练集文件主键
     */
    public String getTrainFileId() {
        return trainFileId;
    }

	/**
     * 设置训练集文件主键
     */
    public void setTrainFileId(String trainFileId) {
        this.trainFileId = trainFileId;
    }
    /**
     * 获取样本集主键
     */
    public String getDataProblemId() {
        return dataProblemId;
    }

	/**
     * 设置样本集主键
     */
    public void setDataProblemId(String dataProblemId) {
        this.dataProblemId = dataProblemId;
    }
    /**
     * 获取问答类型
     */
    public Integer getType() {
        return type;
    }

	/**
     * 设置问答类型
     */
    public void setType(Integer type) {
        this.type = type;
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
    /**
     * 获取对话内容个数
     */
    public Integer getContentCount() {
        return contentCount;
    }

	/**
     * 设置对话内容个数
     */
    public void setContentCount(Integer contentCount) {
        this.contentCount = contentCount;
    }
    /**
     * 获取微调评价主键
     */
    public String getEvaluationId() {
        return evaluationId;
    }

	/**
     * 设置微调评价主键
     */
    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
    }
    /**
     * 获取创建人
     */
    public String getCreator() {
        return creator;
    }

	/**
     * 设置创建人
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
     * 获取训练内容(content)
     */
    public String getQuestion() {
        return question;
    }

	/**
     * 设置训练内容(content)
     */
    public void setQuestion(String question) {
        this.question = question;
    }
    /**
     * 获取更新人名称
     */
    public String getUpdatorName() {
        return updatorName;
    }

	/**
     * 设置更新人名称
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
     * 获取多轮对话
     */
    public String getMultiDialogue() {
        return multiDialogue;
    }

	/**
     * 设置多轮对话
     */
    public void setMultiDialogue(String multiDialogue) {
        this.multiDialogue = multiDialogue;
    }
    /**
     * 获取相似问题
     */
    public String getConceivabilityProblem() {
        return conceivabilityProblem;
    }

	/**
     * 设置相似问题
     */
    public void setConceivabilityProblem(String conceivabilityProblem) {
        this.conceivabilityProblem = conceivabilityProblem;
    }
    /**
     * 获取意图
     */
    public String getIntention() {
        return intention;
    }

	/**
     * 设置意图
     */
    public void setIntention(String intention) {
        this.intention = intention;
    }
    /**
     * 获取问答问题主键
     */
    public String getProblemDataId() {
        return problemDataId;
    }

	/**
     * 设置问答问题主键
     */
    public void setProblemDataId(String problemDataId) {
        this.problemDataId = problemDataId;
    }
    /**
     * 获取样本集名称
     */
    public String getDataProblemName() {
        return dataProblemName;
    }

	/**
     * 设置样本集名称
     */
    public void setDataProblemName(String dataProblemName) {
        this.dataProblemName = dataProblemName;
    }
    /**
     * 获取输入
     */
    public String getInput() {
        return input;
    }

	/**
     * 设置输入
     */
    public void setInput(String input) {
        this.input = input;
    }
    /**
     * 获取样本集问题主键
     */
    public String getDataProblemqId() {
        return dataProblemqId;
    }

	/**
     * 设置样本集问题主键
     */
    public void setDataProblemqId(String dataProblemqId) {
        this.dataProblemqId = dataProblemqId;
    }
    /**
     * 获取系统提示词
     */
    public String getSystem() {
        return system;
    }

	/**
     * 设置系统提示词
     */
    public void setSystem(String system) {
        this.system = system;
    }
    /**
     * 获取人类反馈 [true/false]（必填）
     */
    public Integer getKtoTag() {
        return ktoTag;
    }

	/**
     * 设置人类反馈 [true/false]（必填）
     */
    public void setKtoTag(Integer ktoTag) {
        this.ktoTag = ktoTag;
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
     * 获取更新人
     */
    public String getUpdator() {
        return updator;
    }

	/**
     * 设置更新人
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
     * 获取场景
     */
    public String getQman() {
        return qman;
    }

	/**
     * 设置场景
     */
    public void setQman(String qman) {
        this.qman = qman;
    }
    /**
     * 获取树唯一ID
     */
    public String getTreeUniqueId() {
        return treeUniqueId;
    }

	/**
     * 设置树唯一ID
     */
    public void setTreeUniqueId(String treeUniqueId) {
        this.treeUniqueId = treeUniqueId;
    }
    /**
     * 获取优质回答
     */
    public String getChosena() {
        return chosena;
    }

	/**
     * 设置优质回答
     */
    public void setChosena(String chosena) {
        this.chosena = chosena;
    }

    /**
     *获取对话数据ID
     */
    public String getDialogueId() {
        return dialogueId;
    }
    /**
     *设置对话数据ID
     */
    public void setDialogueId(String dialogueId) {
        this.dialogueId = dialogueId;
    }
}
