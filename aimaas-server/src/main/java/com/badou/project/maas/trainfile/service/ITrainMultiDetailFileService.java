package com.badou.project.maas.trainfile.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.trainfile.model.TrainMultiDetailFileEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2024-11-19 11:40:36.43
 *  多模态训练文件详情 service接口
 **/
public interface ITrainMultiDetailFileService extends IBaseSpringService<TrainMultiDetailFileEntity, Serializable> {
    /**
     * 创建单个文件上传
     * @param attachId 文件主键
     * @param mutiTrainId 多模态图片主键
     */
    void createPhotoDetail(MultipartFile attachId, String mutiTrainId) throws Exception;

}