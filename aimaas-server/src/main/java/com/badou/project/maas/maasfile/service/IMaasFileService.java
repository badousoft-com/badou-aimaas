package com.badou.project.maas.maasfile.service;

import java.io.Serializable;
import java.util.ArrayList;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.project.maas.maasfile.model.MaasFileEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2024-07-15 15:48:36.55
 * @todo 文件管理 service接口
 **/
public interface IMaasFileService extends IBaseSpringService<MaasFileEntity, Serializable> {
    /**
     * 转成预训练集
     * @param files
     * @return
     */
    String coverPreTrain(ArrayList<MultipartFile> files,String modeName) throws Exception;

    /**
     * 上传文件
     * @param files
     * @return
     */
    String uploadFiles(ArrayList<MultipartFile> files,String tipContent) throws Exception;

    /**
     * 异步构建文件数据
     * @param values 需要分析的文件数据
     * @param logonCertificate 当前用户线程信息
     * @return
     */
    String startBuildFiles(ArrayList<MaasFileEntity> values, LogonCertificate logonCertificate);
}