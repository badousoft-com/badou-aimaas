//package com.badou.project.maas.problemdata.service.impl;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.badou.brms.base.support.struts.JsonReturnBean;
//import com.badou.core.runtime.thread.local.LogonCertificateHolder;
//import com.badou.designer.jdbc.core.JDBCConsts;
//import com.badou.designer.jdbc.core.service.impl.BaseCommonService;
//import com.badou.designer.module.design.model.MdModuleEntity;
//import com.badou.project.common.webparams.util.JsonResultUtil;
//import com.badou.project.maas.maasfile.model.MaasFileEntity;
//import com.badou.project.maas.maasfile.service.IMaasFileService;
//import com.badou.project.maas.problemdata.model.ProblemDataEntity;
//import com.badou.project.maas.problemdata.service.IProblemDataImportService;
//import com.badou.project.maas.problemdata.service.IProblemDataService;
//import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;
//import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailService;
//import com.badou.project.maas.trainfile.event.CalcFileSizeEvent;
//import com.badou.project.maas.trainfile.model.TrainFileEntity;
//import com.badou.project.maas.trainfile.service.ITrainFileService;
//import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
//import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
//import com.badou.tools.common.util.StringUtils;
//import jxl.Cell;
//import jxl.Sheet;
//import jxl.Workbook;
//import jxl.biff.EmptyCell;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class ProblemDataImportServiceImpl extends BaseCommonService implements IProblemDataImportService {
//
//    @Autowired
//    private ITrainFileService trainFileService;
//    @Autowired
//    private ITrainFileDialogueService trainFileDialogueService;
//    @Autowired
//    private CalcFileSizeEvent calcFileSizeEvent;
//    @Autowired
//    private IProblemDataService problemDataService;
//    @Autowired
//    private IProblemDataDetailService problemDataDetailService;
//
//    @Override
//    public String importBaseVOWithGroupName(InputStream is, List<String> ids, MdModuleEntity moduleBean, Map<String, String> defaultFieldValue, Object... otherDatas) throws Exception {
//        //支持自定义JSON导入
//        return super.importBaseVOWithGroupName(is, ids, moduleBean, defaultFieldValue, otherDatas);
//    }
//
//    private ProblemDataEntity buildProblemData(){
//        ProblemDataEntity problemDataEntity = new ProblemDataEntity();
//        problemDataEntity.setFlgDeleted(0);
//        problemDataEntity.setRemark("数据导入");
//        problemDataEntity.setUpdateTime(new Date());
//        problemDataEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
//        problemDataEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
//        return problemDataEntity;
//    }
//
//    private TrainFileEntity buildTrainFile(){
//        return trainFileService.buildInitTrainFile();
//    }
//
//    private TrainFileDialogueEntity buildTrainDiaFile(String trainFileId){
//        return trainFileDialogueService.buildTrainDiaFile(trainFileId);
//    }
//
//    private ProblemDataDetailEntity buildProblemDataDetail(String trainFileId){
//        ProblemDataDetailEntity problemDataDetailEntity = new ProblemDataDetailEntity();
//        problemDataDetailEntity.setProblemDataId(trainFileId);
//        problemDataDetailEntity.setType(0);
//        problemDataDetailEntity.setFlgDeleted(0);
//        problemDataDetailEntity.setUpdateTime(new Date());
//        problemDataDetailEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
//        problemDataDetailEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
//        return problemDataDetailEntity;
//    }
//
//    @Override
//    public JsonReturnBean importTrainFileData(String content) throws Exception {
//        if (content.contains("{") && content.contains("}")){
//            //判断是不是JsonArray数组
//            JSONArray buildData = null;
//            try {
//                if (content.startsWith("[") && content.endsWith("]")){
//                    buildData = JSONArray.parseArray(content);
//                }else {
//                    buildData = JSONArray.parseArray("["+content+"]");
//                }
//                TrainFileEntity trainFileEntity = buildTrainFile();
//                trainFileService.create(trainFileEntity);
//                int dataFormat = -1;
//                int n = 0;
//                for (Object line : buildData) {
//                    n++;
//                    JSONObject row = (JSONObject)line;
//                    //预训练数据集
//                    if (row.containsKey("text")){
//                        //计算类型
//                        if (dataFormat == -1){
//                            dataFormat = 1;
//                        }
//                        TrainFileDialogueEntity trainFileDialogueEntity = new TrainFileDialogueEntity();
//                        trainFileDialogueEntity.setTrainFileId(trainFileEntity.getId());
//                        trainFileDialogueEntity.setQuestion(row.getString("text"));
//                        trainFileDialogueEntity.setContentCount(1);
//                        trainFileDialogueEntity.setType(0);
//                        trainFileDialogueEntity.setFlgDeleted(0);
//                        trainFileDialogueEntity.setUpdateTime(new Date());
//                        trainFileDialogueEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
//                        trainFileDialogueEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
//                        trainFileDialogueService.create(trainFileDialogueEntity);
//                        continue;
//                    }
//                    //sft-指令监督数据集
//                    if (row.containsKey("instruction") && row.containsKey("input") && row.containsKey("output") ){
//                        if (dataFormat == -1){
//                            dataFormat = 0;
//                        }
//                        TrainFileDialogueEntity trainFileDialogueEntity = buildBaseEntity(trainFileEntity.getId());
//                        trainFileDialogueEntity.setQuestion(row.getString("instruction"));
//                        trainFileDialogueEntity.setFeedback(row.getString("output"));
//                        trainFileDialogueEntity.setInput(row.getString("input"));
//                        trainFileDialogueService.create(trainFileDialogueEntity);
//                        continue;
//                    }
//                    //rlhf-奖励-偏好数据集
//                    if (row.containsKey("instruction") && row.containsKey("input") && row.containsKey("rejected") && row.containsKey("chosen") ){
//                        if (dataFormat == -1){
//                            dataFormat = 2;
//                        }
//                        TrainFileDialogueEntity trainFileDialogueEntity = buildBaseEntity(trainFileEntity.getId());
//                        trainFileDialogueEntity.setQuestion(row.getString("instruction"));
//                        trainFileDialogueEntity.setRejecteda(row.getString("rejected"));
//                        trainFileDialogueEntity.setChosena(row.getString("chosen"));
//                        trainFileDialogueEntity.setInput(row.getString("input"));
//                        trainFileDialogueService.create(trainFileDialogueEntity);
//                        continue;
//                    }
//                    throw new Exception("无法识别的类型!请类型管理员!"+row.toJSONString());
//                }
//                //计算训练集的信息
//                trainFileDialogueService.getTotalDataCount(trainFileEntity.getId());
//                calcFileSizeEvent.saveAfter(Arrays.asList(trainFileEntity.getId()),null);
//                trainFileEntity.setDataFormat(dataFormat);
//                //默认文本
//                trainFileEntity.setType(0);
//                trainFileService.update(trainFileEntity);
//                return JsonResultUtil.success();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            if (buildData == null){
//                return JsonResultUtil.errorMsg("您提供了无效的JSON数据");
//            }
//        }
//        throw new Exception("无法处理的数据!请联系管理员!");
//    }
//
//    @Override
//    public JsonReturnBean importTrainProblemFileData(String content) throws Exception {
//        if (content.contains("{") && content.contains("}")){
//            //判断是不是JsonArray数组
//            JSONArray buildData = null;
//            try {
//                if (content.startsWith("[") && content.endsWith("]")){
//                    buildData = JSONArray.parseArray(content);
//                }else {
//                    buildData = JSONArray.parseArray("["+content+"]");
//                }
//                ProblemDataEntity trainFileEntity = buildProblemData();
//                problemDataService.create(trainFileEntity);
//                int dataFormat = -1;
//                int n = 0;
//                for (Object line : buildData) {
//                    n++;
//                    JSONObject row = (JSONObject)line;
//                    //预训练数据集
//                    if (row.containsKey("text")){
//                        //计算类型
//                        if (dataFormat == -1){
//                            dataFormat = 1;
//                        }
//                        ProblemDataDetailEntity problemDataDetailEntity = buildProblemDataDetail(trainFileEntity.getId());
//                        problemDataDetailEntity.setProblemDataId(trainFileEntity.getId());
//                        problemDataDetailEntity.setQuestion(row.getString("text"));
//                        problemDataDetailService.create(problemDataDetailEntity);
//                        continue;
//                    }
//                    //sft-指令监督数据集
//                    if (row.containsKey("instruction") && row.containsKey("input") && row.containsKey("output") ){
//                        if (dataFormat == -1){
//                            dataFormat = 0;
//                        }
//                        ProblemDataDetailEntity problemDataDetailEntity = buildProblemDataDetail(trainFileEntity.getId());
//                        problemDataDetailEntity.setProblemDataId(trainFileEntity.getId());
//                        problemDataDetailEntity.setQuestion(row.getString("instruction"));
//                        problemDataDetailEntity.setFeedback(row.getString("output"));
//                        problemDataDetailEntity.setInput(row.getString("input"));
//                        problemDataDetailService.create(problemDataDetailEntity);
//                        continue;
//                    }
//                    //rlhf-奖励-偏好数据集
//                    if (row.containsKey("instruction") && row.containsKey("input") && row.containsKey("rejected") && row.containsKey("chosen") ){
//                        if (dataFormat == -1){
//                            dataFormat = 2;
//                        }
//                        ProblemDataDetailEntity problemDataDetailEntity = buildProblemDataDetail(trainFileEntity.getId());
//                        problemDataDetailEntity.setQuestion(row.getString("instruction"));
//                        problemDataDetailEntity.setRejecteda(row.getString("rejected"));
//                        problemDataDetailEntity.setChosena(row.getString("chosen"));
//                        problemDataDetailEntity.setInput(row.getString("input"));
//                        problemDataDetailService.create(problemDataDetailEntity);
//                        continue;
//                    }
//                    throw new Exception("无法识别的类型!请类型管理员!"+row.toJSONString());
//                }
//                //计算训练集的信息
//                trainFileEntity.setDataFormat(dataFormat);
//                //默认文本
//                trainFileEntity.setType(0);
//                trainFileEntity.setSampleCount(n);
//                problemDataService.update(trainFileEntity);
//                return JsonResultUtil.success();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            if (buildData == null){
//                return JsonResultUtil.errorMsg("您提供了无效的JSON数据");
//            }
//        }
//        throw new Exception("无法处理的数据!请联系管理员!");
//    }
//
//    @Override
//    public JsonReturnBean importTrainFileDataExcel(Workbook wb) throws Exception {
//        logger.info("当前处于importTrainFileDataExcel 时间:"+new Date());
//        Sheet[] sheets = wb.getSheets();
//        int totalRow = 0;
//        for (Sheet sheet : sheets) {
//            TrainFileEntity trainFileEntity = buildTrainFile();
//            trainFileService.create(trainFileEntity);
//            //获取标题行
//            Cell[] head = sheet.getRow(JDBCConsts.DATA_EFFECT_ROWS_HEAD_INDEX);
//            for(int i = JDBCConsts.DATA_EFFECT_ROWS_START_INDEX; i < sheet.getRows(); i++){
//                Cell[] temp = new Cell[head.length];
//                TrainFileDialogueEntity trainFileDialogueEntity = buildTrainDiaFile(trainFileEntity.getId());
//                for (int j = 0; j < temp.length; j++) {
//                    String headName = head[j].getContents();
//                    Cell contentCell = sheet.getCell(j, i);
//                    if (temp[j] instanceof EmptyCell){
//                        continue;
//                    }
//                    String content = contentCell.getContents();
//                    if ("内容".equals(headName) || "*内容".equals(headName)){
//                        trainFileDialogueEntity.setQuestion(content);
//                        trainFileDialogueService.create(trainFileDialogueEntity);
//                        //执行完就走
//                        continue;
//                    }else if ("*问题".equals(headName) || "*问题".equals(headName)){
//                        trainFileDialogueEntity.setQuestion(content);
//                    }else if ("输入".equals(headName) || "*输入".equals(headName)){
//                        trainFileDialogueEntity.setInput(content);
//                    }else if ("*优质回答".equals(headName) || "优质回答".equals(headName)){
//                        trainFileDialogueEntity.setChosena(content);
//                    }else if ("*劣质回答".equals(headName) || "劣质回答".equals(headName)){
//                        trainFileDialogueEntity.setRejecteda(content);
//                    }else if ("*系统提示词".equals(headName) || "系统提示词".equals(headName)){
//                        trainFileDialogueEntity.setSystem(content);
//                    }else if ("*回复答案".equals(headName) || "回复答案".equals(headName)){
//                        trainFileDialogueEntity.setFeedback(content);
//                    }
//                }
//                //计算类型
//                if (StringUtils.isNotBlank(trainFileDialogueEntity.getQuestion()) && StringUtils.isNotBlank(trainFileDialogueEntity.getFeedback())){
//                    trainFileEntity.setDataFormat(0);
//                }else if (StringUtils.isNotBlank(trainFileDialogueEntity.getChosena()) && StringUtils.isNotBlank(trainFileDialogueEntity.getRejecteda())){
//                    trainFileEntity.setDataFormat(2);
//                }else if (StringUtils.isNotBlank(trainFileDialogueEntity.getQuestion()) && head[0].getContents().contains("内容")){
//                    trainFileEntity.setDataFormat(1);
//                }else {
//                    logger.info("当前处于importTrainFileDataExcel失败 时间:"+new Date());
//                    throw new Exception("无效的数据类型!");
//                }
//                trainFileDialogueService.create(trainFileDialogueEntity);
//                //空行跳过
//                if(temp == null || StringUtils.isEmpty(temp.toString())){
//                    continue;
//                }
//                totalRow++;
//            }
//            //计算训练集的信息
//            trainFileDialogueService.getTotalDataCount(trainFileEntity.getId());
//            calcFileSizeEvent.saveAfter(Arrays.asList(trainFileEntity.getId()),null);//默认文本
//            trainFileEntity.setType(0);
//            trainFileService.update(trainFileEntity);
//        }
//        return JsonResultUtil.success("导入数据"+totalRow+"条");
//    }
//
//    @Override
//    public JsonReturnBean importTrainFileProDataExcel(Workbook wb) throws Exception {
//        logger.info("当前处于importTrainFileDataExcel 时间:"+new Date());
//        Sheet[] sheets = wb.getSheets();
//        int totalRow = 0;
//        for (Sheet sheet : sheets) {
//            ProblemDataEntity problemDataEntity = buildProblemData();
//            problemDataService.create(problemDataEntity);
//            //获取标题行
//            Cell[] head = sheet.getRow(JDBCConsts.DATA_EFFECT_ROWS_HEAD_INDEX);
//            for(int i = JDBCConsts.DATA_EFFECT_ROWS_START_INDEX; i < sheet.getRows(); i++){
//                Cell[] temp = new Cell[head.length];
//                ProblemDataDetailEntity problemDataDetailEntity = buildProblemDataDetail(problemDataEntity.getId());
//                for (int j = 0; j < temp.length; j++) {
//                    String headName = head[j].getContents();
//                    Cell contentCell = sheet.getCell(j, i);
//                    if (temp[j] instanceof EmptyCell){
//                        continue;
//                    }
//                    String content = contentCell.getContents();
//                    if ("内容".equals(headName) || "*内容".equals(headName)){
//                        problemDataDetailEntity.setQuestion(content);
//                        problemDataDetailService.create(problemDataDetailEntity);
//                        //执行完就走
//                        continue;
//                    }else if ("*问题".equals(headName) || "*问题".equals(headName)){
//                        problemDataDetailEntity.setQuestion(content);
//                    }else if ("输入".equals(headName) || "*输入".equals(headName)){
//                        problemDataDetailEntity.setInput(content);
//                    }else if ("*优质回答".equals(headName) || "优质回答".equals(headName)){
//                        problemDataDetailEntity.setChosena(content);
//                    }else if ("*劣质回答".equals(headName) || "劣质回答".equals(headName)){
//                        problemDataDetailEntity.setRejecteda(content);
//                    }else if ("*系统提示词".equals(headName) || "系统提示词".equals(headName)){
//                        problemDataDetailEntity.setSystem(content);
//                    }else if ("*回复答案".equals(headName) || "回复答案".equals(headName)){
//                        problemDataDetailEntity.setFeedback(content);
//                    }
//                }
//                //计算类型
//                if (StringUtils.isNotBlank(problemDataDetailEntity.getQuestion()) && StringUtils.isNotBlank(problemDataDetailEntity.getFeedback())){
//                    problemDataEntity.setDataFormat(0);
//                }else if (StringUtils.isNotBlank(problemDataDetailEntity.getChosena()) && StringUtils.isNotBlank(problemDataDetailEntity.getRejecteda())){
//                    problemDataEntity.setDataFormat(2);
//                }else if (StringUtils.isNotBlank(problemDataDetailEntity.getQuestion()) && head[0].getContents().contains("内容")){
//                    problemDataEntity.setDataFormat(1);
//                }else {
//                    logger.info("当前处于importTrainFileDataExcel失败 时间:"+new Date());
//                    throw new Exception("无效的数据类型!");
//                }
//                problemDataDetailService.create(problemDataDetailEntity);
//                //空行跳过
//                if(temp == null || StringUtils.isEmpty(temp.toString())){
//                    continue;
//                }
//                totalRow++;
//            }
//            //计算训练集的信息
//            problemDataEntity.setType(0);
//            problemDataEntity.setSampleCount(totalRow);
//            problemDataService.update(problemDataEntity);
//        }
//        return JsonResultUtil.success("导入数据"+totalRow+"条");
//    }
//
//
//    private TrainFileDialogueEntity buildBaseEntity(String trainFileId){
//        TrainFileDialogueEntity trainFileDialogueEntity = new TrainFileDialogueEntity();
//        trainFileDialogueEntity.setTrainFileId(trainFileId);
//        trainFileDialogueEntity.setContentCount(1);
//        trainFileDialogueEntity.setType(0);
//        trainFileDialogueEntity.setFlgDeleted(0);
//        trainFileDialogueEntity.setUpdateTime(new Date());
//        trainFileDialogueEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
//        trainFileDialogueEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
//        return trainFileDialogueEntity;
//    }
//}
