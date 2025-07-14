package  com.badou.project.maas.programlink.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.programlink.model.ProgramLinkEntity;

/**
 * @author badousoft
 * @date 2024-05-08 14:41:26.637
 * @todo 微调计划方案form
 */
public class ProgramLinkForm extends BaseStrutsEntityForm<ProgramLinkEntity> {

	/**
     * 创建人主键
     */
    protected String  creator;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
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
     * 方案名称
     */
    protected String  name;
	/**
     * 微调方案主键
     */
    protected String  tuningProgramId;
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
     * 方案主键
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
     *  获取方案名称
     */
    public String getName() {
        return name;
    }

	/**
     *  设置方案名称
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     *  获取微调方案主键
     */
    public String getTuningProgramId() {
        return tuningProgramId;
    }

	/**
     *  设置微调方案主键
     */
    public void setTuningProgramId(String tuningProgramId) {
        this.tuningProgramId = tuningProgramId;
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
     *  获取方案主键
     */
    public String getPlanId() {
        return planId;
    }

	/**
     *  设置方案主键
     */
    public void setPlanId(String planId) {
        this.planId = planId;
    }
}