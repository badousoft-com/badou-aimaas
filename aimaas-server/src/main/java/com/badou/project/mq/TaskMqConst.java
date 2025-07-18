package com.badou.project.mq;

/**
 * @ClassName TaskMqConst
 * @Description TODO
 * @date 2023/3/1 10:48
 * @Version 1.0
 */

public class TaskMqConst {

    //交换机
    public static final String TASK_MQ_EXCHANGE = "devops_plan_exchange";
    public static final String TASK_MQ_EX_MODEL_CHANGE = "devops_model_exchange";
    public static final String TASK_MQ_EX_Evaluation_CHANGE = "devops_eva_exchange";
    public static final String TASK_MQ_MODEL_SYNC_CHANGE = "devops_modelsync_exchange";
    public static final String COMMAND_MQ_EXCHANGE = "devops_plan_exchange";
    //路由键
    public static final String TASK_MQ_ROUTING = "routing_";
    public static final String AI_TIP_ANSWER_QUSTION = "answer-qustion-score";


}
