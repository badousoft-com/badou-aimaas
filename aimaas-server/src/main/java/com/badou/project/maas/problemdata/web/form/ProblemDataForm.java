package  com.badou.project.maas.problemdata.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;

/**
 * @author badousoft
 * @date 2024-05-15 17:37:11.964
 * @todo 样本数据集管理form
 */
public class ProblemDataForm extends BaseStrutsEntityForm<ProblemDataEntity> {

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
     * 关联文件主键
     */
    protected String  trainFileId;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
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
     * 关联文件名字
     */
    protected String  trainFileName;
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
     *  获取关联文件主键
     */
    public String getTrainFileId() {
        return trainFileId;
    }

	/**
     *  设置关联文件主键
     */
    public void setTrainFileId(String trainFileId) {
        this.trainFileId = trainFileId;
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
     *  获取关联文件名字
     */
    public String getTrainFileName() {
        return trainFileName;
    }

	/**
     *  设置关联文件名字
     */
    public void setTrainFileName(String trainFileName) {
        this.trainFileName = trainFileName;
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
}