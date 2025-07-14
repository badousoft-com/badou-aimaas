package com.badou.project.maas.trainfile.service;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfile.model.TrainMultiFileEntity;


/**
 * @author badousoft
 * @date 2024-11-19 11:40:09.389
 *  多模态训练文件管理 service接口
 **/
public interface ITrainMultiFileService extends IBaseSpringService<TrainMultiFileEntity, Serializable> {

    /**
     * 批量更新启用状态 启用变成停用 停用变成启用
     * @param datas 数据列表
     */
    void updateStatus(List<TrainMultiFileEntity> datas);

}