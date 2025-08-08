package com.badou.project.maas.trainplan.service.impl;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.maas.planlink.model.PlanLinkEntity;
import com.badou.project.maas.planlink.service.IPlanLinkService;
import com.badou.project.maas.trainplan.dao.ITrainPlanDAO;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.tuningplanparams.model.TuningPlanParamsEntity;
import com.badou.project.maas.tuningplanparams.service.ITuningPlanParamsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.trainplan.service.ITrainPlanService;


/**
 * @author badousoft
 * @date 2024-04-17 15:10:34.232
 * @todo 微调方案 Service接口实现类
 **/
@Service

public class TrainPlanServiceImpl extends BaseSpringService<TrainPlanEntity, Serializable> implements ITrainPlanService {

    @Autowired
    private ITrainPlanDAO trainPlanDAO;
    @Autowired
    private IPlanLinkService planLinkService;
    @Autowired
    private ITuningPlanParamsService tuningPlanParamsService;

    @Autowired
    public void setTrainPlanDAO(ITrainPlanDAO trainPlanDAO) {
        this.trainPlanDAO = trainPlanDAO;
        this.baseDAO = trainPlanDAO;
    }

    @Override
    public void copyObject(String id) {
        //复制主实体
        TrainPlanEntity trainPlanEntity = trainPlanDAO.find(id);
        TrainPlanEntity newTrainPlan = new TrainPlanEntity();
        BeanUtils.copyProperties(trainPlanEntity,newTrainPlan);
        newTrainPlan.setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
        newTrainPlan.setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
        newTrainPlan.setCreateTime(new Date());
        newTrainPlan.setUpdateTime(new Date());
        newTrainPlan.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
        newTrainPlan.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
        newTrainPlan.setName("复制_"+trainPlanEntity.getName());
        newTrainPlan.setCode(trainPlanEntity.getModelName()+"-"+ UUID.randomUUID().toString());
        newTrainPlan.setId(null);
        trainPlanDAO.create(newTrainPlan);
        //复制训练集文件
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("planLinkId",trainPlanEntity.getId(),null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null, QueryOperSymbolEnum.eq));
        List<PlanLinkEntity> planLinkEntities = planLinkService.find(queryCriterion);
        planLinkEntities.forEach(e->{
            PlanLinkEntity planLinkEntity = new PlanLinkEntity();
            BeanUtils.copyProperties(e,planLinkEntity);
            planLinkEntity.setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            planLinkEntity.setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
            planLinkEntity.setCreateTime(new Date());
            planLinkEntity.setUpdateTime(new Date());
            planLinkEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            planLinkEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            planLinkEntity.setId(null);
            planLinkEntity.setPlanLinkId(newTrainPlan.getId());
            planLinkService.create(planLinkEntity);
        });
        //复制微调方案参数
        queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("planId",trainPlanEntity.getId(),null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null, QueryOperSymbolEnum.eq));
        List<TuningPlanParamsEntity> tuningPlanParamsEntities = tuningPlanParamsService.find(queryCriterion);
        tuningPlanParamsEntities.forEach(e->{
            TuningPlanParamsEntity tuningPlanParamsEntity = new TuningPlanParamsEntity();
            BeanUtils.copyProperties(e,tuningPlanParamsEntity);
            tuningPlanParamsEntity.setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            tuningPlanParamsEntity.setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
            tuningPlanParamsEntity.setCreateTime(new Date());
            tuningPlanParamsEntity.setUpdateTime(new Date());
            tuningPlanParamsEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            tuningPlanParamsEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            tuningPlanParamsEntity.setId(null);
            tuningPlanParamsEntity.setPlanId(newTrainPlan.getId());
            tuningPlanParamsService.create(tuningPlanParamsEntity);
        });
    }
}
 
 