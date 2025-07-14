package com.badou.project.server.service;

import com.badou.brms.base.support.spring.BaseSpringService;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.server.dao.RegistryAddressDao;
import com.badou.project.server.model.RegistryAddressEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * @Author lm
 * @Description
 * @Date 2022/12/3 0001 2:38
 * @Version 1.0
 */
@Service
@Transactional
public class
RegistryAddressServiceImpl extends BaseSpringService<RegistryAddressEntity, Serializable> implements IRegistryAddressService {

    @Autowired
    private RegistryAddressDao registryAddressDao;

    @Autowired
    public void setRegistryAddressDao(RegistryAddressDao registryAddressDao) {
        this.registryAddressDao = registryAddressDao;
        this.setBaseDAO(registryAddressDao);
    }

    @Override
    public RegistryAddressEntity getRegistryAddress(String imageKey) {
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("imageKey",imageKey,null, QueryOperSymbolEnum.eq));
        List<RegistryAddressEntity> registryAddressEntities = registryAddressDao.find(queryCriterion);
        if(registryAddressEntities.size()!=1){
            return null;
        }
        return registryAddressEntities.get(0);
    }

    @Override
    public RegistryAddressEntity getDefaultRegistryAddress() throws Exception {
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted", 0, null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("default_flag", 1, null, QueryOperSymbolEnum.eq));
        List<RegistryAddressEntity> registryAddressEntities = registryAddressDao.find(queryCriterion);
        if(registryAddressEntities.size()!=1){
            throw new Exception("镜像仓库必须存在一个默认值!");
        }
        return registryAddressEntities.get(0);
    }

}
