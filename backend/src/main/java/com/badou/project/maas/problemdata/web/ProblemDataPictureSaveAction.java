package com.badou.project.maas.problemdata.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.maasfile.model.MaasFileVideoEntity;
import com.badou.project.maas.maasfile.service.IMaasFileVideoService;
import com.badou.project.maas.problemdata.model.ProblemDataPictureEntity;
import com.badou.project.maas.problemdata.service.IProblemDataPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author badousoft
 * @created 2025-03-26 10:13:00.13
 *  图片样本集 保存实现类
 */
@Controller
public class ProblemDataPictureSaveAction extends BaseCommonSaveAction {
    @Autowired
    private IProblemDataPictureService ProblemDataPictureService;

    @PostMapping
    public JsonReturnBean updateStatus(@RequestBody List<ProblemDataPictureEntity> datas){
        try {
            ProblemDataPictureService.updateStatus(datas);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.error();
        }
        return JsonResultUtil.success();
    }
}
