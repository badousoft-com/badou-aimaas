package com.badou.project.maas.maasfile.service.impl;

import java.io.Serializable;
import java.util.List;

import com.badou.project.maas.maasfile.model.MaasFilePhotoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.maasfile.dao.IMaasFileVideoDAO;
import com.badou.project.maas.maasfile.model.MaasFileVideoEntity;
import com.badou.project.maas.maasfile.service.IMaasFileVideoService;


/**
 * @author badousoft
 * @date 2025-03-25 16:50:44.633
 *  视频管理 Service接口实现类
 **/
@Service
public class MaasFileVideoServiceImpl extends BaseSpringService<MaasFileVideoEntity, Serializable> implements IMaasFileVideoService {

	@Autowired
	private IMaasFileVideoDAO maasFileVideoDAO;

	@Autowired
	public void setMaasFileVideoDAO(IMaasFileVideoDAO maasFileVideoDAO) {
		this.maasFileVideoDAO = maasFileVideoDAO;
		super.setBaseDAO(maasFileVideoDAO);
	}

	@Override
	public void updateStatus(List<MaasFileVideoEntity> datas) {
		for (MaasFileVideoEntity data : datas) {
			Integer upStatus = data.getUpStatus();
			if (upStatus == null){
				upStatus = 0;
			}
			data.setUpStatus(upStatus==0?1:0);
		}
		batchUpdate(datas);
	}
}

