package  com.badou.project.maas.modelapp.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import  com.badou.project.maas.modelapp.dao.IModelAppVllmParamDAO;
import  com.badou.project.maas.modelapp.model.ModelAppVllmParamEntity;


/**
 * @author badousoft
 * @date 2025-03-10 18:23:28.765
 *  模型应用VLLM参数dao接口实现类
 **/
@Repository
public class ModelAppVllmParamDAOImpl extends BaseHibernateDAO<ModelAppVllmParamEntity, Serializable> implements IModelAppVllmParamDAO {

}