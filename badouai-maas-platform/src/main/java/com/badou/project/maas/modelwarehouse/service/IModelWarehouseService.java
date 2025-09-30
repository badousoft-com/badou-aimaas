package com.badou.project.maas.modelwarehouse.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataValidException;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;


/**
 * @author badousoft
 * @date 2024-08-28 11:30:33.707
 *  模型仓库 service接口
 **/
public interface IModelWarehouseService extends IBaseSpringService<ModelWarehouseEntity, Serializable> {
    /**
     * 计算当前模型部署任务需要多少显存
     * 对于双卡（TP=2）的情况：
     * 模型权重：每个GPU存储一半，所以每个GPU需要约36.5G（73G/2）中的权重部分。
     * KV缓存：每个GPU存储一半，所以每个GPU需要约36.5G中的KV缓存部分。
     * 运行时开销：每个GPU都需要完整的运行时开销（比如5G），不能分区。
     * 系统开销：每个GPU都需要完整的系统开销（比如0.6G），不能分区。
     * 所以，双卡部署时，每个GPU的显存需求是：
     * 单卡需求 = (模型权重/2) + (KV缓存/2) + 运行时开销 + 系统开销
     * @param modelWarehouseEntity
     * @return 单卡显存需求是指每张卡需要多少显存（已经除以TP数），集群总显存需求=单卡显存需求*TP数 详细参考VLLM张量并行机制
     */
    double calcGpuCache(ModelWarehouseEntity modelWarehouseEntity) throws DataValidException, DataEmptyException;

}