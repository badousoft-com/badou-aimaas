package  com.badou.project.maas.modelwarehouse.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.modelwarehouse.model.WareHouseVllmParamEntity;

/**
 * @author badousoft
 * @date 2025-03-08 15:09:21.737
 *  模型仓库VLLM参数form
 */
public class WareHouseVllmParamForm extends BaseStrutsEntityForm<WareHouseVllmParamEntity> {

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
     * 组别编码
     */
    protected String  groupCode;
	/**
     * 更新人名字
     */
    protected String  updatorName;
	/**
     * 组别名称
     */
    protected String  groupName;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 组别序号
     */
    protected Integer  groupNo;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
	/**
     * 组别优先级
     */
    protected Integer  groupPriority;
	/**
     * 名字
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
     * 模型仓库主键
     */
    protected String  warehostId;
	/**
     * 值
     */
    protected String  value;
	/**
     * 解释
     */
    protected String  explainName;

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
     *  获取组别编码
     */
    public String getGroupCode() {
        return groupCode;
    }

	/**
     *  设置组别编码
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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
     *  获取组别名称
     */
    public String getGroupName() {
        return groupName;
    }

	/**
     *  设置组别名称
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
     *  获取组别序号
     */
    public Integer getGroupNo() {
        return groupNo;
    }

	/**
     *  设置组别序号
     */
    public void setGroupNo(Integer groupNo) {
        this.groupNo = groupNo;
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
     *  获取组别优先级
     */
    public Integer getGroupPriority() {
        return groupPriority;
    }

	/**
     *  设置组别优先级
     */
    public void setGroupPriority(Integer groupPriority) {
        this.groupPriority = groupPriority;
    }
    /**
     *  获取名字
     */
    public String getName() {
        return name;
    }

	/**
     *  设置名字
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
     *  获取模型仓库主键
     */
    public String getWarehostId() {
        return warehostId;
    }

	/**
     *  设置模型仓库主键
     */
    public void setWarehostId(String warehostId) {
        this.warehostId = warehostId;
    }
    /**
     *  获取值
     */
    public String getValue() {
        return value;
    }

	/**
     *  设置值
     */
    public void setValue(String value) {
        this.value = value;
    }
    /**
     *  获取解释
     */
    public String getExplainName() {
        return explainName;
    }

	/**
     *  设置解释
     */
    public void setExplainName(String explainName) {
        this.explainName = explainName;
    }
}