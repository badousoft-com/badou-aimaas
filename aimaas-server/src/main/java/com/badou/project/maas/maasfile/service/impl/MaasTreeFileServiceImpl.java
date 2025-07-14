package com.badou.project.maas.maasfile.service.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.badou.brms.attach.AttachFactory;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.attach.service.IAttachService;
import com.badou.brms.util.InstanceFactory;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.GlobalConsts;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.maasfile.dao.IMaasTreeFileDAO;
import com.badou.project.maas.maasfile.model.MaasTreeFileEntity;
import com.badou.project.maas.maasfile.service.IMaasTreeFileService;
import com.badou.project.maas.trainfile.event.CalcFileSizeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2025-02-05 14:13:28.676
 * 系统文件夹 Service接口实现类
 **/
@Service
public class MaasTreeFileServiceImpl extends BaseSpringService<MaasTreeFileEntity, Serializable> implements IMaasTreeFileService {

    @Autowired
    private IMaasTreeFileDAO maasTreeFileDAO;

    @Autowired
    public void setMaasTreeFileDAO(IMaasTreeFileDAO maasTreeFileDAO) {
        this.maasTreeFileDAO = maasTreeFileDAO;
        super.setBaseDAO(maasTreeFileDAO);
    }

    @Override
    public void batchCreateEntity(List<MultipartFile> name, String parentId, String parentName) throws Exception {
        for (MultipartFile multipartFile : name) {
            MaasTreeFileEntity maasTreeFileEntity = new MaasTreeFileEntity();
            maasTreeFileEntity.setUpdateTime(new Date());
            maasTreeFileEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            maasTreeFileEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            maasTreeFileEntity.setFlgDeleted(GlobalConsts.ZERO);
//			maasTreeFileEntity.setTreeLevel("1");
            maasTreeFileEntity.setParentId(parentId);
            maasTreeFileEntity.setAnalyzeFlg(GlobalConsts.ZERO);
            maasTreeFileEntity.setFullname(parentName);

            //创建Attach
            Attach attach = new Attach();
            attach.setName(multipartFile.getOriginalFilename());
            AttachFactory attachFactory = InstanceFactory.getInstance(AttachFactory.class);
            LogonCertificate logon = LogonCertificateHolder.getLogonCertificate();
            attach.setExtendName(multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf(".")));
            attach.setGenPersonId(logon.getUserId());
            attach.setGenPersonName(logon.getUserName());
            try {
                attach.setFileContent(multipartFile.getBytes());
                attach.setFileSize(multipartFile.getSize());
                attachFactory.uploadFile(attach);
            } catch (Exception var10) {
                throw new Exception(var10);
            }
            maasTreeFileEntity.setFileSize(StringHandlerUtil.getNetFileSizeToMB(multipartFile.getSize()));
            maasTreeFileEntity.setName(multipartFile.getOriginalFilename());
            maasTreeFileEntity.setAttachId(attach.getId());
            create(maasTreeFileEntity);
        }
    }

    @Override
    public void changeAllRun(List<MaasTreeFileEntity> maasTreeFileEntities) throws RuntimeException {
        for (MaasTreeFileEntity e : maasTreeFileEntities) {
            if ("0".equals(e.getType()) || "0".equals(e.getTreeLevel())){
                throw new RuntimeException("仅允许选择文件."+e.getName()+"是文件夹");
            }
            if (GlobalConsts.ONE.equals(e.getAnalyzeFlg())){
                throw new RuntimeException(e.getName()+"正在执行.请先等待该任务");
            }
            e.setAnalyzeFlg(MaasConst.DOPLAN_RUN_STATUS);
        }
        batchUpdate(maasTreeFileEntities);
    }
}

