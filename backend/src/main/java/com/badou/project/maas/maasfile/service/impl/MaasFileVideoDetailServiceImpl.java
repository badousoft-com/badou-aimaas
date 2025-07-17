package com.badou.project.maas.maasfile.service.impl;

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
import com.badou.project.maas.maasfile.model.MaasFilePhotoDetailEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.maasfile.dao.IMaasFileVideoDetailDAO;
import com.badou.project.maas.maasfile.model.MaasFileVideoDetailEntity;
import com.badou.project.maas.maasfile.service.IMaasFileVideoDetailService;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2025-03-25 17:04:48.273
 *  视频管理详情 Service接口实现类
 **/
@Service
public class MaasFileVideoDetailServiceImpl extends BaseSpringService<MaasFileVideoDetailEntity, Serializable> implements IMaasFileVideoDetailService {

	@Autowired
	private IMaasFileVideoDetailDAO maasFileVideoDetailDAO;

	@Autowired
	public void setMaasFileVideoDetailDAO(IMaasFileVideoDetailDAO maasFileVideoDetailDAO) {
		this.maasFileVideoDetailDAO = maasFileVideoDetailDAO;
		super.setBaseDAO(maasFileVideoDetailDAO);
	}
	@Override
	public void createVideoDetail(MultipartFile attachId, String fileVideoId) throws Exception {
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
		MaasFileVideoDetailEntity maasFileVideoDetailEntity = new MaasFileVideoDetailEntity();
		maasFileVideoDetailEntity.setFileVideoId(fileVideoId);
		maasFileVideoDetailEntity.setUpdateTime(new Date());
		maasFileVideoDetailEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
		maasFileVideoDetailEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
		maasFileVideoDetailEntity.setFlgDeleted(GlobalConsts.ZERO);

		//调用工具类设置附件字段和父ID
		AttachUtil.createAttach(attachId,maasFileVideoDetailEntity,"attachFile","attachId","fileVideoId",fileVideoId);
		create(maasFileVideoDetailEntity);
	}
}

