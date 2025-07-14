package com.badou.project.maas.maasfile.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.maasfile.model.MaasFileVideoDetailEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2025-03-25 17:04:48.273
 *  视频管理详情 service接口
 **/
public interface IMaasFileVideoDetailService extends IBaseSpringService<MaasFileVideoDetailEntity, Serializable> {
    /**
     * 创建单个文件上传
     * @param attachId 文件主键
     * @param fileVideoId 视频管理主键
     */
    void createVideoDetail(MultipartFile attachId, String fileVideoId) throws Exception;
}