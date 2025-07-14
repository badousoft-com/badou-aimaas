package com.badou.project.server.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author badousoft
 * @date 2025-03-19 15:05:08.194
 *  服务器显卡算力历史类
 */
@Entity
@Table(name = "maas_server_gpu_history")
public class ServerGpuHistoryEntity extends AppBaseEntity {

    /*
    服务器显卡详情信息
     */
    @Column(name = "server_gpu_list", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverGpuList;

    /**
     * 线程信息
     */
    @Column(name = "thread_msg", unique = false, nullable = true, insertable = true, updatable = true)
    protected String threadMsg;

	/**
     * 性能状态-p0最强
     */
	@Column(name = "Performance_status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer performanceStatus;

	/**
     * 服务器名称
     */
	@Column(name = "Server_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverName;

	/**
     * 型号
     */
	@Column(name = "Type", unique = false, nullable = true, insertable = true, updatable = true)
    protected String type;

	/**
     * 最大显存
     */
	@Column(name = "Max_graphics_memory", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer maxGraphicsMemory;

	/**
     * 风扇转速百分比
     */
	@Column(name = "Fan_speed_per", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer fanSpeedPer;

	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;

	/**
     * 采集类型
     */
	@Column(name = "Collect_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer collectType;

	/**
     * 温度
     */
	@Column(name = "Temperature", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double temperature;

	/**
     * 主键
     */
	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;

	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;

	/**
     * 当前功耗
     */
	@Column(name = "Current_power_dissipation", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer currentPowerDissipation;

	/**
     * 采集主体名称
     */
	@Column(name = "Collect_obj_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String collectObjName;

	/**
     * 创建时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date createTime;

	/**
     * 更新人名称
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;

	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;

	/**
     * 最大功耗
     */
	@Column(name = "Max_power_dissipation", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer maxPowerDissipation;

	/**
     * 服务器主键
     */
	@Column(name = "Server_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverId;

	/**
     * 采集主体主键
     */
	@Column(name = "Collect_obj_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String collectObjId;

	/**
     * GPU利用率
     */
	@Column(name = "Usage_rate", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer usageRate;

	/**
     * 名称
     */
	@Column(name = "Name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String name;

	/**
     * 更新人主键
     */
	@Column(name = "Updator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updator;

	/**
     * 创建人名称
     */
	@Column(name = "Creator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creatorName;

	/**
     * 序号
     */
	@Column(name = "Order_num", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer orderNum;

    /**
     * 执行显卡
     */
    @Column(name = "exec_gpu_card", unique = false, nullable = true, insertable = true, updatable = true)
    protected String execGpuCard;

	/**
     * 已用显存
     */
	@Column(name = "Used_graphics_memory", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer usedGraphicsMemory;

	/**
     * 状态(0.空闲 1.在线)
     */
	@Column(name = "Status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer status;

    public String getExecGpuCard() {
        return execGpuCard;
    }

    public void setExecGpuCard(String execGpuCard) {
        this.execGpuCard = execGpuCard;
    }

    public String getServerGpuList() {
        return serverGpuList;
    }

    public void setServerGpuList(String serverGpuList) {
        this.serverGpuList = serverGpuList;
    }

    public String getThreadMsg() {
        return threadMsg;
    }

    public void setThreadMsg(String threadMsg) {
        this.threadMsg = threadMsg;
    }

    /**
     * 获取性能状态-p0最强
     */
    public Integer getPerformanceStatus() {
        return performanceStatus;
    }

	/**
     * 设置性能状态-p0最强
     */
    public void setPerformanceStatus(Integer performanceStatus) {
        this.performanceStatus = performanceStatus;
    }
    /**
     * 获取服务器名称
     */
    public String getServerName() {
        return serverName;
    }

	/**
     * 设置服务器名称
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    /**
     * 获取型号
     */
    public String getType() {
        return type;
    }

	/**
     * 设置型号
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * 获取最大显存
     */
    public Integer getMaxGraphicsMemory() {
        return maxGraphicsMemory;
    }

	/**
     * 设置最大显存
     */
    public void setMaxGraphicsMemory(Integer maxGraphicsMemory) {
        this.maxGraphicsMemory = maxGraphicsMemory;
    }
    /**
     * 获取风扇转速百分比
     */
    public Integer getFanSpeedPer() {
        return fanSpeedPer;
    }

	/**
     * 设置风扇转速百分比
     */
    public void setFanSpeedPer(Integer fanSpeedPer) {
        this.fanSpeedPer = fanSpeedPer;
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
     * 获取采集类型
     */
    public Integer getCollectType() {
        return collectType;
    }

	/**
     * 设置采集类型
     */
    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }
    /**
     * 获取温度
     */
    public Double getTemperature() {
        return temperature;
    }

	/**
     * 设置温度
     */
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
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
     * 获取当前功耗
     */
    public Integer getCurrentPowerDissipation() {
        return currentPowerDissipation;
    }

	/**
     * 设置当前功耗
     */
    public void setCurrentPowerDissipation(Integer currentPowerDissipation) {
        this.currentPowerDissipation = currentPowerDissipation;
    }
    /**
     * 获取采集主体名称
     */
    public String getCollectObjName() {
        return collectObjName;
    }

	/**
     * 设置采集主体名称
     */
    public void setCollectObjName(String collectObjName) {
        this.collectObjName = collectObjName;
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
     * 获取更新人名称
     */
    public String getUpdatorName() {
        return updatorName;
    }

	/**
     * 设置更新人名称
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
     * 获取最大功耗
     */
    public Integer getMaxPowerDissipation() {
        return maxPowerDissipation;
    }

	/**
     * 设置最大功耗
     */
    public void setMaxPowerDissipation(Integer maxPowerDissipation) {
        this.maxPowerDissipation = maxPowerDissipation;
    }
    /**
     * 获取服务器主键
     */
    public String getServerId() {
        return serverId;
    }

	/**
     * 设置服务器主键
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    /**
     * 获取采集主体主键
     */
    public String getCollectObjId() {
        return collectObjId;
    }

	/**
     * 设置采集主体主键
     */
    public void setCollectObjId(String collectObjId) {
        this.collectObjId = collectObjId;
    }
    /**
     * 获取GPU利用率
     */
    public Integer getUsageRate() {
        return usageRate;
    }

	/**
     * 设置GPU利用率
     */
    public void setUsageRate(Integer usageRate) {
        this.usageRate = usageRate;
    }
    /**
     * 获取名称
     */
    public String getName() {
        return name;
    }

	/**
     * 设置名称
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
     * 获取创建人名称
     */
    public String getCreatorName() {
        return creatorName;
    }

	/**
     * 设置创建人名称
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    /**
     * 获取序号
     */
    public Integer getOrderNum() {
        return orderNum;
    }

	/**
     * 设置序号
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
    /**
     * 获取已用显存
     */
    public Integer getUsedGraphicsMemory() {
        return usedGraphicsMemory;
    }

	/**
     * 设置已用显存
     */
    public void setUsedGraphicsMemory(Integer usedGraphicsMemory) {
        this.usedGraphicsMemory = usedGraphicsMemory;
    }
    /**
     * 获取状态(0.空闲 1.在线)
     */
    public Integer getStatus() {
        return status;
    }

	/**
     * 设置状态(0.空闲 1.在线)
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}
