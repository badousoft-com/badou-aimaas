package  com.badou.project.maas.tuningprogramparams.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.tuningprogramparams.model.TuningProgramParamsEntity;

/**
 * @author badousoft
 * @date 2024-05-20 16:35:31.725
 * @todo 微调计划参数form
 */
public class TuningProgramParamsForm extends BaseStrutsEntityForm<TuningProgramParamsEntity> {

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
     * 更新人名字
     */
    protected String  updatorName;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 微调计划主键
     */
    protected String  programId;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
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
     * 主键
     */
    protected String  id;
	/**
     * 值
     */
    protected String  value;
	/**
     * 解释
     */
    protected String  explainName;
	/**
     * 微调方案主键
     */
    protected String  planId;

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
     *  获取微调计划主键
     */
    public String getProgramId() {
        return programId;
    }

	/**
     *  设置微调计划主键
     */
    public void setProgramId(String programId) {
        this.programId = programId;
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
    /**
     *  获取微调方案主键
     */
    public String getPlanId() {
        return planId;
    }

	/**
     *  设置微调方案主键
     */
    public void setPlanId(String planId) {
        this.planId = planId;
    }
}