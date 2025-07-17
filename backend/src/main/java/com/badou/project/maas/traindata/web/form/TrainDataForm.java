package  com.badou.project.maas.traindata.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.traindata.model.TrainDataEntity;

/**
 * @author badousoft
 * @date 2024-04-18 09:34:05.295
 * @todo 训练集管理form
 */
public class TrainDataForm extends BaseStrutsEntityForm<TrainDataEntity> {

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
     * 更新人名称
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
     * 模型主键
     */
    protected String  modelIds;
	/**
     * 可见权限
     */
    protected Integer  roleAuth;
	/**
     * 生成路径
     */
    protected String  path;
	/**
     * 使用次数
     */
    protected Integer  useCount;
	/**
     * 模型名称
     */
    protected String  midelNames;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
	/**
     * 用途
     */
    protected String  doWay;
	/**
     * 介绍
     */
    protected String  intro;
	/**
     * 训练集名称
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
     * 标签
     */
    protected String  tag;

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
     *  获取模型主键
     */
    public String getModelIds() {
        return modelIds;
    }

	/**
     *  设置模型主键
     */
    public void setModelIds(String modelIds) {
        this.modelIds = modelIds;
    }
    /**
     *  获取可见权限
     */
    public Integer getRoleAuth() {
        return roleAuth;
    }

	/**
     *  设置可见权限
     */
    public void setRoleAuth(Integer roleAuth) {
        this.roleAuth = roleAuth;
    }
    /**
     *  获取生成路径
     */
    public String getPath() {
        return path;
    }

	/**
     *  设置生成路径
     */
    public void setPath(String path) {
        this.path = path;
    }
    /**
     *  获取使用次数
     */
    public Integer getUseCount() {
        return useCount;
    }

	/**
     *  设置使用次数
     */
    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }
    /**
     *  获取模型名称
     */
    public String getMidelNames() {
        return midelNames;
    }

	/**
     *  设置模型名称
     */
    public void setMidelNames(String midelNames) {
        this.midelNames = midelNames;
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
     *  获取用途
     */
    public String getDoWay() {
        return doWay;
    }

	/**
     *  设置用途
     */
    public void setDoWay(String doWay) {
        this.doWay = doWay;
    }
    /**
     *  获取介绍
     */
    public String getIntro() {
        return intro;
    }

	/**
     *  设置介绍
     */
    public void setIntro(String intro) {
        this.intro = intro;
    }
    /**
     *  获取训练集名称
     */
    public String getName() {
        return name;
    }

	/**
     *  设置训练集名称
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