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

import com.badou.project.maas.trainfile.dao.ITrainMultiVideoDetailDAO;
import com.badou.project.maas.trainfile.model.TrainMultiVideoDetailEntity;
import com.badou.project.maas.trainfile.service.ITrainMultiVideoDetailService;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2025-03-26 15:26:46.762
 *  多模态视频训练文件详情 Service接口实现类
 **/
@Service
public class TrainMultiVideoDetailServiceImpl extends BaseSpringService<TrainMultiVideoDetailEntity, Serializable> implements ITrainMultiVideoDetailService {

	@Autowired
	private ITrainMultiVideoDetailDAO trainMultiVideoDetailDAO;

	@Autowired
	public void setTrainMultiVideoDetailDAO(ITrainMultiVideoDetailDAO trainMultiVideoDetailDAO) {
		this.trainMultiVideoDetailDAO = trainMultiVideoDetailDAO;
		super.setBaseDAO(trainMultiVideoDetailDAO);
	}
	
	@Override
	public void createVideoDetail(MultipartFile attachId, String multiTrainVideoId) throws Exception {
		//视频文件格式验证
		String originalFilename = attachId.getOriginalFilename();
		if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
			throw new Exception("文件无扩展名!请上传视频格式(mp4,avi,mov,mkv)");
		}
		String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
		List<String> allowedExtensions = GlobalConsts.VIDEO_EXTENSIONS;
		if (!allowedExtensions.contains(extension)) {
			throw new Exception("不支持此文件格式!请上传视频格式(mp4,avi,mov,mkv)");
		}
		//创建实体对象
		TrainMultiVideoDetailEntity trainMultiVideoDetailEntity = new TrainMultiVideoDetailEntity();
		trainMultiVideoDetailEntity.setMultiTrainVideoId(multiTrainVideoId);
		trainMultiVideoDetailEntity.setUpdateTime(new Date());
		trainMultiVideoDetailEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		trainMultiVideoDetailEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		trainMultiVideoDetailEntity.setFlgDeleted(GlobalConsts.ZERO);

		//调用工具类设置附件字段和父ID
		AttachUtil.createAttach(attachId, trainMultiVideoDetailEntity, "attachFile", "attachId", "multiTrainVideoId", multiTrainVideoId);
		create(trainMultiVideoDetailEntity);
	}
}

