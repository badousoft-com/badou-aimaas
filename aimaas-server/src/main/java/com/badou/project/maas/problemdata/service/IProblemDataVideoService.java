package com.badou.project.maas.problemdata.service;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.problemdata.model.ProblemDataPictureEntity;
import com.badou.project.maas.problemdata.model.ProblemDataVideoEntity;


/**
 * @author badousoft
 * @date 2025-03-26 11:30:02.141
 *  视频样本集 service接口
 **/
public interface IProblemDataVideoService extends IBaseSpringService<ProblemDataVideoEntity, Serializable> {
    /**
     * 批量更新启用状态 启用变成停用 停用变成启用
     * @param datas 数据列表
     */
    void updateStatus(List<ProblemDataVideoEntity> datas);

}