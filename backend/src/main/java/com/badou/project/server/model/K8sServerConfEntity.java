package com.badou.project.server.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @Author lm
 * @Description k8s服务器配置实体
 * @Date 2022/12/3 0003 16:56
 * @Version 1.0
 */
@javax.persistence.Entity
@Table(name = "fbpt_k8s_server_conf")
@Where(clause = "FLG_DELETED=0")
public class K8sServerConfEntity extends AppBaseEntity {

    @ApiModelProperty("模型路径")
    @Column(name = "model_paths", unique = false, nullable = true, insertable = true, updatable = true)
    private String modelPaths;

    @ApiModelProperty("主节点服务器主键")
    @Column(name = "parent_id", unique = false, nullable = true, insertable = true, updatable = true)
    private String parentId;

    @ApiModelProperty("主节点服务器名称")
    @Column(name = "parent_name", unique = false, nullable = true, insertable = true, updatable = true)
    private String parentName;

    @ApiModelProperty("主前缀本地路径")
    @Column(name = "main_local_path", unique = false, nullable = true, insertable = true, updatable = true)
    private String mainLocalPath;

    @ApiModelProperty("NCCL网卡通信接口")
    @Column(name = "mater_port", unique = false, nullable = true, insertable = true, updatable = true)
    private Integer materPort;

    @ApiModelProperty("NCCL网卡接口名称")
    @Column(name = "nccl_socket", unique = false, nullable = true, insertable = true, updatable = true)
    private String ncclSocket;

    @ApiModelProperty("等级排序 0.代表主机 1.代表子机器")
    @Column(name = "level_sort", unique = false, nullable = true, insertable = true, updatable = true)
    private Integer levelSort;

    @ApiModelProperty("版本")
    @Column(name = "version", unique = false, nullable = true, insertable = true, updatable = true)
    private String version;

    @ApiModelProperty("GPU接口信息地址")
    @Column(name = "gpu_msg_url", unique = false, nullable = true, insertable = true, updatable = true)
    private String gpuMsgUrl;

    @ApiModelProperty("服务器编码")
    @Column(name = "code", unique = false, nullable = true, insertable = true, updatable = true)
    private String code;

    @ApiModelProperty("服务器地址")
    @Column(name = "address", unique = false, nullable = true, insertable = true, updatable = true)
    private String address;

    @ApiModelProperty("服务器端口")
    @Column(name = "port", unique = false, nullable = true, insertable = true, updatable = true)
    private Integer port;

    @ApiModelProperty("对外服务类型(0.ingress)")
    @Column(name = "expose_type", unique = false, nullable = true, insertable = true, updatable = true)
    private Integer exposeType;

    @ApiModelProperty("对外服务地址(当expose_type为0时必填)")
    @Column(name = "expose_address", unique = false, nullable = true, insertable = true, updatable = true)
    private String exposeAddress;

    @ApiModelProperty("状态(0.可用 1.不可用)")
    @Column(name = "status", unique = false, nullable = true, insertable = true, updatable = true)
    private int status;

    @ApiModelProperty("备注")
    @Column(name = "remark", unique = false, nullable = true, insertable = true, updatable = true)
    private String remark;

    @ApiModelProperty("认证类型(0.配置文件 1.Token)")
    @Column(name = "auth_type", unique = false, nullable = true, insertable = true, updatable = true)
    private Integer authType;

    @ApiModelProperty("认证内容(如果是配置文件则提供相对路径 Token则提供内容")
    @Column(name = "auth_content", unique = false, nullable = true, insertable = true, updatable = true)
    private String authContent;

    @ApiModelProperty("服务器外网访问ip地址")
    @Column(name = "ip_addr", unique = false, nullable = true, insertable = true, updatable = true)
    private String ipAddr;

    @ApiModelProperty("服务器文件挂载路径")
    @Column(name = "volumn_path", unique = false, nullable = true, insertable = true, updatable = true)
    private String volumnPath;

    public String getModelPaths() {
        return modelPaths;
    }

    public void setModelPaths(String modelPaths) {
        this.modelPaths = modelPaths;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getMainLocalPath() {
        return mainLocalPath;
    }

    public void setMainLocalPath(String mainLocalPath) {
        this.mainLocalPath = mainLocalPath;
    }

    public Integer getMaterPort() {
        return materPort;
    }

    public void setMaterPort(Integer materPort) {
        this.materPort = materPort;
    }

    public String getNcclSocket() {
        return ncclSocket;
    }

    public void setNcclSocket(String ncclSocket) {
        this.ncclSocket = ncclSocket;
    }

    public Integer getLevelSort() {
        return levelSort;
    }

    public void setLevelSort(Integer levelSort) {
        this.levelSort = levelSort;
    }

    public String getGpuMsgUrl() {
        return gpuMsgUrl;
    }

    public void setGpuMsgUrl(String gpuMsgUrl) {
        this.gpuMsgUrl = gpuMsgUrl;
    }

    public String getVolumnPath() {
        return volumnPath;
    }

    public void setVolumnPath(String volumnPath) {
        this.volumnPath = volumnPath;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getExposeType() {
        return exposeType;
    }

    public void setExposeType(Integer exposeType) {
        this.exposeType = exposeType;
    }

    public String getExposeAddress() {
        return exposeAddress;
    }

    public void setExposeAddress(String exposeAddress) {
        this.exposeAddress = exposeAddress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }

    public String getAuthContent() {
        return authContent;
    }

    public void setAuthContent(String authContent) {
        this.authContent = authContent;
    }
}
