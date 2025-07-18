package  com.badou.project.maas.modelapp.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.modelapp.model.ModelAppEntity;

/**
 * @author badousoft
 * @date 2024-05-27 11:33:46.513
 * @todo 模型应用管理form
 */
public class ModelAppForm extends BaseStrutsEntityForm<ModelAppEntity> {

	/**
     * 创建人主键
     */
    protected String  creator;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 更新人名称
     */
    protected String  updatorName;
	/**
     * 用途
     */
    protected String  useWay;
	/**
     * 逻辑删除标识
     */
    protected Integer  flgDeleted;
	/**
     * 标识名称
     */
    protected String  defineName;
	/**
     * 模型
     */
    protected String  modelId;
	/**
     * 工作目录
     */
    protected String  workPath;
	/**
     * 更新时间
     */
    protected Date  updateTime;
	/**
     * 模型名称
     */
    protected String  modelName;
	/**
     * gpu个数
     */
    protected Integer  gpuCount;
	/**
     * 名称
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
     * 主键
     */
    protected String  id;
	/**
     * 部署状态
     */
    protected Integer  status;

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
     *  获取用途
     */
    public String getUseWay() {
        return useWay;
    }

	/**
     *  设置用途
     */
    public void setUseWay(String useWay) {
        this.useWay = useWay;
    }
    /**
     *  获取逻辑删除标识
     */
    public Integer getFlgDeleted() {
        return flgDeleted;
    }

	/**
     *  设置逻辑删除标识
     */
    public void setFlgDeleted(Integer flgDeleted) {
        this.flgDeleted = flgDeleted;
    }
    /**
     *  获取标识名称
     */
    public String getDefineName() {
        return defineName;
    }

	/**
     *  设置标识名称
     */
    public void setDefineName(String defineName) {
        this.defineName = defineName;
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
     *  获取工作目录
     */
    public String getWorkPath() {
        return workPath;
    }

	/**
     *  设置工作目录
     */
    public void setWorkPath(String workPath) {
        this.workPath = workPath;
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
     *  获取部署状态
     */
    public Integer getStatus() {
        return status;
    }

	/**
     *  设置部署状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}