package com.badou.project.maas.maasfile.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-07-15 15:48:36.55
 * @todo 文件管理类
 */
@Entity
@Table(name = "maas_file")
public class MaasFileEntity extends AppBaseEntity {

    /**
     * 数据格式
     */
    @Column(name = "data_format", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer dataFormat;
    /**
     * 知识点数量
     */
    @Column(name = "analyze_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer analyzeCount;
    /**
     * 解析信息
     */
    @Column(name = "analyze_msg", unique = false, nullable = true, insertable = true, updatable = true)
    protected String analyzeMsg;

	/**
     * 适用主题
     */
	@Column(name = "Applicable_subject", unique = false, nullable = true, insertable = true, updatable = true)
    protected String applicableSubject;
    
	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;
    
	/**
     * 上传时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;
    
	/**
     * 更新人名字
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;
    
	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;
    
	/**
     * 摘要
     */
	@Column(name = "Abstract_content", unique = false, nullable = true, insertable = true, updatable = true)
    protected String abstractContent;
    
	/**
     * 适用行业
     */
	@Column(name = "Applicable_industry", unique = false, nullable = true, insertable = true, updatable = true)
    protected String applicableIndustry;
    
	/**
     * 文件类型
     */
	@Column(name = "Type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer type;
    
	/**
     * 是否解析
     */
	@Column(name = "Analyze_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer analyzeFlg;
    
	/**
     * 更新人时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;
    
	/**
     * 文件大小
     */
	@Column(name = "Size", unique = false, nullable = true, insertable = true, updatable = true)
    protected String size;
    
	/**
     * 是否章节目录
     */
	@Column(name = "Chapter_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer chapterFlg;
    
	/**
     * 文件
     */
	@Column(name = "File_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String fileId;
    
	/**
     * 最后解析时间
     */
	@Column(name = "Analyze_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date analyzeTime;
    
	/**
     * 文件名称
     */
	@Column(name = "Name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String name;
    
	/**
     * 更新人主键
     */
	@Column(name = "Updator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updator;
    
	/**
     * 上传人
     */
	@Column(name = "Creator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creatorName;
    
	/**
     * 主键
     */
	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;

    public Integer getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(Integer dataFormat) {
        this.dataFormat = dataFormat;
    }

    public Integer getAnalyzeCount() {
        return analyzeCount;
    }

    public void setAnalyzeCount(Integer analyzeCount) {
        this.analyzeCount = analyzeCount;
    }

    public String getAnalyzeMsg() {
        return analyzeMsg;
    }

    public void setAnalyzeMsg(String analyzeMsg) {
        this.analyzeMsg = analyzeMsg;
    }

    /**
     * 获取适用主题
     */
    public String getApplicableSubject() {
        return applicableSubject;
    }

	/**
     * 设置适用主题
     */
    public void setApplicableSubject(String applicableSubject) {
        this.applicableSubject = applicableSubject;
    }
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
     * 获取上传时间
     */
    public Date getCreateTime() {
        return createTime;
    }

	/**
     * 设置上传时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public String getAbstractContent() {
        return abstractContent;
    }

    public void setAbstractContent(String abstractContent) {
        this.abstractContent = abstractContent;
    }

    /**
     * 获取适用行业
     */
    public String getApplicableIndustry() {
        return applicableIndustry;
    }

	/**
     * 设置适用行业
     */
    public void setApplicableIndustry(String applicableIndustry) {
        this.applicableIndustry = applicableIndustry;
    }
    /**
     * 获取文件类型
     */
    public Integer getType() {
        return type;
    }

	/**
     * 设置文件类型
     */
    public void setType(Integer type) {
        this.type = type;
    }
    /**
     * 获取是否解析
     */
    public Integer getAnalyzeFlg() {
        return analyzeFlg;
    }

	/**
     * 设置是否解析
     */
    public void setAnalyzeFlg(Integer analyzeFlg) {
        this.analyzeFlg = analyzeFlg;
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
     * 获取文件大小
     */
    public String getSize() {
        return size;
    }

	/**
     * 设置文件大小
     */
    public void setSize(String size) {
        this.size = size;
    }
    /**
     * 获取是否章节目录
     */
    public Integer getChapterFlg() {
        return chapterFlg;
    }

	/**
     * 设置是否章节目录
     */
    public void setChapterFlg(Integer chapterFlg) {
        this.chapterFlg = chapterFlg;
    }
    /**
     * 获取文件
     */
    public String getFileId() {
        return fileId;
    }

	/**
     * 设置文件
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    /**
     * 获取最后解析时间
     */
    public Date getAnalyzeTime() {
        return analyzeTime;
    }

	/**
     * 设置最后解析时间
     */
    public void setAnalyzeTime(Date analyzeTime) {
        this.analyzeTime = analyzeTime;
    }
    /**
     * 获取文件名称
     */
    public String getName() {
        return name;
    }

	/**
     * 设置文件名称
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
     * 获取上传人
     */
    public String getCreatorName() {
        return creatorName;
    }

	/**
     * 设置上传人
     */
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    /**
     * 获取主键
     */
    public String getId() {
        return id;
    }

	/**
     * 设置主键
     */
    public void setId(String id) {
        this.id = id;
    }
}
