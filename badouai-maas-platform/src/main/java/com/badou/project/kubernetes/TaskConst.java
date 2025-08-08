package com.badou.project.kubernetes;

/**
 * @ClassName TaskConst
 * @Description 任务参数
 * @date 2023/2/22 16:47
 * @Version 1.0
 */

public class TaskConst {
    //处理任务参数的时候出错
    public static final String PARAMSERROR = "读取任务参数失败";
    //修改+配置脚本时出错
    public static final String SHELLERROR = "构建镜像异常!设置脚本失败!";
    //镜像发布错误类型
    public static final String IMAGEERROR = "1.常见错误类型说明:\n" +
            "\tCrashLoopBackOff:             应用退出,系统正在将它重启.\n" +
            "    InvalidImageName：            无法解析镜像名称\n" +
            "    ImageInspectError：           无法校验镜像\n" +
            "\tErrImageNeverPull:            策略禁止拉取镜像\n" +
            "    ImagePullBackOff：            正在重试拉取\n" +
            "    RegistryUnavailable：         连接不到镜像中心\n" +
            "    ErrImagePull：                通用的拉取镜像出错.错误的镜像认证、仓库地址、名字信息\n" +
            "    CreateContainerConfigError：  不能创建kubelet使用的容器配置\n" +
            "    CreateContainerError：        创建容器失败        \n" +
            "\tPreStartContainer:            执行hook报错\n" +
            "    RunContainerError：           启动容器失败\n" +
            "    PostStartHookError：          执行hook报错\n" +
            "    ContainersNotInitialized：    容器没有初始化完毕\n" +
            "    ContainersNotReady：          容器没有准备完毕\n" +
            "    ContainerCreating：           容器创建中\n" +
            "    PodInitializing：             pod初始化中\n" +
            "    DockerDaemonNotReady：        docker还没有完全启动\n" +
            "    NetworkPluginNotReady：       网络插件还没有完全启动";
    public static final String PUBTIP = "\n注意事项:\n(1)发布期间应用会处于不可服务的状态!\n(2)发布前请确保剩余的资源足够,不足时会导致项目应用无法正常启动完成并显示内存溢出等错误!";
    //任务用户名字
    public static final String TASKUSERNAME = "system-task";
    //任务用户ID
    public static final String TASKUSERID = "UUID-AUTO-SYSTEM-TASK";
    //任务验证有效的Value
    public static final String TASKVAILDVALUE = "TASK_VALID_OK";
    //任务有效的时间 单位秒 默认30分钟
    public static final int TASKVAILDTIME = 60*30;
    //任务发布成功后的提示!
    public static final String SUCCESS_MSG = "应用发布完成!请通过查看日志功能确认应用启动进度!\n<span style=\"color:red\">注意:后端应用发布完成后启动需要时间!启动期间不可访问!</span>\n注意:CPU资源分配较少的情况下,启动时间会较长,请耐心等待. 比如一个后端工程分配了1核,预估启动完成需要9分钟.2核为4分钟.";
    //任务状态-失败-信息
    public static final String POD_STATUS_FAIL = "Failed";

}
