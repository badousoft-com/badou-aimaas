package com.badou.project.maas.trainfile.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-05-16 11:07:50.5
 * @todo 训练集文件管理类
 */
@Entity
@Table(name = "maas_train_file")
public class TrainFileEntity extends AppBaseEntity {

    /**
     * 启用状态(0.未启用 1.启用)
     */
    @Column(name = "up_status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer upStatus;

    /**
     * 描述
     */
    @Column(name = "remark", unique = false, nullable = true, insertable = true, updatable = true)
    protected String remark;

    /**
     * 数据格式
     */
    @Column(name = "data_format", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer dataFormat;

    /**
     *  类型
     */
    @Column(name = "type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer type;

    /**
     * 角色备注
     */
    @Column(name = "role_desc", unique = false, nullable = true, insertable = true, updatable = true)
    protected String roleDesc;

	/**
     * 文件路径
     */
	@Column(name = "File_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String filePath;
    
	/**
     * 适用主题
     */
	@Column(name = "Applicable_subject", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer applicableSubject;
    
	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;
    
	/**
     * 创建时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
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
     * 内容总数
     */
	@Column(name = "Num_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer numCount;
    
	/**
     * 适用行业
     */
	@Column(name = "Applicable_industry", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer applicableIndustry;
    
	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;
    
	/**
     * 文件大小
     */
	@Column(name = "Size", unique = false, nullable = true, insertable = true, updatable = true)
    protected String size;
    
	/**
     * 文件主键
     */
	@Column(name = "File_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String fileId;
    
	/**
     * 文件名字
     */
	@Column(name = "Name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String name;
    
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

    public Integer getUpStatus() {
        return upStatus;
    }

    public void setUpStatus(Integer upStatus) {
        this.upStatus = upStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(Integer dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    /**
     * 获取文件路径
     */
    public String getFilePath() {
        return filePath;
    }

	/**
     * 设置文件路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    /**
     * 获取适用主题
     */
    public Integer getApplicableSubject() {
        return applicableSubject;
    }

	/**
     * 设置适用主题
     */
    public void setApplicableSubject(Integer applicableSubject) {
        this.applicableSubject = applicableSubject;
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
     * 获取内容总数
     */
    public Integer getNumCount() {
        return numCount;
    }

	/**
     * 设置内容总数
     */
    public void setNumCount(Integer numCount) {
        this.numCount = numCount;
    }
    /**
     * 获取适用行业
     */
    public Integer getApplicableIndustry() {
        return applicableIndustry;
    }

	/**
     * 设置适用行业
     */
    public void setApplicableIndustry(Integer applicableIndustry) {
        this.applicableIndustry = applicableIndustry;
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
     * 获取文件大小
     */
    public String getSize() {
        return size;
    }

	/**
     * 设置文件大小
     */
    public void setSize(String size) {
        this.size = size;
    }
    /**
     * 获取文件主键
     */
    public String getFileId() {
        return fileId;
    }

	/**
     * 设置文件主键
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    /**
     * 获取文件名字
     */
    public String getName() {
        return name;
    }

	/**
     * 设置文件名字
     */
    public void setName(String name) {
        this.name = name;
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
