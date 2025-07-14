package com.badou.project.maas.tuningprogramqueue.service;

import java.io.Serializable;
import java.util.Date;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.tuningprogramqueue.model.TuningProgramQueueEntity;


/**
 * @author badousoft
 * @date 2024-07-22 09:58:48.624
 * @todo 计划任务列表 service接口
 **/
public interface ITuningProgramQueueService extends IBaseSpringService<TuningProgramQueueEntity, Serializable> {
    /**
     * 更新运行状态
     * @param id
     * @param status
     */
    void updateStatusByPlanId(String id,int status);

    /**
     * 获取当前任务的下一步 一般是获取微调后的评价或同步的时候获取下一步
     * @param taskId
     * @param planId
     * @return
     */
    TuningProgramQueueEntity getCurrentNext(String planId, Date createTime);
}