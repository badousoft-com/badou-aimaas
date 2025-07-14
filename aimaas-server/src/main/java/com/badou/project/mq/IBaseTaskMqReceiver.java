package com.badou.project.mq;

public interface IBaseTaskMqReceiver {
    /**
     * 执行任务
     * @param message 任务参数
     */
    void process(String message);

}
