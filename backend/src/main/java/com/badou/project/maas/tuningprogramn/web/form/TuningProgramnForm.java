package com.badou.project.maas.tuningprogramn.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.tuningprogramn.model.TuningProgramnEntity;

/**
 * @author badousoft
 * @date 2024-04-30 16:22:32.674
 * @todo 微调计划管理form
 */
public class TuningProgramnForm extends BaseStrutsEntityForm<TuningProgramnEntity> {

	/**
     * 评价模型主键
     */
    protected String  evaluationId;
	/**
     * 创建人主键
     */
    protected String  creator;
	/**
     * 微调参数
     */
    protected String  doParams;
	/**
     * 计划编码
     */
    protected String  code;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 微调操作方式
     */
    protected Integer  doType;
	/**
     * 更新人名字
     */
    protected String  updatorName;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 自定义脚本路径
     */
    protected String  custonSh;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
	/**
     * 是否开启自动评价
     */
    protected Integer  evaluationFlg;
	/**
     * 是否注册到网关
     */
    protected Integer  gatewayFlg;
	/**
     * gpu个数
     */
    protected Integer  gpuCount;
	/**
     * 微调方式
     */
    protected Integer  doWay;
	/**
     * 执行时间
     */
    protected Date  execTime;
	/**
     * 计划名称
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
     * 主键
     */
    protected String  id;
	/**
     * 脚本路径
     */
    protected String  shPath;
	/**
     * 评价模型名字
     */
    protected String  evaluationName;

        /**
     *  获取评价模型主键
     */
    public String getEvaluationId() {
        return evaluationId;
    }

	/**
     *  设置评价模型主键
     */
    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
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
     *  获取微调参数
     */
    public String getDoParams() {
        return doParams;
    }

	/**
     *  设置微调参数
     */
    public void setDoParams(String doParams) {
        this.doParams = doParams;
    }
    /**
     *  获取计划编码
     */
    public String getCode() {
        return code;
    }

	/**
     *  设置计划编码
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
     *  获取微调操作方式
     */
    public Integer getDoType() {
        return doType;
    }

	/**
     *  设置微调操作方式
     */
    public void setDoType(Integer doType) {
        this.doType = doType;
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
     *  获取自定义脚本路径
     */
    public String getCustonSh() {
        return custonSh;
    }

	/**
     *  设置自定义脚本路径
     */
    public void setCustonSh(String custonSh) {
        this.custonSh = custonSh;
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
     *  获取是否开启自动评价
     */
    public Integer getEvaluationFlg() {
        return evaluationFlg;
    }

	/**
     *  设置是否开启自动评价
     */
    public void setEvaluationFlg(Integer evaluationFlg) {
        this.evaluationFlg = evaluationFlg;
    }
    /**
     *  获取是否注册到网关
     */
    public Integer getGatewayFlg() {
        return gatewayFlg;
    }

	/**
     *  设置是否注册到网关
     */
    public void setGatewayFlg(Integer gatewayFlg) {
        this.gatewayFlg = gatewayFlg;
    }
    /**
     *  获取gpu个数
     */
    public Integer getGpuCount() {
        return gpuCount;
    }

	/**
     *  设置gpu个数
     */
    public void setGpuCount(Integer gpuCount) {
        this.gpuCount = gpuCount;
    }
    /**
     *  获取微调方式
     */
    public Integer getDoWay() {
        return doWay;
    }

	/**
     *  设置微调方式
     */
    public void setDoWay(Integer doWay) {
        this.doWay = doWay;
    }
    /**
     *  获取执行时间
     */
    public Date getExecTime() {
        return execTime;
    }

	/**
     *  设置执行时间
     */
    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }
    /**
     *  获取计划名称
     */
    public String getName() {
        return name;
    }

	/**
     *  设置计划名称
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
     *  获取脚本路径
     */
    public String getShPath() {
        return shPath;
    }

	/**
     *  设置脚本路径
     */
    public void setShPath(String shPath) {
        this.shPath = shPath;
    }
    /**
     *  获取评价模型名字
     */
    public String getEvaluationName() {
        return evaluationName;
    }

	/**
     *  设置评价模型名字
     */
    public void setEvaluationName(String evaluationName) {
        this.evaluationName = evaluationName;
    }
}