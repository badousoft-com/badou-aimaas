package com.badou.project.maas.tuningmodeln.model;

import com.alibaba.fastjson.JSONArray;
import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.badou.project.gpucalc.model.MultipleServersConfig;

import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author badousoft
 * @date 2024-04-30 16:20:58.82
 * @todo 微调模型管理类
 */
@Entity
@Table(name = "maas_fine_tuning_model")
public class TuningModelnEntity extends AppBaseEntity {
    /**
     * 服务器的显卡分配信息
     */
    @Transient
    protected List<MultipleServersConfig> multipleServersConfigs = new ArrayList<>();

    /**
     * 当前使用显卡信息
     */
    @Transient
    protected String usedCardNos;

    /**
     * 执行的显卡
     */
    @Column(name = "exec_gpu_card", unique = false, nullable = true, insertable = true, updatable = true)
    protected String execGpuCard;

    /**
     * 基础模型主键
     */
    @Column(name = "base_model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String baseModelId;

    /**
     * 评价总数
     */
    @Column(name = "eva_total_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaTotalCount;

    /**
     * 评价总分
     */
    @Column(name = "eva_total_score", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaTotalScore;

    /**
     * 模型全名称 用来展示
     */
    @Column(name = "model_all_Name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelAllName;

    /**
     * 上下架状态
     */
    @Column(name = "shelves_status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer shelvesStatus;

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
     * 配置识别编码.多个用,
     */
    @Column(name = "host_files_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String hostFilesPath;

    /**
     * 配置识别编码.多个用,
     */
    @Column(name = "mount_files_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String mountFilesPath;

    /**
     * 配置识别编码.多个用,
     */
    @Column(name = "name_files_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String nameFilesPath;

    /**
     * 微调模式 0.指令监督微调数据集 1.预训练数据集 2.偏好数据集 3.KTO数据集 4.多模态
     */
    @Column(name = "do_way", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer doWay;

    /**
     * 偏好对齐方法·
     */
    @Column(name = "rlhf_way", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer rlhfWay;

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
     * 微调方式
     */
    @Column(name = "do_method", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer doMethod;

    /**
     * 微调框架
     */
    @Column(name = "do_frame", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer doFrame;

    /**
     * 任务主键
     */
    @Column(name = "task_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String taskId;

    /**
     * 微调方案主键
     */
    @Column(name = "plan_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planId;

    /**
     * 微调方案名称
     */
    @Column(name = "plan_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planName;

	/**
     * 模型文件名字
     */
    @Column(name = "Model_file", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelFile;

    /**
     * 评价实例主键
     */
    @Column(name = "evaluatio_instan_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaluatioInstanId;
    /**
     * 评价实例名字
     */
    @Column(name = "evaluatio_instan_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaluatioInstanName;
    /**
     * 模型应用主键
     */
    @Column(name = "model_app_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelAppId;

    /**
     * 模型应用名字
     */
    @Column(name = "model_app_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelAPPName;

    /**
     * 基础模型主键
     */
    @Column(name = "model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelId;

    /**
     * 基础模型名字
     */
    @Column(name = "model_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelName;

    /**
     * 模型文件名字
     */
    @Column(name = "do_params", unique = false, nullable = true, insertable = true, updatable = true)
    protected String doParams;
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
     * 时间估算
     */
	@Column(name = "Run_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected String runTime;
    
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
     * 新模型路径
     */
	@Column(name = "Create_dir", unique = false, nullable = true, insertable = true, updatable = true)
    protected String createDir;

    /**
     * 新模型路径
     */
    @Column(name = "train_file_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String trainFileId;
    
	/**
     * 工作空间
     */
	@Column(name = "Work_space", unique = false, nullable = true, insertable = true, updatable = true)
    protected String workSpace;
    
	/**
     * 微调信息
     */
	@Column(name = "Plan_msg", unique = false, nullable = true, insertable = true, updatable = true)
    protected String planMsg;
    
	/**
     * pod名字
     */
	@Column(name = "Pod_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String podName;
    
	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;
    
	/**
     * 花费时间
     */
	@Column(name = "Cost_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected String costTime;
    
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
     * 生成路径
     */
	@Column(name = "Create_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String createPath;
    
	/**
     * 创建人名称
     */
	@Column(name = "Creator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creatorName;
    /**
     * 自动启动
     */
    @Column(name = "auto_start_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer autoStartFlg;

	/**
     * 主键
     */
	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;
    
	/**
     * 运行状态
     */
	@Column(name = "Do_status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer doStatus;

    /**
     * 运行状态
     */
    @Column(name = "start_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date startTime;
    /**
     * 微调计划主键
     */
    @Column(name = "tun_plan_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tunPlanId;
    /**
     * 微调计划名称
     */
    @Column(name = "tun_plan_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tunPlanName;

    public String getUsedCardNos() {
        return usedCardNos;
    }

    public void setUsedCardNos(String usedCardNos) {
        this.usedCardNos = usedCardNos;
    }

    public String getExecGpuCard() {
        return execGpuCard;
    }

    public void setExecGpuCard(String execGpuCard) {
        this.execGpuCard = execGpuCard;
    }

    public String getBaseModelId() {
        return baseModelId;
    }

    public void setBaseModelId(String baseModelId) {
        this.baseModelId = baseModelId;
    }

    public String getEvaTotalCount() {
        return evaTotalCount;
    }

    public void setEvaTotalCount(String evaTotalCount) {
        this.evaTotalCount = evaTotalCount;
    }

    public String getEvaTotalScore() {
        return evaTotalScore;
    }

    public void setEvaTotalScore(String evaTotalScore) {
        this.evaTotalScore = evaTotalScore;
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

    public String getHostFilesPath() {
        return hostFilesPath;
    }

    public void setHostFilesPath(String hostFilesPath) {
        this.hostFilesPath = hostFilesPath;
    }

    public String getMountFilesPath() {
        return mountFilesPath;
    }

    public void setMountFilesPath(String mountFilesPath) {
        this.mountFilesPath = mountFilesPath;
    }

    public String getNameFilesPath() {
        return nameFilesPath;
    }

    public void setNameFilesPath(String nameFilesPath) {
        this.nameFilesPath = nameFilesPath;
    }

    public Integer getDoWay() {
        return doWay;
    }

    public void setDoWay(Integer doWay) {
        this.doWay = doWay;
    }

    public String getServerId() {
        return serverId;
    }

    public String getRealServerId(){
        if (serverId.split(",").length > 1){
            //当多机多卡任务时 一般第一个服务器就是主服务器 一般只需要选择查看主服务器的日志即可 大部分信息都在主节点
            JSONArray jsonArray = JSONArray.parseArray(execGpuCard);
            return jsonArray.getJSONObject(0).getJSONObject("k8sServerConfEntity").getString("id");
        }
        return serverId;
    }

    public Integer getRlhfWay() {
        return rlhfWay;
    }

    public void setRlhfWay(Integer rlhfWay) {
        this.rlhfWay = rlhfWay;
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

    public Integer getDoMethod() {
        return doMethod;
    }

    public void setDoMethod(Integer doMethod) {
        this.doMethod = doMethod;
    }

    public Integer getDoFrame() {
        return doFrame;
    }

    public void setDoFrame(Integer doFrame) {
        this.doFrame = doFrame;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getTunPlanName() {
        return tunPlanName;
    }

    public void setTunPlanName(String tunPlanName) {
        this.tunPlanName = tunPlanName;
    }

    public Integer getAutoStartFlg() {
        return autoStartFlg;
    }

    public void setAutoStartFlg(Integer autoStartFlg) {
        this.autoStartFlg = autoStartFlg;
    }

    public String getModelId() {
        return modelId;
    }

    public String getEvaluatioInstanId() {
        return evaluatioInstanId;
    }

    public void setEvaluatioInstanId(String evaluatioInstanId) {
        this.evaluatioInstanId = evaluatioInstanId;
    }

    public String getEvaluatioInstanName() {
        return evaluatioInstanName;
    }

    public void setEvaluatioInstanName(String evaluatioInstanName) {
        this.evaluatioInstanName = evaluatioInstanName;
    }

    public String getModelAppId() {
        return modelAppId;
    }

    public void setModelAppId(String modelAppId) {
        this.modelAppId = modelAppId;
    }

    public String getModelAPPName() {
        return modelAPPName;
    }

    public void setModelAPPName(String modelAPPName) {
        this.modelAPPName = modelAPPName;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getTunPlanId() {
        return tunPlanId;
    }

    public void setTunPlanId(String tunPlanId) {
        this.tunPlanId = tunPlanId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getDoParams() {
        return doParams;
    }

    public void setDoParams(String doParams) {
        this.doParams = doParams;
    }

    public Integer getShelvesStatus() {
        return shelvesStatus;
    }

    public void setShelvesStatus(Integer shelvesStatus) {
        this.shelvesStatus = shelvesStatus;
    }

    /**
     * 获取模型文件名字
     */
    public String getModelFile() {
        return modelFile;
    }

	/**
     * 设置模型文件名字
     */
    public void setModelFile(String modelFile) {
        this.modelFile = modelFile;
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
     * 获取工作空间
     */
    public String getWorkSpace() {
        return workSpace;
    }

	/**
     * 设置工作空间
     */
    public void setWorkSpace(String workSpace) {
        this.workSpace = workSpace;
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
     * 获取pod名字
     */
    public String getPodName() {
        return podName;
    }

	/**
     * 设置pod名字
     */
    public void setPodName(String podName) {
        this.podName = podName;
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
     * 获取花费时间
     */
    public String getCostTime() {
        return costTime;
    }

	/**
     * 设置花费时间
     */
    public void setCostTime(String costTime) {
        this.costTime = costTime;
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

    public String getTrainFileId() {
        return trainFileId;
    }

    public void setTrainFileId(String trainFileId) {
        this.trainFileId = trainFileId;
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
     * 获取生成路径
     */
    public String getCreatePath() {
        return createPath;
    }

	/**
     * 设置生成路径
     */
    public void setCreatePath(String createPath) {
        this.createPath = createPath;
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
     * 获取运行状态
     */
    public Integer getDoStatus() {
        return doStatus;
    }

	/**
     * 设置运行状态
     */
    public void setDoStatus(Integer doStatus) {
        this.doStatus = doStatus;
    }


    public List<MultipleServersConfig> getMultipleServersConfigs() {
        return multipleServersConfigs;
    }

    public void setMultipleServersConfigs(List<MultipleServersConfig> multipleServersConfigs) {
        this.multipleServersConfigs = multipleServersConfigs;
    }
}
