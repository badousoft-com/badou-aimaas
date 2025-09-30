package com.badou.project.maas.registryaddress.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @ClassName EnvServerBindEntity
 * @Description 服务器与环境绑定关系实体
 * @date 2023/1/31 14:29
 * @Version 1.0
 */
@javax.persistence.Entity
@Table(name = "fbpt_registry_address")
@Where(clause = "FLG_DELETED=0")
public class RegistryAddressEntity extends AppBaseEntity {

    @ApiModelProperty("服务器主键")
    @Column(name = "key_name", unique = false, nullable = true, insertable = true, updatable = true)
    private String keyName;

    @ApiModelProperty("镜像仓库地址")
    @Column(name = "address", unique = false, nullable = true, insertable = true, updatable = true)
    private String address;

    @ApiModelProperty("类型(0.kubernetes.io/dockerconfigjson)")
    @Column(name = "type", unique = false, nullable = true, insertable = true, updatable = true)
    private Integer type;

    @ApiModelProperty("用户名")
    @Column(name = "user_name", unique = false, nullable = true, insertable = true, updatable = true)
    private String userName;

    @ApiModelProperty("密码")
    @Column(name = "pwd", unique = false, nullable = true, insertable = true, updatable = true)
    private String pwd;

    @ApiModelProperty("镜像匹配关键字")
    @Column(name = "image_key", unique = false, nullable = true, insertable = true, updatable = true)
    private String imageKey;

    @ApiModelProperty("项目名字")
    @Column(name = "project_name", unique = false, nullable = true, insertable = true, updatable = true)
    private String projectName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }
}
