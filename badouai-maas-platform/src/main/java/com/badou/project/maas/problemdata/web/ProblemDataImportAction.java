//package com.badou.project.maas.problemdata.web;
//
//import cn.hutool.core.io.FileUtil;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.badou.brms.base.support.struts.JsonReturnBean;
//import com.badou.designer.jdbc.common.annotations.BaseMdJsonStack;
//import com.badou.designer.jdbc.common.web.BaseCommonImportAction;
//import com.badou.designer.jdbc.core.JDBCConsts;
//import com.badou.designer.jdbc.core.service.IBaseCommonService;
//import com.badou.project.common.webparams.util.DateUtil;
//import com.badou.project.common.webparams.util.JsonResultUtil;
//import com.badou.project.maas.maasfile.model.MaasFileEntity;
//import com.badou.project.maas.maasfile.service.IMaasFileService;
//import com.badou.project.maas.problemdata.service.IProblemDataImportService;
//import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
//import com.badou.tools.annotations.BaseJsonFileStack;
//import com.badou.tools.common.util.StringUtils;
//import jxl.Cell;
//import jxl.Sheet;
//import jxl.Workbook;
//import jxl.biff.EmptyCell;
//import org.apache.commons.io.IOUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//
//@Controller
//public class ProblemDataImportAction extends BaseCommonImportAction {
//
//    @Autowired
//    private IProblemDataImportService problemDataImportService;
//
//    @Autowired
//    public void setBaseCommonService(IBaseCommonService baseCommonService) {
//        this.baseCommonService = baseCommonService;
//        super.setBaseService(baseCommonService);
//    }
//
//    @SuppressWarnings("unchecked")
//    @RequestMapping("/baseImportForResult")
//    @BaseJsonFileStack
//    @BaseMdJsonStack
//    @Override
//    public JsonReturnBean baseImportForResult() throws Exception {
//        logger.info("接收到请求"+ DateUtil.getDateStrMin(new Date()));
//        String attachFileName = this.getAttachFileName();
//        logger.info("开始判断"+ DateUtil.getDateStrMin(new Date()));
//        if (StringUtils.isEmpty(attachFileName)) {
//            return JsonResultUtil.errorMsg("导入无效!");
//        }
//
//        //额外拦截 处理偏好数据集
//        try {
//            if (attachFileName.contains("json")) {
//                logger.info("判断结束"+ DateUtil.getDateStrMin(new Date()));
//                if (attach != null) {
//                    //获取文件内容
//                    String result = IOUtils.toString(new FileInputStream(attach), StandardCharsets.UTF_8.toString());
//                    return problemDataImportService.importTrainFileData(result);
//                }
//            } else {
//                FileInputStream is = new FileInputStream(attach);
//                Workbook wb = Workbook.getWorkbook(is);
//                return problemDataImportService.importTrainFileDataExcel(wb);
//            }
//            return JsonResultUtil.errorMsg("非法的文件!");
//        } catch (Exception e) {
//            return JsonResultUtil.errorMsg(e.getMessage());
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @RequestMapping("/baseImportForProblemResult")
//    @BaseJsonFileStack
//    @BaseMdJsonStack
//    public JsonReturnBean baseImportForProblemResult() throws Exception {
//        logger.info("收到附件"+DateUtil.getDateStrMin(new Date()));
//        String attachFileName = this.getAttachFileName();
//        if (StringUtils.isEmpty(attachFileName)) {
//            return JsonResultUtil.errorMsg("导入无效!");
//        }
//
//        //额外拦截 处理偏好数据集
//        try {
//            if (attachFileName.contains("json")) {
//                if (attach != null) {
//                    //获取文件内容
//                    String result = IOUtils.toString(new FileInputStream(attach), StandardCharsets.UTF_8.toString());
//                    return problemDataImportService.importTrainProblemFileData(result);
//                }
//            } else {
//                logger.info("开始读取文件"+DateUtil.getDateStrMin(new Date()));
//                FileInputStream is = new FileInputStream(attach);
//                logger.info("读取文件完成"+DateUtil.getDateStrMin(new Date()));
//
//                logger.info("开始加载工作表"+DateUtil.getDateStrMin(new Date()));
//                Workbook wb = Workbook.getWorkbook(is);
//                logger.info("工作表文件完成"+DateUtil.getDateStrMin(new Date()));
//                return problemDataImportService.importTrainFileProDataExcel(wb);
//            }
//            return JsonResultUtil.errorMsg("非法的文件!");
//        } catch (Exception e) {
//            return JsonResultUtil.errorMsg(e.getMessage());
//        }
//    }
//
//}
//
//
