package com.badou.project.maas.traindataproblem.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.traindataproblem.service.ITrainDataProblemService;
import com.badou.project.maas.traindataproblem.web.form.TrainDataProblemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author badousoft
 * @created 2024-08-30 10:41:35.751
 *  训练集样本集 保存实现类
 */
@Controller
public class TrainDataProblemSaveAction extends BaseCommonSaveAction {

    @Autowired
    private ITrainDataProblemService trainDataProblemService;

    @PostMapping
    public JsonReturnBean linkOneProblem(@RequestBody TrainDataProblemVo vo){
        trainDataProblemService.linkOneProblem(vo);
        return JsonResultUtil.success();
    }

    @PostMapping
    public JsonReturnBean deleteLink(String trainFileId,String id,String problem){
        try {
            trainDataProblemService.deleteLink(trainFileId,id,problem);
        } catch (DataEmptyException e) {
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
        return JsonResultUtil.success();
    }

}
