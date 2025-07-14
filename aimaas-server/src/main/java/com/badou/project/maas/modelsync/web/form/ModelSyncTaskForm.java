package com.badou.project.maas.modelsync.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;

/**
 * @author badousoft
 * @date 2025-04-11 10:33:39.208
 *  模型同步任务form
 */
public class ModelSyncTaskForm extends BaseStrutsEntityForm<ModelSyncTaskEntity> {

	/**
     * 创建人主键
     */
    protected String  creator;
	/**
     * 编码
     */
    protected String  code;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 执行信息
     */
    protected String  execMsg;
	/**
     * 源服务器主键
     */
    protected String  sourceServerIds;
	/**
     * 源服务器名称
     */
    protected String  sourceServerNames;
	/**
     * 同步数据总大小
     */
    protected String  totalSize;
	/**
     * 失败时间
     */
    protected Date  failTime;
	/**
     * 成功时间
     */
    protected Date  finishTime;
	/**
     * 执行百分比
     */
    protected Integer  execPercentage;
	/**
     * 服务器名称
     */
    protected String  serverNames;
	/**
     * 服务器主键
     */
    protected String  serverIds;
	/**
     * 仓库模型名称
     */
    protected String  warehouseName;
	/**
     * 创建人名称
     */
    protected String  creatorName;
	/**
     * 主键
     */
    protected String  id;
	/**
     * 同步状态
     */
    protected Integer  status;
	/**
     * 仓库模型主键
     */
    protected String  warehouseId;

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
     *  获取编码
     */
    public String getCode() {
        return code;
    }

	/**
     *  设置编码
     */
    public void setCode(String code) {
        this.code = code;
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
     *  获取执行信息
     */
    public String getExecMsg() {
        return execMsg;
    }

	/**
     *  设置执行信息
     */
    public void setExecMsg(String execMsg) {
        this.execMsg = execMsg;
    }
    /**
     *  获取源服务器主键
     */
    public String getSourceServerIds() {
        return sourceServerIds;
    }

	/**
     *  设置源服务器主键
     */
    public void setSourceServerIds(String sourceServerIds) {
        this.sourceServerIds = sourceServerIds;
    }
    /**
     *  获取源服务器名称
     */
    public String getSourceServerNames() {
        return sourceServerNames;
    }

	/**
     *  设置源服务器名称
     */
    public void setSourceServerNames(String sourceServerNames) {
        this.sourceServerNames = sourceServerNames;
    }
    /**
     *  获取同步数据总大小
     */
    public String getTotalSize() {
        return totalSize;
    }

	/**
     *  设置同步数据总大小
     */
    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }
    /**
     *  获取失败时间
     */
    public Date getFailTime() {
        return failTime;
    }

	/**
     *  设置失败时间
     */
    public void setFailTime(Date failTime) {
        this.failTime = failTime;
    }
    /**
     *  获取成功时间
     */
    public Date getFinishTime() {
        return finishTime;
    }

	/**
     *  设置成功时间
     */
    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
    /**
     *  获取执行百分比
     */
    public Integer getExecPercentage() {
        return execPercentage;
    }

	/**
     *  设置执行百分比
     */
    public void setExecPercentage(Integer execPercentage) {
        this.execPercentage = execPercentage;
    }
    /**
     *  获取服务器名称
     */
    public String getServerNames() {
        return serverNames;
    }

	/**
     *  设置服务器名称
     */
    public void setServerNames(String serverNames) {
        this.serverNames = serverNames;
    }
    /**
     *  获取服务器主键
     */
    public String getServerIds() {
        return serverIds;
    }

	/**
     *  设置服务器主键
     */
    public void setServerIds(String serverIds) {
        this.serverIds = serverIds;
    }
    /**
     *  获取仓库模型名称
     */
    public String getWarehouseName() {
        return warehouseName;
    }

	/**
     *  设置仓库模型名称
     */
    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
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
     *  获取同步状态
     */
    public Integer getStatus() {
        return status;
    }

	/**
     *  设置同步状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
    /**
     *  获取仓库模型主键
     */
    public String getWarehouseId() {
        return warehouseId;
    }

	/**
     *  设置仓库模型主键
     */
    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }
}