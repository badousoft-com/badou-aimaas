package com.badou.project.maas.trainfile.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.trainfile.model.TrainMultiFileEntity;
import com.badou.project.maas.trainfile.model.TrainMultiVideoEntity;
import com.badou.project.maas.trainfile.service.ITrainMultiFileService;
import com.badou.project.maas.trainfile.service.ITrainMultiVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author badousoft
 * @created 2025-03-26 15:26:00.791
 *  多模态视频训练文件管理 保存实现类
 */
@Controller
public class TrainMultiVideoSaveAction extends BaseCommonSaveAction {
    @Autowired
    private ITrainMultiVideoService trainMultiFileService;

    @PostMapping
    public JsonReturnBean updateStatus(@RequestBody List<TrainMultiVideoEntity> datas){
        try {
            trainMultiFileService.updateStatus(datas);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.error();
        }
        return JsonResultUtil.success();
    }
}
