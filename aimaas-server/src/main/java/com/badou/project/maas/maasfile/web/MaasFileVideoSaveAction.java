package com.badou.project.maas.maasfile.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.maasfile.model.MaasFilePhotoEntity;
import com.badou.project.maas.maasfile.model.MaasFileVideoEntity;
import com.badou.project.maas.maasfile.service.IMaasFileVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author badousoft
 * @created 2025-03-25 16:50:44.633
 *  视频管理 保存实现类
 */
@Controller
public class MaasFileVideoSaveAction extends BaseCommonSaveAction {
    @Autowired
    private IMaasFileVideoService MaasFileVideoService;

    @PostMapping
    public JsonReturnBean updateStatus(@RequestBody List<MaasFileVideoEntity> datas){
        try {
            MaasFileVideoService.updateStatus(datas);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.error();
        }
        return JsonResultUtil.success();
    }
}
