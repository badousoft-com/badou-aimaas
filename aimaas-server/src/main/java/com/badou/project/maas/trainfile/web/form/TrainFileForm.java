package  com.badou.project.maas.trainfile.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.trainfile.model.TrainFileEntity;

/**
 * @author badousoft
 * @date 2024-05-16 11:07:50.5
 * @todo 训练集文件管理form
 */
public class TrainFileForm extends BaseStrutsEntityForm<TrainFileEntity> {

	/**
     * 文件路径
     */
    protected String  filePath;
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
     * 适用行业
     */
    protected String  applicableIndustry;
	/**
     * 更新人时间
     */
    protected Date  updateTime;
	/**
     * 文件大小
     */
    protected String  size;
	/**
     * 文件主键
     */
    protected String  fileId;
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
     * 主键
     */
    protected String  id;

        /**
     *  获取文件路径
     */
    public String getFilePath() {
        return filePath;
    }

	/**
     *  设置文件路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
     *  获取文件主键
     */
    public String getFileId() {
        return fileId;
    }

	/**
     *  设置文件主键
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
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