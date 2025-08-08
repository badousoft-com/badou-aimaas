package com.badou.project.server.service;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.server.model.K8sServerConfEntity;

import java.io.Serializable;

/**
 * @Author lm
 * @Description
 * @Date 2022/12/3 0001 2:38
 * @Version 1.0
 */
public interface IK8sServerConfService extends IBaseSpringService<K8sServerConfEntity, Serializable> {
    /**
     * 通过应用主键获取k8s服务器配置
     * @param appId
     * @return
     */
    K8sServerConfEntity getK8sConfigByAppId(String appId);

    /**
     * 获取默认的资源服务器
     * @return
     */
    K8sServerConfEntity getDefaultK8sServer();

    /**
     * 获取某个服务器
     * @param id
     * @return
     */
    K8sServerConfEntity getK8sCustomServer(String id);

}
