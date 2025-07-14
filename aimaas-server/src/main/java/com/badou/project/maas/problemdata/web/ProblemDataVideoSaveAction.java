package com.badou.project.maas.problemdata.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.problemdata.model.ProblemDataPictureEntity;
import com.badou.project.maas.problemdata.model.ProblemDataVideoEntity;
import com.badou.project.maas.problemdata.service.IProblemDataVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author badousoft
 * @created 2025-03-26 11:30:02.141
 *  视频样本集 保存实现类
 */
@Controller
public class ProblemDataVideoSaveAction extends BaseCommonSaveAction {
    @Autowired
    private IProblemDataVideoService ProblemDataVideoService;

    @PostMapping
    public JsonReturnBean updateStatus(@RequestBody List<ProblemDataVideoEntity> datas){
        try {
            ProblemDataVideoService.updateStatus(datas);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.error();
        }
        return JsonResultUtil.success();
    }
}
