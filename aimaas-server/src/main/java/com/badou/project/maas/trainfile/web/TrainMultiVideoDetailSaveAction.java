package com.badou.project.maas.trainfile.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.trainfile.service.ITrainMultiVideoDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author badousoft
 * @created 2025-03-26 15:26:46.762
 *  多模态视频训练文件详情 保存实现类
 */
@Controller
public class TrainMultiVideoDetailSaveAction extends BaseCommonSaveAction {
    @Autowired
    private ITrainMultiVideoDetailService trainMultiVideoDetailService;

    @PostMapping
    public JsonReturnBean saveFile(MultipartFile attachId, String multiTrainVideoId) {
        try {
            trainMultiVideoDetailService.createVideoDetail(attachId, multiTrainVideoId);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
        return JsonResultUtil.success();
    }
}
