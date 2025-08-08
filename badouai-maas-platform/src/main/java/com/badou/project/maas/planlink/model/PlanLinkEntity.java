package com.badou.project.maas.planlink.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-05-09 16:48:55.91
 * @todo 微调方案关联类
 */
@Entity
@Table(name = "maas_fine_tuning_plan_link")
public class PlanLinkEntity extends AppBaseEntity {

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
    
	/**
     * 更新人名字
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;
    
	/**
     * 训练集名字
     */
	@Column(name = "Train_data_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataName;
    
	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;
    
	/**
     * 微调方案主键
     */
	@Column(name = "Plan_link_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planLinkId;
    
	/**
     * 训练集
     */
	@Column(name = "Train_data_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataId;
    
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
     * 训练集路径
     */
	@Column(name = "Train_data_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataPath;
    

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
     * 获取训练集名字
     */
    public String getTrainDataName() {
        return trainDataName;
    }

	/**
     * 设置训练集名字
     */
    public void setTrainDataName(String trainDataName) {
        this.trainDataName = trainDataName;
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
     * 获取微调方案主键
     */
    public String getPlanLinkId() {
        return planLinkId;
    }

	/**
     * 设置微调方案主键
     */
    public void setPlanLinkId(String planLinkId) {
        this.planLinkId = planLinkId;
    }
    /**
     * 获取训练集
     */
    public String getTrainDataId() {
        return trainDataId;
    }

	/**
     * 设置训练集
     */
    public void setTrainDataId(String trainDataId) {
        this.trainDataId = trainDataId;
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
     * 获取训练集路径
     */
    public String getTrainDataPath() {
        return trainDataPath;
    }

	/**
     * 设置训练集路径
     */
    public void setTrainDataPath(String trainDataPath) {
        this.trainDataPath = trainDataPath;
    }
}
