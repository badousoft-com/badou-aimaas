package com.badou.project.mq.util;

import com.badou.project.mq.TaskMqConst;

import java.util.UUID;

/**
 * @ClassName RabbitMqUtil
 * @Description 队列工具类
 * @date 2023/4/25 10:10
 * @Version 1.0
 */

public class RabbitMqUtil {

    /**
     * 创建路由键
     * @param queue 队列名字
     * @return 完整的路由键
     */
    public static String buildRoutingKey(String queue){
        return TaskMqConst.TASK_MQ_ROUTING+queue;
    }

    public static String handerRightKey(String key){
        return key.replace("/","").replace("_","-");
    }

    public static String findChatglm3Out(String content){
        int resultAll = content.lastIndexOf("pytorch_model.bin");
        String beforeLine = content.substring(0, resultAll);
        int currentLineIdx = beforeLine.lastIndexOf("\n");
        //例子:  [INFO|modeling_utils.py:2474] 2024-05-28 08:45:14,633 >> Model weights saved in /fine_tuning/output/tool_alpaca_pt-PTV2_0000001-1716885883540/pytorch_model.bin
        String[] split = beforeLine.substring(currentLineIdx, resultAll).split("/");
        if(split.length==4){
            return split[3];
        }
        return null;
    }

}
