package com.badou.project.maas.modelapp.platform.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.badou.core.standard.base.extend.IUniqueCodeEntity;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * AI大模型配置类
 */
@Entity
@Table(name = "ai_llm_config")
@Where(clause = "flg_deleted=0")
@Data
public class AiLlmConfigEntity extends AppBaseEntity implements IUniqueCodeEntity {

    /**
     * 是否支持拓展工具
     */
    @Column(name = "flg_tool", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgTool;

    /**
     * 理解类型  1-文本理解  2-视觉理解
     */
    @Column(name = "understand_type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer understandType;

    /**
     * 模型类型
     */
    @Column(name = "type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer type;

    /**
     * 创建者ID
     */
    @Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;

    /**
     * 编码
     */
    @Column(name = "Code", unique = false, nullable = true, insertable = true, updatable = true)
    protected String code;

    /**
     * 创建时间
     */
    @Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date createTime;

    /**
     * 更新者名称
     */
    @Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;

    /**
     * 删除标识
     */
    @Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;

    /**
     * API服务器
     */
    @Column(name = "Api_host", unique = false, nullable = true, insertable = true, updatable = true)
    protected String apiHost;

    /**
     * 备注
     */
    @Column(name = "Remark", unique = false, nullable = true, insertable = true, updatable = true)
    protected String remark;

    /**
     * 更新时间
     */
    @Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date updateTime;

    /**
     * API密钥
     */
    @Column(name = "Api_key", unique = false, nullable = true, insertable = true, updatable = true)
    protected String apiKey;

    /**
     * 应用名
     */
    @Column(name = "Name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String name;

    /**
     * 更新者ID
     */
    @Column(name = "Updator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updator;

    /**
     * 创建者名称
     */
    @Column(name = "Creator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creatorName;

    /**
     * 状态
     */
    @Column(name = "Status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer status;

    /**
     * 模型工厂
     */
    @Column(name = "model_factory", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelFactory;

    public Integer getUnderstandType() {
        return understandType;
    }

    public void setUnderstandType(Integer understandType) {
        this.understandType = understandType;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getFlgTool() {
        return flgTool;
    }

    public void setFlgTool(Integer flgTool) {
        this.flgTool = flgTool;
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
     * 获取API服务器
     */
    public String getApiHost() {
        return apiHost;
    }

    /**
     * 设置API服务器
     */
    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    /**
     * 获取备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取API密钥
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * 设置API密钥
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 获取应用名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置应用名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}
