package com.badou.project.mq.config;

import com.badou.project.mq.TaskMqConst;
import com.badou.project.mq.util.RabbitMqUtil;
import com.badou.tools.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * @ClassName DirectRabbitConfig
 * @Description RabbitMQ配置
 * @date 2023/3/1 10:31
 * @Version 1.0
 */
@Configuration
@Slf4j
public class DirectRabbitConfig {

    @Value("${spring.rabbitmq.plan-queue}")
    private String pubTaskQueue;
    @Value("${spring.rabbitmq.modelapp-queue}")
    private String modelAppQueue;
    @Value("${spring.rabbitmq.evaluation-queue}")
    private String evaluationQueue;
    @Value("${spring.rabbitmq.modelsync-queue}")
    private String modelsyncnQueue;

    //队列
    @Bean
    public Queue directQueue() throws Exception {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        //获取不同环境对应的Queue队列名字 为了避免重复使用导致任务调度混乱 增加唯一性.
        if(StringUtils.isEmpty(pubTaskQueue)){
            throw new Exception("无法获取到有效的任务队列信息");
        }
        Queue queue = new Queue(pubTaskQueue, true, true, true);
//        log.info("创建MQ快速发布队伍队列:"+pubTaskQueue+"成功");
        return queue;
    }

    //队列
    @Bean
    public Queue directEvaluationQueue() throws Exception {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        //获取不同环境对应的Queue队列名字 为了避免重复使用导致任务调度混乱 增加唯一性.
        if(StringUtils.isEmpty(evaluationQueue)){
            throw new Exception("无法获取到有效的任务队列信息");
        }
        Queue queue = new Queue(evaluationQueue, true, true, true);
//        log.info("创建MQ快速发布队伍队列:"+evaluationQueue+"成功");
        return queue;
    }

    //队列
    @Bean
    public Queue directModelsyncQueue() throws Exception {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        //获取不同环境对应的Queue队列名字 为了避免重复使用导致任务调度混乱 增加唯一性.
        if(StringUtils.isEmpty(modelsyncnQueue)){
            throw new Exception("无法获取到有效的任务队列信息");
        }
        Queue queue = new Queue(modelsyncnQueue, true, true, true);
//        log.info("创建MQ快速发布队伍队列:"+modelsyncnQueue+"成功");
        return queue;
    }

    //队列
    @Bean
    public Queue directQueueApp() throws Exception {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        //获取不同环境对应的Queue队列名字 为了避免重复使用导致任务调度混乱 增加唯一性.
        if(StringUtils.isEmpty(modelAppQueue)){
            throw new Exception("无法获取到有效的任务队列信息");
        }
        Queue queue = new Queue(modelAppQueue, true, true, true);
//        log.info("创建MQ快速发布队伍队列:"+modelAppQueue+"成功");
        return queue;
    }

    //Direct交换机
    @Bean
    DirectExchange directExchangeModelSync() {
        return new DirectExchange(TaskMqConst.TASK_MQ_MODEL_SYNC_CHANGE,true,false);
    }

    //Direct交换机
    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(TaskMqConst.TASK_MQ_EXCHANGE,true,false);
    }

    //Direct交换机
    @Bean
    DirectExchange directExchangeApp() {
        return new DirectExchange(TaskMqConst.TASK_MQ_EX_MODEL_CHANGE,true,false);
    }

    //Direct交换机
    @Bean
    DirectExchange directExchangeEvaluation() {
        return new DirectExchange(TaskMqConst.TASK_MQ_EX_Evaluation_CHANGE,true,false);
    }


    //绑定  将队列directExchangeModelSync和交换机绑定, 并设置用于匹配键
    @Bean
    Binding bindingModelSyncDirect() throws Exception {
        String routingKey = RabbitMqUtil.buildRoutingKey(modelsyncnQueue);
//        log.info("创建交换机"+TaskMqConst.TASK_MQ_MODEL_SYNC_CHANGE+",路由键:"+routingKey);
        return BindingBuilder.bind(directModelsyncQueue()).to(directExchangeModelSync()).with(routingKey);
    }

    //绑定  将队列directExchangeModelSync和交换机绑定, 并设置用于匹配键
    @Bean
    Binding bindingDirect() throws Exception {
        String routingKey = RabbitMqUtil.buildRoutingKey(pubTaskQueue);
//        log.info("创建交换机"+TaskMqConst.TASK_MQ_EXCHANGE+",路由键:"+routingKey);
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(routingKey);
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键
    @Bean
    Binding bindingDirectApp() throws Exception {
        String routingKey = RabbitMqUtil.buildRoutingKey(modelAppQueue);
//        log.info("创建应用交换机"+TaskMqConst.TASK_MQ_EX_MODEL_CHANGE+",路由键:"+routingKey);
        return BindingBuilder.bind(directQueueApp()).to(directExchangeApp()).with(routingKey);
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键
    @Bean
    Binding bindingDirectEvaluation() throws Exception {
        String routingKey = RabbitMqUtil.buildRoutingKey(evaluationQueue);
//        log.info("创建应用交换机"+TaskMqConst.TASK_MQ_EX_Evaluation_CHANGE+",路由键:"+routingKey);
        return BindingBuilder.bind(directEvaluationQueue()).to(directExchangeEvaluation()).with(routingKey);
    }

}
