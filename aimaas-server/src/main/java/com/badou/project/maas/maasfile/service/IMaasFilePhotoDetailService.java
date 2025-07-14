package com.badou.project.maas.maasfile.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.maasfile.model.MaasFilePhotoDetailEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2025-03-25 14:50:33.074
 *  图片管理详情 service接口
 **/
public interface IMaasFilePhotoDetailService extends IBaseSpringService<MaasFilePhotoDetailEntity, Serializable> {

    /**
     * 创建单个文件上传
     * @param attachId 文件主键
     * @param filePhotoId 图片管理主键
     */
    void createPhotoDetail(MultipartFile attachId, String filePhotoId) throws Exception;
}