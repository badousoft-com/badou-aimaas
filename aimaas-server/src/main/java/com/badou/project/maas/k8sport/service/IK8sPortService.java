package com.badou.project.maas.k8sport.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.modeeval.model.TunModeEvalEntity;


/**
 * @author badousoft
 * @date 2024-05-08 17:03:35.357
 * @todo 微调模型评价管理 service接口
 **/
public interface IK8sPortService extends IBaseSpringService<TunModeEvalEntity, Serializable> {
    /**
     * 通用计算下一个端口
     * @return
     */
    int calcNextPort() throws Exception;

    /**
     * 新增一条数据
     * @param port
     * @return
     * @throws Exception
     */
    int deleteNew(int port) throws Exception;

    /**
     * 获取最大的端口号码 默认是从35000开始
     * @return
     * @throws Exception
     */
    Integer getMax() throws Exception;
}