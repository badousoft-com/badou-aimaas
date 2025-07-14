package  com.badou.project.maas.evaluationinstan.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanqEntity;

/**
 * @author badousoft
 * @date 2024-06-06 15:58:58.809
 * @todo 模型评价实例问题form
 */
public class EvaluationInstanqForm extends BaseStrutsEntityForm<EvaluationInstanqEntity> {

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
     * 提交时间
     */
    protected Date  submitTime;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 答案评分
     */
    protected Double  answerScore;
	/**
     * 回答
     */
    protected String  feedback;
	/**
     * 实例主键
     */
    protected String  instanq;
	/**
     * 问题
     */
    protected String  qustion;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
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
     * 标准答案
     */
    protected String  standardAnswer;

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
     *  获取提交时间
     */
    public Date getSubmitTime() {
        return submitTime;
    }

	/**
     *  设置提交时间
     */
    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
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
     *  获取答案评分
     */
    public Double getAnswerScore() {
        return answerScore;
    }

	/**
     *  设置答案评分
     */
    public void setAnswerScore(Double answerScore) {
        this.answerScore = answerScore;
    }
    /**
     *  获取回答
     */
    public String getFeedback() {
        return feedback;
    }

	/**
     *  设置回答
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    /**
     *  获取实例主键
     */
    public String getInstanq() {
        return instanq;
    }

	/**
     *  设置实例主键
     */
    public void setInstanq(String instanq) {
        this.instanq = instanq;
    }
    /**
     *  获取问题
     */
    public String getQustion() {
        return qustion;
    }

	/**
     *  设置问题
     */
    public void setQustion(String qustion) {
        this.qustion = qustion;
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
     *  获取标准答案
     */
    public String getStandardAnswer() {
        return standardAnswer;
    }

	/**
     *  设置标准答案
     */
    public void setStandardAnswer(String standardAnswer) {
        this.standardAnswer = standardAnswer;
    }
}