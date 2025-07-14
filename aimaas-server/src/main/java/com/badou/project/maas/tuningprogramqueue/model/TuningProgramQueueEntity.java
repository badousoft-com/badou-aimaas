package com.badou.project.maas.tuningprogramqueue.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-07-22 09:58:48.624
 * @todo 计划任务列表类
 */
@Entity
@Table(name = "maas_fine_tuning_program_queue")
public class TuningProgramQueueEntity extends AppBaseEntity {

    /**
     * 下一个任务主键
     */
    @Column(name = "next_task_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String nextTaskId;

	/**
     * 任务名称
     */
	@Column(name = "Task_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String taskName;
    
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
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;
    
	/**
     * 微调计划主键
     */
	@Column(name = "Program_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String programId;
    
	/**
     * 微调计划名称
     */
	@Column(name = "Program_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String programName;
    
	/**
     * 计划结束时间
     */
	@Column(name = "End_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date endTime;
    
	/**
     * 任务主键
     */
	@Column(name = "Task_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String taskId;
    
	/**
     * 模型主键
     */
	@Column(name = "Model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelId;
    
	/**
     * 微调方案名称
     */
	@Column(name = "Plan_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planName;
    
	/**
     * 计划启动时间
     */
	@Column(name = "Start_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date startTime;
    
	/**
     * 更新时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;
    
	/**
     * 模型名称
     */
	@Column(name = "Model_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelName;
    
	/**
     * 执行花费时间
     */
	@Column(name = "Cost_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected String costTime;
    
	/**
     * 计划执行时间
     */
	@Column(name = "Exec_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected String execTime;
    
	/**
     * 编号
     */
	@Column(name = "Queue_no", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer queueNo;
    
	/**
     * 任务状态
     */
	@Column(name = "Exec_status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer execStatus;
    
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
     * 任务类型
     */
	@Column(name = "Task_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer taskType;
    
	/**
     * 微调方案主键
     */
	@Column(name = "Plan_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planId;


    public String getNextTaskId() {
        return nextTaskId;
    }

    public void setNextTaskId(String nextTaskId) {
        this.nextTaskId = nextTaskId;
    }

    /**
     * 获取任务名称
     */
    public String getTaskName() {
        return taskName;
    }

	/**
     * 设置任务名称
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
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
     * 获取微调计划名称
     */
    public String getProgramName() {
        return programName;
    }

	/**
     * 设置微调计划名称
     */
    public void setProgramName(String programName) {
        this.programName = programName;
    }
    /**
     * 获取计划结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

	/**
     * 设置计划结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    /**
     * 获取任务主键
     */
    public String getTaskId() {
        return taskId;
    }

	/**
     * 设置任务主键
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    /**
     * 获取模型主键
     */
    public String getModelId() {
        return modelId;
    }

	/**
     * 设置模型主键
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    /**
     * 获取微调方案名称
     */
    public String getPlanName() {
        return planName;
    }

	/**
     * 设置微调方案名称
     */
    public void setPlanName(String planName) {
        this.planName = planName;
    }
    /**
     * 获取计划启动时间
     */
    public Date getStartTime() {
        return startTime;
    }

	/**
     * 设置计划启动时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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
     * 获取模型名称
     */
    public String getModelName() {
        return modelName;
    }

	/**
     * 设置模型名称
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    /**
     * 获取执行花费时间
     */
    public String getCostTime() {
        return costTime;
    }

	/**
     * 设置执行花费时间
     */
    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }
    /**
     * 获取计划执行时间
     */
    public String getExecTime() {
        return execTime;
    }

	/**
     * 设置计划执行时间
     */
    public void setExecTime(String execTime) {
        this.execTime = execTime;
    }
    /**
     * 获取编号
     */
    public Integer getQueueNo() {
        return queueNo;
    }

	/**
     * 设置编号
     */
    public void setQueueNo(Integer queueNo) {
        this.queueNo = queueNo;
    }
    /**
     * 获取任务状态
     */
    public Integer getExecStatus() {
        return execStatus;
    }

	/**
     * 设置任务状态
     */
    public void setExecStatus(Integer execStatus) {
        this.execStatus = execStatus;
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
     * 获取任务类型
     */
    public Integer getTaskType() {
        return taskType;
    }

	/**
     * 设置任务类型
     */
    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
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
