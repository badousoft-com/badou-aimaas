package com.badou.project.maas.trainfile.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.trainfile.model.TrainMultiVideoDetailEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2025-03-26 15:26:46.762
 *  多模态视频训练文件详情 service接口
 **/
public interface ITrainMultiVideoDetailService extends IBaseSpringService<TrainMultiVideoDetailEntity, Serializable> {
    /**
     * 创建单个文件上传
     * @param attachId 文件主键
     * @param multiTrainVideoId 多模态视频主键
     */
    void createVideoDetail(MultipartFile attachId, String multiTrainVideoId) throws Exception;
}