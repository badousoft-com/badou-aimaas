package com.badou.project.maas.trainfile.service.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.badou.brms.attach.AttachFactory;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.util.InstanceFactory;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.GlobalConsts;
import com.badou.project.maas.common.util.AttachUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.trainfile.dao.ITrainMultiDetailFileDAO;
import com.badou.project.maas.trainfile.model.TrainMultiDetailFileEntity;
import com.badou.project.maas.trainfile.service.ITrainMultiDetailFileService;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2024-11-19 11:40:36.43
 *  多模态训练文件详情 Service接口实现类
 **/
@Service
public class TrainMultiDetailFileServiceImpl extends BaseSpringService<TrainMultiDetailFileEntity, Serializable> implements ITrainMultiDetailFileService {

	@Autowired
	private ITrainMultiDetailFileDAO trainMultiDetailFileDAO;

	@Autowired
	public void setTrainMultiDetailFileDAO(ITrainMultiDetailFileDAO trainMultiDetailFileDAO) {
		this.trainMultiDetailFileDAO = trainMultiDetailFileDAO;
		super.setBaseDAO(trainMultiDetailFileDAO);
	}
	@Override
	public void createPhotoDetail(MultipartFile attachId, String mutiTrainId) throws Exception{
		//图片文件格式验证
		String originalFilename = attachId.getOriginalFilename();
		if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
			throw new Exception("文件无扩展名!请上传图片格式(jpg,jpeg,png,gif,bmp)");
		}
		String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
		List<String> allowedExtensions = GlobalConsts.IMAGE_EXTENSIONS;
		if (!allowedExtensions.contains(extension)) {
			throw new Exception("不支持此文件格式!请上传图片格式(jpg,jpeg,png,gif,bmp)");
		}
		//创建实体对象
		TrainMultiDetailFileEntity trainMultiDetailFileEntity = new TrainMultiDetailFileEntity();
		trainMultiDetailFileEntity.setMutiTrainId(mutiTrainId);
		trainMultiDetailFileEntity.setUpdateTime(new Date());
		trainMultiDetailFileEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		trainMultiDetailFileEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		trainMultiDetailFileEntity.setFlgDeleted(GlobalConsts.ZERO);

		//调用工具类设置附件字段和父ID
		AttachUtil.createAttach(attachId, trainMultiDetailFileEntity, "attachFile", "attachId", "mutiTrainId", mutiTrainId);
		create(trainMultiDetailFileEntity);
	}
}

