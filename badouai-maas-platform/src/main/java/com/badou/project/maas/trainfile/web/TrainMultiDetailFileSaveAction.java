package com.badou.project.maas.trainfile.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.trainfile.service.ITrainMultiDetailFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author badousoft
 * @created 2024-11-19 11:40:36.43
 *  多模态训练文件详情 保存实现类
 */
@Controller
public class TrainMultiDetailFileSaveAction extends BaseCommonSaveAction {
    @Autowired
    private ITrainMultiDetailFileService trainmultidetailfileservice;

    @PostMapping
    public JsonReturnBean saveFile(MultipartFile attachId, String mutiTrainId) {
        try {
            trainmultidetailfileservice.createPhotoDetail(attachId, mutiTrainId);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
        return JsonResultUtil.success();
    }

}
