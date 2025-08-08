package  com.badou.project.maas.problemdata.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.problemdata.model.ProblemDataVideoEntity;

/**
 * @author badousoft
 * @date 2025-03-26 11:30:02.141
 *  视频样本集form
 */
public class ProblemDataVideoForm extends BaseStrutsEntityForm<ProblemDataVideoEntity> {

	/**
     * 数据格式
     */
    protected Integer  dataFormat;
	/**
     * 适用主题
     */
    protected String  applicableSubject;
	/**
     * 创建人主键
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
     * 内容总数
     */
    protected Integer  numCount;
	/**
     * 描述
     */
    protected String  remark;
	/**
     * 适用行业
     */
    protected String  applicableIndustry;
	/**
     * 类型
     */
    protected Integer  type;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
	/**
     * 文件大小
     */
    protected String  size;
	/**
     * 文件名字
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
     * 角色备注(希望模型扮演的角色)
     */
    protected String  roleDesc;
	/**
     * 启用状态
     */
    protected Integer  upStatus;

        /**
     *  获取数据格式
     */
    public Integer getDataFormat() {
        return dataFormat;
    }

	/**
     *  设置数据格式
     */
    public void setDataFormat(Integer dataFormat) {
        this.dataFormat = dataFormat;
    }
    /**
     *  获取适用主题
     */
    public String getApplicableSubject() {
        return applicableSubject;
    }

	/**
     *  设置适用主题
     */
    public void setApplicableSubject(String applicableSubject) {
        this.applicableSubject = applicableSubject;
    }
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
     *  获取内容总数
     */
    public Integer getNumCount() {
        return numCount;
    }

	/**
     *  设置内容总数
     */
    public void setNumCount(Integer numCount) {
        this.numCount = numCount;
    }
    /**
     *  获取描述
     */
    public String getRemark() {
        return remark;
    }

	/**
     *  设置描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
    /**
     *  获取适用行业
     */
    public String getApplicableIndustry() {
        return applicableIndustry;
    }

	/**
     *  设置适用行业
     */
    public void setApplicableIndustry(String applicableIndustry) {
        this.applicableIndustry = applicableIndustry;
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
     *  获取文件大小
     */
    public String getSize() {
        return size;
    }

	/**
     *  设置文件大小
     */
    public void setSize(String size) {
        this.size = size;
    }
    /**
     *  获取文件名字
     */
    public String getName() {
        return name;
    }

	/**
     *  设置文件名字
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
     *  获取角色备注(希望模型扮演的角色)
     */
    public String getRoleDesc() {
        return roleDesc;
    }

	/**
     *  设置角色备注(希望模型扮演的角色)
     */
    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }
    /**
     *  获取启用状态
     */
    public Integer getUpStatus() {
        return upStatus;
    }

	/**
     *  设置启用状态
     */
    public void setUpStatus(Integer upStatus) {
        this.upStatus = upStatus;
    }
}