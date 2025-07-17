package com.badou.project.maas.trainfiledialogueevaluation.service;

import java.io.Serializable;
import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.trainfiledialogueevaluation.model.TrainFileDialogueEvaluationEntity;

/**
 * @author badousoft
 * @date 2025-02-18 19:04:55.108
 *  评价管理与训练集文件对话数据的关联 service接口
 **/
public interface ITrainFileDialogueEvaluationService extends IBaseSpringService<TrainFileDialogueEvaluationEntity, Serializable> {

    /**
     * 微调评价与训练集对话进行关联
     * @param evaluationId 微调评价ID
     * @param trainFileId 训练集文件ID
     */
    void correlation (String evaluationId, String trainFileId);
}