package com.badou.project.maas.tuningplanparams.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-05-20 14:12:13.912
 * @todo 微调方案参数类
 */
@Entity
@Table(name = "maas_fine_tuning_plan_params")
public class TuningPlanParamsEntity extends AppBaseEntity {

    /**
     * 组别序号
     */
    @Column(name = "group_no", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer groupNo;

    /**
     * 组别名称
     */
    @Column(name = "group_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String groupName;

    /**
     * 组别编码
     */
    @Column(name = "group_code", unique = false, nullable = true, insertable = true, updatable = true)
    protected String groupCode;

    /**
     * 组别优先级
     */
    @Column(name = "group_priority", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer groupPriority;

	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;
    
	/**
     * 编码
     */
	@Column(name = "Code", unique = false, nullable = true, insertable = true, updatable = true)
    protected String code;
    
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
     * 微调计划主键
     */
	@Column(name = "Program_Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String programId;
    
	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;
    
	/**
     * 名字
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
    
	/**
     * 值
     */
	@Column(name = "Value", unique = false, nullable = true, insertable = true, updatable = true)
    protected String value;
    
	/**
     * 解释
     */
	@Column(name = "Explain_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String explainName;
    
	/**
     * 微调方案主键
     */
	@Column(name = "Plan_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planId;
    

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

    public Integer getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(Integer groupNo) {
        this.groupNo = groupNo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Integer getGroupPriority() {
        return groupPriority;
    }

    public void setGroupPriority(Integer groupPriority) {
        this.groupPriority = groupPriority;
    }

    /**
     * 获取编码
     */
    public String getCode() {
        return code;
    }

	/**
     * 设置编码
     */
    public void setCode(String code) {
        this.code = code;
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
     * 获取微调计划主键
     */
    public String getProgramId() {
        return programId;
    }

	/**
     * 设置微调计划主键
     */
    public void setProgramId(String programId) {
        this.programId = programId;
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
     * 获取名字
     */
    public String getName() {
        return name;
    }

	/**
     * 设置名字
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
    /**
     * 获取值
     */
    public String getValue() {
        return value;
    }

	/**
     * 设置值
     */
    public void setValue(String value) {
        this.value = value;
    }
    /**
     * 获取解释
     */
    public String getExplainName() {
        return explainName;
    }

	/**
     * 设置解释
     */
    public void setExplainName(String explainName) {
        this.explainName = explainName;
    }
    /**
     * 获取微调方案主键
     */
    public String getPlanId() {
        return planId;
    }

	/**
     * 设置微调方案主键
     */
    public void setPlanId(String planId) {
        this.planId = planId;
    }
}
