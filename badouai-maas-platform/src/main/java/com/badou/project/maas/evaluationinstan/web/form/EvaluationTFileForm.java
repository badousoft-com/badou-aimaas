package  com.badou.project.maas.evaluationinstan.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.evaluationinstan.model.EvaluationTFileEntity;

/**
 * @author badousoft
 * @date 2024-12-04 15:58:41.796
 *  微调评价训练集form
 */
public class EvaluationTFileForm extends BaseStrutsEntityForm<EvaluationTFileEntity> {

	/**
     * 微调评价主键
     */
    protected String  evaluationId;
	/**
     * 创建人主键
     */
    protected String  creator;
	/**
     * 更新时间
     */
    protected Date  updateTime;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 更新人名称
     */
    protected String  updatorName;
	/**
     * 训练集主键
     */
    protected String  trainFileId;
	/**
     * 逻辑删除符号
     */
    protected Integer  flgDeleted;
	/**
     * 训练集名称
     */
    protected String  trainFileName;
	/**
     * 更新人主键
     */
    protected String  updator;
	/**
     * 创建人名称
     */
    protected String  creatorName;
	/**
     * 主键
     */
    protected String  id;

        /**
     *  获取微调评价主键
     */
    public String getEvaluationId() {
        return evaluationId;
    }

	/**
     *  设置微调评价主键
     */
    public void setEvaluationId(String evaluationId) {
        this.evaluationId = evaluationId;
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
     *  获取训练集主键
     */
    public String getTrainFileId() {
        return trainFileId;
    }

	/**
     *  设置训练集主键
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
     *  获取训练集名称
     */
    public String getTrainFileName() {
        return trainFileName;
    }

	/**
     *  设置训练集名称
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
     *  获取创建人名称
     */
    public String getCreatorName() {
        return creatorName;
    }

	/**
     *  设置创建人名称
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