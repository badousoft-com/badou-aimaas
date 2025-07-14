package com.badou.project.maas.trainfile.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
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
 * @created 2024-11-19 11:40:09.389
 *  多模态训练文件管理 保存实现类
 */
@Controller
public class TrainMultiFileSaveAction extends BaseCommonSaveAction {

    @Autowired
    private ITrainMultiFileService trainMultiFileService;

    @PostMapping
    public JsonReturnBean updateStatus(@RequestBody List<TrainMultiFileEntity> datas){
        try {
            trainMultiFileService.updateStatus(datas);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.error();
        }
        return JsonResultUtil.success();
    }

}
