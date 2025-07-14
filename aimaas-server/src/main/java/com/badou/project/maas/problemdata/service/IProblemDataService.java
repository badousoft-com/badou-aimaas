package com.badou.project.maas.problemdata.service;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;
import com.badou.project.maas.trainfile.model.TrainFileEntity;


/**
 * @author badousoft
 * @date 2024-05-15 17:37:11.964
 * @todo 样本数据集管理 service接口
 **/
public interface IProblemDataService extends IBaseSpringService<ProblemDataEntity, Serializable> {
     /**
      *  通过JSON 创建样本集
      * @param jsonObject json对象
      * @param type 样本类型
      * @return
      */
     ProblemDataDetailEntity buildEntityByJSON(String mainId, JSONObject jsonObject, int type);

     /**
      * 获取初始化的对象
      * @return
      */
     ProblemDataEntity buildInitEntity();

     /**
      * 把当前预训练集转成指令监督训练集
      * @param id
      * @return
      */
     String coverSft(String id);

     /**
      * 更新最新的样本数据
      * @param id 样本数据导入
      */
     void updateNewestCount(String id);

     /**
      * 创建实体
      * @param problemDataEntity
      */
     void createEntity(ProblemDataEntity problemDataEntity);

     /**
      * 导出到训练集文件
      * @param ids 样本数据ID数组
      * @param type 导入类型
      * @param valueList 导入的目标训练集
      * @return
      */
     String exportTrainFile(String fileName,String[] ids,int type, List<TrainFileEntity> valueList);
}