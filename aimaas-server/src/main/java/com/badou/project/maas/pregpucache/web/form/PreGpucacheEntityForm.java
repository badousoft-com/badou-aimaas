package  com.badou.project.maas.pregpucache.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.pregpucache.model.PreGpucacheEntityEntity;

/**
 * @author badousoft
 * @date 2024-09-24 10:45:48.232
 *  模型GPU显存预估form
 */
public class PreGpucacheEntityForm extends BaseStrutsEntityForm<PreGpucacheEntityEntity> {

	/**
     * 训练环境
     */
    protected String  serverName;
	/**
     * 创建人
     */
    protected String  creator;
	/**
     * 微调参数设置
     */
    protected String  doParams;
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
     * 模型
     */
    protected String  modelId;
	/**
     * 训练环境
     */
    protected String  serverId;
	/**
     * 更新时间
     */
    protected Date  updateTime;
	/**
     * 微调框架
     */
    protected Integer  tunFrame;
	/**
     * 模型名称
     */
    protected String  modelName;
	/**
     * gpu个数
     */
    protected Integer  gpuCount;
	/**
     * 微调方式
     */
    protected Integer  doWay;
	/**
     * 更新人
     */
    protected String  updator;
	/**
     * 创建人名字
     */
    protected String  creatorName;
	/**
     * 预估需要显存(GB)
     */
    protected Integer  preGpucache;

        /**
     *  获取训练环境
     */
    public String getServerName() {
        return serverName;
    }

	/**
     *  设置训练环境
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    /**
     *  获取创建人
     */
    public String getCreator() {
        return creator;
    }

	/**
     *  设置创建人
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }
    /**
     *  获取微调参数设置
     */
    public String getDoParams() {
        return doParams;
    }

	/**
     *  设置微调参数设置
     */
    public void setDoParams(String doParams) {
        this.doParams = doParams;
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
     *  获取模型
     */
    public String getModelId() {
        return modelId;
    }

	/**
     *  设置模型
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    /**
     *  获取训练环境
     */
    public String getServerId() {
        return serverId;
    }

	/**
     *  设置训练环境
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
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
     *  获取微调框架
     */
    public Integer getTunFrame() {
        return tunFrame;
    }

	/**
     *  设置微调框架
     */
    public void setTunFrame(Integer tunFrame) {
        this.tunFrame = tunFrame;
    }
    /**
     *  获取模型名称
     */
    public String getModelName() {
        return modelName;
    }

	/**
     *  设置模型名称
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
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
     *  获取更新人
     */
    public String getUpdator() {
        return updator;
    }

	/**
     *  设置更新人
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
     *  获取预估需要显存(GB)
     */
    public Integer getPreGpucache() {
        return preGpucache;
    }

	/**
     *  设置预估需要显存(GB)
     */
    public void setPreGpucache(Integer preGpucache) {
        this.preGpucache = preGpucache;
    }
}