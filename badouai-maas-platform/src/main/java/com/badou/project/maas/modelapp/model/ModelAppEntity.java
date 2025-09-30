package com.badou.project.maas.modelapp.model;

import com.badou.brms.base.support.hibernate.used.AppBaseEntity;
import com.badou.project.GlobalConsts;
import com.badou.project.maas.common.FileControllerService;
import com.badou.tools.common.util.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date; 
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author badousoft
 * @date 2024-05-27 11:33:46.513
 * @todo 模型应用管理类
 */
@Entity
@Table(name = "maas_model_app")
public class ModelAppEntity extends AppBaseEntity {
    /**
     * 仓库模型主键
     */
    @Column(name = "warehouse_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String warehouseId;
    /**
     * 上下文大小
     */
    @Column(name = "content_length", unique = false, nullable = true, insertable = true, updatable = true)
    protected Double contentLength;
    //线程信息
    @Column(name = "thread_msg", unique = false, nullable = true, insertable = true, updatable = true)
    protected String threadMsg;
    //执行服务器显卡
    @Column(name = "exec_gpu_card", unique = false, nullable = true, insertable = true, updatable = true)
    protected String execGpuCard;
    //必填
    @Column(name = "platform_assistant_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String platformAssistantId;
    //必填
    @Column(name = "platform_model_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String platformModelName;
    //必填
    @Column(name = "platform_model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String platformModelId;
    //必填 没有服务器则不算有效的服务
    @Column(name = "server_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverId;
    //必填 没有服务器则不算有效的服务
    @Column(name = "server_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String serverName;
    @Column(name = "msg", unique = false, nullable = true, insertable = true, updatable = true)
    protected String msg;
    @Column(name = "tun_model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String tunModelId;
    @Column(name = "port", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer port;
    @Column(name = "model_result_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelResultId;

    /**
     * 微调模式 0.指令监督微调数据集 1.预训练数据集 2.偏好数据集 3.KTO数据集 4.多模态
     */
    @Column(name = "do_way", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer doWay;

    /**
     * 构建Pod
     */
    @Column(name = "build_pod", unique = false, nullable = true, insertable = true, updatable = true)
    protected String buildPod;

    /**
     * 构建SVC
     */
    @Column(name = "build_svc", unique = false, nullable = true, insertable = true, updatable = true)
    protected String buildSvc;

    /**
     * 模型类型
     */
    @Column(name = "type", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer type;

    /**
     * 模型来源
     */
    @Column(name = "source", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer source;

    /**
     * 模型厂家
     */
    @Column(name = "model_provider", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelProvider;

	/**
     * 创建人主键
     */
	@Column(name = "Creator", unique = false, nullable = true, insertable = true, updatable = true)
    protected String creator;
    
	/**
     * 创建时间
     */
	@Column(name = "Create_time", unique = false, nullable = true, insertable = true, updatable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;
    
	/**
     * 更新人名称
     */
	@Column(name = "Updator_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String updatorName;
    
	/**
     * 应用描述
     */
	@Column(name = "app_desc", unique = false, nullable = true, insertable = true, updatable = true)
    protected String appDesc;
    
	/**
     * 逻辑删除标识
     */
	@Column(name = "Flg_deleted", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer flgDeleted;
    
	/**
     * 标识名称
     */
	@Column(name = "Define_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String defineName;
    
	/**
     * 模型
     */
	@Column(name = "Model_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelId;
    
	/**
     * 工作目录
     */
	@Column(name = "Work_path", unique = false, nullable = true, insertable = true, updatable = true)
    protected String workPath;
    
	/**
     * 更新时间
     */
	@Column(name = "Update_time", unique = false, nullable = true, insertable = true, updatable = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;
    
	/**
     * 模型名称
     */
	@Column(name = "Model_name", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelName;
    
	/**
     * gpu个数
     */
	@Column(name = "Gpu_count", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer gpuCount;
    
	/**
     * 名称
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
    
	/**
     * 主键
     */
	@Column(name = "Id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String id;
    
	/**
     * 部署状态
     */
	@Column(name = "Status", unique = false, nullable = true, insertable = true, updatable = true)
    protected Integer status;
    /**
     * 服务的标识
     */
    @Column(name = "model_server_id", unique = false, nullable = true, insertable = true, updatable = true)
    protected String modelServerId;

    {
        if(StringUtils.isEmpty(modelServerId)){
            modelServerId = "a"+UUID.randomUUID().toString();
            if(modelServerId.length()>63){
                try {
                    throw new Exception("服务主键的长度不允许大于63");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getTotalApiPath() {
        return FileControllerService.getCacheK8sClient(getServerId()).getServer().getAddress() + ":" +port+getApiPath();
    }

    public String getTotalModelPath() {
        return FileControllerService.getCacheK8sClient(getServerId()).getServer().getAddress() + ":" +port+"/v1/models";
    }

    public String getApiPath(){
        String execUrl = "/v1/chat/completions";
        if (GlobalConsts.FIVE.equals(type)){
            execUrl = "/v2/rerank";
        }
        if (GlobalConsts.FOUR.equals(type)){
            execUrl = "/v1/embeddings";
        }
        return execUrl;
    }

    public String getResponse(){
        String response = "{\n" +
                "\t\"id\": \"chatcmpl-9af5dbbeb22d4d268426c5a60d379ddb\",\n" +
                "\t\"object\": \"chat.completion\",\n" +
                "\t\"created\": 1744165707,\n" +
                "\t\"model\": \""+modelName+"\",\n" +
                "\t\"choices\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"index\": 0,\n" +
                "\t\t\t\"message\": {\n" +
                "\t\t\t\t\"role\": \"assistant\",\n" +
                "\t\t\t\t\"reasoning_content\": null,\n" +
                "\t\t\t\t\"content\": \"你好！有什么可以帮助你的吗？\",\n" +
                "\t\t\t\t\"tool_calls\": []\n" +
                "\t\t\t},\n" +
                "\t\t\t\"logprobs\": '',\n" +
                "\t\t\t\"finish_reason\": \"stop\",\n" +
                "\t\t\t\"stop_reason\": ''\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"usage\": {\n" +
                "\t\t\"prompt_tokens\": 30,\n" +
                "\t\t\"total_tokens\": 38,\n" +
                "\t\t\"completion_tokens\": 8,\n" +
                "\t\t\"prompt_tokens_details\": null\n" +
                "\t},\n" +
                "\t\"prompt_logprobs\": ''\n" +
                "}";
        //embedding模型的返回例子
        if (GlobalConsts.FOUR.equals(type)){
            response = EMED_RESPONSE.replace("bge-large-zh-v1.5",modelName);
        }
        //VL模型的返回例子
        if (GlobalConsts.THREE.equals(type)){
            response = "{\n" +
                    "\t\"id\": \"chatcmpl-13a3f77919824f26bc2d2e11bdc4cfcb\",\n" +
                    "\t\"object\": \"chat.completion\",\n" +
                    "\t\"created\": 1744267096,\n" +
                    "\t\"model\": \""+modelName+"\",\n" +
                    "\t\"choices\": [\n" +
                    "\t\t{\n" +
                    "\t\t\t\"index\": 0,\n" +
                    "\t\t\t\"message\": {\n" +
                    "\t\t\t\t\"role\": \"assistant\",\n" +
                    "\t\t\t\t\"reasoning_content\": null,\n" +
                    "\t\t\t\t\"content\": \"这张图片显示的是一个企业信息填写界面，界面顶部显示了当前的网络状态、时间（10:42）和电池电量（98%）。界面中间部分是企业信息的填写区域，包括以下字段：\\n\\n1. 企业名称：输入框中显示“深圳市龙华区万众福电动”。\\n2. 法定代表人：输入框为空。\\n3. 企业地址：输入框为空。\\n4. 所属地区：提示“请选择”。\\n5. 企业类型：有两个选项，“生产”和“销售”，但未选择任何一项。\\n\\n界面底部显示了一个键盘，可以输入更多的信息。右上角有一个“撤回”按钮，左上角有一个“vConsole”标签，可能是用于查看控制台信息的。\",\n" +
                    "\t\t\t\t\"tool_calls\": []\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"logprobs\": null,\n" +
                    "\t\t\t\"finish_reason\": \"stop\",\n" +
                    "\t\t\t\"stop_reason\": null\n" +
                    "\t\t}\n" +
                    "\t],\n" +
                    "\t\"usage\": {\n" +
                    "\t\t\"prompt_tokens\": 3639,\n" +
                    "\t\t\"total_tokens\": 3804,\n" +
                    "\t\t\"completion_tokens\": 165,\n" +
                    "\t\t\"prompt_tokens_details\": null\n" +
                    "\t},\n" +
                    "\t\"prompt_logprobs\": null\n" +
                    "}";
        }
        //rerank的返回例子
        if (GlobalConsts.FIVE.equals(type)){
            response = "{\n" +
                    "\t\"id\": \"rerank-c66909e4720e49519ebc78e4ca841e79\",\n" +
                    "\t\"model\": \""+modelName+"\",\n" +
                    "\t\"usage\": {\n" +
                    "\t\t\"total_tokens\": 128\n" +
                    "\t},\n" +
                    "\t\"results\": [\n" +
                    "\t\t{\n" +
                    "\t\t\t\"index\": 1,\n" +
                    "\t\t\t\"document\": {\n" +
                    "\t\t\t\t\"text\": \"购买一本深度学习入门书籍，跟着章节逐步学习。\"\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"relevance_score\": 0.99462890625\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"index\": 0,\n" +
                    "\t\t\t\"document\": {\n" +
                    "\t\t\t\t\"text\": \"深度学习是机器学习的一个分支，需要先掌握 Python 和数学基础。\"\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"relevance_score\": 0.99169921875\n" +
                    "\t\t},\n" +
                    "\t\t{\n" +
                    "\t\t\t\"index\": 2,\n" +
                    "\t\t\t\"document\": {\n" +
                    "\t\t\t\t\"text\": \"参加在线课程如 Coursera 上的 Deep Learning 专项课程。\"\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"relevance_score\": 0.9755859375\n" +
                    "\t\t}\n" +
                    "\t]\n" +
                    "}";
        }
        return response;
    }

    public String getPlatformModelName() {
        return platformModelName;
    }

    public void setPlatformModelName(String platformModelName) {
        this.platformModelName = platformModelName;
    }

    public Double getContentLength() {
        return contentLength;
    }

    public void setContentLength(Double contentLength) {
        this.contentLength = contentLength;
    }

    public String getThreadMsg() {
        return threadMsg;
    }

    public void setThreadMsg(String threadMsg) {
        this.threadMsg = threadMsg;
    }


    public String getExecGpuCard() {
        return execGpuCard;
    }

    public void setExecGpuCard(String execGpuCard) {
        this.execGpuCard = execGpuCard;
    }

    public String getPlatformAssistantId() {
        return platformAssistantId;
    }

    public void setPlatformAssistantId(String platformAssistantId) {
        this.platformAssistantId = platformAssistantId;
    }

    public String getPlatformModelId() {
        return platformModelId;
    }

    public void setPlatformModelId(String platformModelId) {
        this.platformModelId = platformModelId;
    }

    public Integer getDoWay() {
        return doWay;
    }

    public void setDoWay(Integer doWay) {
        this.doWay = doWay;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(String modelProvider) {
        this.modelProvider = modelProvider;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getTunModelId() {
        return tunModelId;
    }

    public void setTunModelId(String tunModelId) {
        this.tunModelId = tunModelId;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getModelServerId() {
        return modelServerId;
    }

    public void setModelServerId(String modelServerId) {
        this.modelServerId = modelServerId;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public String getModelResultId() {
        return modelResultId;
    }

    public void setModelResultId(String modelResultId) {
        this.modelResultId = modelResultId;
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
     * 获取创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

	/**
     * 设置创建时间
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

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    /**
     * 获取逻辑删除标识
     */
    public Integer getFlgDeleted() {
        return flgDeleted;
    }

	/**
     * 设置逻辑删除标识
     */
    public void setFlgDeleted(Integer flgDeleted) {
        this.flgDeleted = flgDeleted;
    }
    /**
     * 获取标识名称
     */
    public String getDefineName() {
        return defineName;
    }

	/**
     * 设置标识名称
     */
    public void setDefineName(String defineName) {
        this.defineName = defineName;
    }
    /**
     * 获取模型
     */
    public String getModelId() {
        return modelId;
    }

	/**
     * 设置模型
     */
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    /**
     * 获取工作目录
     */
    public String getWorkPath() {
        return workPath;
    }

	/**
     * 设置工作目录
     */
    public void setWorkPath(String workPath) {
        this.workPath = workPath;
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
    /**
     * 获取模型名称
     */
    public String getModelName() {
        return modelName;
    }

	/**
     * 设置模型名称
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    /**
     * 获取gpu个数
     */
    public Integer getGpuCount() {
        return gpuCount;
    }

	/**
     * 设置gpu个数
     */
    public void setGpuCount(Integer gpuCount) {
        this.gpuCount = gpuCount;
    }

    public String getBuildPod() {
        return buildPod;
    }

    public void setBuildPod(String buildPod) {
        this.buildPod = buildPod;
    }

    public String getBuildSvc() {
        return buildSvc;
    }

    public void setBuildSvc(String buildSvc) {
        this.buildSvc = buildSvc;
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
    /**
     * 获取部署状态
     */
    public Integer getStatus() {
        return status;
    }

	/**
     * 设置部署状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    private static final String EMED_RESPONSE = "{\n" +
            "\t\"id\": \"embd-e96a0f0123b946d488cdd7439b28ddb4\",\n" +
            "\t\"object\": \"list\",\n" +
            "\t\"created\": 1744165999,\n" +
            "\t\"model\": \"bge-large-zh-v1.7\",\n" +
            "\t\"data\": [\n" +
            "\t\t{\n" +
            "\t\t\t\"index\": 0,\n" +
            "\t\t\t\"object\": \"embedding\",\n" +
            "\t\t\t\"embedding\": [\n" +
            "\t\t\t\t-0.01422119140625,\n" +
            "\t\t\t\t-0.01230621337890625,\n" +
            "\t\t\t\t-0.025848388671875,\n" +
            "\t\t\t\t0.02178955078125,\n" +
            "\t\t\t\t0.021270751953125,\n" +
            "\t\t\t\t0.013885498046875,\n" +
            "\t\t\t\t-0.0005412101745605469,\n" +
            "\t\t\t\t-0.0146636962890625,\n" +
            "\t\t\t\t-0.00850677490234375,\n" +
            "\t\t\t\t0.03643798828125,\n" +
            "\t\t\t\t0.0238189697265625,\n" +
            "\t\t\t\t0.003101348876953125,\n" +
            "\t\t\t\t0.003955841064453125,\n" +
            "\t\t\t\t-0.01126861572265625,\n" +
            "\t\t\t\t0.033294677734375,\n" +
            "\t\t\t\t0.0228118896484375,\n" +
            "\t\t\t\t0.045196533203125,\n" +
            "\t\t\t\t0.009124755859375,\n" +
            "\t\t\t\t0.03424072265625,\n" +
            "\t\t\t\t-0.0208892822265625,\n" +
            "\t\t\t\t0.0753173828125,\n" +
            "\t\t\t\t-0.0150909423828125,\n" +
            "\t\t\t\t-0.035308837890625,\n" +
            "\t\t\t\t-0.053955078125,\n" +
            "\t\t\t\t0.006473541259765625,\n" +
            "\t\t\t\t0.0013113021850585938,\n" +
            "\t\t\t\t-0.05517578125,\n" +
            "\t\t\t\t0.02630615234375,\n" +
            "\t\t\t\t0.0258941650390625,\n" +
            "\t\t\t\t0.0031070709228515625,\n" +
            "\t\t\t\t0.00036454200744628906,\n" +
            "\t\t\t\t0.035888671875,\n" +
            "\t\t\t\t0.026641845703125,\n" +
            "\t\t\t\t0.033203125,\n" +
            "\t\t\t\t-0.0240020751953125,\n" +
            "\t\t\t\t0.002079010009765625,\n" +
            "\t\t\t\t-0.068359375,\n" +
            "\t\t\t\t-0.0255584716796875,\n" +
            "\t\t\t\t-0.0176849365234375,\n" +
            "\t\t\t\t0.00646209716796875,\n" +
            "\t\t\t\t0.00959014892578125,\n" +
            "\t\t\t\t0.0120697021484375,\n" +
            "\t\t\t\t0.00508880615234375,\n" +
            "\t\t\t\t-0.0269012451171875,\n" +
            "\t\t\t\t0.026519775390625,\n" +
            "\t\t\t\t0.0192718505859375,\n" +
            "\t\t\t\t-0.06109619140625,\n" +
            "\t\t\t\t-0.015655517578125,\n" +
            "\t\t\t\t-0.01125335693359375,\n" +
            "\t\t\t\t0.03533935546875,\n" +
            "\t\t\t\t0.00838470458984375,\n" +
            "\t\t\t\t0.0052947998046875,\n" +
            "\t\t\t\t0.044036865234375,\n" +
            "\t\t\t\t0.01544952392578125,\n" +
            "\t\t\t\t-0.042816162109375,\n" +
            "\t\t\t\t0.05474853515625,\n" +
            "\t\t\t\t0.045806884765625,\n" +
            "\t\t\t\t-0.0213775634765625,\n" +
            "\t\t\t\t-0.0108642578125,\n" +
            "\t\t\t\t-0.022857666015625,\n" +
            "\t\t\t\t-0.0014238357543945312,\n" +
            "\t\t\t\t0.0755615234375,\n" +
            "\t\t\t\t-0.0022907257080078125,\n" +
            "\t\t\t\t-0.0235748291015625,\n" +
            "\t\t\t\t-0.048553466796875,\n" +
            "\t\t\t\t-0.006320953369140625,\n" +
            "\t\t\t\t0.03399658203125,\n" +
            "\t\t\t\t0.0087738037109375,\n" +
            "\t\t\t\t-0.00275421142578125,\n" +
            "\t\t\t\t-0.004962921142578125,\n" +
            "\t\t\t\t0.00628662109375,\n" +
            "\t\t\t\t-0.00015234947204589844,\n" +
            "\t\t\t\t0.040679931640625,\n" +
            "\t\t\t\t0.0227813720703125,\n" +
            "\t\t\t\t-0.001041412353515625,\n" +
            "\t\t\t\t-0.0142669677734375,\n" +
            "\t\t\t\t0.019805908203125,\n" +
            "\t\t\t\t0.02386474609375,\n" +
            "\t\t\t\t0.0726318359375,\n" +
            "\t\t\t\t0.051055908203125,\n" +
            "\t\t\t\t0.00334930419921875,\n" +
            "\t\t\t\t-0.00423431396484375,\n" +
            "\t\t\t\t-0.0207061767578125,\n" +
            "\t\t\t\t0.023956298828125,\n" +
            "\t\t\t\t0.02447509765625,\n" +
            "\t\t\t\t-0.07635498046875,\n" +
            "\t\t\t\t0.02667236328125,\n" +
            "\t\t\t\t0.36474609375,\n" +
            "\t\t\t\t-0.043548583984375,\n" +
            "\t\t\t\t-0.0174407958984375,\n" +
            "\t\t\t\t-0.0418701171875,\n" +
            "\t\t\t\t0.0189056396484375,\n" +
            "\t\t\t\t-0.032440185546875,\n" +
            "\t\t\t\t0.0496826171875,\n" +
            "\t\t\t\t0.0203704833984375,\n" +
            "\t\t\t\t0.01023101806640625,\n" +
            "\t\t\t\t-0.0252838134765625,\n" +
            "\t\t\t\t-0.0164031982421875,\n" +
            "\t\t\t\t-0.020965576171875,\n" +
            "\t\t\t\t-0.033111572265625,\n" +
            "\t\t\t\t0.0284881591796875,\n" +
            "\t\t\t\t-0.01300048828125,\n" +
            "\t\t\t\t-0.00577545166015625,\n" +
            "\t\t\t\t-0.037261962890625,\n" +
            "\t\t\t\t-0.048065185546875,\n" +
            "\t\t\t\t0.018524169921875,\n" +
            "\t\t\t\t0.0004782676696777344,\n" +
            "\t\t\t\t-0.0028476715087890625,\n" +
            "\t\t\t\t0.0343017578125,\n" +
            "\t\t\t\t0.037078857421875,\n" +
            "\t\t\t\t0.05352783203125,\n" +
            "\t\t\t\t-0.0111236572265625,\n" +
            "\t\t\t\t-0.0299072265625,\n" +
            "\t\t\t\t0.0107574462890625,\n" +
            "\t\t\t\t0.009185791015625,\n" +
            "\t\t\t\t-0.04168701171875,\n" +
            "\t\t\t\t-0.028961181640625,\n" +
            "\t\t\t\t0.0182037353515625,\n" +
            "\t\t\t\t0.03094482421875,\n" +
            "\t\t\t\t0.0277099609375,\n" +
            "\t\t\t\t-0.0216217041015625,\n" +
            "\t\t\t\t-0.0033550262451171875,\n" +
            "\t\t\t\t0.032257080078125,\n" +
            "\t\t\t\t-0.014801025390625,\n" +
            "\t\t\t\t0.00934600830078125,\n" +
            "\t\t\t\t0.027069091796875,\n" +
            "\t\t\t\t0.0149383544921875,\n" +
            "\t\t\t\t-0.0139312744140625,\n" +
            "\t\t\t\t-0.0214691162109375,\n" +
            "\t\t\t\t-0.025238037109375,\n" +
            "\t\t\t\t-0.039520263671875,\n" +
            "\t\t\t\t-0.029876708984375,\n" +
            "\t\t\t\t-0.007396697998046875,\n" +
            "\t\t\t\t-0.036956787109375,\n" +
            "\t\t\t\t-0.0382080078125,\n" +
            "\t\t\t\t0.03155517578125,\n" +
            "\t\t\t\t-0.0188446044921875,\n" +
            "\t\t\t\t-0.0271148681640625,\n" +
            "\t\t\t\t0.062103271484375,\n" +
            "\t\t\t\t0.001567840576171875,\n" +
            "\t\t\t\t0.0264434814453125,\n" +
            "\t\t\t\t-0.0032253265380859375,\n" +
            "\t\t\t\t-0.017181396484375,\n" +
            "\t\t\t\t0.01125335693359375,\n" +
            "\t\t\t\t0.0236663818359375,\n" +
            "\t\t\t\t-0.01385498046875,\n" +
            "\t\t\t\t-0.010223388671875,\n" +
            "\t\t\t\t0.034881591796875,\n" +
            "\t\t\t\t-0.006488800048828125,\n" +
            "\t\t\t\t-0.033843994140625,\n" +
            "\t\t\t\t-0.056121826171875,\n" +
            "\t\t\t\t-0.020965576171875,\n" +
            "\t\t\t\t-0.0228118896484375,\n" +
            "\t\t\t\t-0.0237579345703125,\n" +
            "\t\t\t\t-0.020263671875,\n" +
            "\t\t\t\t-0.07965087890625,\n" +
            "\t\t\t\t-0.0008187294006347656,\n" +
            "\t\t\t\t-0.05206298828125,\n" +
            "\t\t\t\t0.0308990478515625,\n" +
            "\t\t\t\t-0.0261993408203125,\n" +
            "\t\t\t\t0.0728759765625,\n" +
            "\t\t\t\t0.0009355545043945312,\n" +
            "\t\t\t\t0.004474639892578125,\n" +
            "\t\t\t\t0.0290374755859375,\n" +
            "\t\t\t\t0.0093994140625,\n" +
            "\t\t\t\t-0.00787353515625,\n" +
            "\t\t\t\t0.047821044921875,\n" +
            "\t\t\t\t-0.035736083984375,\n" +
            "\t\t\t\t-0.005336761474609375,\n" +
            "\t\t\t\t-0.00754547119140625,\n" +
            "\t\t\t\t-0.049102783203125,\n" +
            "\t\t\t\t0.030853271484375,\n" +
            "\t\t\t\t-0.0031452178955078125,\n" +
            "\t\t\t\t0.0119476318359375,\n" +
            "\t\t\t\t-0.0233612060546875,\n" +
            "\t\t\t\t0.015289306640625,\n" +
            "\t\t\t\t-0.03216552734375,\n" +
            "\t\t\t\t0.005832672119140625,\n" +
            "\t\t\t\t-0.00975799560546875,\n" +
            "\t\t\t\t-0.0087432861328125,\n" +
            "\t\t\t\t0.015655517578125,\n" +
            "\t\t\t\t0.0197601318359375,\n" +
            "\t\t\t\t-0.02203369140625,\n" +
            "\t\t\t\t0.0001709461212158203,\n" +
            "\t\t\t\t-0.0360107421875,\n" +
            "\t\t\t\t-0.0018205642700195312,\n" +
            "\t\t\t\t0.036346435546875,\n" +
            "\t\t\t\t0.0849609375,\n" +
            "\t\t\t\t-0.050689697265625,\n" +
            "\t\t\t\t-0.0160675048828125,\n" +
            "\t\t\t\t-0.015228271484375,\n" +
            "\t\t\t\t0.00844573974609375,\n" +
            "\t\t\t\t0.050567626953125,\n" +
            "\t\t\t\t-0.0008001327514648438,\n" +
            "\t\t\t\t0.0235443115234375,\n" +
            "\t\t\t\t-0.059173583984375,\n" +
            "\t\t\t\t-0.0174560546875,\n" +
            "\t\t\t\t0.004360198974609375,\n" +
            "\t\t\t\t0.034698486328125,\n" +
            "\t\t\t\t-0.01251220703125,\n" +
            "\t\t\t\t-0.007282257080078125,\n" +
            "\t\t\t\t0.006443023681640625,\n" +
            "\t\t\t\t0.0110931396484375,\n" +
            "\t\t\t\t0.007625579833984375,\n" +
            "\t\t\t\t0.013671875,\n" +
            "\t\t\t\t-0.04541015625,\n" +
            "\t\t\t\t-0.0028171539306640625,\n" +
            "\t\t\t\t-0.0198974609375,\n" +
            "\t\t\t\t0.045745849609375,\n" +
            "\t\t\t\t0.0250396728515625,\n" +
            "\t\t\t\t-0.0811767578125,\n" +
            "\t\t\t\t0.01207733154296875,\n" +
            "\t\t\t\t-0.020294189453125,\n" +
            "\t\t\t\t-0.059906005859375,\n" +
            "\t\t\t\t-0.00997161865234375,\n" +
            "\t\t\t\t0.0088043212890625,\n" +
            "\t\t\t\t0.01383209228515625,\n" +
            "\t\t\t\t-0.0251922607421875,\n" +
            "\t\t\t\t0.00676727294921875,\n" +
            "\t\t\t\t-0.006855010986328125,\n" +
            "\t\t\t\t-0.0228424072265625,\n" +
            "\t\t\t\t-0.01288604736328125,\n" +
            "\t\t\t\t0.0197296142578125,\n" +
            "\t\t\t\t-0.0121612548828125,\n" +
            "\t\t\t\t-0.00839996337890625,\n" +
            "\t\t\t\t-0.007755279541015625,\n" +
            "\t\t\t\t0.0306549072265625,\n" +
            "\t\t\t\t0.03265380859375,\n" +
            "\t\t\t\t-0.00017976760864257812,\n" +
            "\t\t\t\t0.03509521484375,\n" +
            "\t\t\t\t-0.0104522705078125,\n" +
            "\t\t\t\t-0.0003094673156738281,\n" +
            "\t\t\t\t0.021728515625,\n" +
            "\t\t\t\t0.05230712890625,\n" +
            "\t\t\t\t-0.0183563232421875,\n" +
            "\t\t\t\t0.0084381103515625,\n" +
            "\t\t\t\t-0.019134521484375,\n" +
            "\t\t\t\t-0.00839996337890625,\n" +
            "\t\t\t\t-0.0369873046875,\n" +
            "\t\t\t\t-0.01418304443359375,\n" +
            "\t\t\t\t0.00864410400390625,\n" +
            "\t\t\t\t-0.0066986083984375,\n" +
            "\t\t\t\t-0.0036487579345703125,\n" +
            "\t\t\t\t-0.04876708984375,\n" +
            "\t\t\t\t-0.0024547576904296875,\n" +
            "\t\t\t\t0.005008697509765625,\n" +
            "\t\t\t\t-0.054534912109375,\n" +
            "\t\t\t\t-0.00656890869140625,\n" +
            "\t\t\t\t-0.006946563720703125,\n" +
            "\t\t\t\t-0.027069091796875,\n" +
            "\t\t\t\t-0.0083770751953125,\n" +
            "\t\t\t\t0.03460693359375,\n" +
            "\t\t\t\t0.017181396484375,\n" +
            "\t\t\t\t-0.03179931640625,\n" +
            "\t\t\t\t0.0036258697509765625,\n" +
            "\t\t\t\t-0.020355224609375,\n" +
            "\t\t\t\t0.015655517578125,\n" +
            "\t\t\t\t-0.02117919921875,\n" +
            "\t\t\t\t0.00443267822265625,\n" +
            "\t\t\t\t0.0218353271484375,\n" +
            "\t\t\t\t0.037506103515625,\n" +
            "\t\t\t\t-0.0194244384765625,\n" +
            "\t\t\t\t0.061004638671875,\n" +
            "\t\t\t\t-0.0374755859375,\n" +
            "\t\t\t\t-0.006053924560546875,\n" +
            "\t\t\t\t-0.044769287109375,\n" +
            "\t\t\t\t-0.0050201416015625,\n" +
            "\t\t\t\t0.0288848876953125,\n" +
            "\t\t\t\t-0.045867919921875,\n" +
            "\t\t\t\t0.02667236328125,\n" +
            "\t\t\t\t0.00960540771484375,\n" +
            "\t\t\t\t-0.020294189453125,\n" +
            "\t\t\t\t0.00704193115234375,\n" +
            "\t\t\t\t-0.0174102783203125,\n" +
            "\t\t\t\t0.0275115966796875,\n" +
            "\t\t\t\t0.0117034912109375,\n" +
            "\t\t\t\t-0.040679931640625,\n" +
            "\t\t\t\t-0.007476806640625,\n" +
            "\t\t\t\t-0.00045752525329589844,\n" +
            "\t\t\t\t0.004955291748046875,\n" +
            "\t\t\t\t0.0207672119140625,\n" +
            "\t\t\t\t-0.041046142578125,\n" +
            "\t\t\t\t0.035064697265625,\n" +
            "\t\t\t\t-0.0242156982421875,\n" +
            "\t\t\t\t0.004924774169921875,\n" +
            "\t\t\t\t0.046844482421875,\n" +
            "\t\t\t\t-0.015655517578125,\n" +
            "\t\t\t\t-0.034271240234375,\n" +
            "\t\t\t\t-0.0006742477416992188,\n" +
            "\t\t\t\t-0.024658203125,\n" +
            "\t\t\t\t-0.001361846923828125,\n" +
            "\t\t\t\t-0.00806427001953125,\n" +
            "\t\t\t\t-0.01369476318359375,\n" +
            "\t\t\t\t-0.0031337738037109375,\n" +
            "\t\t\t\t-0.00997161865234375,\n" +
            "\t\t\t\t0.01593017578125,\n" +
            "\t\t\t\t0.02838134765625,\n" +
            "\t\t\t\t0.003536224365234375,\n" +
            "\t\t\t\t-0.0250701904296875,\n" +
            "\t\t\t\t-0.060150146484375,\n" +
            "\t\t\t\t-0.03173828125,\n" +
            "\t\t\t\t0.004268646240234375,\n" +
            "\t\t\t\t0.030975341796875,\n" +
            "\t\t\t\t-0.0289306640625,\n" +
            "\t\t\t\t0.03564453125,\n" +
            "\t\t\t\t0.0199432373046875,\n" +
            "\t\t\t\t0.006137847900390625,\n" +
            "\t\t\t\t-0.03778076171875,\n" +
            "\t\t\t\t0.0087432861328125,\n" +
            "\t\t\t\t0.01763916015625,\n" +
            "\t\t\t\t0.01042938232421875,\n" +
            "\t\t\t\t-0.047607421875,\n" +
            "\t\t\t\t0.06365966796875,\n" +
            "\t\t\t\t-0.04827880859375,\n" +
            "\t\t\t\t0.03863525390625,\n" +
            "\t\t\t\t-0.036712646484375,\n" +
            "\t\t\t\t0.007843017578125,\n" +
            "\t\t\t\t0.034698486328125,\n" +
            "\t\t\t\t-0.0146636962890625,\n" +
            "\t\t\t\t0.01959228515625,\n" +
            "\t\t\t\t-0.007251739501953125,\n" +
            "\t\t\t\t0.034149169921875,\n" +
            "\t\t\t\t0.01464080810546875,\n" +
            "\t\t\t\t-0.033599853515625,\n" +
            "\t\t\t\t-0.0155487060546875,\n" +
            "\t\t\t\t-0.004375457763671875,\n" +
            "\t\t\t\t0.002262115478515625,\n" +
            "\t\t\t\t0.004474639892578125,\n" +
            "\t\t\t\t0.0284423828125,\n" +
            "\t\t\t\t-0.018951416015625,\n" +
            "\t\t\t\t0.07305908203125,\n" +
            "\t\t\t\t0.027191162109375,\n" +
            "\t\t\t\t-0.0304718017578125,\n" +
            "\t\t\t\t-0.0213623046875,\n" +
            "\t\t\t\t-0.029937744140625,\n" +
            "\t\t\t\t0.044036865234375,\n" +
            "\t\t\t\t0.028167724609375,\n" +
            "\t\t\t\t-0.01361083984375,\n" +
            "\t\t\t\t-0.0091705322265625,\n" +
            "\t\t\t\t0.007801055908203125,\n" +
            "\t\t\t\t0.00035262107849121094,\n" +
            "\t\t\t\t0.007083892822265625,\n" +
            "\t\t\t\t-0.03778076171875,\n" +
            "\t\t\t\t0.012542724609375,\n" +
            "\t\t\t\t-0.018890380859375,\n" +
            "\t\t\t\t0.00615692138671875,\n" +
            "\t\t\t\t0.00972747802734375,\n" +
            "\t\t\t\t0.0013294219970703125,\n" +
            "\t\t\t\t-0.0168914794921875,\n" +
            "\t\t\t\t-0.036163330078125,\n" +
            "\t\t\t\t-0.01410675048828125,\n" +
            "\t\t\t\t0.056427001953125,\n" +
            "\t\t\t\t0.0196685791015625,\n" +
            "\t\t\t\t-0.00653839111328125,\n" +
            "\t\t\t\t0.04046630859375,\n" +
            "\t\t\t\t0.0104217529296875,\n" +
            "\t\t\t\t-0.0101165771484375,\n" +
            "\t\t\t\t-0.005413055419921875,\n" +
            "\t\t\t\t-0.031982421875,\n" +
            "\t\t\t\t0.0188446044921875,\n" +
            "\t\t\t\t0.00528717041015625,\n" +
            "\t\t\t\t-0.01343536376953125,\n" +
            "\t\t\t\t-0.0026416778564453125,\n" +
            "\t\t\t\t-0.021728515625,\n" +
            "\t\t\t\t0.011688232421875,\n" +
            "\t\t\t\t-0.023712158203125,\n" +
            "\t\t\t\t-0.035064697265625,\n" +
            "\t\t\t\t-0.0260467529296875,\n" +
            "\t\t\t\t0.0330810546875,\n" +
            "\t\t\t\t0.0246124267578125,\n" +
            "\t\t\t\t0.01213836669921875,\n" +
            "\t\t\t\t0.016021728515625,\n" +
            "\t\t\t\t0.01265716552734375,\n" +
            "\t\t\t\t-0.0648193359375,\n" +
            "\t\t\t\t0.0233306884765625,\n" +
            "\t\t\t\t0.0169219970703125,\n" +
            "\t\t\t\t-0.028656005859375,\n" +
            "\t\t\t\t0.01186370849609375,\n" +
            "\t\t\t\t-0.003719329833984375,\n" +
            "\t\t\t\t-0.035369873046875,\n" +
            "\t\t\t\t-0.028656005859375,\n" +
            "\t\t\t\t-0.01654052734375,\n" +
            "\t\t\t\t0.0164794921875,\n" +
            "\t\t\t\t0.01331329345703125,\n" +
            "\t\t\t\t0.0357666015625,\n" +
            "\t\t\t\t-0.0014142990112304688,\n" +
            "\t\t\t\t0.01004791259765625,\n" +
            "\t\t\t\t-0.0138702392578125,\n" +
            "\t\t\t\t0.0047607421875,\n" +
            "\t\t\t\t0.007350921630859375,\n" +
            "\t\t\t\t-0.037445068359375,\n" +
            "\t\t\t\t-0.0211181640625,\n" +
            "\t\t\t\t-0.0184173583984375,\n" +
            "\t\t\t\t0.00939178466796875,\n" +
            "\t\t\t\t-0.00875091552734375,\n" +
            "\t\t\t\t-0.020904541015625,\n" +
            "\t\t\t\t-0.06317138671875,\n" +
            "\t\t\t\t-0.0221405029296875,\n" +
            "\t\t\t\t-0.0250091552734375,\n" +
            "\t\t\t\t0.0263519287109375,\n" +
            "\t\t\t\t0.00904083251953125,\n" +
            "\t\t\t\t0.0010824203491210938,\n" +
            "\t\t\t\t0.00168609619140625,\n" +
            "\t\t\t\t0.0035228729248046875,\n" +
            "\t\t\t\t0.0179595947265625,\n" +
            "\t\t\t\t0.0256805419921875,\n" +
            "\t\t\t\t-0.0213775634765625,\n" +
            "\t\t\t\t-0.036712646484375,\n" +
            "\t\t\t\t0.070556640625,\n" +
            "\t\t\t\t0.002613067626953125,\n" +
            "\t\t\t\t0.0213623046875,\n" +
            "\t\t\t\t0.025726318359375,\n" +
            "\t\t\t\t-0.01629638671875,\n" +
            "\t\t\t\t0.00823211669921875,\n" +
            "\t\t\t\t0.00485992431640625,\n" +
            "\t\t\t\t0.016571044921875,\n" +
            "\t\t\t\t-0.00396728515625,\n" +
            "\t\t\t\t0.05206298828125,\n" +
            "\t\t\t\t0.003368377685546875,\n" +
            "\t\t\t\t-0.03350830078125,\n" +
            "\t\t\t\t0.0025787353515625,\n" +
            "\t\t\t\t-0.01160430908203125,\n" +
            "\t\t\t\t0.006103515625,\n" +
            "\t\t\t\t0.0209808349609375,\n" +
            "\t\t\t\t0.033599853515625,\n" +
            "\t\t\t\t-0.0159454345703125,\n" +
            "\t\t\t\t0.006557464599609375,\n" +
            "\t\t\t\t-0.0029964447021484375,\n" +
            "\t\t\t\t-0.03448486328125,\n" +
            "\t\t\t\t-0.007190704345703125,\n" +
            "\t\t\t\t-0.02020263671875,\n" +
            "\t\t\t\t-0.0400390625,\n" +
            "\t\t\t\t-0.002895355224609375,\n" +
            "\t\t\t\t0.0003981590270996094,\n" +
            "\t\t\t\t-0.0271759033203125,\n" +
            "\t\t\t\t0.023223876953125,\n" +
            "\t\t\t\t0.005199432373046875,\n" +
            "\t\t\t\t0.02813720703125,\n" +
            "\t\t\t\t-0.002033233642578125,\n" +
            "\t\t\t\t0.000017940998077392578,\n" +
            "\t\t\t\t0.0030460357666015625,\n" +
            "\t\t\t\t-0.016143798828125,\n" +
            "\t\t\t\t-0.0033893585205078125,\n" +
            "\t\t\t\t0.03228759765625,\n" +
            "\t\t\t\t0.00797271728515625,\n" +
            "\t\t\t\t0.0731201171875,\n" +
            "\t\t\t\t0.013336181640625,\n" +
            "\t\t\t\t-0.02630615234375,\n" +
            "\t\t\t\t0.010833740234375,\n" +
            "\t\t\t\t0.04351806640625,\n" +
            "\t\t\t\t0.03924560546875,\n" +
            "\t\t\t\t0.08660888671875,\n" +
            "\t\t\t\t-0.00279998779296875,\n" +
            "\t\t\t\t-0.05865478515625,\n" +
            "\t\t\t\t0.046173095703125,\n" +
            "\t\t\t\t-0.0199432373046875,\n" +
            "\t\t\t\t-0.0096893310546875,\n" +
            "\t\t\t\t-0.0633544921875,\n" +
            "\t\t\t\t0.00884246826171875,\n" +
            "\t\t\t\t-0.00804901123046875,\n" +
            "\t\t\t\t0.060943603515625,\n" +
            "\t\t\t\t0.00760650634765625,\n" +
            "\t\t\t\t-0.01224517822265625,\n" +
            "\t\t\t\t-0.0284423828125,\n" +
            "\t\t\t\t-0.0299530029296875,\n" +
            "\t\t\t\t-0.014892578125,\n" +
            "\t\t\t\t-0.028045654296875,\n" +
            "\t\t\t\t-0.00901031494140625,\n" +
            "\t\t\t\t0.009521484375,\n" +
            "\t\t\t\t-0.021759033203125,\n" +
            "\t\t\t\t-0.01666259765625,\n" +
            "\t\t\t\t-0.01067352294921875,\n" +
            "\t\t\t\t0.0251007080078125,\n" +
            "\t\t\t\t-0.030426025390625,\n" +
            "\t\t\t\t-0.01538848876953125,\n" +
            "\t\t\t\t0.034942626953125,\n" +
            "\t\t\t\t-0.006561279296875,\n" +
            "\t\t\t\t-0.052001953125,\n" +
            "\t\t\t\t-0.0555419921875,\n" +
            "\t\t\t\t0.006046295166015625,\n" +
            "\t\t\t\t0.0372314453125,\n" +
            "\t\t\t\t0.020904541015625,\n" +
            "\t\t\t\t0.0063934326171875,\n" +
            "\t\t\t\t0.026947021484375,\n" +
            "\t\t\t\t0.02679443359375,\n" +
            "\t\t\t\t-0.0400390625,\n" +
            "\t\t\t\t0.0005788803100585938,\n" +
            "\t\t\t\t-0.0298004150390625,\n" +
            "\t\t\t\t0.004718780517578125,\n" +
            "\t\t\t\t0.0023059844970703125,\n" +
            "\t\t\t\t-0.0043487548828125,\n" +
            "\t\t\t\t-0.0090789794921875,\n" +
            "\t\t\t\t-0.0242156982421875,\n" +
            "\t\t\t\t-0.0311431884765625,\n" +
            "\t\t\t\t0.01171112060546875,\n" +
            "\t\t\t\t0.0094757080078125,\n" +
            "\t\t\t\t0.029937744140625,\n" +
            "\t\t\t\t0.0235748291015625,\n" +
            "\t\t\t\t0.01666259765625,\n" +
            "\t\t\t\t-0.0192718505859375,\n" +
            "\t\t\t\t0.0301055908203125,\n" +
            "\t\t\t\t-0.0165557861328125,\n" +
            "\t\t\t\t-0.03253173828125,\n" +
            "\t\t\t\t0.003170013427734375,\n" +
            "\t\t\t\t0.0225677490234375,\n" +
            "\t\t\t\t-0.079345703125,\n" +
            "\t\t\t\t-0.0579833984375,\n" +
            "\t\t\t\t-0.0009794235229492188,\n" +
            "\t\t\t\t0.042510986328125,\n" +
            "\t\t\t\t0.0322265625,\n" +
            "\t\t\t\t0.05950927734375,\n" +
            "\t\t\t\t-0.03765869140625,\n" +
            "\t\t\t\t-0.020843505859375,\n" +
            "\t\t\t\t0.01055145263671875,\n" +
            "\t\t\t\t-0.0224456787109375,\n" +
            "\t\t\t\t0.024444580078125,\n" +
            "\t\t\t\t0.021697998046875,\n" +
            "\t\t\t\t0.04547119140625,\n" +
            "\t\t\t\t0.017974853515625,\n" +
            "\t\t\t\t0.0296783447265625,\n" +
            "\t\t\t\t-0.012237548828125,\n" +
            "\t\t\t\t0.004024505615234375,\n" +
            "\t\t\t\t0.03558349609375,\n" +
            "\t\t\t\t-0.0198516845703125,\n" +
            "\t\t\t\t0.0165252685546875,\n" +
            "\t\t\t\t0.049591064453125,\n" +
            "\t\t\t\t0.0205841064453125,\n" +
            "\t\t\t\t0.0184173583984375,\n" +
            "\t\t\t\t-0.03436279296875,\n" +
            "\t\t\t\t-0.060516357421875,\n" +
            "\t\t\t\t-0.041778564453125,\n" +
            "\t\t\t\t0.0193023681640625,\n" +
            "\t\t\t\t0.05218505859375,\n" +
            "\t\t\t\t-0.034423828125,\n" +
            "\t\t\t\t0.0007357597351074219,\n" +
            "\t\t\t\t-0.0024433135986328125,\n" +
            "\t\t\t\t0.01495361328125,\n" +
            "\t\t\t\t0.0579833984375,\n" +
            "\t\t\t\t-0.003772735595703125,\n" +
            "\t\t\t\t0.028564453125,\n" +
            "\t\t\t\t-0.003749847412109375,\n" +
            "\t\t\t\t-0.0291595458984375,\n" +
            "\t\t\t\t0.045318603515625,\n" +
            "\t\t\t\t0.00334930419921875,\n" +
            "\t\t\t\t-0.01088714599609375,\n" +
            "\t\t\t\t-0.031982421875,\n" +
            "\t\t\t\t-0.0154876708984375,\n" +
            "\t\t\t\t-0.037933349609375,\n" +
            "\t\t\t\t0.0006494522094726562,\n" +
            "\t\t\t\t-0.00614166259765625,\n" +
            "\t\t\t\t-0.01788330078125,\n" +
            "\t\t\t\t0.01485443115234375,\n" +
            "\t\t\t\t0.04290771484375,\n" +
            "\t\t\t\t-0.029754638671875,\n" +
            "\t\t\t\t0.050201416015625,\n" +
            "\t\t\t\t-0.01219940185546875,\n" +
            "\t\t\t\t-0.00937652587890625,\n" +
            "\t\t\t\t0.0374755859375,\n" +
            "\t\t\t\t0.0372314453125,\n" +
            "\t\t\t\t0.0290679931640625,\n" +
            "\t\t\t\t0.01107025146484375,\n" +
            "\t\t\t\t0.0482177734375,\n" +
            "\t\t\t\t0.01349639892578125,\n" +
            "\t\t\t\t0.04803466796875,\n" +
            "\t\t\t\t-0.056884765625,\n" +
            "\t\t\t\t-0.0239410400390625,\n" +
            "\t\t\t\t0.00867462158203125,\n" +
            "\t\t\t\t0.006805419921875,\n" +
            "\t\t\t\t-0.019500732421875,\n" +
            "\t\t\t\t0.01277923583984375,\n" +
            "\t\t\t\t-0.0307769775390625,\n" +
            "\t\t\t\t0.00179290771484375,\n" +
            "\t\t\t\t-0.01444244384765625,\n" +
            "\t\t\t\t-0.0007352828979492188,\n" +
            "\t\t\t\t0.0167999267578125,\n" +
            "\t\t\t\t-0.01416015625,\n" +
            "\t\t\t\t-0.014678955078125,\n" +
            "\t\t\t\t-0.055633544921875,\n" +
            "\t\t\t\t-0.0260772705078125,\n" +
            "\t\t\t\t-0.0408935546875,\n" +
            "\t\t\t\t0.02252197265625,\n" +
            "\t\t\t\t-0.03131103515625,\n" +
            "\t\t\t\t-0.030975341796875,\n" +
            "\t\t\t\t0.03631591796875,\n" +
            "\t\t\t\t0.004955291748046875,\n" +
            "\t\t\t\t-0.00313568115234375,\n" +
            "\t\t\t\t0.035125732421875,\n" +
            "\t\t\t\t0.03961181640625,\n" +
            "\t\t\t\t-0.0138702392578125,\n" +
            "\t\t\t\t0.01678466796875,\n" +
            "\t\t\t\t0.00830078125,\n" +
            "\t\t\t\t-0.0008668899536132812,\n" +
            "\t\t\t\t0.00777435302734375,\n" +
            "\t\t\t\t0.0094146728515625,\n" +
            "\t\t\t\t-0.00913238525390625,\n" +
            "\t\t\t\t-0.05096435546875,\n" +
            "\t\t\t\t-0.006755828857421875,\n" +
            "\t\t\t\t0.0223541259765625,\n" +
            "\t\t\t\t0.0165863037109375,\n" +
            "\t\t\t\t0.00833892822265625,\n" +
            "\t\t\t\t-0.03460693359375,\n" +
            "\t\t\t\t-0.0301513671875,\n" +
            "\t\t\t\t0.01629638671875,\n" +
            "\t\t\t\t-0.040985107421875,\n" +
            "\t\t\t\t0.05413818359375,\n" +
            "\t\t\t\t-0.00543975830078125,\n" +
            "\t\t\t\t0.02252197265625,\n" +
            "\t\t\t\t0.038421630859375,\n" +
            "\t\t\t\t-0.00568389892578125,\n" +
            "\t\t\t\t-0.0007877349853515625,\n" +
            "\t\t\t\t0.01430511474609375,\n" +
            "\t\t\t\t-0.014190673828125,\n" +
            "\t\t\t\t-0.047943115234375,\n" +
            "\t\t\t\t-0.006488800048828125,\n" +
            "\t\t\t\t-0.00786590576171875,\n" +
            "\t\t\t\t-0.0821533203125,\n" +
            "\t\t\t\t0.00043511390686035156,\n" +
            "\t\t\t\t0.002193450927734375,\n" +
            "\t\t\t\t-0.03179931640625,\n" +
            "\t\t\t\t-0.0109710693359375,\n" +
            "\t\t\t\t0.04071044921875,\n" +
            "\t\t\t\t0.007297515869140625,\n" +
            "\t\t\t\t0.000055849552154541016,\n" +
            "\t\t\t\t0.020050048828125,\n" +
            "\t\t\t\t-0.0284576416015625,\n" +
            "\t\t\t\t-0.0171051025390625,\n" +
            "\t\t\t\t-0.0338134765625,\n" +
            "\t\t\t\t0.0172271728515625,\n" +
            "\t\t\t\t-0.01373291015625,\n" +
            "\t\t\t\t-0.022125244140625,\n" +
            "\t\t\t\t0.04449462890625,\n" +
            "\t\t\t\t0.0242919921875,\n" +
            "\t\t\t\t-0.0166168212890625,\n" +
            "\t\t\t\t-0.002346038818359375,\n" +
            "\t\t\t\t-0.0115509033203125,\n" +
            "\t\t\t\t0.0184326171875,\n" +
            "\t\t\t\t0.029266357421875,\n" +
            "\t\t\t\t-0.018768310546875,\n" +
            "\t\t\t\t0.037506103515625,\n" +
            "\t\t\t\t0.00006568431854248047,\n" +
            "\t\t\t\t0.01282501220703125,\n" +
            "\t\t\t\t-0.0015201568603515625,\n" +
            "\t\t\t\t0.006927490234375,\n" +
            "\t\t\t\t-0.0037784576416015625,\n" +
            "\t\t\t\t0.01154327392578125,\n" +
            "\t\t\t\t-0.026519775390625,\n" +
            "\t\t\t\t0.004611968994140625,\n" +
            "\t\t\t\t0.0025577545166015625,\n" +
            "\t\t\t\t-0.038787841796875,\n" +
            "\t\t\t\t-0.037109375,\n" +
            "\t\t\t\t-0.025390625,\n" +
            "\t\t\t\t0.01554107666015625,\n" +
            "\t\t\t\t-0.07867431640625,\n" +
            "\t\t\t\t0.034210205078125,\n" +
            "\t\t\t\t0.007724761962890625,\n" +
            "\t\t\t\t0.01220703125,\n" +
            "\t\t\t\t0.000007569789886474609,\n" +
            "\t\t\t\t-0.041015625,\n" +
            "\t\t\t\t0.0009603500366210938,\n" +
            "\t\t\t\t0.01096343994140625,\n" +
            "\t\t\t\t-0.0009322166442871094,\n" +
            "\t\t\t\t-0.03277587890625,\n" +
            "\t\t\t\t0.0555419921875,\n" +
            "\t\t\t\t0.027313232421875,\n" +
            "\t\t\t\t-0.01079559326171875,\n" +
            "\t\t\t\t-0.07073974609375,\n" +
            "\t\t\t\t-0.03399658203125,\n" +
            "\t\t\t\t0.0009870529174804688,\n" +
            "\t\t\t\t-0.003757476806640625,\n" +
            "\t\t\t\t-0.0208892822265625,\n" +
            "\t\t\t\t0.0181732177734375,\n" +
            "\t\t\t\t-0.045623779296875,\n" +
            "\t\t\t\t-0.006992340087890625,\n" +
            "\t\t\t\t0.0447998046875,\n" +
            "\t\t\t\t-0.007114410400390625,\n" +
            "\t\t\t\t-0.040008544921875,\n" +
            "\t\t\t\t0.0008530616760253906,\n" +
            "\t\t\t\t0.038330078125,\n" +
            "\t\t\t\t0.010498046875,\n" +
            "\t\t\t\t-0.025177001953125,\n" +
            "\t\t\t\t-0.012664794921875,\n" +
            "\t\t\t\t-0.00913238525390625,\n" +
            "\t\t\t\t0.05792236328125,\n" +
            "\t\t\t\t-0.049530029296875,\n" +
            "\t\t\t\t0.0214691162109375,\n" +
            "\t\t\t\t-0.02386474609375,\n" +
            "\t\t\t\t0.03253173828125,\n" +
            "\t\t\t\t0.0171356201171875,\n" +
            "\t\t\t\t0.09393310546875,\n" +
            "\t\t\t\t-0.0262298583984375,\n" +
            "\t\t\t\t0.0123748779296875,\n" +
            "\t\t\t\t-0.056182861328125,\n" +
            "\t\t\t\t0.007495880126953125,\n" +
            "\t\t\t\t0.014739990234375,\n" +
            "\t\t\t\t0.026763916015625,\n" +
            "\t\t\t\t0.00417327880859375,\n" +
            "\t\t\t\t-0.0094451904296875,\n" +
            "\t\t\t\t-0.02813720703125,\n" +
            "\t\t\t\t0.032806396484375,\n" +
            "\t\t\t\t0.047943115234375,\n" +
            "\t\t\t\t0.02178955078125,\n" +
            "\t\t\t\t0.01416778564453125,\n" +
            "\t\t\t\t-0.00696563720703125,\n" +
            "\t\t\t\t-0.01151275634765625,\n" +
            "\t\t\t\t-0.0291290283203125,\n" +
            "\t\t\t\t0.053955078125,\n" +
            "\t\t\t\t0.04998779296875,\n" +
            "\t\t\t\t-0.02703857421875,\n" +
            "\t\t\t\t-0.01309967041015625,\n" +
            "\t\t\t\t0.044189453125,\n" +
            "\t\t\t\t-0.00305938720703125,\n" +
            "\t\t\t\t-0.0435791015625,\n" +
            "\t\t\t\t-0.00812530517578125,\n" +
            "\t\t\t\t-0.018035888671875,\n" +
            "\t\t\t\t0.058135986328125,\n" +
            "\t\t\t\t0.014556884765625,\n" +
            "\t\t\t\t-0.007297515869140625,\n" +
            "\t\t\t\t-0.036285400390625,\n" +
            "\t\t\t\t0.001728057861328125,\n" +
            "\t\t\t\t0.01605224609375,\n" +
            "\t\t\t\t0.014739990234375,\n" +
            "\t\t\t\t0.054229736328125,\n" +
            "\t\t\t\t-0.0023517608642578125,\n" +
            "\t\t\t\t-0.00579071044921875,\n" +
            "\t\t\t\t-0.0103302001953125,\n" +
            "\t\t\t\t-0.069580078125,\n" +
            "\t\t\t\t0.0372314453125,\n" +
            "\t\t\t\t-0.0093231201171875,\n" +
            "\t\t\t\t0.0084075927734375,\n" +
            "\t\t\t\t0.00016939640045166016,\n" +
            "\t\t\t\t0.0190887451171875,\n" +
            "\t\t\t\t-0.029998779296875,\n" +
            "\t\t\t\t-0.04681396484375,\n" +
            "\t\t\t\t-0.0517578125,\n" +
            "\t\t\t\t0.00015306472778320312,\n" +
            "\t\t\t\t0.0236663818359375,\n" +
            "\t\t\t\t0.016021728515625,\n" +
            "\t\t\t\t-0.0677490234375,\n" +
            "\t\t\t\t-0.039886474609375,\n" +
            "\t\t\t\t-0.031402587890625,\n" +
            "\t\t\t\t-0.016693115234375,\n" +
            "\t\t\t\t0.0775146484375,\n" +
            "\t\t\t\t0.03497314453125,\n" +
            "\t\t\t\t0.0191497802734375,\n" +
            "\t\t\t\t-0.01197052001953125,\n" +
            "\t\t\t\t-0.04058837890625,\n" +
            "\t\t\t\t0.020660400390625,\n" +
            "\t\t\t\t0.01059722900390625,\n" +
            "\t\t\t\t-0.06805419921875,\n" +
            "\t\t\t\t0.0085906982421875,\n" +
            "\t\t\t\t0.01291656494140625,\n" +
            "\t\t\t\t0.0081634521484375,\n" +
            "\t\t\t\t-0.034271240234375,\n" +
            "\t\t\t\t-0.007595062255859375,\n" +
            "\t\t\t\t-0.017303466796875,\n" +
            "\t\t\t\t0.04937744140625,\n" +
            "\t\t\t\t-0.04278564453125,\n" +
            "\t\t\t\t0.00689697265625,\n" +
            "\t\t\t\t0.00811004638671875,\n" +
            "\t\t\t\t-0.0165863037109375,\n" +
            "\t\t\t\t-0.0160980224609375,\n" +
            "\t\t\t\t-0.01007843017578125,\n" +
            "\t\t\t\t0.01146697998046875,\n" +
            "\t\t\t\t-0.037109375,\n" +
            "\t\t\t\t0.0097503662109375,\n" +
            "\t\t\t\t-0.10498046875,\n" +
            "\t\t\t\t-0.0201568603515625,\n" +
            "\t\t\t\t-0.000025153160095214844,\n" +
            "\t\t\t\t0.00965118408203125,\n" +
            "\t\t\t\t-0.0136871337890625,\n" +
            "\t\t\t\t-0.0193634033203125,\n" +
            "\t\t\t\t0.043853759765625,\n" +
            "\t\t\t\t-0.006748199462890625,\n" +
            "\t\t\t\t0.0158843994140625,\n" +
            "\t\t\t\t-0.044525146484375,\n" +
            "\t\t\t\t-0.0335693359375,\n" +
            "\t\t\t\t-0.01358795166015625,\n" +
            "\t\t\t\t0.0050201416015625,\n" +
            "\t\t\t\t0.032135009765625,\n" +
            "\t\t\t\t-0.03057861328125,\n" +
            "\t\t\t\t-0.033447265625,\n" +
            "\t\t\t\t-0.0204925537109375,\n" +
            "\t\t\t\t-0.0221099853515625,\n" +
            "\t\t\t\t0.0176544189453125,\n" +
            "\t\t\t\t-0.035614013671875,\n" +
            "\t\t\t\t0.02130126953125,\n" +
            "\t\t\t\t0.0298614501953125,\n" +
            "\t\t\t\t-0.01265716552734375,\n" +
            "\t\t\t\t0.03778076171875,\n" +
            "\t\t\t\t-0.00536346435546875,\n" +
            "\t\t\t\t-0.0208587646484375,\n" +
            "\t\t\t\t0.0208587646484375,\n" +
            "\t\t\t\t-0.00603485107421875,\n" +
            "\t\t\t\t0.022674560546875,\n" +
            "\t\t\t\t0.0235443115234375,\n" +
            "\t\t\t\t-0.072509765625,\n" +
            "\t\t\t\t-0.0616455078125,\n" +
            "\t\t\t\t0.0015048980712890625,\n" +
            "\t\t\t\t-0.006626129150390625,\n" +
            "\t\t\t\t-0.024169921875,\n" +
            "\t\t\t\t-0.03607177734375,\n" +
            "\t\t\t\t0.043853759765625,\n" +
            "\t\t\t\t-0.0009455680847167969,\n" +
            "\t\t\t\t0.045074462890625,\n" +
            "\t\t\t\t0.016082763671875,\n" +
            "\t\t\t\t-0.0780029296875,\n" +
            "\t\t\t\t-0.0418701171875,\n" +
            "\t\t\t\t-0.049407958984375,\n" +
            "\t\t\t\t-0.05084228515625,\n" +
            "\t\t\t\t0.00833892822265625,\n" +
            "\t\t\t\t-0.01861572265625,\n" +
            "\t\t\t\t-0.036956787109375,\n" +
            "\t\t\t\t0.0199737548828125,\n" +
            "\t\t\t\t-0.0299835205078125,\n" +
            "\t\t\t\t-0.033172607421875,\n" +
            "\t\t\t\t0.0214080810546875,\n" +
            "\t\t\t\t0.002864837646484375,\n" +
            "\t\t\t\t-0.01284027099609375,\n" +
            "\t\t\t\t0.00997161865234375,\n" +
            "\t\t\t\t-0.0162200927734375,\n" +
            "\t\t\t\t-0.0265960693359375,\n" +
            "\t\t\t\t0.00498199462890625,\n" +
            "\t\t\t\t-0.0120391845703125,\n" +
            "\t\t\t\t0.020233154296875,\n" +
            "\t\t\t\t0.07635498046875,\n" +
            "\t\t\t\t0.00007241964340209961,\n" +
            "\t\t\t\t0.03790283203125,\n" +
            "\t\t\t\t-0.0258331298828125,\n" +
            "\t\t\t\t-0.035614013671875,\n" +
            "\t\t\t\t-0.0038909912109375,\n" +
            "\t\t\t\t-0.032196044921875,\n" +
            "\t\t\t\t0.030609130859375,\n" +
            "\t\t\t\t-0.0112762451171875,\n" +
            "\t\t\t\t0.0174560546875,\n" +
            "\t\t\t\t-0.03411865234375,\n" +
            "\t\t\t\t0.0450439453125,\n" +
            "\t\t\t\t-0.02093505859375,\n" +
            "\t\t\t\t-0.0230712890625,\n" +
            "\t\t\t\t0.0261993408203125,\n" +
            "\t\t\t\t-0.0005946159362792969,\n" +
            "\t\t\t\t-0.027984619140625,\n" +
            "\t\t\t\t0.0214996337890625,\n" +
            "\t\t\t\t0.036590576171875,\n" +
            "\t\t\t\t-0.033355712890625,\n" +
            "\t\t\t\t0.046417236328125,\n" +
            "\t\t\t\t0.018890380859375,\n" +
            "\t\t\t\t0.015411376953125,\n" +
            "\t\t\t\t0.028594970703125,\n" +
            "\t\t\t\t0.0287322998046875,\n" +
            "\t\t\t\t-0.02630615234375,\n" +
            "\t\t\t\t-0.02435302734375,\n" +
            "\t\t\t\t-0.050323486328125,\n" +
            "\t\t\t\t0.0283355712890625,\n" +
            "\t\t\t\t0.007724761962890625,\n" +
            "\t\t\t\t-0.0245513916015625,\n" +
            "\t\t\t\t-0.02325439453125,\n" +
            "\t\t\t\t0.05816650390625,\n" +
            "\t\t\t\t-0.0181427001953125,\n" +
            "\t\t\t\t0.0007534027099609375,\n" +
            "\t\t\t\t0.04425048828125,\n" +
            "\t\t\t\t-0.0330810546875,\n" +
            "\t\t\t\t-0.032806396484375,\n" +
            "\t\t\t\t-0.0240020751953125,\n" +
            "\t\t\t\t0.0013113021850585938,\n" +
            "\t\t\t\t0.0269775390625,\n" +
            "\t\t\t\t0.00556182861328125,\n" +
            "\t\t\t\t-0.06365966796875,\n" +
            "\t\t\t\t-0.004627227783203125,\n" +
            "\t\t\t\t-0.043853759765625,\n" +
            "\t\t\t\t0.006961822509765625,\n" +
            "\t\t\t\t0.004608154296875,\n" +
            "\t\t\t\t0.0126953125,\n" +
            "\t\t\t\t0.034820556640625,\n" +
            "\t\t\t\t-0.0106048583984375,\n" +
            "\t\t\t\t-0.04290771484375,\n" +
            "\t\t\t\t0.0178070068359375,\n" +
            "\t\t\t\t-0.024871826171875,\n" +
            "\t\t\t\t0.0215301513671875,\n" +
            "\t\t\t\t0.02398681640625,\n" +
            "\t\t\t\t-0.0218963623046875,\n" +
            "\t\t\t\t-0.0166015625,\n" +
            "\t\t\t\t0.018524169921875,\n" +
            "\t\t\t\t0.037445068359375,\n" +
            "\t\t\t\t-0.01739501953125,\n" +
            "\t\t\t\t-0.01239013671875,\n" +
            "\t\t\t\t0.01045989990234375,\n" +
            "\t\t\t\t-0.028289794921875,\n" +
            "\t\t\t\t0.037628173828125,\n" +
            "\t\t\t\t-0.0017709732055664062,\n" +
            "\t\t\t\t-0.0197601318359375,\n" +
            "\t\t\t\t0.030426025390625,\n" +
            "\t\t\t\t-0.0061798095703125,\n" +
            "\t\t\t\t-0.05194091796875,\n" +
            "\t\t\t\t0.016265869140625,\n" +
            "\t\t\t\t-0.0156402587890625,\n" +
            "\t\t\t\t-0.0225982666015625,\n" +
            "\t\t\t\t-0.01082611083984375,\n" +
            "\t\t\t\t-0.0237579345703125,\n" +
            "\t\t\t\t-0.012298583984375,\n" +
            "\t\t\t\t-0.016204833984375,\n" +
            "\t\t\t\t-0.01079559326171875,\n" +
            "\t\t\t\t-0.01465606689453125,\n" +
            "\t\t\t\t0.0235595703125,\n" +
            "\t\t\t\t-0.0127410888671875,\n" +
            "\t\t\t\t-0.0007967948913574219,\n" +
            "\t\t\t\t0.007045745849609375,\n" +
            "\t\t\t\t-0.080322265625,\n" +
            "\t\t\t\t-0.0242767333984375,\n" +
            "\t\t\t\t-0.01227569580078125,\n" +
            "\t\t\t\t0.0218658447265625,\n" +
            "\t\t\t\t0.0108489990234375,\n" +
            "\t\t\t\t0.02734375,\n" +
            "\t\t\t\t-0.0198974609375,\n" +
            "\t\t\t\t0.018157958984375,\n" +
            "\t\t\t\t0.0271148681640625,\n" +
            "\t\t\t\t0.02099609375,\n" +
            "\t\t\t\t-0.0020580291748046875,\n" +
            "\t\t\t\t-0.052459716796875,\n" +
            "\t\t\t\t-0.023590087890625,\n" +
            "\t\t\t\t-0.0193023681640625,\n" +
            "\t\t\t\t0.0291595458984375,\n" +
            "\t\t\t\t-0.0190887451171875,\n" +
            "\t\t\t\t-0.023284912109375,\n" +
            "\t\t\t\t-0.0015344619750976562,\n" +
            "\t\t\t\t0.03399658203125,\n" +
            "\t\t\t\t-0.045745849609375,\n" +
            "\t\t\t\t-0.00862884521484375,\n" +
            "\t\t\t\t0.0164031982421875,\n" +
            "\t\t\t\t0.01174163818359375,\n" +
            "\t\t\t\t0.02117919921875,\n" +
            "\t\t\t\t-0.057708740234375,\n" +
            "\t\t\t\t0.0290679931640625,\n" +
            "\t\t\t\t-0.0272369384765625,\n" +
            "\t\t\t\t-0.007495880126953125,\n" +
            "\t\t\t\t0.038726806640625,\n" +
            "\t\t\t\t0.027587890625,\n" +
            "\t\t\t\t0.0078582763671875,\n" +
            "\t\t\t\t0.01654052734375,\n" +
            "\t\t\t\t-0.010986328125,\n" +
            "\t\t\t\t0.043853759765625,\n" +
            "\t\t\t\t0.00836944580078125,\n" +
            "\t\t\t\t0.0190277099609375,\n" +
            "\t\t\t\t-0.04693603515625,\n" +
            "\t\t\t\t0.007663726806640625,\n" +
            "\t\t\t\t0.06561279296875,\n" +
            "\t\t\t\t0.0231781005859375,\n" +
            "\t\t\t\t-0.040740966796875,\n" +
            "\t\t\t\t-0.033538818359375,\n" +
            "\t\t\t\t0.03021240234375,\n" +
            "\t\t\t\t-0.028717041015625,\n" +
            "\t\t\t\t0.032928466796875,\n" +
            "\t\t\t\t-0.01384735107421875,\n" +
            "\t\t\t\t-0.039459228515625,\n" +
            "\t\t\t\t-0.0225982666015625,\n" +
            "\t\t\t\t0.05816650390625,\n" +
            "\t\t\t\t-0.04815673828125,\n" +
            "\t\t\t\t-0.0296630859375,\n" +
            "\t\t\t\t-0.033050537109375,\n" +
            "\t\t\t\t0.0028076171875,\n" +
            "\t\t\t\t0.0125885009765625,\n" +
            "\t\t\t\t0.0182342529296875,\n" +
            "\t\t\t\t-0.010589599609375,\n" +
            "\t\t\t\t0.036865234375,\n" +
            "\t\t\t\t0.0264739990234375,\n" +
            "\t\t\t\t-0.036224365234375,\n" +
            "\t\t\t\t-0.0164947509765625,\n" +
            "\t\t\t\t0.04144287109375,\n" +
            "\t\t\t\t-0.0048370361328125,\n" +
            "\t\t\t\t0.0166015625,\n" +
            "\t\t\t\t0.00591278076171875,\n" +
            "\t\t\t\t0.013824462890625,\n" +
            "\t\t\t\t-0.0343017578125,\n" +
            "\t\t\t\t0.0184326171875,\n" +
            "\t\t\t\t-0.013671875,\n" +
            "\t\t\t\t-0.043212890625,\n" +
            "\t\t\t\t0.019134521484375,\n" +
            "\t\t\t\t-0.0654296875,\n" +
            "\t\t\t\t-0.01384735107421875,\n" +
            "\t\t\t\t0.044891357421875,\n" +
            "\t\t\t\t0.025054931640625,\n" +
            "\t\t\t\t-0.0076141357421875,\n" +
            "\t\t\t\t-0.0153045654296875,\n" +
            "\t\t\t\t0.0380859375,\n" +
            "\t\t\t\t0.037933349609375,\n" +
            "\t\t\t\t-0.0243682861328125,\n" +
            "\t\t\t\t0.02532958984375,\n" +
            "\t\t\t\t-0.004058837890625,\n" +
            "\t\t\t\t-0.0075531005859375,\n" +
            "\t\t\t\t-0.0109100341796875,\n" +
            "\t\t\t\t0.028564453125,\n" +
            "\t\t\t\t0.027374267578125,\n" +
            "\t\t\t\t0.037322998046875,\n" +
            "\t\t\t\t-0.0290374755859375,\n" +
            "\t\t\t\t0.015960693359375,\n" +
            "\t\t\t\t0.001247406005859375,\n" +
            "\t\t\t\t-0.0292816162109375,\n" +
            "\t\t\t\t0.012542724609375,\n" +
            "\t\t\t\t-0.009490966796875,\n" +
            "\t\t\t\t-0.01517486572265625,\n" +
            "\t\t\t\t0.00027179718017578125,\n" +
            "\t\t\t\t-0.006847381591796875,\n" +
            "\t\t\t\t0.043243408203125,\n" +
            "\t\t\t\t0.0038776397705078125,\n" +
            "\t\t\t\t-0.044952392578125,\n" +
            "\t\t\t\t-0.0115966796875,\n" +
            "\t\t\t\t-0.0200347900390625,\n" +
            "\t\t\t\t0.0323486328125,\n" +
            "\t\t\t\t0.0175323486328125,\n" +
            "\t\t\t\t-0.057464599609375,\n" +
            "\t\t\t\t-0.02508544921875,\n" +
            "\t\t\t\t0.03387451171875,\n" +
            "\t\t\t\t0.004791259765625,\n" +
            "\t\t\t\t0.00946807861328125,\n" +
            "\t\t\t\t0.00988006591796875,\n" +
            "\t\t\t\t-0.0197296142578125,\n" +
            "\t\t\t\t-0.0103759765625,\n" +
            "\t\t\t\t-0.0458984375,\n" +
            "\t\t\t\t0.04071044921875,\n" +
            "\t\t\t\t0.013946533203125,\n" +
            "\t\t\t\t0.01384735107421875,\n" +
            "\t\t\t\t-0.02508544921875,\n" +
            "\t\t\t\t0.00951385498046875,\n" +
            "\t\t\t\t0.00012624263763427734,\n" +
            "\t\t\t\t-0.042755126953125\n" +
            "\t\t\t]\n" +
            "\t\t}\n" +
            "\t],\n" +
            "\t\"usage\": {\n" +
            "\t\t\"prompt_tokens\": 16,\n" +
            "\t\t\"total_tokens\": 16,\n" +
            "\t\t\"completion_tokens\": 0,\n" +
            "\t\t\"prompt_tokens_details\": null\n" +
            "\t}\n" +
            "}";

}
