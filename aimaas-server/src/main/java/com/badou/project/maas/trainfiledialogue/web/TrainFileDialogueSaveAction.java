package com.badou.project.maas.trainfiledialogue.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.trainfile.service.ITrainFileService;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author badousoft
 * @created 2024-05-16 14:39:38.124
 * @todo 训练集文件对话 保存实现类
 */
@Controller
public class TrainFileDialogueSaveAction extends BaseCommonSaveAction {

    @Autowired
    private ITrainFileDialogueService trainFileDialogueService;

    @PostMapping
    public JsonReturnBean batchUpdateTag(@RequestBody JSONObject jsonObject){
        if (jsonObject.size()!=2){
            return JsonResultUtil.errorData();
        }
        ArrayList<String> ids = (ArrayList<String>)jsonObject.get("ids");
        String newSystemRole = jsonObject.getString("newSystemRole");
        if (ids!=null && ids.size()>0 && StringUtils.isNotBlank(newSystemRole)){
            trainFileDialogueService.bacthUpdateSystemRole(ids,newSystemRole);
            return JsonResultUtil.success();
        }
        return JsonResultUtil.errorData();
    }

}
