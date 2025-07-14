package  com.badou.project.maas.traindatalink.web.form;

import java.util.Date;
import java.sql.Timestamp;

import com.badou.brms.base.support.struts.form.BaseStrutsEntityForm;
import com.badou.project.maas.traindatalink.model.TrainDataLinkEntity;

/**
 * @author badousoft
 * @date 2024-04-22 16:50:47.367
 * @todo 训练集数据与训练集关联form
 */
public class TrainDataLinkForm extends BaseStrutsEntityForm<TrainDataLinkEntity> {

	/**
     * 训练集数据标题
     */
    protected String  trainDataTitle;
	/**
     * 创建人主键
     */
    protected String  creator;
	/**
     * 创建时间
     */
    protected Date  createTime;
	/**
     * 训练集名称
     */
    protected String  trainName;
	/**
     * 训练集数据
     */
    protected String  trainDataId;
	/**
     * 创建人名字
     */
    protected String  creatorName;
	/**
     * 主键
     */
    protected String  id;
	/**
     * 训练集数据内容
     */
    protected String  trainDataContent;
	/**
     * 训练集数据标签
     */
    protected String  trainDataTag;
	/**
     * 训练集主键
     */
    protected String  trainId;
	/**
     * 训练集数据版本
     */
    protected String  trainDataVersion;

        /**
     *  获取训练集数据标题
     */
    public String getTrainDataTitle() {
        return trainDataTitle;
    }

	/**
     *  设置训练集数据标题
     */
    public void setTrainDataTitle(String trainDataTitle) {
        this.trainDataTitle = trainDataTitle;
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
     *  获取训练集名称
     */
    public String getTrainName() {
        return trainName;
    }

	/**
     *  设置训练集名称
     */
    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }
    /**
     *  获取训练集数据
     */
    public String getTrainDataId() {
        return trainDataId;
    }

	/**
     *  设置训练集数据
     */
    public void setTrainDataId(String trainDataId) {
        this.trainDataId = trainDataId;
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
     *  获取训练集数据内容
     */
    public String getTrainDataContent() {
        return trainDataContent;
    }

	/**
     *  设置训练集数据内容
     */
    public void setTrainDataContent(String trainDataContent) {
        this.trainDataContent = trainDataContent;
    }
    /**
     *  获取训练集数据标签
     */
    public String getTrainDataTag() {
        return trainDataTag;
    }

	/**
     *  设置训练集数据标签
     */
    public void setTrainDataTag(String trainDataTag) {
        this.trainDataTag = trainDataTag;
    }
    /**
     *  获取训练集主键
     */
    public String getTrainId() {
        return trainId;
    }

	/**
     *  设置训练集主键
     */
    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }
    /**
     *  获取训练集数据版本
     */
    public String getTrainDataVersion() {
        return trainDataVersion;
    }

	/**
     *  设置训练集数据版本
     */
    public void setTrainDataVersion(String trainDataVersion) {
        this.trainDataVersion = trainDataVersion;
    }
}