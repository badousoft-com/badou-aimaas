package com.badou.project.server.service;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.server.model.RegistryAddressEntity;

import java.io.Serializable;

/**
 * @Author lm
 * @Description
 * @Date 2022/12/3 0001 2:38
 * @Version 1.0
 */
public interface IRegistryAddressService extends IBaseSpringService<RegistryAddressEntity, Serializable> {
    /**
     * 根据关键词获取镜像仓库信息
     * @param imageKey 镜像Key
     * @return
     */
    RegistryAddressEntity getRegistryAddress(String imageKey);

    /**
     * 获取默认的镜像仓库信息
     * @return
     */
    RegistryAddressEntity getDefaultRegistryAddress() throws Exception;
}
