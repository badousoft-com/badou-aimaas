package com.badou.project.maas.problemdata.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.problemdata.model.ProblemDataVideoDetailEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2025-03-26 11:30:40.876
 *  视频样本集详情 service接口
 **/
public interface IProblemDataVideoDetailService extends IBaseSpringService<ProblemDataVideoDetailEntity, Serializable> {
    /**
     * 创建单个文件
     * @param attachId 文件主键
     * @param dataVideoId 视频样本集主键
     */
    void createVideoDetail(MultipartFile attachId, String dataVideoId) throws Exception;
}