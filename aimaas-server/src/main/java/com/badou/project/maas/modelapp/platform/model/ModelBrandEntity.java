package com.badou.project.maas.modelapp.platform.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.badou.core.standard.base.extend.IUniqueCodeEntity;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-05-20 16:48:21.656
 *  大模型品牌类
 */
@Entity
@Table(name = "ai_model_brand")
@Where(clause = "flg_deleted=0")
public class ModelBrandEntity extends AppBaseEntity implements IUniqueCodeEntity {

	/**
     * 工厂实现类路径
     */
	@Column(name = "Factory_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String factoryPath;

	/**
     * 编码
     */
	@Column(name = "Code", unique = false, nullable = true, insertable = true, updatable = true)
    protected String code;

	/**
     * 名称
     */
	@Column(name = "Name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String name;


    /**
     * 获取工厂实现类路径
     */
    public String getFactoryPath() {
        return factoryPath;
    }

	/**
     * 设置工厂实现类路径
     */
    public void setFactoryPath(String factoryPath) {
        this.factoryPath = factoryPath;
    }
    /**
     * 获取名称
     */
    public String getName() {
        return name;
    }

	/**
     * 设置名称
     */
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }
}
