package com.badou.project.maas.tuningprogramn.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningprogramn.model.TuningProgramnEntity;
import io.kubernetes.client.openapi.ApiException;


/**
 * @author badousoft
 * @date 2024-04-30 16:22:32.674
 * @todo 微调计划管理 service接口
 **/
public interface ITuningProgramnService extends IBaseSpringService<TuningProgramnEntity, Serializable> {

    /**
     * 启动计划
     * @param trainPlanEntity 微调计划实体
     * @return
     */
    boolean startPlan(Map<String,String[]> trainPlanEntity, String id) throws Exception;

    /**
     * 创建实体
     * @param tuningProgramnEntity 模型计划实体
     * @return
     */
    boolean createEntity(TuningProgramnEntity tuningProgramnEntity);

    void updateAttachId(String id);
}