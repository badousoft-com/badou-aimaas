package com.badou.project.maas.modelsync.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;


/**
 * @author badousoft
 * @date 2025-04-11 10:33:39.208
 *  模型同步任务 service接口
 **/
public interface IModelSyncTaskService extends IBaseSpringService<ModelSyncTaskEntity, Serializable> {

    /**
     * 从ModelScope下载模型 通过路径文根
     * @param modelWarehouseEntity 模型仓库实体
     * @return 如果返回String 代表失败了 如果不是 则代表成功
     */
    Object loadModelByModelScope(String paramServerId,String serverName,String path);

    /**
     * 关闭模型同步任务
     * @param modelSyncTaskEntity
     */
    void closeTask(ModelSyncTaskEntity modelSyncTaskEntity);

    /**
     * 设置任务为成功状态
     * @param modelSyncTaskEntity
     */
    void updateTaskRunning(ModelSyncTaskEntity modelSyncTaskEntity);

    /**
     * 设置任务为成功状态
     * @param modelSyncTaskEntity
     */
    void updateTaskSuccess(ModelSyncTaskEntity modelSyncTaskEntity,String msg);

    /**
     * 设置任务为失败状态
     * @param modelSyncTaskEntity
     */
    void updateTaskFail(ModelSyncTaskEntity modelSyncTaskEntity,String msg);

    /**
     * 计算模型仓库是否需要进行模型之间的同步
     * 需要确保所有服务器都有该模型才可以往下执行
     * @param modelWarehouseEntity 模型仓库实体
     * @return 执行结果信息
     */
    JsonReturnBean calcModelSyncStatus(ModelWarehouseEntity modelWarehouseEntity);

    /**
     * 开始模型同步任务
     * @param modelWarehouseEntity 模型仓库实体
     * @param targetServers 同步目标服务器
     * @return
     */
    JsonReturnBean startServerSyncTask(ModelWarehouseEntity modelWarehouseEntity,String[] targetServers);

    /**
     * 检查当前模型是否有过期/异常的模型同步任务
     * @param modelWarehouseEntity 模型部署方案
     * @return
     */
    String checkValidTask(ModelWarehouseEntity modelWarehouseEntity);
}