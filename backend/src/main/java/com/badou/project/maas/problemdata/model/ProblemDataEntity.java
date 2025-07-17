package com.badou.project.maas.problemdata.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-05-15 17:37:11.964
 * @todo 样本数据集管理
 */
@Entity
@Table(name = "maas_problem_data")
public class ProblemDataEntity extends AppBaseEntity {

    /**
     * 父主键
     */
    @Column(name = "parent_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String parentId;

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
     * 名称
     */
    @Column(name = "name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String name;

    /**
     * 样本权限
     */
    @Column(name = "sample_permission", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer samplePermission;

    /**
     * 样本数量
     */
    @Column(name = "sample_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer sampleCount;

	/**
     * 适用主题
     */
	@Column(name = "Applicable_subject", unique = false, nullable = true, insertable = true, updatable = true)
    protected String applicableSubject;
    
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
     * 更新人名字
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;
    
	/**
     * 关联文件主键
     */
	@Column(name = "Train_file_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainFileId;
    
	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;
    
	/**
     * 适用行业
     */
	@Column(name = "Applicable_industry", unique = false, nullable = true, insertable = true, updatable = true)
    protected String applicableIndustry;
    
	/**
     * 类型
     */
	@Column(name = "Type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer type;
    
	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;
    
	/**
     * 关联文件名字
     */
	@Column(name = "Train_file_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainFileName;
    
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(Integer dataFormat) {
        this.dataFormat = dataFormat;
    }

    /**
     * 获取适用主题
     */
    public String getApplicableSubject() {
        return applicableSubject;
    }

	/**
     * 设置适用主题
     */
    public void setApplicableSubject(String applicableSubject) {
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
     * 获取关联文件主键
     */
    public String getTrainFileId() {
        return trainFileId;
    }

	/**
     * 设置关联文件主键
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
    /**
     * 获取适用行业
     */
    public String getApplicableIndustry() {
        return applicableIndustry;
    }

	/**
     * 设置适用行业
     */
    public void setApplicableIndustry(String applicableIndustry) {
        this.applicableIndustry = applicableIndustry;
    }
    /**
     * 获取类型
     */
    public Integer getType() {
        return type;
    }

	/**
     * 设置类型
     */
    public void setType(Integer type) {
        this.type = type;
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
     * 获取关联文件名字
     */
    public String getTrainFileName() {
        return trainFileName;
    }

	/**
     * 设置关联文件名字
     */
    public void setTrainFileName(String trainFileName) {
        this.trainFileName = trainFileName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSamplePermission() {
        return samplePermission;
    }

    public void setSamplePermission(Integer samplePermission) {
        this.samplePermission = samplePermission;
    }

    public Integer getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(Integer sampleCount) {
        this.sampleCount = sampleCount;
    }
}
