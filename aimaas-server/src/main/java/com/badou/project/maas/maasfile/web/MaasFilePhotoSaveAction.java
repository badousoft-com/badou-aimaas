package com.badou.project.maas.maasfile.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.maasfile.model.MaasFilePhotoEntity;
import com.badou.project.maas.maasfile.service.IMaasFilePhotoService;
import com.badou.project.maas.trainfile.model.TrainMultiFileEntity;
import com.badou.project.maas.trainfile.service.ITrainMultiFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author badousoft
 * @created 2025-03-25 10:18:20.709
 *  图片管理 保存实现类
 */
@Controller
public class MaasFilePhotoSaveAction extends BaseCommonSaveAction {
    @Autowired
    private IMaasFilePhotoService MaasFilePhotoService;

    @PostMapping
    public JsonReturnBean updateStatus(@RequestBody List<MaasFilePhotoEntity> datas){
        try {
            MaasFilePhotoService.updateStatus(datas);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.error();
        }
        return JsonResultUtil.success();
    }
}
