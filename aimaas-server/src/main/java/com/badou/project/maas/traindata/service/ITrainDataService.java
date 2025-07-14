package com.badou.project.maas.traindata.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.traindata.model.TrainDataEntity;


/**
 * @author badousoft
 * @date 2024-04-18 09:34:05.295
 * @todo 训练集管理 service接口
 **/
public interface ITrainDataService extends IBaseSpringService<TrainDataEntity, Serializable> {
    
}