package com.badou.project.maas.tuningprogramn.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-05-16 17:55:36.368
 * @todo 微调计划管理类
 */
@Entity
@Table(name = "maas_fine_tuning_program")
public class TuningProgramnEntity extends AppBaseEntity {

    /**
     * 计划执行的模型
     */
    @Column(name = "do_models", unique = false, nullable = true, insertable = true, updatable = true)
    protected String doModels;

    /**
     * 微调任务数
     */
    @Column(name = "task_num", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer taskNum;

	/**
     * 评价策略(0.评分模式 相似度模式)
     */
	@Column(name = "Evaluation_strategy", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer evaluationStrategy;
    
	/**
     * 训练环境
     */
	@Column(name = "Server_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverName;
    
	/**
     * 微调参数
     */
	@Column(name = "Do_params", unique = false, nullable = true, insertable = true, updatable = true)
    protected String doParams;
    
	/**
     * 计划编码
     */
	@Column(name = "Code", unique = false, nullable = true, insertable = true, updatable = true)
    protected String code;
    
	/**
     * 微调方案
     */
	@Column(name = "Tuning_plan_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tuningPlanName;
    
	/**
     * 自定义脚本路径
     */
	@Column(name = "Custon_sh", unique = false, nullable = true, insertable = true, updatable = true)
    protected String custonSh;
    
	/**
     * 验证策略
     */
	@Column(name = "Validation_strategy", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer validationStrategy;
    
	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;
    
	/**
     * 是否开启自动评价
     */
	@Column(name = "Evaluation_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer evaluationFlg;
    
	/**
     * 随机抽的比例(比如随机抽样50% 随机抽30% 随机抽10% 达到则停止评价生成评价报告)
     */
	@Column(name = "Random_proportion", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer randomProportion;
    
	/**
     * 微调方式
     */
	@Column(name = "Do_way", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer doWay;
    
	/**
     * 显卡最小温度
     */
	@Column(name = "Gpu_min_temperature", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double gpuMinTemperature;
    
	/**
     * 是否自动启动
     */
	@Column(name = "Auto_start_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer autoStartFlg;
    
	/**
     * 显卡最大温度
     */
	@Column(name = "Gpu_max_temperature", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double gpuMaxTemperature;
    
	/**
     * 主键
     */
	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;
    
	/**
     * 脚本路径
     */
	@Column(name = "Sh_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String shPath;
    
	/**
     * 微调方案主键
     */
	@Column(name = "Tuning_plan_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tuningPlanId;
    
	/**
     * 模型输出目录
     */
	@Column(name = "Model_out_dir", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelOutDir;
    
	/**
     * 模型应用主键
     */
	@Column(name = "Model_app_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelAppId;
    
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
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;
    
	/**
     * 显卡平均温度
     */
	@Column(name = "Gpu_average_temperature", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double gpuAverageTemperature;
    
	/**
     * 创建时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date createTime;
    
	/**
     * 微调操作方式
     */
	@Column(name = "Do_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer doType;
    
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
     * 模型应用名称
     */
	@Column(name = "Model_app_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelAppName;
    
	/**
     * 预估时长
     */
	@Column(name = "Estimated_duration", unique = false, nullable = true, insertable = true, updatable = true)
    protected String estimatedDuration;
    
	/**
     * 训练环境主键
     */
	@Column(name = "Server_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverId;
    
	/**
     * 是否注册到网关
     */
	@Column(name = "Gateway_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer gatewayFlg;
    
	/**
     * gpu个数
     */
	@Column(name = "Gpu_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer gpuCount;
    
	/**
     * 执行时间
     */
	@Column(name = "Exec_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date execTime;
    
	/**
     * 计划名称
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
     * 评价模型名字
     */
	@Column(name = "Evaluation_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String evaluationName;

    public void setTaskNum(Integer taskNum) {
        this.taskNum = taskNum;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getTaskNum() {
        return taskNum;
    }

    public String getCode() {
        return code;
    }

    /**
     * 获取评价策略(0.评分模式 相似度模式)
     */
    public Integer getEvaluationStrategy() {
        return evaluationStrategy;
    }

	/**
     * 设置评价策略(0.评分模式 相似度模式)
     */
    public void setEvaluationStrategy(Integer evaluationStrategy) {
        this.evaluationStrategy = evaluationStrategy;
    }
    /**
     * 获取训练环境
     */
    public String getServerName() {
        return serverName;
    }

	/**
     * 设置训练环境
     */
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
//    /**
////     * 获取计划编码
////     */
////    public String getCode() {
////        return code;
////    }
////
////	/**
////     * 设置计划编码
////     */
////    public void setCode(String code) {
////        this.code = code;
////    }
    /**
     * 获取微调方案
     */
    public String getTuningPlanName() {
        return tuningPlanName;
    }

	/**
     * 设置微调方案
     */
    public void setTuningPlanName(String tuningPlanName) {
        this.tuningPlanName = tuningPlanName;
    }

    public String getDoModels() {
        return doModels;
    }

    public void setDoModels(String doModels) {
        this.doModels = doModels;
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
     * 获取验证策略
     */
    public Integer getValidationStrategy() {
        return validationStrategy;
    }

	/**
     * 设置验证策略
     */
    public void setValidationStrategy(Integer validationStrategy) {
        this.validationStrategy = validationStrategy;
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
     * 获取是否开启自动评价
     */
    public Integer getEvaluationFlg() {
        return evaluationFlg;
    }

	/**
     * 设置是否开启自动评价
     */
    public void setEvaluationFlg(Integer evaluationFlg) {
        this.evaluationFlg = evaluationFlg;
    }
    /**
     * 获取随机抽的比例(比如随机抽样50% 随机抽30% 随机抽10% 达到则停止评价生成评价报告)
     */
    public Integer getRandomProportion() {
        return randomProportion;
    }

	/**
     * 设置随机抽的比例(比如随机抽样50% 随机抽30% 随机抽10% 达到则停止评价生成评价报告)
     */
    public void setRandomProportion(Integer randomProportion) {
        this.randomProportion = randomProportion;
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
     * 获取显卡最小温度
     */
    public Double getGpuMinTemperature() {
        return gpuMinTemperature;
    }

	/**
     * 设置显卡最小温度
     */
    public void setGpuMinTemperature(Double gpuMinTemperature) {
        this.gpuMinTemperature = gpuMinTemperature;
    }
    /**
     * 获取是否自动启动
     */
    public Integer getAutoStartFlg() {
        return autoStartFlg;
    }

	/**
     * 设置是否自动启动
     */
    public void setAutoStartFlg(Integer autoStartFlg) {
        this.autoStartFlg = autoStartFlg;
    }
    /**
     * 获取显卡最大温度
     */
    public Double getGpuMaxTemperature() {
        return gpuMaxTemperature;
    }

	/**
     * 设置显卡最大温度
     */
    public void setGpuMaxTemperature(Double gpuMaxTemperature) {
        this.gpuMaxTemperature = gpuMaxTemperature;
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
     * 获取微调方案主键
     */
    public String getTuningPlanId() {
        return tuningPlanId;
    }

	/**
     * 设置微调方案主键
     */
    public void setTuningPlanId(String tuningPlanId) {
        this.tuningPlanId = tuningPlanId;
    }
    /**
     * 获取模型输出目录
     */
    public String getModelOutDir() {
        return modelOutDir;
    }

	/**
     * 设置模型输出目录
     */
    public void setModelOutDir(String modelOutDir) {
        this.modelOutDir = modelOutDir;
    }
    /**
     * 获取模型应用主键
     */
    public String getModelAppId() {
        return modelAppId;
    }

	/**
     * 设置模型应用主键
     */
    public void setModelAppId(String modelAppId) {
        this.modelAppId = modelAppId;
    }
    /**
     * 获取最大评价条目数
     */
    public Integer getMaxTrainCount() {
        return maxTrainCount;
    }

	/**
     * 设置最大评价条目数
     */
    public void setMaxTrainCount(Integer maxTrainCount) {
        this.maxTrainCount = maxTrainCount;
    }
    /**
     * 获取评价模型主键
     */
    public String getEvaluationId() {
        return evaluationId;
    }

	/**
     * 设置评价模型主键
     */
    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
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
     * 获取显卡平均温度
     */
    public Double getGpuAverageTemperature() {
        return gpuAverageTemperature;
    }

	/**
     * 设置显卡平均温度
     */
    public void setGpuAverageTemperature(Double gpuAverageTemperature) {
        this.gpuAverageTemperature = gpuAverageTemperature;
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
     * 获取微调操作方式
     */
    public Integer getDoType() {
        return doType;
    }

	/**
     * 设置微调操作方式
     */
    public void setDoType(Integer doType) {
        this.doType = doType;
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
     * 获取模型应用名称
     */
    public String getModelAppName() {
        return modelAppName;
    }

	/**
     * 设置模型应用名称
     */
    public void setModelAppName(String modelAppName) {
        this.modelAppName = modelAppName;
    }
    /**
     * 获取预估时长
     */
    public String getEstimatedDuration() {
        return estimatedDuration;
    }

	/**
     * 设置预估时长
     */
    public void setEstimatedDuration(String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }
    /**
     * 获取训练环境主键
     */
    public String getServerId() {
        return serverId;
    }

	/**
     * 设置训练环境主键
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    /**
     * 获取是否注册到网关
     */
    public Integer getGatewayFlg() {
        return gatewayFlg;
    }

	/**
     * 设置是否注册到网关
     */
    public void setGatewayFlg(Integer gatewayFlg) {
        this.gatewayFlg = gatewayFlg;
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
     * 获取执行时间
     */
    public Date getExecTime() {
        return execTime;
    }

	/**
     * 设置执行时间
     */
    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }
    /**
     * 获取计划名称
     */
    public String getName() {
        return name;
    }

	/**
     * 设置计划名称
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
     * 获取评价模型名字
     */
    public String getEvaluationName() {
        return evaluationName;
    }

	/**
     * 设置评价模型名字
     */
    public void setEvaluationName(String evaluationName) {
        this.evaluationName = evaluationName;
    }
}
