package com.badou.project.maas.problemdata.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.problemdata.model.ProblemDataPictureDetailEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2025-03-26 10:20:39.371
 *  图片样本集详情 service接口
 **/
public interface IProblemDataPictureDetailService extends IBaseSpringService<ProblemDataPictureDetailEntity, Serializable> {
    /**
     * 创建单个文件上传
     * @param attachId 文件主键
     * @param dataPictureId 图片样本集主键
     */
    void createPictureDetail(MultipartFile attachId, String dataPictureId) throws Exception;
}