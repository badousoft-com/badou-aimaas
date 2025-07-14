package com.badou.project.model.modelfile.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;

import java.util.Date; 
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-04-01 17:57:13.932
 * @todo 模型文件管理类
 */
@Entity
@Table(name = "maas_model_file")
public class modelEntity extends AppBaseEntity {

	/**
     * 最后更新时间
     */
	@Column(name = "Last_update", unique = false, nullable = true, insertable = true, updatable = true)
    protected Date lastUpdate;
    
	/**
     * 文件名字
     */
	@Column(name = "Name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String name;
    
	/**
     * 文件大小
     */
	@Column(name = "File_size", unique = false, nullable = true, insertable = true, updatable = true)
    protected String fileSize;
    
	/**
     * 控制权限
     */
	@Column(name = "Role_auth", unique = false, nullable = true, insertable = true, updatable = true)
    protected String roleAuth;
    

    /**
     * 获取最后更新时间
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

	/**
     * 设置最后更新时间
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    /**
     * 获取文件名字
     */
    public String getName() {
        return name;
    }

	/**
     * 设置文件名字
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * 获取文件大小
     */
    public String getFileSize() {
        return fileSize;
    }

	/**
     * 设置文件大小
     */
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
    /**
     * 获取控制权限
     */
    public String getRoleAuth() {
        return roleAuth;
    }

	/**
     * 设置控制权限
     */
    public void setRoleAuth(String roleAuth) {
        this.roleAuth = roleAuth;
    }
}
