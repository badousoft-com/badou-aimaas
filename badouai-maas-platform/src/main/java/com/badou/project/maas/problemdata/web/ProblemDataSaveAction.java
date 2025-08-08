package com.badou.project.maas.problemdata.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.designer.module.design.ModuleCacheContainer;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.problemdata.service.IProblemDataService;
import com.badou.project.maas.problemdata.web.form.ExportTrainDataForm;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;
import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailService;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author badousoft
 * @created 2024-05-15 17:37:11.964
 * @todo 样本数据集管理 保存实现类
 */
@Controller
public class ProblemDataSaveAction extends BaseCommonSaveAction {

    @Autowired
    private IProblemDataService problemDataService;
    @Autowired
    private IProblemDataDetailService problemDataDetailService;

    /**
     * 复制公开的训练集给自己
     *
     * @param id 复制给自己的文件
     * @return 执行结果
     */
    @PostMapping
    public JsonReturnBean fork(String id) {
        ProblemDataEntity problemDataEntity = problemDataService.find(id);
        if (problemDataEntity.getSamplePermission() == 0) {
            return JsonResultUtil.errorMsg("仅限公共样本数据可以复制!");
        }
        ProblemDataEntity newObject = new ProblemDataEntity();
        BeanUtils.copyProperties(problemDataEntity, newObject);

        newObject.setCreateTime(new Date());
        newObject.setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
        newObject.setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
        newObject.setUpdateTime(new Date());
        newObject.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
        newObject.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
        newObject.setId(null);
        //复制出来的样本默认是私有的
        newObject.setSamplePermission(0);
        problemDataService.createEntity(newObject);
        //复制数据
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null, QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("problemDataId",id,null, QueryOperSymbolEnum.eq));
        List<ProblemDataDetailEntity> problemDataDetailEntities = problemDataDetailService.find(queryCriterion);
        for(ProblemDataDetailEntity problemDataDetailEntity:problemDataDetailEntities){
            ProblemDataDetailEntity newProObject = new ProblemDataDetailEntity();
            BeanUtils.copyProperties(problemDataDetailEntity, newProObject);
            newProObject.setCreateTime(new Date());
            newProObject.setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
            newProObject.setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            newProObject.setUpdateTime(new Date());
            newProObject.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            newProObject.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            newProObject.setId(null);
            newProObject.setProblemDataId(newObject.getId());
            problemDataDetailService.createEntity(newProObject);
        }
        return JsonResultUtil.success();
    }

    @PostMapping
    public JsonReturnBean coverSft(String id){
        try {
            String result = problemDataService.coverSft(id);
            if (StringUtils.isNotBlank(result)){
                return JsonResultUtil.errorMsg(result);
            }
            return JsonResultUtil.success();
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
    }

    @PostMapping
    public JsonReturnBean exportTrainFile(@RequestBody ExportTrainDataForm value) {
        if (value.getIds().length == 0) {
            return JsonResultUtil.errorMsg("请至少选择一个样本数据!");
        }
        if (value.getType() == 0 && value.getValueList() == null) {
            return JsonResultUtil.errorMsg("请至少选择一个旧样本数据!");
        }
        String result = problemDataService.exportTrainFile(value.getFileName(),value.getIds(),value.getType(),value.getValueList());
        if (StringUtils.isNotEmpty(result)) {
            return JsonResultUtil.errorMsg(result);
        }
        return JsonResultUtil.success();
    }

    @Override
    protected void exeAfterSave() throws Exception {
        logger.info("更新当前样本的样本数量");
        //保存完 更新次数
        problemDataService.updateNewestCount(this.custForm.getId());
        logger.info("更新完成样本的样本数量");
    }

}
