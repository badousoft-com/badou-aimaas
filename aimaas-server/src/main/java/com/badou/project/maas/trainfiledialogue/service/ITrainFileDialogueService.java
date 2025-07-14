package com.badou.project.maas.trainfiledialogue.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;


/**
 * @author badousoft
 * @date 2024-05-16 14:39:38.124
 * @todo 训练集文件对话 service接口
 **/
public interface ITrainFileDialogueService extends IBaseSpringService<TrainFileDialogueEntity, Serializable> {

    JsonReturnBean testeva() throws Exception;

    /**
     * 获取初始化对象
     * @param trainFileId 文件数据主键
     * @return
     */
    TrainFileDialogueEntity buildTrainDiaFile(String trainFileId);


        /**
         * 获取评价相关的训练集数据
         * @param evaluation 评价主键
         * @return
         */
    List<TrainFileDialogueEntity> getListByEvaluation(String evaluationId);

    /**
     * 批量更新提示词
     * @param ids 主键数组
     * @param newSystemRole 系统角色提示词
     * @return
     */
    Integer bacthUpdateSystemRole(ArrayList ids,String newSystemRole);

    /**
     * 根据训练集文件主键获取训练集文件对话列表
      * @param trainFileId 训练集文件主键
     * @return 训练集文件答案列表
     */
    List<TrainFileDialogueEntity> findDatasByFileId(String trainFileId);

    /**
     * 统计训练集文件下的数据数量
     * @param trainFileId 训练集文件主键
     * @return 训练集文件问答总数
     */
    int getTotalDataCount(String trainFileId);
}