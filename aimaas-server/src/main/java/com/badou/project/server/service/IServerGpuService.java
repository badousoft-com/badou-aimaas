package com.badou.project.server.service;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.gpucalc.GpuCalcCardModel;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.server.model.ServerGpuEntity;


/**
 * @author badousoft
 * @date 2025-03-17 14:30:08.365
 *  显卡资源管理 service接口
 **/
public interface IServerGpuService extends IBaseSpringService<ServerGpuEntity, Serializable> {
    /**
     * 获取单个服务器的显卡信息
     * @param serverId
     * @return
     */
    List<ServerGpuEntity> getCardData(String serverId);

    /**
     * 获取某个服务器的显卡任务信息
     * @param execGpuCard
     * @param serverId
     * @return
     */
    String buildTunTaskCard(String execGpuCard,String serverId) throws DataEmptyException;

    /**
     * 获取某个服务器的显卡模型部署信息
     * @param serverId
     * @return
     */
    List<TuningModelnEntity> getModelApps(String serverId);
}