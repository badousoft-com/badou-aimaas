package com.badou.project.maas.problemdatadetail.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-11-12 10:04:53.545
 *  样本数据历史对话表类
 */
@Entity
@Table(name = "maas_problem_data_detail_history")
public class ProblemDataDetailHisEntity extends AppBaseEntity {

	/**
     * 排序号
     */
	@Column(name = "Order_no", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer orderNo;

	/**
     * 历史对话回答(output)
     */
	@Column(name = "Output", unique = false, nullable = true, insertable = true, updatable = true)
    protected String output;

	/**
     * 对话记录主键
     */
	@Column(name = "Problem_data_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String problemDataId;

	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;

	/**
     * 更新时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;

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
     * 历史对话问题(instruction)
     */
	@Column(name = "Instruction", unique = false, nullable = true, insertable = true, updatable = true)
    protected String instruction;

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
     * 获取历史对话回答(output)
     */
    public String getOutput() {
        return output;
    }

	/**
     * 设置历史对话回答(output)
     */
    public void setOutput(String output) {
        this.output = output;
    }
    /**
     * 获取对话记录主键
     */
    public String getProblemDataId() {
        return problemDataId;
    }

	/**
     * 设置对话记录主键
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
     * 获取历史对话问题(instruction)
     */
    public String getInstruction() {
        return instruction;
    }

	/**
     * 设置历史对话问题(instruction)
     */
    public void setInstruction(String instruction) {
        this.instruction = instruction;
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
}
