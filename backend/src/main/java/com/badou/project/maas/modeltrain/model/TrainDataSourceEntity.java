package com.badou.project.maas.modeltrain.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-04-07 15:39:08.379
 * @todo 训练集数据类
 */
@Entity
@Table(name = "maas_train_data_source")
//@Document
public class TrainDataSourceEntity extends AppBaseEntity {

	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;

	/**
     * 内容
     */
	@Column(name = "Train_content", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainContent;

	/**
     * 创建时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date createTime;

	/**
     * 数据源说明
     */
	@Column(name = "Source_desc", unique = false, nullable = true, insertable = true, updatable = true)
    protected String sourceDesc;

	/**
     * 创建人名字
     */
	@Column(name = "Creator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creatorName;

	/**
     * 数据源类型
     */
	@Column(name = "Source_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer sourceType;

	/**
     * 主键
     */
	@Id
//	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    public String id;

	/**
     * 标签
     */
	@Column(name = "Tag", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tag;

	/**
     * 标题
     */
	@Column(name = "Title", unique = false, nullable = true, insertable = true, updatable = true)
    protected String title;

	/**
     * 版本
     */
	@Column(name = "Version", unique = false, nullable = true, insertable = true, updatable = true)
    protected String version;


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
     * 获取内容
     */
    public String getTrainContent() {
        return trainContent;
    }

	/**
     * 设置内容
     */
    public void setTrainContent(String trainContent) {
        this.trainContent = trainContent;
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
     * 获取数据源说明
     */
    public String getSourceDesc() {
        return sourceDesc;
    }

	/**
     * 设置数据源说明
     */
    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
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
     * 获取数据源类型
     */
    public Integer getSourceType() {
        return sourceType;
    }

	/**
     * 设置数据源类型
     */
    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
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
     * 获取标签
     */
    public String getTag() {
        return tag;
    }

	/**
     * 设置标签
     */
    public void setTag(String tag) {
        this.tag = tag;
    }
    /**
     * 获取标题
     */
    public String getTitle() {
        return title;
    }

	/**
     * 设置标题
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * 获取版本
     */
    public String getVersion() {
        return version;
    }

	/**
     * 设置版本
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
