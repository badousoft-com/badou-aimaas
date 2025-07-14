package com.badou.project.mq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.project.maas.evaluationinstan.model.EvaluationMqEntity;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;
import com.badou.project.mq.util.RabbitMqUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName TaskMqSend
 * @Description 消息队列-任务发送任务
 * @date 2023/3/1 10:47
 * @Version 1.0
 */
@Component
public class TaskMqSender {

    //使用RabbitTemplate,这提供了接收/发送等等方法
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${spring.rabbitmq.plan-queue}")
    private String pubTaskQueue;
    @Value("${spring.rabbitmq.modelapp-queue}")
    private String modelAppQueue;
    @Value("${spring.rabbitmq.evaluation-queue}")
    private String evaluationQueue;
    @Value("${spring.rabbitmq.modelsync-queue}")
    private String modelSyncQueue;


    /**
     * 发送任务业务数据
     * @param message 消息
     */
    public void sendTaskMessage(String message){
        //创建路由键
        String routingKey = RabbitMqUtil.buildRoutingKey(pubTaskQueue);
        //发送消息
        rabbitTemplate.convertAndSend(TaskMqConst.TASK_MQ_EXCHANGE,routingKey,message);
    }

    /**
     * 发送任务业务数据
     * @param message 消息
     */
    public void sendModelAppMessage(String message){
        //创建路由键
        String routingKey = RabbitMqUtil.buildRoutingKey(modelAppQueue);
        //发送消息
        rabbitTemplate.convertAndSend(TaskMqConst.TASK_MQ_EX_MODEL_CHANGE,routingKey,message);
    }

    /**
     * 发送模型同步任务业务数据
     * @param modelSyncTaskEntity 消息
     */
    public void sendModelSyncMessage(ModelSyncTaskEntity modelSyncTaskEntity){
        //创建路由键
        String routingKey = RabbitMqUtil.buildRoutingKey(modelSyncQueue);
        //发送消息
        rabbitTemplate.convertAndSend(TaskMqConst.TASK_MQ_MODEL_SYNC_CHANGE,routingKey, JSONArray.toJSONString(modelSyncTaskEntity));
    }

    /**
     * 发送任务给评价模型
     * @param message 消息
     */
    public void senEvaluationdMessage(EvaluationMqEntity message){
        //创建路由键
        String routingKey = RabbitMqUtil.buildRoutingKey(evaluationQueue);
        //发送消息
        rabbitTemplate.convertAndSend(TaskMqConst.TASK_MQ_EX_Evaluation_CHANGE,routingKey, JSONObject.toJSONString(message));
    }

}
