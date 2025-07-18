package  com.badou.project.maas.trainfiledialoguedetail.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.trainfiledialoguedetail.model.TrainFileDialoguedetailEntity;

/**
 * @author badousoft
 * @date 2024-05-16 14:47:48.749
 * @todo 训练集文件对话详情form
 */
public class TrainFileDialoguedetailForm extends BaseStrutsEntityForm<TrainFileDialoguedetailEntity> {

	/**
     * 创建人
     */
    protected String  creator;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 用户问题
     */
    protected String  question;
	/**
     * 更新人名称
     */
    protected String  updatorName;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 问答类型
     */
    protected Integer  type;
	/**
     * 回复答案
     */
    protected String  feedback;
	/**
     * 更新时间
     */
    protected Date  updateTime;
	/**
     * 样本数据集详情管理主键
     */
    protected String  problemDetailData;
	/**
     * 名称
     */
    protected String  name;
	/**
     * 更新人
     */
    protected String  updator;
	/**
     * 创建人名字
     */
    protected String  creatorName;
	/**
     * 对话主键
     */
    protected String  dialogueId;
	/**
     * 主键
     */
    protected String  id;
	/**
     * 对话内容个数
     */
    protected Integer  contentCount;
	/**
     * 树唯一ID
     */
    protected String  treeUniqueId;

        /**
     *  获取创建人
     */
    public String getCreator() {
        return creator;
    }

	/**
     *  设置创建人
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
     *  获取用户问题
     */
    public String getQuestion() {
        return question;
    }

	/**
     *  设置用户问题
     */
    public void setQuestion(String question) {
        this.question = question;
    }
    /**
     *  获取更新人名称
     */
    public String getUpdatorName() {
        return updatorName;
    }

	/**
     *  设置更新人名称
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
     *  获取问答类型
     */
    public Integer getType() {
        return type;
    }

	/**
     *  设置问答类型
     */
    public void setType(Integer type) {
        this.type = type;
    }
    /**
     *  获取回复答案
     */
    public String getFeedback() {
        return feedback;
    }

	/**
     *  设置回复答案
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
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
     *  获取样本数据集详情管理w主键
     */
    public String getProblemDetailData() {
        return problemDetailData;
    }

	/**
     *  设置样本数据集详情管理主键
     */
    public void setProblemDetailData(String problemDetailData) {
        this.problemDetailData = problemDetailData;
    }
    /**
     *  获取名称
     */
    public String getName() {
        return name;
    }

	/**
     *  设置名称
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     *  获取更新人
     */
    public String getUpdator() {
        return updator;
    }

	/**
     *  设置更新人
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
     *  获取对话主键
     */
    public String getDialogueId() {
        return dialogueId;
    }

	/**
     *  设置对话主键
     */
    public void setDialogueId(String dialogueId) {
        this.dialogueId = dialogueId;
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
     *  获取对话内容个数
     */
    public Integer getContentCount() {
        return contentCount;
    }

	/**
     *  设置对话内容个数
     */
    public void setContentCount(Integer contentCount) {
        this.contentCount = contentCount;
    }
    /**
     *  获取树唯一ID
     */
    public String getTreeUniqueId() {
        return treeUniqueId;
    }

	/**
     *  设置树唯一ID
     */
    public void setTreeUniqueId(String treeUniqueId) {
        this.treeUniqueId = treeUniqueId;
    }
}