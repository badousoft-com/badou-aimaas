package com.badou.project.maas.trainfile.service;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.trainfile.model.TrainMultiFileEntity;
import com.badou.project.maas.trainfile.model.TrainMultiVideoEntity;


/**
 * @author badousoft
 * @date 2025-03-26 15:26:00.791
 *  多模态视频训练文件管理 service接口
 **/
public interface ITrainMultiVideoService extends IBaseSpringService<TrainMultiVideoEntity, Serializable> {
    /**
     * 批量更新启用状态 启用变成停用 停用变成启用
     * @param datas 数据列表
     */
    void updateStatus(List<TrainMultiVideoEntity> datas);
}