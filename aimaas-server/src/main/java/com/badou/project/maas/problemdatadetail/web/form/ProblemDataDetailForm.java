package  com.badou.project.maas.problemdatadetail.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;

/**
 * @author badousoft
 * @date 2024-05-15 17:37:43.24
 * @todo 样本数据集详情管理form
 */
public class ProblemDataDetailForm extends BaseStrutsEntityForm<ProblemDataDetailEntity> {

	/**
     * 回复答案
     */
    protected String  feedback;
	/**
     * 问题答案主键
     */
    protected String  problemDataId;
	/**
     * 创建人主键
     */
    protected String  creator;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 用户问题
     */
    protected String  question;
	/**
     * 更新人名字
     */
    protected String  updatorName;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 更新人主键
     */
    protected String  updator;
	/**
     * 创建人名字
     */
    protected String  creatorName;
	/**
     * 主键
     */
    protected String  id;
	/**
     * 类型
     */
    protected Integer  type;

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
     *  获取问题答案主键
     */
    public String getProblemDataId() {
        return problemDataId;
    }

	/**
     *  设置问题答案主键
     */
    public void setProblemDataId(String problemDataId) {
        this.problemDataId = problemDataId;
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
     *  获取更新人时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

	/**
     *  设置更新人时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
     *  获取类型
     */
    public Integer getType() {
        return type;
    }

	/**
     *  设置类型
     */
    public void setType(Integer type) {
        this.type = type;
    }
}