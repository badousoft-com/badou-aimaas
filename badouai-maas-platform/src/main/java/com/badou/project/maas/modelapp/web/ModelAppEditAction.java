package com.badou.project.maas.modelapp.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelapp.service.IModelAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonEditAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.TimeUnit;

/**
 * @author badousoft
 * @created 2024-05-27 11:33:46.513
 * @todo 模型应用管理 编辑实现类
 */
@Controller
public class ModelAppEditAction extends BaseCommonEditAction {

    @Autowired
    private IModelAppService modelAppService;

    @PostMapping
    public JsonReturnBean linkAppTalk(@RequestParam String id){
        try {
            return modelAppService.linkAppTalk(id);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
    }

    @RequestMapping
    public JsonReturnBean restartPod(String id){
        try {
            String s = modelAppService.restartApp(id);
            if(s==null){
                return JsonResultUtil.success("重启成功!");
            }
            return JsonResultUtil.errorMsg(s);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg("重启失败!");
        }

    }

}
