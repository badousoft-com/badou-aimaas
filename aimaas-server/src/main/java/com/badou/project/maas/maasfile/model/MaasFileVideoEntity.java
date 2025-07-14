package com.badou.project.maas.maasfile.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2025-03-25 16:50:44.633
 *  视频管理类
 */
@Entity
@Table(name = "maas_file_video")
public class MaasFileVideoEntity extends AppBaseEntity {

	/**
     * 数据格式
     */
	@Column(name = "Data_format", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer dataFormat;

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
     * 描述
     */
	@Column(name = "Remark", unique = false, nullable = true, insertable = true, updatable = true)
    protected String remark;

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
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;

	/**
     * 文件大小
     */
	@Column(name = "Size", unique = false, nullable = true, insertable = true, updatable = true)
    protected String size;

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
     * 角色备注(希望模型扮演的角色)
     */
	@Column(name = "Role_desc", unique = false, nullable = true, insertable = true, updatable = true)
    protected String roleDesc;

	/**
     * 启用状态
     */
	@Column(name = "up_status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer upStatus;


    /**
     * 获取数据格式
     */
    public Integer getDataFormat() {
        return dataFormat;
    }

	/**
     * 设置数据格式
     */
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
     * 获取描述
     */
    public String getRemark() {
        return remark;
    }

	/**
     * 设置描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
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
     * 获取角色备注(希望模型扮演的角色)
     */
    public String getRoleDesc() {
        return roleDesc;
    }

	/**
     * 设置角色备注(希望模型扮演的角色)
     */
    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }
    /**
     * 获取启用状态
     */
    public Integer getUpStatus() {
        return upStatus;
    }

	/**
     * 设置启用状态
     */
    public void setUpStatus(Integer upStatus) {
        this.upStatus = upStatus;
    }
}
