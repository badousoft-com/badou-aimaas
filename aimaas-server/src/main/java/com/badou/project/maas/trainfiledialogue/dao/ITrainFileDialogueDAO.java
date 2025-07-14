package com.badou.project.maas.trainfiledialogue.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;

/**
 * @author badousoft
 * @created 2024-05-16 14:39:38.124
 * @todo 训练集文件对话dao接口
 */
public interface ITrainFileDialogueDAO extends IBaseHibernateDAO<TrainFileDialogueEntity, Serializable> {

    /**
     * 获取评价相关的训练集数据
     * @param evaluation 评价主键
     * @return
     */
    List<TrainFileDialogueEntity> getListByEvaluation(String evaluationId);

    /**
     * 统计训练集文件下的数据数量
     * @param trainFileId 训练集文件主键
     * @return 训练集文件问答总数
     */
    int getTotalDataCount(String trainFileId);

    /**
     * 批量更新提示词
     * @param ids 主键数组
     * @param newSystemRole 系统角色提示词
     * @return
     */
    Integer bacthUpdateSystemRole(ArrayList ids, String newSystemRole);

}
