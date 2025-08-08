package  com.badou.project.server.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.server.model.ServerGpuHistoryEntity;

/**
 * @author badousoft
 * @date 2025-03-19 15:05:08.194
 *  服务器显卡算力历史form
 */
public class ServerGpuHistoryForm extends BaseStrutsEntityForm<ServerGpuHistoryEntity> {

	/**
     * 性能状态-p0最强
     */
    protected Integer  performanceStatus;
	/**
     * 服务器名称
     */
    protected String  serverName;
	/**
     * 型号
     */
    protected String  type;
	/**
     * 最大显存
     */
    protected Integer  maxGraphicsMemory;
	/**
     * 风扇转速百分比
     */
    protected Integer  fanSpeedPer;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
	/**
     * 采集类型
     */
    protected Integer  collectType;
	/**
     * 温度
     */
    protected Double  temperature;
	/**
     * 主键
     */
    protected String  id;
	/**
     * 创建人主键
     */
    protected String  creator;
	/**
     * 当前功耗
     */
    protected Integer  currentPowerDissipation;
	/**
     * 采集主体名称
     */
    protected String  collectObjName;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 更新人名称
     */
    protected String  updatorName;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 最大功耗
     */
    protected Integer  maxPowerDissipation;
	/**
     * 服务器主键
     */
    protected String  serverId;
	/**
     * 采集主体主键
     */
    protected String  collectObjId;
	/**
     * GPU利用率
     */
    protected Integer  usageRate;
	/**
     * 名称
     */
    protected String  name;
	/**
     * 更新人主键
     */
    protected String  updator;
	/**
     * 创建人名称
     */
    protected String  creatorName;
	/**
     * 序号
     */
    protected Integer  orderNum;
	/**
     * 已用显存
     */
    protected Integer  usedGraphicsMemory;
	/**
     * 状态(0.空闲 1.在线)
     */
    protected Integer  status;

        /**
     *  获取性能状态-p0最强
     */
    public Integer getPerformanceStatus() {
        return performanceStatus;
    }

	/**
     *  设置性能状态-p0最强
     */
    public void setPerformanceStatus(Integer performanceStatus) {
        this.performanceStatus = performanceStatus;
    }
    /**
     *  获取服务器名称
     */
    public String getServerName() {
        return serverName;
    }

	/**
     *  设置服务器名称
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    /**
     *  获取型号
     */
    public String getType() {
        return type;
    }

	/**
     *  设置型号
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     *  获取最大显存
     */
    public Integer getMaxGraphicsMemory() {
        return maxGraphicsMemory;
    }

	/**
     *  设置最大显存
     */
    public void setMaxGraphicsMemory(Integer maxGraphicsMemory) {
        this.maxGraphicsMemory = maxGraphicsMemory;
    }
    /**
     *  获取风扇转速百分比
     */
    public Integer getFanSpeedPer() {
        return fanSpeedPer;
    }

	/**
     *  设置风扇转速百分比
     */
    public void setFanSpeedPer(Integer fanSpeedPer) {
        this.fanSpeedPer = fanSpeedPer;
    }
    /**
     *  获取更新人时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

	/**
     *  设置更新人时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    /**
     *  获取采集类型
     */
    public Integer getCollectType() {
        return collectType;
    }

	/**
     *  设置采集类型
     */
    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }
    /**
     *  获取温度
     */
    public Double getTemperature() {
        return temperature;
    }

	/**
     *  设置温度
     */
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    /**
     *  获取主键
     */
    public String getId() {
        return id;
    }

	/**
     *  设置主键
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     *  获取创建人主键
     */
    public String getCreator() {
        return creator;
    }

	/**
     *  设置创建人主键
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }
    /**
     *  获取当前功耗
     */
    public Integer getCurrentPowerDissipation() {
        return currentPowerDissipation;
    }

	/**
     *  设置当前功耗
     */
    public void setCurrentPowerDissipation(Integer currentPowerDissipation) {
        this.currentPowerDissipation = currentPowerDissipation;
    }
    /**
     *  获取采集主体名称
     */
    public String getCollectObjName() {
        return collectObjName;
    }

	/**
     *  设置采集主体名称
     */
    public void setCollectObjName(String collectObjName) {
        this.collectObjName = collectObjName;
    }
    /**
     *  获取创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

	/**
     *  设置创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    /**
     *  获取更新人名称
     */
    public String getUpdatorName() {
        return updatorName;
    }

	/**
     *  设置更新人名称
     */
    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }
    /**
     *  获取逻辑删除符号
     */
    public Integer getFlgDeleted() {
        return flgDeleted;
    }

	/**
     *  设置逻辑删除符号
     */
    public void setFlgDeleted(Integer flgDeleted) {
        this.flgDeleted = flgDeleted;
    }
    /**
     *  获取最大功耗
     */
    public Integer getMaxPowerDissipation() {
        return maxPowerDissipation;
    }

	/**
     *  设置最大功耗
     */
    public void setMaxPowerDissipation(Integer maxPowerDissipation) {
        this.maxPowerDissipation = maxPowerDissipation;
    }
    /**
     *  获取服务器主键
     */
    public String getServerId() {
        return serverId;
    }

	/**
     *  设置服务器主键
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    /**
     *  获取采集主体主键
     */
    public String getCollectObjId() {
        return collectObjId;
    }

	/**
     *  设置采集主体主键
     */
    public void setCollectObjId(String collectObjId) {
        this.collectObjId = collectObjId;
    }
    /**
     *  获取GPU利用率
     */
    public Integer getUsageRate() {
        return usageRate;
    }

	/**
     *  设置GPU利用率
     */
    public void setUsageRate(Integer usageRate) {
        this.usageRate = usageRate;
    }
    /**
     *  获取名称
     */
    public String getName() {
        return name;
    }

	/**
     *  设置名称
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     *  获取更新人主键
     */
    public String getUpdator() {
        return updator;
    }

	/**
     *  设置更新人主键
     */
    public void setUpdator(String updator) {
        this.updator = updator;
    }
    /**
     *  获取创建人名称
     */
    public String getCreatorName() {
        return creatorName;
    }

	/**
     *  设置创建人名称
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    /**
     *  获取序号
     */
    public Integer getOrderNum() {
        return orderNum;
    }

	/**
     *  设置序号
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
    /**
     *  获取已用显存
     */
    public Integer getUsedGraphicsMemory() {
        return usedGraphicsMemory;
    }

	/**
     *  设置已用显存
     */
    public void setUsedGraphicsMemory(Integer usedGraphicsMemory) {
        this.usedGraphicsMemory = usedGraphicsMemory;
    }
    /**
     *  获取状态(0.空闲 1.在线)
     */
    public Integer getStatus() {
        return status;
    }

	/**
     *  设置状态(0.空闲 1.在线)
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}