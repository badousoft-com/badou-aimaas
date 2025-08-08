package com.badou.project.maas.traindatalink.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.badou.brms.base.support.hibernate.used.AppEntityWithCreator;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-04-22 16:50:47.367
 * @todo 训练集数据与训练集关联类
 */
@Entity
@Table(name = "maas_train_data_link")
public class TrainDataLinkEntity extends AppEntityWithCreator {

	/**
     * 训练集数据标题
     */
	@Column(name = "Train_data_title", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataTitle;
    
	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;
    
	/**
     * 创建时间
     *
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date createTime;
    
	/**
     * 训练集名称
     */
	@Column(name = "Train_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainName;
    
	/**
     * 训练集数据
     */
	@Column(name = "Train_data_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataId;
    
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
     * 训练集数据内容
     */
	@Column(name = "Train_data_content", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataContent;
    
	/**
     * 训练集数据标签
     */
	@Column(name = "Train_data_tag", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataTag;
    
	/**
     * 训练集主键
     */
	@Column(name = "Train_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainId;
    
	/**
     * 训练集数据版本
     */
	@Column(name = "Train_data_version", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataVersion;
    

    /**
     * 获取训练集数据标题
     */
    public String getTrainDataTitle() {
        return trainDataTitle;
    }

	/**
     * 设置训练集数据标题
     */
    public void setTrainDataTitle(String trainDataTitle) {
        this.trainDataTitle = trainDataTitle;
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
     * 获取训练集名称
     */
    public String getTrainName() {
        return trainName;
    }

	/**
     * 设置训练集名称
     */
    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }
    /**
     * 获取训练集数据
     */
    public String getTrainDataId() {
        return trainDataId;
    }

	/**
     * 设置训练集数据
     */
    public void setTrainDataId(String trainDataId) {
        this.trainDataId = trainDataId;
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
     * 获取训练集数据内容
     */
    public String getTrainDataContent() {
        return trainDataContent;
    }

	/**
     * 设置训练集数据内容
     */
    public void setTrainDataContent(String trainDataContent) {
        this.trainDataContent = trainDataContent;
    }
    /**
     * 获取训练集数据标签
     */
    public String getTrainDataTag() {
        return trainDataTag;
    }

	/**
     * 设置训练集数据标签
     */
    public void setTrainDataTag(String trainDataTag) {
        this.trainDataTag = trainDataTag;
    }
    /**
     * 获取训练集主键
     */
    public String getTrainId() {
        return trainId;
    }

	/**
     * 设置训练集主键
     */
    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }
    /**
     * 获取训练集数据版本
     */
    public String getTrainDataVersion() {
        return trainDataVersion;
    }

	/**
     * 设置训练集数据版本
     */
    public void setTrainDataVersion(String trainDataVersion) {
        this.trainDataVersion = trainDataVersion;
    }
}
