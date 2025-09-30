package com.badou.project.maas.modelapp.service;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataValidException;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.model.TalkEntity;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.kubernetes.client.openapi.models.V1Pod;


/**
 * @author badousoft
 * @date 2024-05-27 11:33:46.513
 * @todo 模型应用管理 service接口
 **/
public interface IModelAppService extends IBaseSpringService<ModelAppEntity, Serializable> {
    /**
     * 检查该模型是否重复部署
     * @param warehouseId 模型仓库ID
     * @return 查询到的模型任务列表
     */
    List<ModelAppEntity> checkSameTask(String warehouseId);

    /**
     * 获取模型应用POD
     * @param modelAppEntity 模型应用实体
     * @param kubernetesApiClient K8S客户端
     * @return
     */
    V1Pod getModelAppPod(ModelAppEntity modelAppEntity, KubernetesApiClient kubernetesApiClient);

    /**
     * 设置部署模型任务失败状态
     * @param modelAppEntity 模型应用部署实体
     * @param msg 失败信息
     * @return
     */
    ModelAppEntity setFail(ModelAppEntity modelAppEntity, String msg);

    /**
     * 根据模型仓库信息生成部署模型需要的显卡资源
     * @param modelWarehouseEntity
     * @return
     * @throws Exception
     */
    TuningModelnEntity getAppCardMsg(ModelWarehouseEntity modelWarehouseEntity) throws Exception;

    /**
     * 关联应用聊天信息
     * @param id
     * @return
     */
    JsonReturnBean linkAppTalk(String id);

    /**
     * 检查应用运行状态 返回Null返回启动成功 默认最多等待5钟
     * @param modelAppEntity
     * @return
     */
    String checkAppStarted(ModelAppEntity modelAppEntity,Integer timeOutMin);

    /**
     * 获取模型api信息
     * @param id
     * @return
     */
    Object loadApiMsg(String id) throws DataEmptyException, DataValidException;

    /**
     * 通过应用主键 获取与该应用关联的模型结果
     * @param id
     * @return
     */
    TuningModelnEntity getLinkAppModel(String id);

    /**
     * 创建实体
     * @param modelAppEntity
     */
    void createEntity(ModelAppEntity modelAppEntity);

    /**
     * 回收移除当前部署应用的相关资源
     * @param modelAppEntity 部署应用主体
     * @param kubernetesClient k8s客户端
     * @param v1Pod k8s POD 可以是空 如果不给 自动查
     * @return
     */
    String removeAppResouce(ModelAppEntity modelAppEntity, KubernetesApiClient kubernetesClient,V1Pod v1Pod) throws Exception;

    /**
     * 停止应用
     * @param id 获取主键
     */
    String stopApp(String id,boolean deleteFlag,String customMsg);

    /**
     * 获取应用日志
     * @param id 获取主键
     */
    String getLogs(String id,ModelAppEntity modelAppEntity,String logType, Integer size, Integer flushTime,String nameSpace);

    /**
     * 和AI对话
     * @param talkEntity 对话实体
     * @return
     */
    String talkToAi(TalkEntity talkEntity);

    /**
     * 重启应用
     * @param id
     * @return
     */
    String restartApp(String id);

    /**
     * 检查模型是否正在运行
     * @param modelAppEntity 模型应用实体
     * @return true 正在运行 false 未运行
     */
    Integer checkModelRunningStatus(ModelAppEntity modelAppEntity,JSONObject e);
}