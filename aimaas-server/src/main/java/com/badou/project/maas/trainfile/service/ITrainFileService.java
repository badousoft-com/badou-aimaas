package com.badou.project.maas.trainfile.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.maas.traindata.web.form.TrainDataForm;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfile.web.form.TrainFileQustionForm;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2024-05-16 11:07:50.5
 * @todo 训练集文件管理 service接口
 **/
public interface ITrainFileService extends IBaseSpringService<TrainFileEntity, Serializable> {
    /**
     * 批量更新启用状态 启用变成停用 停用变成启用
     * @param datas 数据列表
     */
    void updateStatus(List<TrainFileEntity> datas);

    /**
     * 获取初始化的对象
     * @return
     */
    TrainFileEntity buildInitTrainFile();

    /**
     * 根据文件
     * @param importFile
     * @param type
     */
    boolean createTrainDataBYFiles(List<MultipartFile> importFile, int type,String[] ids) throws Exception;

    /**
     *
     * @param trainFileQustionForm
     * @return
     * @throws Exception
     */
    void getTrainDataToQues(TrainFileQustionForm trainFileQustionForm) throws Exception;

    /**
     * 强行刷新hibernate并获取训练集问答数量来更新
     * @param ids
     */
    void flushAndGetTotalCount(String[] ids);

    /**
     * 训练集文件增加更新数量
     * @param id 训练集文件主键
     * @param numCount 训练集内容总数
     */
    void updateCount(String id,int numCount);
}