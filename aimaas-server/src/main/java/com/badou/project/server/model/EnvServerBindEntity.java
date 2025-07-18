package com.badou.project.server.model;

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
@Table(name = "fbpt_env_server_bind")
@Where(clause = "FLG_DELETED=0")
public class EnvServerBindEntity extends AppBaseEntity {

    @ApiModelProperty("服务器主键")
    @Column(name = "server_id", unique = false, nullable = true, insertable = true, updatable = true)
    private String serverId;

    @ApiModelProperty("服务器类型 0.k8s")
    @Column(name = "server_type", unique = false, nullable = true, insertable = true, updatable = true)
    private int serverType;

    @ApiModelProperty("环境主键")
    @Column(name = "env_id", unique = false, nullable = true, insertable = true, updatable = true)
    private String envId;

    @ApiModelProperty("项目主键")
    @Column(name = "project_id", unique = false, nullable = true, insertable = true, updatable = true)
    private String projectId;

    public EnvServerBindEntity() {
    }

    public EnvServerBindEntity(String serverId, int serverType, String envId, String projectId) {
        this.serverId = serverId;
        this.serverType = serverType;
        this.envId = envId;
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public int getServerType() {
        return serverType;
    }

    public void setServerType(int serverType) {
        this.serverType = serverType;
    }

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }
}
