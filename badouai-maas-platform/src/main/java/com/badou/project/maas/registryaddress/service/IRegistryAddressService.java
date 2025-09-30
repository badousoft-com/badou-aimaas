package com.badou.project.maas.registryaddress.service;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.registryaddress.model.RegistryAddressEntity;

import java.io.Serializable;

/**
 * @Author lm
 * @Description
 * @Date 2022/12/3 0001 2:38
 * @Version 1.0
 */
public interface IRegistryAddressService extends IBaseSpringService<RegistryAddressEntity, Serializable> {
    /**
     * 获取默认的镜像仓库信息
     * @return
     */
    RegistryAddressEntity getDefaultRegistryAddress();
}
