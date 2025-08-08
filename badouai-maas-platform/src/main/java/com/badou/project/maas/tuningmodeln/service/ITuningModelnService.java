package com.badou.project.maas.tuningmodeln.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.tuningmodeln.model.TrainPlanlogstatus;
import  com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import io.kubernetes.client.openapi.ApiException;


/**
 * @author badousoft
 * @date 2024-04-30 16:20:58.82
 * @todo 微调模型管理 service接口
 **/
public interface ITuningModelnService extends IBaseSpringService<TuningModelnEntity, Serializable> {

    /**
     * 获取正在运行的任务
     */
    TuningModelnEntity getRunningTask();

    /**
     * 设置失败的状态
     * @param tuningModelnEntity
     * @param msg
     */
    TuningModelnEntity setFailStatus(TuningModelnEntity tuningModelnEntity, String msg);

    /**
     * 设置微调成功!
     * @param tuningModelnEntity 微调对象
     * @param msg 消息
     * @return
     */
    TuningModelnEntity setSucccessStatus(TuningModelnEntity tuningModelnEntity, String msg);

    /**
     * 设置为应用运行状态
     * @param tuningModelnEntity
     */
    void setAppRunStatus(TuningModelnEntity tuningModelnEntity);

    /**
     * 把微调成功的任务 通过上下架转成可用于微调的模型
     * @param id 任务主键
     * @param type 处理类型 0.上架 1.下架
     * @return
     */
    JsonReturnBean coverTunModel(TuningModelnEntity tuningModelnEntity,Integer type,String pubVersion,String pubMsg);

    /**
     * 构建任务状态面板
     * @param tuningModelnEntity
     * @return
     */
    JsonReturnBean buildTaskStatusPanel(int type, TuningModelnEntity tuningModelnEntity) throws Exception;

    /**
     * 作废任务
     * @param id
     */
    String closeTask(String id) throws Exception;

    String getServerId(TuningModelnEntity tuningModelnEntity);

    /**
     * 根据模型ID 生成数据
     * @param id
     * @return
     */
    List<TrainPlanlogstatus> getCurrentPlanLog(String id);

    /**
     * 根据日志分析信息获得进度情况
     * @param kubernetesClient 当前服务的k8s服务器
     * @param nameSpace 工作空间
     * @param podName 服务名字
     * @param tuningModelnEntity 微调模型名字
     * @param allFlg 是否把执行日志拼接在前面. true为是 false为否
     * @return
     * @throws IOException
     * @throws ApiException
     */
    TuningModelnEntity getPodLogs(KubernetesApiClient kubernetesClient, String nameSpace, String podName, TuningModelnEntity tuningModelnEntity, boolean allFlg) throws IOException, ApiException;

    /**
     * 根据日志分析信息获得进度情况
     * @param kubernetesClient 当前服务的k8s服务器
     * @param nameSpace 工作空间
     * @param podName 服务名字
     * @param tuningModelnEntity 微调模型名字
     * @param allFlg 是否把执行日志拼接在前面. true为是 false为否
     * @return
     * @throws IOException
     * @throws ApiException
     */
    TuningModelnEntity getPodLogsUnUpdate(KubernetesApiClient kubernetesClient, String nameSpace, String podName, TuningModelnEntity tuningModelnEntity, boolean allFlg) throws IOException, ApiException;


    /**
     * 创建模型实体类
     * @param tuningModelnEntity 模型实体类
     */
    void createEntity(TuningModelnEntity tuningModelnEntity);

    /**
     * 创建训练集
     * @param tuningModelnEntity 模型实体类
     */
   void createTrainFile(TuningModelnEntity tuningModelnEntity);

    /**
     * 启动模型
     * @param modelWarehouseEntity 模型仓库实体
     * @return 是否成功 true代表成功
     * @throws Exception
     */
   boolean startmodel(ModelWarehouseEntity modelWarehouseEntity, ModelAppEntity modelAppEntity) throws Exception;

    /**
     * 构建数据集-一行的数据
     * @param trainFileDialogueEntity 训练数据
     * @param tuningModelnEntity 模型任务数据
     * @param roleDesc 角色描述
     * @param trainPlanEntity 微调方案
     * @return 构建完成的一行数据
     * @throws Exception
     */
   JSONObject buildOneRound(TrainFileDialogueEntity trainFileDialogueEntity, TuningModelnEntity tuningModelnEntity,String roleDesc,TrainPlanEntity trainPlanEntity) throws Exception;

    /**
     * 更新数据库-立即刷新
     * @param tuningModelnEntity 任务实体
     */
   void updateImmediately(TuningModelnEntity tuningModelnEntity);
}