package  com.badou.project.maas.modelapp.service.impl;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import  com.badou.project.maas.modelapp.dao.IModelAppVllmParamDAO;
import  com.badou.project.maas.modelapp.model.ModelAppVllmParamEntity;
import  com.badou.project.maas.modelapp.service.IModelAppVllmParamService;


/**
 * @author badousoft
 * @date 2025-03-10 18:23:28.765
 *  模型应用VLLM参数 Service接口实现类
 **/
@Service
public class ModelAppVllmParamServiceImpl extends BaseSpringService<ModelAppVllmParamEntity, Serializable> implements IModelAppVllmParamService {

	@Autowired
	private IModelAppVllmParamDAO modelAppVllmParamDAO;

	@Autowired
	public void setModelAppVllmParamDAO(IModelAppVllmParamDAO modelAppVllmParamDAO) {
		this.modelAppVllmParamDAO = modelAppVllmParamDAO;
		super.setBaseDAO(modelAppVllmParamDAO);
	}
}

