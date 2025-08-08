package com.badou.project.maas.trainplan.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-04-23 17:00:18.811
 * @todo 微调方案类
 */
@Entity
@Table(name = "maas_fine_tuning_plan")
public class TrainPlanEntity extends AppBaseEntity {

    /**
     * 偏好对齐方法·
     */
    @Column(name = "rlhf_way", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer rlhfWay;

    /**
     * 服务器显卡执行模式
     */
    @Column(name = "server_gpu_mode", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer serverGpuMode;

    /**
     * 模型全名称
     */
    @Column(name = "plan_desc", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planDesc;

    /**
     * 模型全名称
     */
    @Column(name = "model_all_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelAllName;

    /**
     * 奖励模型主键
     */
    @Column(name = "reward_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String rewardId;

    /**
     * 讲咯模型名称
     */
    @Column(name = "reward_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String rewardName;

    /**
     * 预估需要显存
     */
    @Column(name = "pre_gpucache", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer preGpucache;

    /**
     * 是否自动启动
     */
    @Column(name = "Auto_start_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer autoStartFlg;

    /**
     * 微调框架
     */
    @Column(name = "tun_frame", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer tunFrame;

    /**
     * 编码
     */
    @Column(name = "code", unique = false, nullable = true, insertable = true, updatable = true)
    protected String code;

    /**
     * 基础模型名字
     */
    @Column(name = "model_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelName;

    /**
     * 基础模型主键
     */
    @Column(name = "model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelId;
    
	/**
     * 微调参数
     */
	@Column(name = "Do_params", unique = false, nullable = true, insertable = true, updatable = true)
    protected String doParams;
    
	/**
     * 新模型主键
     */
	@Column(name = "New_model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String newModelId;
    
	/**
     * 训练集名称
     */
	@Column(name = "Train_data_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataName;
    
	/**
     * 新模型名称
     */
	@Column(name = "New_model_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String newModelName;
    
	/**
     * 自定义脚本路径
     */
	@Column(name = "Custon_sh", unique = false, nullable = true, insertable = true, updatable = true)
    protected String custonSh;

    /**
     * 评价策略(0.评分模式 相似度模式)
     */
    @Column(name = "Evaluation_strategy", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer evaluationStrategy;

    /**
     * 验证策略
     */
    @Column(name = "Validation_strategy", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer validationStrategy;

    /**
     * 是否注册到网关
     */
    @Column(name = "Gateway_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer gatewayFlg;

    /**
     * 评价模型名字
     */
    @Column(name = "Evaluation_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaluationName;

    /**
     * 预估时长
     */
    @Column(name = "Estimated_duration", unique = false, nullable = true, insertable = true, updatable = true)
    protected String estimatedDuration;

    /**
     * 随机抽的比例(比如随机抽样50% 随机抽30% 随机抽10% 达到则停止评价生成评价报告)
     */
    @Column(name = "Random_proportion", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer randomProportion;

    /**
     * 最大评价条目数
     */
    @Column(name = "Max_train_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer maxTrainCount;

    /**
     * 评价模型主键
     */
    @Column(name = "Evaluation_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaluationId;
    
	/**
     * 微调信息
     */
	@Column(name = "Plan_msg", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planMsg;
    
	/**
     * 更新时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;
    
	/**
     * 花费总时间
     */
	@Column(name = "Cost_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected String costTime;
    
	/**
     * 微调方式 0.指令监督微调数据集 1.预训练数据集 2.偏好数据集 3.KTO数据集 4.多模态
     */
	@Column(name = "Do_way", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer doWay;
    
	/**
     * 主键
     */
	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;

    /**
     * 训练环境主键
     */
    @Column(name = "server_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverId;

    /**
     * 训练环境
     */
    @Column(name = "server_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverName;
	/**
     * 脚本路径
     */
	@Column(name = "Sh_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String shPath;

	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;
    
	/**
     * 新模型编码
     */
	@Column(name = "New_model_code", unique = false, nullable = true, insertable = true, updatable = true)
    protected String newModelCode;
    
	/**
     * 创建时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date createTime;
    
	/**
     * 操作方式
     */
	@Column(name = "Do_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer doType;

	/**
     * 时间估算
     */
	@Column(name = "Run_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected String runTime;

    /**
     * 开始时间
     */
    @Column(name = "start_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date startTime;
    
	/**
     * 更新人名字
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;
    
	/**
     * 是否合并到旧模型
     */
	@Column(name = "Merge_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer mergeFlg;
    
	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;
    
	/**
     * 训练集主键
     */
	@Column(name = "Train_data_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataId;

	/**
     * 新模型路径
     */
	@Column(name = "Create_dir", unique = false, nullable = true, insertable = true, updatable = true)
    protected String createDir;
    
	/**
     * 工作目录
     */
	@Column(name = "Work_space", unique = false, nullable = true, insertable = true, updatable = true)
    protected String workSpace;
    
	/**
     * 训练集路径
     */
	@Column(name = "Train_data_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainDataPath;
    
	/**
     * 容器名称
     */
	@Column(name = "Pod_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String podName;
    
	/**
     * 新模型工作路径
     */
	@Column(name = "Create_all_dir", unique = false, nullable = true, insertable = true, updatable = true)
    protected String createAllDir;
    
	/**
     * 显卡执行个数
     */
	@Column(name = "Gpu_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer gpuCount;

    /**
     * 显卡多卡框架
     */
    @Column(name = "Gpu_frame", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer gpuFrame;

	/**
     * 计划名字
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
     * 执行状态
     */
	@Column(name = "Do_status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer doStatus;

    /**
     * 执行方式
     */
    @Column(name = "exec_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer execType;

    /**
     * 微调方式
     */
    @Column(name = "mode", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer mode;

    public Integer getRlhfWay() {
        return rlhfWay;
    }

    public void setRlhfWay(Integer rlhfWay) {
        this.rlhfWay = rlhfWay;
    }

    public Integer getServerGpuMode() {
        return serverGpuMode;
    }

    public void setServerGpuMode(Integer serverGpuMode) {
        this.serverGpuMode = serverGpuMode;
    }
    public String getPlanDesc() {
        return planDesc;
    }

    public void setPlanDesc(String planDesc) {
        this.planDesc = planDesc;
    }

    public String getModelAllName() {
        return modelAllName;
    }

    public void setModelAllName(String modelAllName) {
        this.modelAllName = modelAllName;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public Integer getGpuFrame() {
        return gpuFrame;
    }

    public void setGpuFrame(Integer gpuFrame) {
        this.gpuFrame = gpuFrame;
    }

    public Integer getPreGpucache() {
        return preGpucache;
    }

    public void setPreGpucache(Integer preGpucache) {
        this.preGpucache = preGpucache;
    }

    public Integer getAutoStartFlg() {
        return autoStartFlg;
    }

    public void setAutoStartFlg(Integer autoStartFlg) {
        this.autoStartFlg = autoStartFlg;
    }

    public Integer getEvaluationStrategy() {
        return evaluationStrategy;
    }

    public void setEvaluationStrategy(Integer evaluationStrategy) {
        this.evaluationStrategy = evaluationStrategy;
    }

    public Integer getValidationStrategy() {
        return validationStrategy;
    }

    public void setValidationStrategy(Integer validationStrategy) {
        this.validationStrategy = validationStrategy;
    }

    public Integer getGatewayFlg() {
        return gatewayFlg;
    }

    public void setGatewayFlg(Integer gatewayFlg) {
        this.gatewayFlg = gatewayFlg;
    }

    public String getEvaluationName() {
        return evaluationName;
    }

    public void setEvaluationName(String evaluationName) {
        this.evaluationName = evaluationName;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Integer getRandomProportion() {
        return randomProportion;
    }

    public void setRandomProportion(Integer randomProportion) {
        this.randomProportion = randomProportion;
    }

    public Integer getMaxTrainCount() {
        return maxTrainCount;
    }

    public void setMaxTrainCount(Integer maxTrainCount) {
        this.maxTrainCount = maxTrainCount;
    }

    public String getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Integer getTunFrame() {
        return tunFrame;
    }

    public void setTunFrame(Integer tunFrame) {
        this.tunFrame = tunFrame;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getExecType() {
        return execType;
    }

    public void setExecType(Integer execType) {
        this.execType = execType;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * 获取微调参数
     */
    public String getDoParams() {
        return doParams;
    }

	/**
     * 设置微调参数
     */
    public void setDoParams(String doParams) {
        this.doParams = doParams;
    }
    /**
     * 获取新模型主键
     */
    public String getNewModelId() {
        return newModelId;
    }

	/**
     * 设置新模型主键
     */
    public void setNewModelId(String newModelId) {
        this.newModelId = newModelId;
    }
    /**
     * 获取训练集名称
     */
    public String getTrainDataName() {
        return trainDataName;
    }

	/**
     * 设置训练集名称
     */
    public void setTrainDataName(String trainDataName) {
        this.trainDataName = trainDataName;
    }
    /**
     * 获取新模型名称
     */
    public String getNewModelName() {
        return newModelName;
    }

	/**
     * 设置新模型名称
     */
    public void setNewModelName(String newModelName) {
        this.newModelName = newModelName;
    }
    /**
     * 获取自定义脚本路径
     */
    public String getCustonSh() {
        return custonSh;
    }

	/**
     * 设置自定义脚本路径
     */
    public void setCustonSh(String custonSh) {
        this.custonSh = custonSh;
    }
    /**
     * 获取微调信息
     */
    public String getPlanMsg() {
        return planMsg;
    }

	/**
     * 设置微调信息
     */
    public void setPlanMsg(String planMsg) {
        this.planMsg = planMsg;
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
     * 获取花费总时间
     */
    public String getCostTime() {
        return costTime;
    }

	/**
     * 设置花费总时间
     */
    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }
    /**
     * 获取微调方式
     */
    public Integer getDoWay() {
        return doWay;
    }

	/**
     * 设置微调方式
     */
    public void setDoWay(Integer doWay) {
        this.doWay = doWay;
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
     * 获取脚本路径
     */
    public String getShPath() {
        return shPath;
    }

	/**
     * 设置脚本路径
     */
    public void setShPath(String shPath) {
        this.shPath = shPath;
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
     * 获取新模型编码
     */
    public String getNewModelCode() {
        return newModelCode;
    }

	/**
     * 设置新模型编码
     */
    public void setNewModelCode(String newModelCode) {
        this.newModelCode = newModelCode;
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
     * 获取操作方式
     */
    public Integer getDoType() {
        return doType;
    }

	/**
     * 设置操作方式
     */
    public void setDoType(Integer doType) {
        this.doType = doType;
    }
    /**
     * 获取时间估算
     */
    public String getRunTime() {
        return runTime;
    }

	/**
     * 设置时间估算
     */
    public void setRunTime(String runTime) {
        this.runTime = runTime;
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
     * 获取是否合并到旧模型
     */
    public Integer getMergeFlg() {
        return mergeFlg;
    }

	/**
     * 设置是否合并到旧模型
     */
    public void setMergeFlg(Integer mergeFlg) {
        this.mergeFlg = mergeFlg;
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
     * 获取训练集主键
     */
    public String getTrainDataId() {
        return trainDataId;
    }

	/**
     * 设置训练集主键
     */
    public void setTrainDataId(String trainDataId) {
        this.trainDataId = trainDataId;
    }

    /**
     * 获取新模型路径
     */
    public String getCreateDir() {
        return createDir;
    }

	/**
     * 设置新模型路径
     */
    public void setCreateDir(String createDir) {
        this.createDir = createDir;
    }
    /**
     * 获取工作目录
     */
    public String getWorkSpace() {
        return workSpace;
    }

	/**
     * 设置工作目录
     */
    public void setWorkSpace(String workSpace) {
        this.workSpace = workSpace;
    }
    /**
     * 获取训练集路径
     */
    public String getTrainDataPath() {
        return trainDataPath;
    }

	/**
     * 设置训练集路径
     */
    public void setTrainDataPath(String trainDataPath) {
        this.trainDataPath = trainDataPath;
    }
    /**
     * 获取容器名称
     */
    public String getPodName() {
        return podName;
    }

	/**
     * 设置容器名称
     */
    public void setPodName(String podName) {
        this.podName = podName;
    }
    /**
     * 获取新模型工作路径
     */
    public String getCreateAllDir() {
        return createAllDir;
    }

	/**
     * 设置新模型工作路径
     */
    public void setCreateAllDir(String createAllDir) {
        this.createAllDir = createAllDir;
    }
    /**
     * 获取gpu个数
     */
    public Integer getGpuCount() {
        return gpuCount;
    }

	/**
     * 设置gpu个数
     */
    public void setGpuCount(Integer gpuCount) {
        this.gpuCount = gpuCount;
    }
    /**
     * 获取计划名字
     */
    public String getName() {
        return name;
    }

	/**
     * 设置计划名字
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
     * 获取执行状态
     */
    public Integer getDoStatus() {
        return doStatus;
    }

	/**
     * 设置执行状态
     */
    public void setDoStatus(Integer doStatus) {
        this.doStatus = doStatus;
    }

    @Override
    public String toString() {
        return "TrainPlanEntity{" +
                ", doParams='" + doParams + '\'' +
                ", newModelId='" + newModelId + '\'' +
                ", trainDataName='" + trainDataName + '\'' +
                ", newModelName='" + newModelName + '\'' +
                ", custonSh='" + custonSh + '\'' +
                ", planMsg='" + planMsg + '\'' +
                ", updateTime=" + updateTime +
                ", costTime='" + costTime + '\'' +
                ", doWay=" + doWay +
                ", id='" + id + '\'' +
                ", shPath='" + shPath + '\'' +
                ", creator='" + creator + '\'' +
                ", newModelCode='" + newModelCode + '\'' +
                ", createTime=" + createTime +
                ", doType=" + doType +
                ", runTime='" + runTime + '\'' +
                ", startTime=" + startTime +
                ", updatorName='" + updatorName + '\'' +
                ", mergeFlg=" + mergeFlg +
                ", flgDeleted=" + flgDeleted +
                ", trainDataId='" + trainDataId + '\'' +
                ", createDir='" + createDir + '\'' +
                ", workSpace='" + workSpace + '\'' +
                ", trainDataPath='" + trainDataPath + '\'' +
                ", podName='" + podName + '\'' +
                ", createAllDir='" + createAllDir + '\'' +
                ", gpuCount=" + gpuCount +
                ", name='" + name + '\'' +
                ", updator='" + updator + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", doStatus=" + doStatus +
                '}';
    }
}
