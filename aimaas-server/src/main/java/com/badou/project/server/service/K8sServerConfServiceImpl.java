package com.badou.project.server.service;

import com.badou.brms.base.support.spring.BaseSpringService;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.server.dao.K8sServerConfDao;
import com.badou.project.server.model.K8sServerConfEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @Author lm
 * @Description
 * @Date 2022/12/3 0001 2:38
 * @Version 1.0
 */
@Service
@Transactional
public class K8sServerConfServiceImpl extends BaseSpringService<K8sServerConfEntity, Serializable> implements IK8sServerConfService {

    @Autowired
    private K8sServerConfDao k8sServerConfDao;
    private K8sServerConfEntity defaultK8sservce;
    @Autowired
    private IModelAppService modelAppService;
    @Autowired
    private ITuningModelnService tuningModelnService;

    @Autowired
    public void setK8sServerConfDao(K8sServerConfDao k8sServerConfDao) {
        this.k8sServerConfDao = k8sServerConfDao;
        this.setBaseDAO(k8sServerConfDao);
    }

    @Override
    public K8sServerConfEntity getK8sConfigByAppId(String appId) {
        TuningModelnEntity linkAppModel = modelAppService.getLinkAppModel(appId);
        if(linkAppModel!=null){
            String serverId = tuningModelnService.getServerId(linkAppModel);
            return getK8sCustomServer(serverId);
        }
        return null;
    }

    @Override
    public K8sServerConfEntity getDefaultK8sServer() {
        if(Objects.nonNull(defaultK8sservce)){
            return defaultK8sservce;
        }
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0,null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("default_flag",1,null, QueryOperSymbolEnum.eq));
        List<K8sServerConfEntity> k8sServerConfEntities = k8sServerConfDao.find(queryCriterion);
        if(JsonResultUtil.arrayOneElement(k8sServerConfEntities)){
            defaultK8sservce = k8sServerConfEntities.get(0);
            return defaultK8sservce;
        }
        return null;
    }

    @Override
    public K8sServerConfEntity getK8sCustomServer(String id) {
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0,null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("id",id,null, QueryOperSymbolEnum.eq));
        List<K8sServerConfEntity> k8sServerConfEntities = k8sServerConfDao.find(queryCriterion);
        if(JsonResultUtil.arrayOneElement(k8sServerConfEntities)){
            defaultK8sservce = k8sServerConfEntities.get(0);
            return defaultK8sservce;
        }
        return null;
    }
}
