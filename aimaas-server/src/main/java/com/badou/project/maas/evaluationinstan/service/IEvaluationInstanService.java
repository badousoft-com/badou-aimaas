package com.badou.project.maas.evaluationinstan.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;


/**
 * @author badousoft
 * @date 2024-06-06 15:58:38.064
 * @todo 模型评价实例 service接口
 **/
public interface IEvaluationInstanService extends IBaseSpringService<EvaluationInstanEntity, Serializable> {

    void createEntity(EvaluationInstanEntity evaluationInstanEntity);

    /***
     * 修改进度
     * @param evaluationInstanEntity
     */
    void startUpdate(EvaluationInstanEntity evaluationInstanEntity);

    /**
     * 计算完成的状态 比如最大最小平均
     * @param id 主键
     * @return
     */
    EvaluationInstanEntity calcFinishStatus(String id);

    /**
     * huq1
     * @param id
     * @return
     */
    int getExecCount(String id);
}