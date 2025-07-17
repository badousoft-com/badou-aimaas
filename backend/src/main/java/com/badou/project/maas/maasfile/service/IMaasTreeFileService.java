package com.badou.project.maas.maasfile.service;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.maasfile.model.MaasTreeFileEntity;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2025-02-05 14:13:28.676
 *  系统文件夹 service接口
 **/
public interface IMaasTreeFileService extends IBaseSpringService<MaasTreeFileEntity, Serializable> {
    /**
     * 批量创建文件
     * @param name 文件
     * @param parentId 父主键
     * @param parentName 父名称
     */
    void batchCreateEntity(List<MultipartFile> name, String parentId, String parentName) throws Exception;

    /**
     * 变更这些任务运行中
     * @param maasTreeFileEntities
     */
    void changeAllRun(List<MaasTreeFileEntity> maasTreeFileEntities) throws RuntimeException;
}