package  com.badou.project.maas.trainplan.web.form;

import java.util.Date;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;

/**
 * @author badousoft
 * @date 2024-04-17 15:10:34.232
 * @todo 微调方案orm
 */
public class TrainPlanForm extends BaseStrutsEntityForm<TrainPlanEntity> {

	/**
     * 模型应用路径
     */
    protected String  modelAppPath;
	/**
     * 微调参数
     */
    protected String  doParams;
	/**
     * 新模型主键
     */
    protected String  newModelId;
	/**
     * 新模型名称
     */
    protected String  newModelName;
	/**
     * 自定义脚本路径
     */
    protected String  custonSh;
	/**
     * 更新时间
     */
    protected Date  updateTime;
	/**
     * 微调方式
     */
    protected Integer  doWay;
	/**
     * 主键
     */
    protected String  id;
	/**
     * 脚本路径
     */
    protected String  shPath;
	/**
     * 模型应用
     */
    protected String  modelAppId;
	/**
     * 创建人主键
     */
    protected String  creator;
	/**
     * 新模型编码
     */
    protected String  newModelCode;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 操作方式
     */
    protected Integer  doType;
	/**
     * 时间估算
     */
    protected String  runTime;
	/**
     * 更新人名字
     */
    protected String  updatorName;
	/**
     * 是否合并到旧模型
     */
    protected Integer  mergeFlg;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 模型应用名称
     */
    protected String  modelAppName;
	/**
     * 新模型路径
     */
    protected String  createDir;
	/**
     * 新模型工作路径
     */
    protected String  createAllDir;
	/**
     * 计划名字
     */
    protected String  name;
	/**
     * 更新人主键
     */
    protected String  updator;
	/**
     * 创建人名字
     */
    protected String  creatorName;
	/**
     * 执行状态
     */
    protected Integer  doStatus;

        /**
     *  获取模型应用路径
     */
    public String getModelAppPath() {
        return modelAppPath;
    }

	/**
     *  设置模型应用路径
     */
    public void setModelAppPath(String modelAppPath) {
        this.modelAppPath = modelAppPath;
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
     *  获取新模型主键
     */
    public String getNewModelId() {
        return newModelId;
    }

	/**
     *  设置新模型主键
     */
    public void setNewModelId(String newModelId) {
        this.newModelId = newModelId;
    }
    /**
     *  获取新模型名称
     */
    public String getNewModelName() {
        return newModelName;
    }

	/**
     *  设置新模型名称
     */
    public void setNewModelName(String newModelName) {
        this.newModelName = newModelName;
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
     *  获取更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

	/**
     *  设置更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
     *  获取模型应用
     */
    public String getModelAppId() {
        return modelAppId;
    }

	/**
     *  设置模型应用
     */
    public void setModelAppId(String modelAppId) {
        this.modelAppId = modelAppId;
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
     *  获取新模型编码
     */
    public String getNewModelCode() {
        return newModelCode;
    }

	/**
     *  设置新模型编码
     */
    public void setNewModelCode(String newModelCode) {
        this.newModelCode = newModelCode;
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
     *  获取操作方式
     */
    public Integer getDoType() {
        return doType;
    }

	/**
     *  设置操作方式
     */
    public void setDoType(Integer doType) {
        this.doType = doType;
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
     *  获取是否合并到旧模型
     */
    public Integer getMergeFlg() {
        return mergeFlg;
    }

	/**
     *  设置是否合并到旧模型
     */
    public void setMergeFlg(Integer mergeFlg) {
        this.mergeFlg = mergeFlg;
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
     *  获取模型应用名称
     */
    public String getModelAppName() {
        return modelAppName;
    }

	/**
     *  设置模型应用名称
     */
    public void setModelAppName(String modelAppName) {
        this.modelAppName = modelAppName;
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
     *  获取新模型工作路径
     */
    public String getCreateAllDir() {
        return createAllDir;
    }

	/**
     *  设置新模型工作路径
     */
    public void setCreateAllDir(String createAllDir) {
        this.createAllDir = createAllDir;
    }
    /**
     *  获取计划名字
     */
    public String getName() {
        return name;
    }

	/**
     *  设置计划名字
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
     *  获取创建人名字
     */
    public String getCreatorName() {
        return creatorName;
    }

	/**
     *  设置创建人名字
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    /**
     *  获取执行状态
     */
    public Integer getDoStatus() {
        return doStatus;
    }

	/**
     *  设置执行状态
     */
    public void setDoStatus(Integer doStatus) {
        this.doStatus = doStatus;
    }
}