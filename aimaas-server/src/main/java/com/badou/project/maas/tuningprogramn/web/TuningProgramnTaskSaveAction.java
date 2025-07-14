package com.badou.project.maas.tuningprogramn.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.problemdata.service.IProblemDataService;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;
import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailService;
import com.badou.project.maas.tuningprogramn.service.ITuningProgramnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author badousoft
 * @created 2024-04-30 16:22:32.674
 * @todo 微调计划管理 保存实现类
 */
@Controller
public class TuningProgramnTaskSaveAction extends BaseCommonSaveAction {

    @Autowired
    private ITuningProgramnService tuningProgramnService;

    @Override
    protected void exeBeforeSave() {

    }

    @Override
    protected void exeAfterSave() throws Exception {
        tuningProgramnService.startPlan(this.custForm.getDetails(),this.custForm.getId());
    }

    @Autowired
    private IProblemDataService problemDataService;
    @Autowired
    private IProblemDataDetailService problemDataDetailService;

    /**
     * 获取默认的微调参数
     */
    @GetMapping
    public JsonReturnBean createData() throws IOException {
        //获取文件 生成数据
        String  fileName = "E:\\develop\\project\\八斗MaaS平台\\数据集\\千问数据集.jsonl";

        // 读取文件内容到Stream流中，按行读取
        Stream<String> lines = Files.lines(Paths.get(fileName));
        ProblemDataEntity problemDataEntity = new ProblemDataEntity();
        //设置成单轮对话
        problemDataEntity.setType(0);
        //设置行业为法律
        problemDataEntity.setApplicableIndustry("7");
        problemDataEntity.setApplicableSubject("0");
        problemDataEntity.setFlgDeleted(0);
        problemDataService.createEntity(problemDataEntity);
        lines.forEach(ele -> {
            JSONObject jsonObject = JSONObject.parseObject(ele);
            String output = jsonObject.getString("output");
            String input = jsonObject.getString("input");
            ProblemDataDetailEntity problemDataDetailEntity = new ProblemDataDetailEntity();
            problemDataDetailEntity.setQuestion(input);
            problemDataDetailEntity.setFeedback(output);
            problemDataDetailEntity.setProblemDataId(problemDataEntity.getId());
            problemDataDetailEntity.setFlgDeleted(0);
            //创建具体的对话数据
            problemDataDetailService.createEntity(problemDataDetailEntity);
            System.out.println(ele);
        });
        return JsonResultUtil.success();
    }


}
