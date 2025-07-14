package com.badou.project.maas.modelwarehouse.service;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.modelwarehouse.model.WareHouseVllmParamEntity;


/**
 * @author badousoft
 * @date 2025-03-08 15:09:21.737
 *  模型仓库VLLM参数 service接口
 **/
public interface IWareHouseVllmParamService extends IBaseSpringService<WareHouseVllmParamEntity, Serializable> {
    /**
     * 根据模型仓库主键获取对应的VLLM参数
     * @param id
     * @return
     */
    List<WareHouseVllmParamEntity> getListByWareHoseId(String id);

}