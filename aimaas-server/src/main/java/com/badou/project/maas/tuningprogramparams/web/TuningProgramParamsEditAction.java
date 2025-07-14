package com.badou.project.maas.tuningprogramparams.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.k8sport.service.IK8sPortService;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.tuningplanparams.model.TuningPlanParamsEntity;
import com.badou.project.maas.tuningplanparams.service.ITuningPlanParamsService;
import com.badou.project.maas.tuningprogramparams.model.TuningProgramParamsEntity;
import com.badou.project.maas.tuningprogramparams.service.ITuningProgramParamsService;
import com.badou.project.mq.TaskMqSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonEditAction;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author badousoft
 * @created 2024-05-20 16:35:31.725
 * @todo 微调计划参数 编辑实现类
 */
@Controller
public class TuningProgramParamsEditAction extends BaseCommonEditAction {

    @Autowired
    private ITuningProgramParamsService tuningProgramParamsService;
    @Autowired
    private TaskMqSender taskMqSender;
    @Autowired
    private IK8sPortService ik8sPortService;

    @PostMapping
    public JsonReturnBean updateValue(String id, String newValue){
        TuningProgramParamsEntity tuningProgramParamsEntity = tuningProgramParamsService.find(id);
        if(tuningProgramParamsEntity!=null){
            tuningProgramParamsEntity.setValue(newValue);
            tuningProgramParamsService.update(tuningProgramParamsEntity);
        }
        return JsonResultUtil.success();
    }

    @Autowired
    private IModelAppService modelAppService;

    @PostMapping
    public JsonReturnBean testCreateModel() throws Exception {
        ModelAppEntity modelAppEntity = modelAppService.find("d9a02144b8c7487aa4954a740163369b8cceaeee489a46ba82629fea13c4dedf");
        Integer max = ik8sPortService.getMax();
        System.out.println(max);
//        taskMqSenderYou are using the Lightweight API Client, sign in or create an account to work with collections, environments and
        taskMqSender.sendModelAppMessage(JSONObject.toJSONString(modelAppEntity));
        return JsonResultUtil.success();
    }

}
