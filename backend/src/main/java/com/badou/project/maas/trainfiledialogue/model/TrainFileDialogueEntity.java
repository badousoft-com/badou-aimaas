package com.badou.project.maas.trainfiledialogue.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.badou.project.maas.trainfiledialoguedetail.model.TrainFileDialoguedetailEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.*;

/**
 * @author badousoft
 * @date 2024-05-16 14:39:38.124
 * @todo 训练集文件对话类
 */
@Entity
@Table(name = "maas_train_file_dialogue")
public class TrainFileDialogueEntity extends AppBaseEntity {

    /**
     * 人类反馈 [true/false]（必填）
     */
    @Column(name = "kto_tag", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer ktoTag;

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
     * 多轮对话
     */
    @Column(name = "multi_dialogue", unique = false, nullable = true, insertable = true, updatable = true)
    protected String multiDialogue;

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
     * 样本集主键
     */
    @Column(name = "data_problem_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String dataProblemId;

    /**
     * 样本集名称
     */
    @Column(name = "data_problem_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String dataProblemName;

    /**
     * 样本集问题主键
     */
    @Column(name = "data_problemq_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String dataProblemqId;

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
     * 用户问题
     */
	@Column(name = "Question", unique = false, nullable = true, insertable = true, updatable = true)
    protected String question;
    
	/**
     * 更新人名称
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;
    
	/**
     * 训练集文件主键
     */
	@Column(name = "Train_file_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainFileId;
//    @OneToMany(mappedBy = "children", cascade = {CascadeType.ALL})
////    @NotFound(action = NotFoundAction.IGNORE)
////    public List<TrainFileDialoguedetailEntity> children = new ArrayList<TrainFileDialoguedetailEntity>();
	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;
    
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
     * 样本数据集管理主键
     */
	@Column(name = "Problem_data_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String problemDataId;
    
	/**
     * 更新时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;
    
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
     * 主键
     */
	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;
    
	/**
     * 对话内容个数
     */
	@Column(name = "Content_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer contentCount;
    
	/**
     * 树唯一ID
     */
	@Column(name = "Tree_unique_id", unique = false, nullable = true, insertable = true, updatable = true)
    @Transient
    protected String treeUniqueId;



    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
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

    public String getMultiDialogue() {
        return multiDialogue;
    }

    public void setMultiDialogue(String multiDialogue) {
        this.multiDialogue = multiDialogue;
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

    //    public List<TrainFileDialoguedetailEntity> getChildren() {
    //        return children;
    //    }
    //
    //    public void setChildren(List<TrainFileDialoguedetailEntity> children) {
    //        this.children = children;
    //    }


    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
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

    public Integer getKtoTag() {
        return ktoTag;
    }

    public void setKtoTag(Integer ktoTag) {
        this.ktoTag = ktoTag;
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
     * 获取样本数据集管理主键
     */
    public String getProblemDataId() {
        return problemDataId;
    }

	/**
     * 设置样本数据集管理主键
     */
    public void setProblemDataId(String problemDataId) {
        this.problemDataId = problemDataId;
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

    public String getDataProblemId() {
        return dataProblemId;
    }

    public void setDataProblemId(String dataProblemId) {
        this.dataProblemId = dataProblemId;
    }

    public String getDataProblemName() {
        return dataProblemName;
    }

    public void setDataProblemName(String dataProblemName) {
        this.dataProblemName = dataProblemName;
    }

    public String getDataProblemqId() {
        return dataProblemqId;
    }

    public void setDataProblemqId(String dataProblemqId) {
        this.dataProblemqId = dataProblemqId;
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
}
