package com.badou.project.maas.maasfile.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author badousoft
 * @date 2025-02-05 14:13:28.676
 *  系统文件夹类
 */
@Entity
@Table(name = "maas_new_file")
public class MaasTreeFileEntity extends AppBaseEntity {

    /**
     * 文件大小
     */
    @Column(name = "file_size", unique = false, nullable = true, insertable = true, updatable = true)
    protected String fileSize;

	/**
     * 全路径主键
     */
	@Column(name = "full_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String fullpath;

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
     * 上传时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

	/**
     * 更新人名称
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;

	/**
     * 附件主键
     */
	@Column(name = "Attach_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String attachId;

	/**
     * 逻辑删除符号
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;

	/**
     * 等级
     */
	@Column(name = "Level_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String levelId;

	/**
     * 树类型
     */
	@Column(name = "tree_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected String treetype;

	/**
     * 分析数量
     */
	@Column(name = "Analyze_counts", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer analyzeCounts;

    /**
     * 分析信息
     */
    @Column(name = "analyze_msg", unique = false, nullable = true, insertable = true, updatable = true)
    protected String analyzeMsg;

	/**
     * 全路径名称
     */
	@Column(name = "full_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String fullname;

	/**
     * 优先级
     */
	@Column(name = "Priority", unique = false, nullable = true, insertable = true, updatable = true)
    protected String priority;

	/**
     * 文件类型(0.文件夹 1.文件)
     */
	@Column(name = "Type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer type;

	/**
     * 是否已分析
     */
	@Column(name = "Analyze_flg", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer analyzeFlg;

	/**
     * 更新时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;

	/**
     * 树等级
     */
//	@Column(name = "treeLevel", unique = false, nullable = true, insertable = true, updatable = true)
    @Transient
    protected String treeLevel;

	/**
     * 父主键
     */
	@Column(name = "Parent_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String parentId;

	/**
     * 文件夹名称
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

    public String getAnalyzeMsg() {
        return analyzeMsg;
    }

    public void setAnalyzeMsg(String analyzeMsg) {
        this.analyzeMsg = analyzeMsg;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 获取全路径主键
     */
    public String getFullpath() {
        return fullpath;
    }

	/**
     * 设置全路径主键
     */
    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
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
     * 获取更新人名称
     */
    public String getUpdatorName() {
        return updatorName;
    }

	/**
     * 设置更新人名称
     */
    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }
    /**
     * 获取附件主键
     */
    public String getAttachId() {
        return attachId;
    }

	/**
     * 设置附件主键
     */
    public void setAttachId(String attachId) {
        this.attachId = attachId;
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
     * 获取等级
     */
    public String getLevelId() {
        return levelId;
    }

	/**
     * 设置等级
     */
    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }
    /**
     * 获取树类型
     */
    public String getTreetype() {
        return treetype;
    }

	/**
     * 设置树类型
     */
    public void setTreetype(String treetype) {
        this.treetype = treetype;
    }
    /**
     * 获取分析数量
     */
    public Integer getAnalyzeCounts() {
        return analyzeCounts;
    }

	/**
     * 设置分析数量
     */
    public void setAnalyzeCounts(Integer analyzeCounts) {
        this.analyzeCounts = analyzeCounts;
    }
    /**
     * 获取全路径名称
     */
    public String getFullname() {
        return fullname;
    }

	/**
     * 设置全路径名称
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    /**
     * 获取优先级
     */
    public String getPriority() {
        return priority;
    }

	/**
     * 设置优先级
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }
    /**
     * 获取文件类型(0.文件夹 1.文件)
     */
    public Integer getType() {
        return type;
    }

	/**
     * 设置文件类型(0.文件夹 1.文件)
     */
    public void setType(Integer type) {
        this.type = type;
    }
    /**
     * 获取是否已分析
     */
    public Integer getAnalyzeFlg() {
        return analyzeFlg;
    }

	/**
     * 设置是否已分析
     */
    public void setAnalyzeFlg(Integer analyzeFlg) {
        this.analyzeFlg = analyzeFlg;
    }
    /**
     * 获取更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

	/**
     * 设置更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getTreeLevel() {
        return treeLevel;
    }

    public void setTreeLevel(String treeLevel) {
        this.treeLevel = treeLevel;
    }

    /**
     * 获取父主键
     */
    public String getParentId() {
        return parentId;
    }

	/**
     * 设置父主键
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    /**
     * 获取文件夹名称
     */
    public String getName() {
        return name;
    }

	/**
     * 设置文件夹名称
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
}
