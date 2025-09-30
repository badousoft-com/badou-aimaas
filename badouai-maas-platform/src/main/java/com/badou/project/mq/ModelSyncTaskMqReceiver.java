package com.badou.project.mq;

import com.alibaba.fastjson.JSONObject;
import com.badou.project.GlobalConsts;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;
import com.badou.project.maas.modelsync.service.IModelSyncTaskService;
import io.kubernetes.client.openapi.models.V1Pod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ModelSyncTaskMqReceiver implements IBaseTaskMqReceiver{

    @Autowired
    private IModelSyncTaskService modelSyncTaskService;
    @Autowired
    private TaskMqSender taskMqSender;
    @Autowired
    private KubernetesPodHandler kubernetesPodHandler;
    private Map<String,String> runningMap = new HashMap<>();

    public Map<String, String> getRunningMap() {
        return runningMap;
    }

    public void setRunningMap(Map<String, String> runningMap) {
        this.runningMap = runningMap;
    }

    /**
     * 1.@RabbitListener 注解是指定某方法作为消息消费的方法，例如监听某 Queue 里面的消息。
     * <p>
     * 2.@RabbitListener标注在方法上，直接监听指定的队列，此时接收的参数需要与发送市类型一致
     *
     * @param message 收到的消息
     */
    @RabbitListener(queues = {"${spring.rabbitmq.modelsync-queue}"})
    @RabbitHandler
    @Override
    public void process(String message) {
        ModelSyncTaskEntity modelSyncTaskEntity = JSONObject.parseObject(message, ModelSyncTaskEntity.class);
        if (GlobalConsts.ZERO.equals(modelSyncTaskEntity.getType())){
            //模型同步任务
            try {
                KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(modelSyncTaskEntity.getServerIds());
                V1Pod pod = kubernetesPodHandler.getPod(kubernetesApiClient, MaasConst.MODEL_SYNC_NS, modelSyncTaskEntity.getCode());
                //检查模型同步进度怎么样
                //先看任务状态
                if (pod == null){
                    modelSyncTaskService.updateTaskFail(modelSyncTaskEntity,"不存在有效的任务!");
                    return;
                }
                //如果还在PodInit则跳过
                if (pod.getStatus().getPhase().equals(MaasConst.K8S_POD_PENDING)){
                    if (pod.getStatus().getInitContainerStatuses() == null){
                        log.info("等待初始化容器信息");
                        reloadTask(modelSyncTaskEntity);
                        return;
                    }
                    /**
                     * Status:       Pending
                     *     State:          Waiting
                     *       Reason:       PodInitializing
                     */
                    //判断是不是PodInit
                    if (!pod.getStatus().getInitContainerStatuses().get(0).getState().getWaiting().getReason().equals(MaasConst.K8S_POD_INIT)){
                        modelSyncTaskService.updateTaskFail(modelSyncTaskEntity,"非法的初始状态:"+pod.getStatus().getPhase());
                        return;
                    }
                    modelSyncTaskService.updateTaskRunning(modelSyncTaskEntity);
                    log.info(modelSyncTaskEntity.getCode()+"同步任务正在运行");
                    reloadTask(modelSyncTaskEntity);
                }else if (pod.getStatus().getPhase().equals(MaasConst.K8S_POD_RUNNING)){
                    //获取POD的重启次数 如果>0的情况
                    int restartCount = pod.getStatus().getContainerStatuses().get(0).getRestartCount();
                    if (restartCount > 0 ){
                        modelSyncTaskService.updateTaskFail(modelSyncTaskEntity,"该模型已下载.不允许重复下载.任务关闭");
                        return;
                    }
                    runningMap.put(modelSyncTaskEntity.getTargetObject(),"1");
                    modelSyncTaskService.updateTaskRunning(modelSyncTaskEntity);
                    log.info(modelSyncTaskEntity.getCode()+"同步任务正在运行");
                    //如果还在Running也跳过
                    reloadTask(modelSyncTaskEntity);
                }else if (pod.getStatus().getPhase().equals(MaasConst.K8S_POD_FAILED)){
                    //根据Succuss和FALID状态更新同步任务状态
                    modelSyncTaskService.updateTaskFail(modelSyncTaskEntity,"");
                }else if (pod.getStatus().getPhase().equals(MaasConst.K8S_POD_SUCCEEDED)){
                    //根据Succuss和FALID状态更新同步任务状态
                    modelSyncTaskService.updateTaskSuccess(modelSyncTaskEntity,"");
                }else {
                    //未知的错误 任务终止
                    modelSyncTaskService.updateTaskFail(modelSyncTaskEntity,"非法的状态:"+pod.getStatus().getPhase());
                }
            } catch (Exception e) {
                e.printStackTrace();
                modelSyncTaskService.updateTaskFail(modelSyncTaskEntity,"加载执行客户端失败!");
            }
        }
    }

    public void reloadTask(ModelSyncTaskEntity modelSyncTaskEntity) {
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        taskMqSender.sendModelSyncMessage(modelSyncTaskEntity);
    }

}
