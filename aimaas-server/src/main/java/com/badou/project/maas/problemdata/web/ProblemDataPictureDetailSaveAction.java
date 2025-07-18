package com.badou.project.maas.problemdata.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.problemdata.service.IProblemDataPictureDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author badousoft
 * @created 2025-03-26 10:20:39.371
 *  图片样本集详情 保存实现类
 */
@Controller
public class ProblemDataPictureDetailSaveAction extends BaseCommonSaveAction {
    @Autowired
    private IProblemDataPictureDetailService problemDataPictureDetailService;

    @PostMapping
    public JsonReturnBean saveFile(MultipartFile attachId, String dataPictureId) {
        try {
            problemDataPictureDetailService.createPictureDetail(attachId, dataPictureId);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
        return JsonResultUtil.success();
    }

}
