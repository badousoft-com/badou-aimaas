package com.badou.project.maas.trainfiledialoguedetail.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.trainfiledialoguedetail.model.TrainFileDialoguedetailEntity;


/**
 * @author badousoft
 * @date 2024-05-16 14:47:48.749
 * @todo 训练集文件对话详情 service接口
 **/
public interface ITrainFileDialoguedetailService extends IBaseSpringService<TrainFileDialoguedetailEntity, Serializable> {
     void createEntity(TrainFileDialoguedetailEntity trainFileDialoguedetailEntity);
}