package com.badou.project.maas.problemdata.web;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.designer.jdbc.common.annotations.BaseMdJsonStack;
import com.badou.designer.jdbc.common.web.BaseCommonImportAction;
import com.badou.designer.jdbc.core.JDBCConsts;
import com.badou.designer.jdbc.core.service.IBaseCommonService;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.maasfile.model.MaasFileEntity;
import com.badou.project.maas.maasfile.service.IMaasFileService;
import com.badou.project.maas.problemdata.service.IProblemDataImportService;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.tools.annotations.BaseJsonFileStack;
import com.badou.tools.common.util.StringUtils;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Controller
public class ProblemDataImportAction extends BaseCommonImportAction {

    @Autowired
    private IProblemDataImportService problemDataImportService;

    @Autowired
    public void setBaseCommonService(IBaseCommonService baseCommonService) {
        this.baseCommonService = baseCommonService;
        super.setBaseService(baseCommonService);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/baseImportForResult")
    @BaseJsonFileStack
    @BaseMdJsonStack
    public JsonReturnBean baseImportForResult() throws Exception {
        String attachFileName = this.getAttachFileName();
        if (StringUtils.isEmpty(attachFileName)) {
            return JsonResultUtil.errorMsg("导入无效!");
        }
        File attach = this.getAttach();
        //额外拦截 处理偏好数据集
        try {
            if (attachFileName.contains("json")) {
                if (attach != null) {
                    //获取文件内容
                    String result = IOUtils.toString(new FileInputStream(attach), StandardCharsets.UTF_8.toString());
                    return problemDataImportService.importTrainFileData(result);
                }
            } else {
                FileInputStream is = new FileInputStream(attach);
                Workbook wb = Workbook.getWorkbook(is);
                return problemDataImportService.importTrainFileDataExcel(wb);
            }
            return JsonResultUtil.errorMsg("非法的文件!");
        } catch (Exception e) {
            return JsonResultUtil.errorMsg(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/baseImportForProblemResult")
    @BaseJsonFileStack
    @BaseMdJsonStack
    public JsonReturnBean baseImportForProblemResult() throws Exception {
        String attachFileName = this.getAttachFileName();
        if (StringUtils.isEmpty(attachFileName)) {
            return JsonResultUtil.errorMsg("导入无效!");
        }
        File attach = this.getAttach();
        //额外拦截 处理偏好数据集
        try {
            if (attachFileName.contains("json")) {
                if (attach != null) {
                    //获取文件内容
                    String result = IOUtils.toString(new FileInputStream(attach), StandardCharsets.UTF_8.toString());
                    return problemDataImportService.importTrainProblemFileData(result);
                }
            } else {
                FileInputStream is = new FileInputStream(attach);
                Workbook wb = Workbook.getWorkbook(is);
                return problemDataImportService.importTrainFileProDataExcel(wb);
            }
            return JsonResultUtil.errorMsg("非法的文件!");
        } catch (Exception e) {
            return JsonResultUtil.errorMsg(e.getMessage());
        }
    }

}


