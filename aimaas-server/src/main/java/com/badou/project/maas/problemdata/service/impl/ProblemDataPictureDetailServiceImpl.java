package com.badou.project.maas.problemdata.service.impl;

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

import com.badou.project.maas.problemdata.dao.IProblemDataPictureDetailDAO;
import com.badou.project.maas.problemdata.model.ProblemDataPictureDetailEntity;
import com.badou.project.maas.problemdata.service.IProblemDataPictureDetailService;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2025-03-26 10:20:39.371
 *  图片样本集详情 Service接口实现类
 **/
@Service
public class ProblemDataPictureDetailServiceImpl extends BaseSpringService<ProblemDataPictureDetailEntity, Serializable> implements IProblemDataPictureDetailService {

	@Autowired
	private IProblemDataPictureDetailDAO problemDataPictureDetailDAO;

	@Autowired
	public void setProblemDataPictureDetailDAO(IProblemDataPictureDetailDAO problemDataPictureDetailDAO) {
		this.problemDataPictureDetailDAO = problemDataPictureDetailDAO;
		super.setBaseDAO(problemDataPictureDetailDAO);
	}

	@Override
	public void createPictureDetail(MultipartFile attachId, String dataPictureId) throws  Exception {
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
		ProblemDataPictureDetailEntity problemDataPictureDetailEntity = new ProblemDataPictureDetailEntity();
		problemDataPictureDetailEntity.setDataPictureId(dataPictureId);
		problemDataPictureDetailEntity.setUpdateTime(new Date());
		problemDataPictureDetailEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		problemDataPictureDetailEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		problemDataPictureDetailEntity.setFlgDeleted(GlobalConsts.ZERO);

		//调用工具类设置附件字段和父ID
		AttachUtil.createAttach(attachId, problemDataPictureDetailEntity, "attachFile", "attachId","dataPictureId",dataPictureId);
		create(problemDataPictureDetailEntity);
	}
}

