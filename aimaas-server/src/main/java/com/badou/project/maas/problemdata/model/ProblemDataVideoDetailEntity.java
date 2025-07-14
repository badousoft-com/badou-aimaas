package com.badou.project.maas.problemdata.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2025-03-26 11:30:40.876
 *  视频样本集详情类
 */
@Entity
@Table(name = "maas_problem_data_video_detail")
public class ProblemDataVideoDetailEntity extends AppBaseEntity {

	/**
     * 排序号
     */
	@Column(name = "Order_no", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer orderNo;

	/**
     * 文件类型
     */
	@Column(name = "Attach_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer attachType;

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
     * 文件
     */
	@Column(name = "Attach_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String attachId;

	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;

	/**
     * 视频样本集主键
     */
	@Column(name = "Data_video_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String dataVideoId;

	/**
     * 回答
     */
	@Column(name = "Feedback", unique = false, nullable = true, insertable = true, updatable = true)
    protected String feedback;

	/**
     * 输入
     */
	@Column(name = "Input", unique = false, nullable = true, insertable = true, updatable = true)
    protected String input;

	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;

	/**
     * 文件名称
     */
	@Column(name = "Attach_file", unique = false, nullable = true, insertable = true, updatable = true)
    protected String attachFile;

	/**
     * 图片定义-图片在训练集里面的标识
     */
	@Column(name = "Img_defind", unique = false, nullable = true, insertable = true, updatable = true)
    protected String imgDefind;

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
     * 获取排序号
     */
    public Integer getOrderNo() {
        return orderNo;
    }

	/**
     * 设置排序号
     */
    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }
    /**
     * 获取文件类型
     */
    public Integer getAttachType() {
        return attachType;
    }

	/**
     * 设置文件类型
     */
    public void setAttachType(Integer attachType) {
        this.attachType = attachType;
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
     * 获取文件
     */
    public String getAttachId() {
        return attachId;
    }

	/**
     * 设置文件
     */
    public void setAttachId(String attachId) {
        this.attachId = attachId;
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
     * 获取视频样本集主键
     */
    public String getDataVideoId() {
        return dataVideoId;
    }

	/**
     * 设置视频样本集主键
     */
    public void setDataVideoId(String dataVideoId) {
        this.dataVideoId = dataVideoId;
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
     * 获取文件名称
     */
    public String getAttachFile() {
        return attachFile;
    }

	/**
     * 设置文件名称
     */
    public void setAttachFile(String attachFile) {
        this.attachFile = attachFile;
    }
    /**
     * 获取图片定义-图片在训练集里面的标识
     */
    public String getImgDefind() {
        return imgDefind;
    }

	/**
     * 设置图片定义-图片在训练集里面的标识
     */
    public void setImgDefind(String imgDefind) {
        this.imgDefind = imgDefind;
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
}
