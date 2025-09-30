package com.badou.project.maas.trainfile.service.impl;

import java.io.*;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.problemdata.service.IProblemDataService;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;
import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailService;
import com.badou.project.maas.trainfile.TrainFileAsyncTask;
import com.badou.project.maas.trainfile.web.form.TrainFileQustionForm;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.project.maas.trainfiledialoguedetail.model.TrainFileDialoguedetailEntity;
import com.badou.project.maas.trainfiledialoguedetail.service.ITrainFileDialoguedetailService;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.trainfile.dao.ITrainFileDAO;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfile.service.ITrainFileService;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2024-05-16 11:07:50.5
 * @todo 训练集文件管理 Service接口实现类
 **/
@Service
public class TrainFileServiceImpl extends BaseSpringService<TrainFileEntity, Serializable> implements ITrainFileService {

    @Autowired
    private ITrainFileDAO trainFileDAO;
    @Autowired
    private IProblemDataService problemDataService;
    @Autowired
    private IProblemDataDetailService problemDataDetailService;
    @Autowired
    private ITrainFileDialogueService trainFileDialogueService;
    @Autowired
    private ITrainFileDialoguedetailService trainFileDialoguedetailService;
    @Autowired
    private TrainFileAsyncTask asyncTask;

    @Autowired
    public void setTrainFileDAO(ITrainFileDAO trainFileDAO) {
        this.trainFileDAO = trainFileDAO;
        super.setBaseDAO(trainFileDAO);
    }

    @Override
    public void updateStatus(List<TrainFileEntity> datas) {
        for (TrainFileEntity data : datas) {
            Integer upStatus = data.getUpStatus();
            if (upStatus == null){
                upStatus = 0;
            }
            data.setUpStatus(upStatus==0?1:0);
        }
        batchUpdate(datas);
    }

    @Override
    public TrainFileEntity buildInitTrainFile() {
        TrainFileEntity trainFileEntity = new TrainFileEntity();
        trainFileEntity.setFlgDeleted(0);
        trainFileEntity.setApplicableSubject(0);
        trainFileEntity.setRemark("数据导入");
        trainFileEntity.setUpdateTime(new Date());
        trainFileEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
        trainFileEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
        trainFileEntity.setRoleDesc("你是一个中文小助手.擅长把收集来的问题分析得出结论");
        return trainFileEntity;
    }

    @Override
    public boolean createTrainDataBYFiles(List<MultipartFile> importFile, int type, String[] ids) throws Exception {
//        asyncTask.startAnycTask(importFile, type, ids,LogonCertificateHolder.getLogonCertificate());
        return true;
    }

    @Override
    public void getTrainDataToQues(TrainFileQustionForm trainFileQustionForm) throws Exception {
        //根据ID获取当前问题
        ProblemDataEntity problemDataEntity = problemDataService.find(trainFileQustionForm.getId());
        if (StringUtils.isEmpty(problemDataEntity.getId())) {
            throw new Exception("数据异常!");
        }
        JSONArray trainFileEntity = trainFileQustionForm.getTrainFileEntitys();
        if (trainFileEntity == null || trainFileEntity.size() == 0) {
            throw new Exception("数据异常!");
        }
        //获取对应训练集的问题数据
        trainFileEntity.forEach(e -> {
            LinkedHashMap result = (LinkedHashMap) e;
            QueryCriterion queryCriterion = new QueryCriterion();
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("trainFileId", result.get("id"), null, QueryOperSymbolEnum.eq));
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
            List<TrainFileDialogueEntity> trainFileDialogueEntities = trainFileDialogueService.find(queryCriterion);
            trainFileDialogueEntities.forEach(trainFileDialogueEntity -> {
                if (JsonResultUtil.isNotNull(trainFileDialogueEntity.getQuestion(), trainFileDialogueEntity.getFeedback())) {
                    ProblemDataDetailEntity problemDataDetailEntity = new ProblemDataDetailEntity();
                    problemDataDetailEntity.setFlgDeleted(0);
                    problemDataDetailEntity.setFeedback(trainFileDialogueEntity.getFeedback());
                    problemDataDetailEntity.setQuestion(trainFileDialogueEntity.getQuestion());
                    problemDataDetailEntity.setProblemDataId(trainFileQustionForm.getId());
                    problemDataDetailEntity.setType(0);
                    problemDataDetailService.createEntity(problemDataDetailEntity);
                }
            });
            //查询多轮数据
            for (TrainFileDialogueEntity trainFileDialogueEntity : trainFileDialogueEntities) {
                queryCriterion = new QueryCriterion();
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("dialogueId", trainFileDialogueEntity.getId(), null, QueryOperSymbolEnum.eq));
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
                List<TrainFileDialoguedetailEntity> trainFileDialoguedetailEntities = trainFileDialoguedetailService.find(queryCriterion);
                //根据训练集数据创建问答
                trainFileDialoguedetailEntities.forEach(trainFileDialoguedetailEntity -> {
                    ProblemDataDetailEntity problemDataDetailEntity = new ProblemDataDetailEntity();
                    problemDataDetailEntity.setFlgDeleted(0);
                    problemDataDetailEntity.setFeedback(trainFileDialoguedetailEntity.getFeedback());
                    problemDataDetailEntity.setQuestion(trainFileDialoguedetailEntity.getQuestion());
                    problemDataDetailEntity.setProblemDataId(trainFileQustionForm.getId());
                    problemDataDetailEntity.setType(0);
                    problemDataDetailService.createEntity(problemDataDetailEntity);
                });
            }
        });
    }

    @Override
    public void flushAndGetTotalCount(String[] ids) {
        trainFileDAO.flushAndGetTotalCount(ids);
    }

    @Override
    public void updateCount(String id, int numCount) {
        trainFileDAO.updateCount(id,numCount);
    }
}
 
 