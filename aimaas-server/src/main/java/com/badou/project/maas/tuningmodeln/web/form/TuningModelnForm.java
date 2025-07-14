package com.badou.project.maas.tuningmodeln.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import  com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;

/**
 * @author badousoft
 * @date 2024-04-30 16:20:58.82
 * @todo 微调模型管理form
 */
public class TuningModelnForm extends BaseStrutsEntityForm<TuningModelnEntity> {

	/**
     * 模型文件名字
     */
    protected String  modelFile;
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
     * 时间估算
     */
    protected String  runTime;
	/**
     * 更新人名字
     */
    protected String  updatorName;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 新模型路径
     */
    protected String  createDir;
	/**
     * 工作空间
     */
    protected String  workSpace;
	/**
     * 微调信息
     */
    protected String  planMsg;
	/**
     * pod名字
     */
    protected String  podName;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
	/**
     * 花费时间
     */
    protected String  costTime;
	/**
     * 名字
     */
    protected String  name;
	/**
     * 更新人主键
     */
    protected String  updator;
	/**
     * 生成路径
     */
    protected String  createPath;
	/**
     * 创建人名称
     */
    protected String  creatorName;
	/**
     * 主键
     */
    protected String  id;
	/**
     * 运行状态
     */
    protected Integer  doStatus;

        /**
     *  获取模型文件名字
     */
    public String getModelFile() {
        return modelFile;
    }

	/**
     *  设置模型文件名字
     */
    public void setModelFile(String modelFile) {
        this.modelFile = modelFile;
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
     *  获取时间估算
     */
    public String getRunTime() {
        return runTime;
    }

	/**
     *  设置时间估算
     */
    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }
    /**
     *  获取更新人名字
     */
    public String getUpdatorName() {
        return updatorName;
    }

	/**
     *  设置更新人名字
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
     *  获取新模型路径
     */
    public String getCreateDir() {
        return createDir;
    }

	/**
     *  设置新模型路径
     */
    public void setCreateDir(String createDir) {
        this.createDir = createDir;
    }
    /**
     *  获取工作空间
     */
    public String getWorkSpace() {
        return workSpace;
    }

	/**
     *  设置工作空间
     */
    public void setWorkSpace(String workSpace) {
        this.workSpace = workSpace;
    }
    /**
     *  获取微调信息
     */
    public String getPlanMsg() {
        return planMsg;
    }

	/**
     *  设置微调信息
     */
    public void setPlanMsg(String planMsg) {
        this.planMsg = planMsg;
    }
    /**
     *  获取pod名字
     */
    public String getPodName() {
        return podName;
    }

	/**
     *  设置pod名字
     */
    public void setPodName(String podName) {
        this.podName = podName;
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
     *  获取花费时间
     */
    public String getCostTime() {
        return costTime;
    }

	/**
     *  设置花费时间
     */
    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }
    /**
     *  获取名字
     */
    public String getName() {
        return name;
    }

	/**
     *  设置名字
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
     *  获取生成路径
     */
    public String getCreatePath() {
        return createPath;
    }

	/**
     *  设置生成路径
     */
    public void setCreatePath(String createPath) {
        this.createPath = createPath;
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
     *  获取运行状态
     */
    public Integer getDoStatus() {
        return doStatus;
    }

	/**
     *  设置运行状态
     */
    public void setDoStatus(Integer doStatus) {
        this.doStatus = doStatus;
    }
}