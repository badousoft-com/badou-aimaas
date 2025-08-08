package com.badou.project.maas.evaluationinstan.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanqEntity;


/**
 * @author badousoft
 * @date 2024-06-06 15:58:58.809
 * @todo 模型评价实例问题 service接口
 **/
public interface IEvaluationInstanqService extends IBaseSpringService<EvaluationInstanqEntity, Serializable> {
    void createEntity(EvaluationInstanqEntity evaluationInstanqEntity);

}