package com.badou.project.maas.modelwarehouse.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.badou.project.gpucalc.GpuCalcCardModel;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;
import com.badou.project.server.service.IK8sServerConfService;
import com.badou.tools.common.util.SpringHelper;
import com.badou.tools.common.util.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

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
 * @date 2024-08-28 11:30:33.707
 *  模型仓库类
 */
@Entity
@Table(name = "maas_model_warehouse")
public class ModelWarehouseEntity extends AppBaseEntity {

    /**
     * 模型市场文根 用来下载ModelScope模型使用
     */
    @Column(name = "custom_gpu_card", unique = false, nullable = true, insertable = true, updatable = true)
    protected String customGpuCard;

    /**
     * 模型市场文根 用来下载ModelScope模型使用
     */
    @Column(name = "custom_gpu_card_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String customGpuCardName;

    /**
     * 模型市场文根 用来下载ModelScope模型使用
     */
    @Column(name = "model_scope_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelScopePath;

    /**
     * 框架版本
     */
    @Column(name = "frame_version", unique = false, nullable = true, insertable = true, updatable = true)
    protected String frameVersion;

    /**
     * 上下文大小
     */
    @Column(name = "content_length", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double contentLength;

    /**
     * 名称
     */
    @Column(name = "up_status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer upStatus;

    /**
     * 名称
     */
    @Column(name = "modelName", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelName;

    /**
     * 服务器显卡模式/算力配置
     */
    @Column(name = "server_gpu_mode", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer serverGpuMode;

    /**
     * GPU数量
     */
    @Column(name = "gpu_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer gpuCount;

    /**
     * 部署框架
     */
    @Column(name = "deploy_frame", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer deployFrame;

    /**
     * 来源
     */
    @Column(name = "source", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer source;

    /**
     * 基础模型主键
     */
    @Column(name = "base_model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String baseModelId;

    /**
     * 父主键
     */
    @Column(name = "parent_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String parentId;

    /**
     * 发布信息
     */
    @Column(name = "pub_msg", unique = false, nullable = true, insertable = true, updatable = true)
    protected String pubMsg;

    /**
     * 发布版本
     */
    @Column(name = "pub_version", unique = false, nullable = true, insertable = true, updatable = true)
    protected String pubVersion;

    /**
     * 参数量(b)
     */
    @Column(name = "model_params_size", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double modelParamsSize;

    /**
     * 量化方法
     */
    @Column(name = "quantization_method", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer quantizationMethod;

    /**
     * 量化位数(bit)
     */
    @Column(name = "quantization_bits", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double quantizationBits;

    /**
     * 微调任务主键
     */
    @Column(name = "tun_model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tunModelId;

    /**
     * 微调需要GPU显存大小
     */
    @Column(name = "tun_need_gpum", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer tunNeedGpum;
    /**
     * 启动需要GPU显存大小
     */
    @Column(name = "start_need_gpum", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer startNeedGpum;
    /**
     * 模型厂家
     */
    @Column(name = "model_provider", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelProvider;

	/**
     * 执行服务器
     */
	@Column(name = "Server_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverName;

	/**
     * 创建人
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
     * 描述
     */
	@Column(name = "Description", unique = false, nullable = true, insertable = true, updatable = true)
    protected String description;

	/**
     * 执行服务器
     */
	@Column(name = "Server_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverId;

	/**
     * 类型
     */
	@Column(name = "Type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer type;

	/**
     * 模型路径
     */
	@Column(name = "Path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String path;

	/**
     * 更新时间
     */
    @Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;

	/**
     * 总大小
     */
	@Column(name = "Size", unique = false, nullable = true, insertable = true, updatable = true)
    protected String size;

	/**
     * 收藏数
     */
	@Column(name = "Favorites_num", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer favoritesNum;

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
     * 创建人主键
     */
	@Column(name = "Creator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creatorName;

	/**
     * 部署次数
     */
	@Column(name = "Deploy_num", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer deployNum;

	/**
     * 主键
     */
	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;

	/**
     * 标签
     */
	@Column(name = "Tag", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tag;

    /**
     * 端口
     */
    @Column(name = "port", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer port;

    @Transient
    //是否清理旧任务 默认不处理 需要明确客户点击确认清理/覆盖才执行
    protected boolean checkSameFlag = false;
    @Transient
    protected ModelSyncTaskEntity modelSyncTaskEntity;

    public String getModelScopePath() {
        return modelScopePath;
    }

    public void setModelScopePath(String modelScopePath) {
        this.modelScopePath = modelScopePath;
    }

    public String getFrameVersion() {
        return frameVersion;
    }

    public void setFrameVersion(String frameVersion) {
        this.frameVersion = frameVersion;
    }

    public boolean isCheckSameFlag() {
        return checkSameFlag;
    }

    public void setCheckSameFlag(boolean checkSameFlag) {
        this.checkSameFlag = checkSameFlag;
    }

    public Double getContentLength() {
        return contentLength;
    }

    public void setContentLength(Double contentLength) {
        this.contentLength = StringHandlerUtil.convertToK(contentLength);
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getUpStatus() {
        return upStatus;
    }

    public void setUpStatus(Integer upStatus) {
        this.upStatus = upStatus;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getServerGpuMode() {
        return serverGpuMode;
    }

    public void setServerGpuMode(Integer serverGpuMode) {
        this.serverGpuMode = serverGpuMode;
    }

    public Integer getGpuCount() {
        return gpuCount;
    }

    public void setGpuCount(Integer gpuCount) {
        this.gpuCount = gpuCount;
    }

    public Integer getDeployFrame() {
        return deployFrame;
    }

    public void setDeployFrame(Integer deployFrame) {
        this.deployFrame = deployFrame;
    }

    public String getBaseModelId() {
        return baseModelId;
    }

    public void setBaseModelId(String baseModelId) {
        this.baseModelId = baseModelId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getPubMsg() {
        return pubMsg;
    }

    public void setPubMsg(String pubMsg) {
        this.pubMsg = pubMsg;
    }

    public String getPubVersion() {
        return pubVersion;
    }

    public void setPubVersion(String pubVersion) {
        this.pubVersion = pubVersion;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public ModelSyncTaskEntity getModelSyncTaskEntity() {
        return modelSyncTaskEntity;
    }

    public void setModelSyncTaskEntity(ModelSyncTaskEntity modelSyncTaskEntity) {
        this.modelSyncTaskEntity = modelSyncTaskEntity;
    }

    public Double getModelParamsSize() {
        return modelParamsSize;
    }

    public void setModelParamsSize(Double modelParamsSize) {
        this.modelParamsSize = modelParamsSize;
    }

    public Integer getQuantizationMethod() {
        return quantizationMethod;
    }

    public void setQuantizationMethod(Integer quantizationMethod) {
        this.quantizationMethod = quantizationMethod;
    }

    public Double getQuantizationBits() {
        return quantizationBits;
    }

    public void setQuantizationBits(Double quantizationBits) {
        this.quantizationBits = quantizationBits;
    }

    public String getTunModelId() {
        return tunModelId;
    }

    public void setTunModelId(String tunModelId) {
        this.tunModelId = tunModelId;
    }

    public String getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(String modelProvider) {
        this.modelProvider = modelProvider;
    }

    public Integer getTunNeedGpum() {
        return tunNeedGpum;
    }

    public void setTunNeedGpum(Integer tunNeedGpum) {
        this.tunNeedGpum = tunNeedGpum;
    }

    public Integer getStartNeedGpum() {
        return startNeedGpum;
    }

    public void setStartNeedGpum(Integer startNeedGpum) {
        this.startNeedGpum = startNeedGpum;
    }

    /**
     * 获取执行服务器
     */
    public String getServerName() {
        return serverName;
    }

	/**
     * 设置执行服务器
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    /**
     * 获取创建人
     */
    public String getCreator() {
        return creator;
    }

	/**
     * 设置创建人
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
     * 获取描述
     */
    public String getDescription() {
        return description;
    }

	/**
     * 设置描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取执行服务器
     */
    public String getServerId() {
        return serverId;
    }

	/**
     * 设置执行服务器
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    /**
     * 获取类型
     */
    public Integer getType() {
        return type;
    }

	/**
     * 设置类型
     */
    public void setType(Integer type) {
        this.type = type;
    }
    /**
     * 获取模型路径
     */
    public String getPath() {
        KubernetesApiClient cacheK8sClient = FileControllerService.getCacheK8sClient(serverId);
        return path.replace("/home",cacheK8sClient.getServer().getMainLocalPath()==null?"/home":cacheK8sClient.getServer().getMainLocalPath());
    }

	/**
     * 设置模型路径
     */
    public void setPath(String path) {
        this.path = path;
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
     * 获取总大小
     */
    public String getSize() {
        return size;
    }

	/**
     * 设置总大小
     */
    public void setSize(String size) {
        this.size = size;
    }
    /**
     * 获取收藏数
     */
    public Integer getFavoritesNum() {
        return favoritesNum;
    }

	/**
     * 设置收藏数
     */
    public void setFavoritesNum(Integer favoritesNum) {
        this.favoritesNum = favoritesNum;
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
     * 获取创建人主键
     */
    public String getCreatorName() {
        return creatorName;
    }

	/**
     * 设置创建人主键
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    /**
     * 获取部署次数
     */
    public Integer getDeployNum() {
        return deployNum;
    }

	/**
     * 设置部署次数
     */
    public void setDeployNum(Integer deployNum) {
        this.deployNum = deployNum;
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
     * 获取标签
     */
    public String getTag() {
        return tag;
    }

	/**
     * 设置标签
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCustomGpuCard() {
        return customGpuCard;
    }

    public void setCustomGpuCard(String customGpuCard) {
        this.customGpuCard = customGpuCard;
    }

    public String getCustomGpuCardName() {
        return customGpuCardName;
    }

    public void setCustomGpuCardName(String customGpuCardName) {
        this.customGpuCardName = customGpuCardName;
    }

}
