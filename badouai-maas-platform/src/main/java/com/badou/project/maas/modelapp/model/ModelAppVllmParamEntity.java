package  com.badou.project.maas.modelapp.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2025-03-10 18:23:28.765
 *  模型应用VLLM参数类
 */
@Entity
@Table(name = "maas_model_app_vllm_params")
public class ModelAppVllmParamEntity extends AppBaseEntity {

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
     * 组别编码
     */
	@Column(name = "Group_code", unique = false, nullable = true, insertable = true, updatable = true)
    protected String groupCode;

	/**
     * 更新人名字
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;

	/**
     * 组别名称
     */
	@Column(name = "Group_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String groupName;

	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;

	/**
     * 组别序号
     */
	@Column(name = "Group_no", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer groupNo;

	/**
     * VLLM参数模板主键
     */
	@Column(name = "Params_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String paramsId;

	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;

	/**
     * 组别优先级
     */
	@Column(name = "Group_priority", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer groupPriority;

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
     * 创建人名字
     */
	@Column(name = "Creator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creatorName;

	/**
     * 模型仓库主键
     */
	@Column(name = "Warehost_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String warehostId;

	/**
     * 值
     */
	@Column(name = "Value", unique = false, nullable = true, insertable = true, updatable = true)
    protected String value;

	/**
     * 解释
     */
	@Column(name = "Explain_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String explainName;

	/**
     * 模型应用主键
     */
	@Column(name = "Model_app_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelAppId;


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
     * 获取组别编码
     */
    public String getGroupCode() {
        return groupCode;
    }

	/**
     * 设置组别编码
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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
     * 获取组别名称
     */
    public String getGroupName() {
        return groupName;
    }

	/**
     * 设置组别名称
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
     * 获取组别序号
     */
    public Integer getGroupNo() {
        return groupNo;
    }

	/**
     * 设置组别序号
     */
    public void setGroupNo(Integer groupNo) {
        this.groupNo = groupNo;
    }
    /**
     * 获取VLLM参数模板主键
     */
    public String getParamsId() {
        return paramsId;
    }

	/**
     * 设置VLLM参数模板主键
     */
    public void setParamsId(String paramsId) {
        this.paramsId = paramsId;
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
     * 获取组别优先级
     */
    public Integer getGroupPriority() {
        return groupPriority;
    }

	/**
     * 设置组别优先级
     */
    public void setGroupPriority(Integer groupPriority) {
        this.groupPriority = groupPriority;
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
     * 获取模型仓库主键
     */
    public String getWarehostId() {
        return warehostId;
    }

	/**
     * 设置模型仓库主键
     */
    public void setWarehostId(String warehostId) {
        this.warehostId = warehostId;
    }
    /**
     * 获取值
     */
    public String getValue() {
        return value;
    }

	/**
     * 设置值
     */
    public void setValue(String value) {
        this.value = value;
    }
    /**
     * 获取解释
     */
    public String getExplainName() {
        return explainName;
    }

	/**
     * 设置解释
     */
    public void setExplainName(String explainName) {
        this.explainName = explainName;
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
}
