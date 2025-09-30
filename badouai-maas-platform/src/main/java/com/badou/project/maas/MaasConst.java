package com.badou.project.maas;

import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.trainplan.service.ITrainPlanService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.tools.common.util.SpringHelper;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1EmptyDirVolumeSource;

import java.util.Date;

public class MaasConst {
    //任务分类名称-微调
    public static final String TUN_PLAN_NAME = "微调任务";
    //任务分类名称-模型部署
    public static final String MODEL_APP_NAME = "模型部署任务";
    //K8S POD状态 镜像拉取失败字符串
    //镜像信息错误
    public static final String K8S_POD_ERRIMAGEPULL = "ErrImagePull";
    //下载镜像失败
    public static final String K8S_POD_IMAGEPULLBACKOFF = "ImagePullBackOff";
    //无效的镜像名
    public static final String K8S_POD_INVALIDIMAGENAME = "InvalidImageName";
    //K8S POD状态 运行字符串
    public static final String K8S_POD_RUNNING = "Running";
    //K8S POD状态 完成字符串
    public static final String K8S_POD_COMPLETED = "Completed";
    //K8S POD状态 失败字符串
    public static final String K8S_POD_FAILED = "Failed";
    //K8S POD状态 成功字符串
    public static final String K8S_POD_SUCCEEDED = "Succeeded";
    //K8S POD状态 等待分配开始调度
    public static final String K8S_POD_PENDING = "Pending";
    //K8S POD状态 初始化
    public static final String K8S_POD_INIT = "PodInitializing";
    //K8S Pod 已经被调度到节点上，正在创建容器，但容器尚未启动。
    public static final String K8S_POD_CONTAINERCREATING = "ContainerCreating";
    //K8S Pod 正在被删除，其中的容器正在停止运行。
    public static final String K8S_POD_TERMINATING = "Terminating";
    //微调任务-等待开始
    public static final int DOPLAN_WAIT_STATUS = 0;
    //微调任务-微调中
    public static final int DOPLAN_RUN_STATUS = 1;
    //微调任务-微调成功
    public static final int DOPLAN_SUCCESS_STATUS = 2;
    //微调任务-微调失败
    public static final int DOPLAN_FAIL_STATUS = 3;
    //微调任务-作废
    public static final int DOPLAN_CLOSE_STATUS = 4;
    //微调任务-启动应用中
    public static final int DOPLAN_APP_STATUS = 5;
    //微调任务-评价
    public static final int DOPLAN_SCORE_STATUS = 6;
    //需要控制的路径数据字典 配置文件的名称
    public static final String FILE_PATH_DIC_CONFIG = "config";
    //需要控制的路径数据字典 输出目录的名称
    public static final String FILE_PATH_DIC_OUTPUT = "output";
    //微调参数数据字典
    public static final String TUNINGPARAMSDic = "FINE_TUNING_PARAMS";
    //所有容器存储的路径
    public static final String TRAIN_MAIN_PATH = "/fine_tuning/dataset/";
    //训练集文件固定正确的后缀
    public static final String TRAIN_END_FIX = "jsonl";
    //ai模型应用的命名空间
    public static final String MODEL_APP_NSAPCE = "model-app";
    //nginx占位的命名空间
    public static final String NGINX_POSIT_NSPACE = "nginx-posit";
    //微调的命名空间
    public static final String TRIAN_PLAN_NSPACE = "train-plan";
    //微调的命名空间
    public static final String TRAIN_REGISTRY_NAME = "tuning_";
    //启动模型微调镜像
    public static final String TRAIN_REGISTRY_MODEL = "tuning_model_";
    //创建问题的提示词
    public static final String CREATEQUSTION_TIPE = "create-qustion";
    //微调模型字典
    public static final String TRAIN_METHOD_DIC = "TUNINGMODE";
    //是否开启自动删除
    public static final String OPEN_AUTO_DELETE = "OPEN_AUTO_DELETE";
    //模型任务状态-数据字典
    public static final String DIC_MODEL_STATUS = "MODEL_STATUS";
    //VLLM版本-数据字典
    public static final String DIC_VLLM_VERSION = "VLLM_IMAGE_VERSION";
    //模型运行状态-数据字典
    public static final String DIC_MODEL_RUN_STATUS = "MODEL_RUN_STATUS";
    //模型上下架状态
    public static final String DIC_SHELVES_STATUS = "shelves_status";
    //llamavactory参数的数据字典
    public static final String LLAMA_FACTORY_DIC = "llama_factory";
    //VLLM参数的数据字典
    public static final String VLLM_PARAMS_DIC = "VLLM_PARAMS";
    //llamavactory模板的数据字典
    public static final String LLAMA_FACTORY_TEMPLATE_DIC = "llama_factory_template";
    //微调框架字典
    public static final String TUN_PLAN_FRAME_DIC = "TUN_PLAN_FRAME";
    //微调数据集类型数据字典
    public static final String TUN_DATA_FORMAT = "data_format";
    //模型接口参数字典
    public static final String MODEL_API_PARAMS_DIC = "MODEL_API_PARAMS";
    //指令监督微调数据集微调配置文件主键 固定 在src/resource/llamafactory/dataset_info.json路径下有备份 如果不存在 则上传该文件 把附件ID改成8a7480199149d820019168581d270049
    //微调模式 0. 1.预训练数据集 2.偏好数据集 3.KTO数据集 4.多模态
    public static final String TUN_FILE_ID = "8a74802a92ff6829019305ad067c0120";
   //模型类型-基础模型
    public static final Integer MODEL_SOURCE_BASE = 0;
    //模型类型-微调模型
    public static final Integer MODEL_SOURCE_TUN = 1;
    //默认所有训练集的角色提示词·
    public static final String DEFAULT_ROLE_DESC = "你是一个中文小助手.擅长把收集来的问题分析得出结论";
    //数据字典-配置文件的字典前缀
    public static final String DLC_TUN_CONFIG_PREFIX = "tun_config_";
    //数据字典-默认监控面板参数
    public static final String DIC_PANEL_PARAMS = "DEFAULT_PANEL_PARAMS_";
    //数据字典-微调方案参数组别字典
    public static final String DIC_PLAN_PARAMS_GROUP = "PLAN_PARAMS_GROUP";
    //数据字典-微调方案参数-下拉选项-选项是True或者False的参数
    public static final String DIC_PLAN_PARAMS_SELECTIONS = "PLAN_PARAMS_SELECTIONS";
    //数据字典-微调方案参数-下拉选项-选项多个选项的参数
    public static final String DIC_PLAN_PARAMS_COMMON_BOOLEAN = "PLAN_PARAMS_COMMON_BOOLEAN";
    //数据字典-VLLM参数组别字典
    public static final String DIC_VLLM_PARAMS_GROUP = "VLLM_PARAMS_GROUP";
    //数据字典-VLLM参数-下拉选项-选项是True或者False的参数
    public static final String DIC_VLLM_PARAMS_SELECTIONS = "VLLM_PARAMS_SELECTIONS";
    //数据字典-VLLM参数-下拉选项-选项多个选项的参数
    public static final String DIC_VLLM_PARAMS_COMMON_BOOLEAN = "VLLM_PARAMS_COMMON_BOOLEAN";
    //VLLM-所有的执行参数名称
    public static final String VLLM_EXEC_PARAMS_NAME = "ENV_CUSTOM_PARAMETER";
    //监控服务工作命名空间
    public static final String SPACE_TENSORBOARD = "logs-tensorboard";
    //下载模型文件Nginx的工作命名空间
    public static final String DOWNLOAD_NGINX_NS = "download-nginx";
    //模型同步任务-命名空间
    public static final String MODEL_SYNC_NS = "model-sync";
    //存储微调配置文件的数据字典
    public static final String TUN_CONFIG_FILE_DIC = "TUN_CONFIG_FILE_DIC";
    //存储微调偏好 子项的字典
    public static final String TUN_CONFIG_RL_DIC = "RLHF_WAY";
    //微调多卡服务任务的共享内存区域 提供2GB
    public static final V1EmptyDirVolumeSource SHM_AREA_2GB = new V1EmptyDirVolumeSource().medium("Memory").sizeLimit(new Quantity("2048Mi"));
    //微调类型 偏好
    public static final Integer TUN_PLAN_TYPE_RL = 2;
    // 微调类型-RM
    public static final Integer TUN_PLAN_TYPE_RM = 0;
    // 微调类型-PPO
    public static final Integer TUN_PLAN_TYPE_PPO = 1;
    // 微调类型-DPO
    public static final Integer TUN_PLAN_TYPE_DPO = 2;
    // 微调类型-KTO
    public static final Integer TUN_PLAN_TYPE_KPO = 3;
    // 微调类型-GRPO
    public static final Integer TUN_PLAN_TYPE_GRPO = 5;

    // 微调类型-多模态
    public static final Integer TUN_PLAN_TYPE_MULIT = 4;
    //微调类型-预训练
    public static final Integer TUN_PLAN_TYPE_PTTRAIN = 0;
    //微调类型-指令监督
    public static Integer TUN_PLAN_TYPE_SFT = 1;
    //微调类型-基础字典
    public static String DIC_PLAN_DO_WAY = "PLAN_DO_WAY";
    //多模态对话 base64图片参数固定前缀
    public static final String MULIT_BASE64_IMAGE_PREFIX = "data:image;base64,";
    //连接到其他平台的AI对话字典
    public static final String DIC_LINK_AI_TALK = "LINK_AI_TALK";
    //ModelScope执行数据字典
    public static final String DIC_MODEL_SCOPE_CONFIG = "MODEL_SCOPE_CONFIG";
    //服务器硬盘安全值 如果检查到当前服务器大于这个安全值 不应该继续启动任务了
    public static final Integer SERVER_SAFE_DISK = 85;
    //最大AI模型启动等待时间 默认要求模型要在5分钟内启动完成 60*5 = 300
    public static final Integer TIMEOUT_AIMODEL_START_WAIT = 5;
    //本系统在K8s 存在微调任务/-存在显卡分配任务的命名空间
    public static final String[] CARD_TASK_SPACE = new String[]{TRIAN_PLAN_NSPACE,MODEL_APP_NSAPCE};
    //默认的主键
    public static final String ROOT_ID = "4028884488bd3cf90188bd4591860003";
    //K8S 控制GPU显示隐藏的环境变量名称
    public static final String NVIDIA_VISIBLE_DEVICES = "NVIDIA_VISIBLE_DEVICES";
    //当前常用模型的Transformer的注意力头一般是40.只有这个注意力的倍数做均等张量切分.才是最好的集群、分布式推理性能
    public static final Integer TRANSFORMER_ATTENTION_HEADS_COUNT = 40;
    // VLLM多卡-张量并行字段
    public static final String TENSOR_PARALLEL_SIZE_FIELD = "--tensor-parallel-size";
    // VLLM多卡-管型并行字段
    public static final String PIPELINE_PARALLEL_SIZE = "--pipeline-parallel-size";
    //默认的Cuda目录
    public static final String CUDA_HOME = "/home/servers/cuda";
    //默认的Cuda_Tool目录
    public static final String[] CUDA_TOOL_PATH = new String[]{"bin/nvcc","bin/ptxas"};
    //执行配置Yaml配置路径开始
    //模型下载Nginx-Yaml配置路径
    public static final String DOWNLOAD_NGINX_PATH = "kubernetes\\downloadnginx\\downloadnginx.yaml".replace("\\",System.getProperty("file.separator"));;
    //ModelScope下载模型-Yaml配置路径
    public static final String DOWN_MODELSCOPE_PATH = "kubernetes\\downloadnginx\\downmodelscope.yaml".replace("\\",System.getProperty("file.separator"));;

    //模型仓库 类型 字典值 其他
    public static final Integer MODEL_TYPE_OTHER = 999;

    //执行配置Yaml配置路径结束
    /**
     * 构建与AI对话时,base64格式的图片
     * @param base64Image
     * @return
     */
    public static String buildTalkBase64Image(String base64Image){
        return MULIT_BASE64_IMAGE_PREFIX+base64Image;
    }


    public static DictionaryCacheObject buildConfigDic(TuningModelnEntity tuningModelnEntity){
        //20250508 如果是偏好类型的训练 还需要根据子类型来找对应
        String frame = DictionaryLib.getItemName(MaasConst.TUN_PLAN_FRAME_DIC, tuningModelnEntity.getDoFrame());
        String format = DictionaryLib.getItemCodeByItemValue(TUN_DATA_FORMAT, tuningModelnEntity.getDoWay().toString());
        TrainPlanEntity trainPlanEntity = SpringHelper.getBean(ITrainPlanService.class).find(tuningModelnEntity.getPlanId());

        if (TUN_PLAN_TYPE_RL.equals(tuningModelnEntity.getDoWay()) && trainPlanEntity.getRlhfWay()!=null){
            //如果是偏好类型 需要拿偏好的子项
            format = DictionaryLib.getItemCodeByItemValue(TUN_CONFIG_RL_DIC, trainPlanEntity.getRlhfWay().toString());
        }
        String extendsDic = DLC_TUN_CONFIG_PREFIX+frame+"_"+format;

        System.out.println("加载字典:"+extendsDic);
        return DictionaryLib.getDictionaryByCode(extendsDic);
    }

    public static final String buildWorkPath(TuningModelnEntity tuningModelnEntity){
        String volumnPath = FileControllerService.getCacheK8sClient(tuningModelnEntity.getServerId()).getServer().getVolumnPath();
        volumnPath = volumnPath.endsWith("/")?volumnPath:volumnPath+"/";

        return volumnPath+tuningModelnEntity.getModelName()+"/" + tuningModelnEntity.getCode();
    }

    /**
     * 构建配置路径
     * @param tuningModelnEntity
     * @return
     */
    public static String buildConfigPath(TuningModelnEntity tuningModelnEntity){
        return buildWorkPath(tuningModelnEntity)+"/config";
    }

    public static String buildTrainFilePath(TuningModelnEntity tuningModelnEntity){
        return  buildWorkPath(tuningModelnEntity)+"/trainfile";
    }

    public static String buildOutPath(TuningModelnEntity tuningModelnEntity){
        return buildWorkPath(tuningModelnEntity)+"/output";
    }

    public static String buildModelOutPath(TuningModelnEntity tuningModelnEntity){
        return buildWorkPath(tuningModelnEntity)+"/"+tuningModelnEntity.getCreateDir();
    }

    public static String buildMergedModelOutPath(TuningModelnEntity tuningModelnEntity){
        return buildWorkPath(tuningModelnEntity)+"/output/"+tuningModelnEntity.getCreateDir()+"/merged_model";
    }

    public static String getTuningAllName(String frame,String modelName){
        return "tuning_"+DictionaryLib.getItemName(TUN_PLAN_FRAME_DIC,frame) +"_"+modelName;
    }
    public static String buildNewTunModel(String modelName){
        return modelName+"-"+DateUtil.getDateStrMin(new Date()).replace(" ", "-").replace(":", "-");
    }

}
