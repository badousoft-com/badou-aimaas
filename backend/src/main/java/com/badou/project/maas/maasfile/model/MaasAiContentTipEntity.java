package com.badou.project.maas.maasfile.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.badou.brms.base.support.hibernate.used.AppEntityOnlyId;

import java.util.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2025-02-05 17:51:01.635
 *  ai内容生成提示词类
 */
@Entity
@Table(name = "maas_ai_content_tip")
public class MaasAiContentTipEntity extends AppEntityOnlyId {

	/**
     * 指令监督生成提示词
     */
	@Column(name = "sft_tip", unique = false, nullable = true, insertable = true, updatable = true)
    protected String sftTip;

    /**
     * kto偏好数据生成提示词
     */
    @Column(name = "kto_tip", unique = false, nullable = true, insertable = true, updatable = true)
    protected String ktoTip;

	/**
     * PPO偏好数据生成提示词
     */
	@Column(name = "ppo_tip", unique = false, nullable = true, insertable = true, updatable = true)
    protected String ppoTip;

	/**
     * 预训练数据生成提示词
     */
	@Column(name = "Pt_tip", unique = false, nullable = true, insertable = true, updatable = true)
    protected String ptTip;

    public String getSftTip() {
        return sftTip;
    }

    public void setSftTip(String sftTip) {
        this.sftTip = sftTip;
    }

    public String getKtoTip() {
        return ktoTip;
    }

    public void setKtoTip(String ktoTip) {
        this.ktoTip = ktoTip;
    }

    public String getPpoTip() {
        return ppoTip;
    }

    public void setPpoTip(String ppoTip) {
        this.ppoTip = ppoTip;
    }

    /**
     * 获取预训练数据生成提示词
     */
    public String getPtTip() {
        return ptTip;
    }

	/**
     * 设置预训练数据生成提示词
     */
    public void setPtTip(String ptTip) {
        this.ptTip = ptTip;
    }
}
