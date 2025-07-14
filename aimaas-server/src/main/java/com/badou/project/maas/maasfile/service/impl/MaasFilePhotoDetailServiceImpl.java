package com.badou.project.maas.maasfile.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.GlobalConsts;
import com.badou.project.maas.common.util.AttachUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;
import com.badou.project.maas.maasfile.dao.IMaasFilePhotoDetailDAO;
import com.badou.project.maas.maasfile.model.MaasFilePhotoDetailEntity;
import com.badou.project.maas.maasfile.service.IMaasFilePhotoDetailService;
import org.springframework.web.multipart.MultipartFile;



/**
 * @author badousoft
 * @date 2025-03-25 14:50:33.074
 *  图片管理详情 Service接口实现类
 **/
@Service
public class MaasFilePhotoDetailServiceImpl extends BaseSpringService<MaasFilePhotoDetailEntity, Serializable> implements IMaasFilePhotoDetailService {

	@Autowired
	private IMaasFilePhotoDetailDAO maasFilePhotoDetailDAO;

	@Autowired
	public void setMaasFilePhotoDetailDAO(IMaasFilePhotoDetailDAO maasFilePhotoDetailDAO) {
		this.maasFilePhotoDetailDAO = maasFilePhotoDetailDAO;
		super.setBaseDAO(maasFilePhotoDetailDAO);
	}

	@Override
	public void createPhotoDetail(MultipartFile attachId, String filePhotoId) throws Exception {
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
		MaasFilePhotoDetailEntity maasFilePhotoDetailEntity = new MaasFilePhotoDetailEntity();
		maasFilePhotoDetailEntity.setFilePhotoId(filePhotoId);
		maasFilePhotoDetailEntity.setUpdateTime(new Date());
		maasFilePhotoDetailEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		maasFilePhotoDetailEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		maasFilePhotoDetailEntity.setFlgDeleted(GlobalConsts.ZERO);

		//调用工具类设置附件字段和父ID
		AttachUtil.createAttach(attachId,maasFilePhotoDetailEntity,"attachFile","attachId","filePhotoId",filePhotoId);
		create(maasFilePhotoDetailEntity);
	}
}

