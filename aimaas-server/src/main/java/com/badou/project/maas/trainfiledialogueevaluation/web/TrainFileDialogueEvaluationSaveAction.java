package com.badou.project.maas.trainfiledialogueevaluation.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.maas.trainfiledialogueevaluation.service.ITrainFileDialogueEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author badousoft
 * @created 2025-02-18 19:04:55.108
 *  评价管理与训练集文件对话数据的关联 保存实现类
 */
@Controller
public class TrainFileDialogueEvaluationSaveAction extends BaseCommonSaveAction {

    @Autowired
    private ITrainFileDialogueEvaluationService trainFileDialogueEvaluationService;

    /**
     * 微调评价与训练集对话进行关联
     * @param evaluationId 微调评价ID
     * @param trainFileId 训练集文件ID
     * @return
     */
    @PostMapping
    public JsonReturnBean correlation (String evaluationId, String trainFileId){
        JsonReturnBean returnBean = new JsonReturnBean();
        try {
            trainFileDialogueEvaluationService.correlation(evaluationId, trainFileId);
            returnBean.setHasOk(true);
            returnBean.setMessage("关联成功!");
        }catch (Exception e){
            returnBean.setHasOk(false);
            returnBean.setMessage("关联失败!" + e.getMessage());
            e.printStackTrace();
        }
        return returnBean;
    }
}
