package com.badou.project.maas.tuningprogramn.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.tuningplanparams.model.TuningPlanParamsEntity;
import com.badou.project.maas.tuningplanparams.service.ITuningPlanParamsService;
import com.badou.project.maas.tuningprogramparams.model.TuningProgramParamsEntity;
import com.badou.project.maas.tuningprogramparams.service.ITuningProgramParamsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author badousoft
 * @created 2024-04-30 16:22:32.674
 * @todo 微调计划管理 保存实现类
 */
@Controller
public class TuningProgramnSaveAction extends BaseCommonSaveAction {

    @Autowired
    private ITuningPlanParamsService tuningPlanParamsService;
    @Autowired
    private ITuningProgramParamsService tuningProgramParamsService;

    //启动微调的模型
    @PostMapping
    public JsonReturnBean startModelByTrain(){
        return JsonResultUtil.success();
    }


    @Override
    protected void exeBeforeSave() throws Exception {

    }

    @Override
    protected void exeAfterSave() throws Exception {
        //保存后获取方案的微调参数给到微调计划
//        Map<String, String[]> details = this.custForm.getDetails();
//        String tuningPlanId = details.get("tuningPlanId")[0];
//        QueryCriterion queryCriterion = new QueryCriterion();
//        queryCriterion.addParam(new QueryHibernatePlaceholderParam("planId", tuningPlanId, null, QueryOperSymbolEnum.eq));
//        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
//        List<TuningPlanParamsEntity> tuningPlanParamsEntities = tuningPlanParamsService.find(queryCriterion);
//        if(JsonResultUtil.arrayEmpty(tuningPlanParamsEntities)){
//            throw new Exception("微调方案未配置微调参数!");
//        }
//        //检查当前微调计划是否已生成微调参数 如果是 则跳过
//        queryCriterion = new QueryCriterion();
//        queryCriterion.addParam(new QueryHibernatePlaceholderParam("programId", this.custForm.getId(), null, QueryOperSymbolEnum.eq));
//        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted", 0, null, QueryOperSymbolEnum.eq));
//        List<TuningProgramParamsEntity> exist = tuningProgramParamsService.find(queryCriterion);
//        if(JsonResultUtil.arrayNotEmpty(exist)){
//            return;
//        }
//        List<TuningProgramParamsEntity> tuningProgramParamsEntities = new ArrayList<>();
//        tuningPlanParamsEntities.forEach(e->{
//            e.setProgramId(this.custForm.getId());
//            TuningProgramParamsEntity tuningProgramParamsEntity = new TuningProgramParamsEntity();
//            BeanUtils.copyProperties(e,tuningProgramParamsEntity);
//            tuningProgramParamsEntities.add(tuningProgramParamsEntity);
//        });
//        tuningPlanParamsService.batchUpdate(tuningPlanParamsEntities);
//        tuningProgramParamsService.batchCreate(tuningProgramParamsEntities);
    }


}
