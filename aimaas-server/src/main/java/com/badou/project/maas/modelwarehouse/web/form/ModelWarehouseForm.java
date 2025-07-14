package  com.badou.project.maas.modelwarehouse.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;

/**
 * @author badousoft
 * @date 2024-08-28 11:30:33.707
 *  模型仓库form
 */
public class ModelWarehouseForm extends BaseStrutsEntityForm<ModelWarehouseEntity> {

	/**
     * 执行服务器
     */
    protected String  serverName;
	/**
     * 创建人
     */
    protected String  creator;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 更新人名字
     */
    protected String  updatorName;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 描述
     */
    protected String  description;
	/**
     * 来源
     */
    protected Integer  source;
	/**
     * 执行服务器
     */
    protected String  serverId;
	/**
     * 类型
     */
    protected Integer  type;
	/**
     * 模型路径
     */
    protected String  path;
	/**
     * 更新时间
     */
    protected Date  updateTime;
	/**
     * 总大小
     */
    protected String  size;
	/**
     * 收藏数
     */
    protected Integer  favoritesNum;
	/**
     * 名称
     */
    protected String  name;
	/**
     * 更新人主键
     */
    protected String  updator;
	/**
     * 创建人主键
     */
    protected String  creatorName;
	/**
     * 部署次数
     */
    protected Integer  deployNum;
	/**
     * 主键
     */
    protected String  id;
	/**
     * 标签
     */
    protected String  tag;

        /**
     *  获取执行服务器
     */
    public String getServerName() {
        return serverName;
    }

	/**
     *  设置执行服务器
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
     *  获取描述
     */
    public String getDescription() {
        return description;
    }

	/**
     *  设置描述
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     *  获取来源
     */
    public Integer getSource() {
        return source;
    }

	/**
     *  设置来源
     */
    public void setSource(Integer source) {
        this.source = source;
    }
    /**
     *  获取执行服务器
     */
    public String getServerId() {
        return serverId;
    }

	/**
     *  设置执行服务器
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    /**
     *  获取类型
     */
    public Integer getType() {
        return type;
    }

	/**
     *  设置类型
     */
    public void setType(Integer type) {
        this.type = type;
    }
    /**
     *  获取模型路径
     */
    public String getPath() {
        return path;
    }

	/**
     *  设置模型路径
     */
    public void setPath(String path) {
        this.path = path;
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
     *  获取总大小
     */
    public String getSize() {
        return size;
    }

	/**
     *  设置总大小
     */
    public void setSize(String size) {
        this.size = size;
    }
    /**
     *  获取收藏数
     */
    public Integer getFavoritesNum() {
        return favoritesNum;
    }

	/**
     *  设置收藏数
     */
    public void setFavoritesNum(Integer favoritesNum) {
        this.favoritesNum = favoritesNum;
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
     *  获取创建人主键
     */
    public String getCreatorName() {
        return creatorName;
    }

	/**
     *  设置创建人主键
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    /**
     *  获取部署次数
     */
    public Integer getDeployNum() {
        return deployNum;
    }

	/**
     *  设置部署次数
     */
    public void setDeployNum(Integer deployNum) {
        this.deployNum = deployNum;
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
     *  获取标签
     */
    public String getTag() {
        return tag;
    }

	/**
     *  设置标签
     */
    public void setTag(String tag) {
        this.tag = tag;
    }
}