package com.badou.project.maas.maasfile.service;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.maasfile.model.MaasFilePhotoEntity;
import com.badou.project.maas.trainfile.model.TrainMultiFileEntity;


/**
 * @author badousoft
 * @date 2025-03-25 10:18:20.709
 *  图片管理 service接口
 **/
public interface IMaasFilePhotoService extends IBaseSpringService<MaasFilePhotoEntity, Serializable> {
    /**
     * 批量更新启用状态 启用变成停用 停用变成启用
     * @param datas 数据列表
     */
    void updateStatus(List<MaasFilePhotoEntity> datas);
}