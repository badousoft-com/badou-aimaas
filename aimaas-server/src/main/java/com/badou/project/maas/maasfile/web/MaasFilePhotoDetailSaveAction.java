package com.badou.project.maas.maasfile.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.maasfile.model.MaasFilePhotoDetailEntity;
import com.badou.project.maas.maasfile.model.MaasFilePhotoEntity;
import com.badou.project.maas.maasfile.service.IMaasFilePhotoDetailService;
import com.badou.project.maas.maasfile.service.IMaasFilePhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author badousoft
 * @created 2025-03-25 14:50:33.074
 *  图片管理详情 保存实现类
 */
@Controller
public class MaasFilePhotoDetailSaveAction extends BaseCommonSaveAction {
    @Autowired
    private IMaasFilePhotoDetailService maasFilePhotoDetailService;

    @PostMapping
    public JsonReturnBean saveFile(MultipartFile attachId,String filePhotoId) {
        try {
            maasFilePhotoDetailService.createPhotoDetail(attachId, filePhotoId);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
        return JsonResultUtil.success();
    }

}
