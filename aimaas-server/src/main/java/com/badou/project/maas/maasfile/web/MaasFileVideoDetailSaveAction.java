package com.badou.project.maas.maasfile.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.maasfile.service.IMaasFileVideoDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author badousoft
 * @created 2025-03-25 17:04:48.273
 *  视频管理详情 保存实现类
 */
@Controller
public class MaasFileVideoDetailSaveAction extends BaseCommonSaveAction {
    @Autowired
    private IMaasFileVideoDetailService maasFileVideoDetailService;

    @PostMapping
    public JsonReturnBean saveFile(MultipartFile attachId, String fileVideoId) {
        try {
            maasFileVideoDetailService.createVideoDetail(attachId, fileVideoId);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
        return JsonResultUtil.success();
    }

}
