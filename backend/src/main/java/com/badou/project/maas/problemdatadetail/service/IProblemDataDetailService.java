package com.badou.project.maas.problemdatadetail.service;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;


/**
 * @author badousoft
 * @date 2024-05-15 17:37:43.24
 * @todo 样本数据集详情管理 service接口
 **/
public interface IProblemDataDetailService extends IBaseSpringService<ProblemDataDetailEntity, Serializable> {
    /**
     * 创建初始化对象
     * @param parentId
     * @return
     */
    ProblemDataDetailEntity initEntity(String parentId);

    /**
     * 创建对象到数据库
     * @param problemDataDetailEntity
     */
     void createEntity(ProblemDataDetailEntity problemDataDetailEntity);

    /**
     * 获取指定范围的数据
     * @param id
     * @param range
     * @return
     */
    List<Object> getRangeData(String id, double range);
}