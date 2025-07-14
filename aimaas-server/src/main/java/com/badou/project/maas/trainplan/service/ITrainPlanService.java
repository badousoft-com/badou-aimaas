package com.badou.project.maas.trainplan.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.openapi.ApiException;


/**
 * @author badousoft
 * @date 2024-04-17 15:10:34.232
 * @todo 微调方案 service接口
 **/
public interface ITrainPlanService extends IBaseSpringService<TrainPlanEntity, Serializable> {

    /**
     * 复制方案
     * @param id 微调方案主键
     */
    void copyObject(String id);

}
