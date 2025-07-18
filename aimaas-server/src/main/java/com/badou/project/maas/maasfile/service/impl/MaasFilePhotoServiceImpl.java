package com.badou.project.maas.maasfile.service.impl;

import java.io.Serializable;
import java.util.List;

import com.badou.project.maas.trainfile.model.TrainMultiFileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.maasfile.dao.IMaasFilePhotoDAO;
import com.badou.project.maas.maasfile.model.MaasFilePhotoEntity;
import com.badou.project.maas.maasfile.service.IMaasFilePhotoService;


/**
 * @author badousoft
 * @date 2025-03-25 10:18:20.709
 *  图片管理 Service接口实现类
 **/
@Service
public class MaasFilePhotoServiceImpl extends BaseSpringService<MaasFilePhotoEntity, Serializable> implements IMaasFilePhotoService {

	@Autowired
	private IMaasFilePhotoDAO maasFilePhotoDAO;

	@Autowired
	public void setMaasFilePhotoDAO(IMaasFilePhotoDAO maasFilePhotoDAO) {
		this.maasFilePhotoDAO = maasFilePhotoDAO;
		super.setBaseDAO(maasFilePhotoDAO);
	}

	@Override
	public void updateStatus(List<MaasFilePhotoEntity> datas) {
		for (MaasFilePhotoEntity data : datas) {
			Integer upStatus = data.getUpStatus();
			if (upStatus == null){
				upStatus = 0;
			}
			data.setUpStatus(upStatus==0?1:0);
		}
		batchUpdate(datas);
	}
}

