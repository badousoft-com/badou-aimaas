package com.badou.project.maas.problemdata.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.problemdata.service.IProblemDataVideoDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author badousoft
 * @created 2025-03-26 11:30:40.876
 *  视频样本集详情 保存实现类
 */
@Controller
public class ProblemDataVideoDetailSaveAction extends BaseCommonSaveAction {
    @Autowired
    private IProblemDataVideoDetailService problemDataVideoDetailService;

    @PostMapping
    public JsonReturnBean saveFile(MultipartFile attachId, String dataVideoId) {
        try {
            problemDataVideoDetailService.createVideoDetail(attachId, dataVideoId);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
        return JsonResultUtil.success();
    }

}
