package com.badou.project.maas.traindataproblem.service;

import java.io.Serializable;
import java.util.ArrayList;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.traindataproblem.model.TrainDataProblemEntity;
import com.badou.project.maas.traindataproblem.web.form.TrainDataProblemVo;


/**
 * @author badousoft
 * @date 2024-08-30 10:41:35.751
 *  训练集样本集 service接口
 **/
public interface ITrainDataProblemService extends IBaseSpringService<TrainDataProblemEntity, Serializable> {
    /**
     * 关联数据集
     * @param vo
     */
    void linkOneProblem(TrainDataProblemVo vo);

    /**
     * 移除关联
     * @param trainFileId
     * @param id
     * @param problem
     * @throws DataEmptyException
     */
    void deleteLink(String trainFileId,String id,String problem) throws DataEmptyException;

}