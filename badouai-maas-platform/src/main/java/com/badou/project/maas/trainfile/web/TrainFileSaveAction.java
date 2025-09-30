//package com.badou.project.maas.trainfile.web;
//
//import com.badou.brms.base.support.struts.JsonReturnBean;
//import com.badou.brms.dboperation.query.QueryCriterion;
//import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
//import com.badou.brms.dboperation.query.QueryParam;
//import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
//import com.badou.project.common.webparams.util.JsonResultUtil;
//import com.badou.project.maas.trainfile.model.TrainFileEntity;
//import com.badou.project.maas.trainfile.service.ITrainFileService;
//import com.badou.project.maas.trainfile.service.impl.TrainFileServiceImpl;
//import com.badou.project.maas.trainfile.web.form.TrainFileQustionForm;
//import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
//import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
//import com.badou.project.maas.trainfiledialoguedetail.model.TrainFileDialoguedetailEntity;
//import com.badou.project.maas.trainfiledialoguedetail.service.ITrainFileDialoguedetailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//
//import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author badousoft
// * @created 2024-05-16 11:07:50.5
// * @todo 训练集文件管理 保存实现类
// */
//@Controller
//public class TrainFileSaveAction extends BaseCommonSaveAction {
//
//    @Autowired
//    private ITrainFileService trainFileService;
//    @Autowired
//    private ITrainFileDialogueService trainFileDialogueService;
//    @Autowired
//    private ITrainFileDialoguedetailService trainFileDialoguedetailService;
//
//    @PostMapping
//    public JsonReturnBean createTrainFileData(List<MultipartFile> importFile,int type,String[] ids) throws IOException {
//        if (importFile.size()==0){
//            return JsonResultUtil.errorMsg("数据长度异常");
//        }
//        if(type == 0){
//            if (ids == null ||ids.length == 0){
//                return JsonResultUtil.errorMsg("请至少选择一个训练集文件.");
//            }
//        }
//        for (int i = 0; i < importFile.size(); i++) {
//            MultipartFile e = importFile.get(i);
//            if (e.getOriginalFilename().endsWith(".doc") || e.getOriginalFilename().endsWith(".docx") || e.getOriginalFilename().endsWith(".pdf")){
//
//            }else {
//                return JsonResultUtil.errorMsg("存在无效的文件类型! 有效类型为pdf和doc、docx后缀的文件!");
//            }
//            File file = new File(System.getProperty("user.dir")+ File.separator + "tmp" + File.separator + importFile.get(i).getOriginalFilename());
//            new File(System.getProperty("user.dir")+File.separator + "tmp" ).mkdir();
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            int flag = 0;
//            while (flag == 0){
//                try{
//                    importFile.get(i).transferTo(file);
//                    flag = 1;
//                }catch (Exception ess){
//                    System.out.println("报错");
//                    ess.printStackTrace();
//                }
//            }
//            String value = null;
//            BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
//            if(is.available() == 0 ){
//                logger.error("错误的文件!！！");
//                return JsonResultUtil.errorMsg("存在无效的文件!请联系管理员!");
//            }
//        }
//        try {
//            trainFileService.createTrainDataBYFiles(importFile,type,ids);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return JsonResultUtil.errorMsg("启动失败!请联系管理员!");
//        }
//        return JsonResultUtil.success();
//    }
//
//    @PostMapping
//    public JsonReturnBean importToQuestion(@RequestBody TrainFileQustionForm trainFileQustionForm) throws Exception {
//        trainFileService.getTrainDataToQues(trainFileQustionForm);
//        return JsonResultUtil.success();
//    }
//
//    @PostMapping
//    public JsonReturnBean updateStatus(@RequestBody List<TrainFileEntity> datas){
//        try {
//            trainFileService.updateStatus(datas);
//        }catch (Exception e){
//            e.printStackTrace();
//            return JsonResultUtil.error();
//        }
//        return JsonResultUtil.success();
//    }
//
//
//    @Override
//    protected void exeBeforeSave() throws Exception {
//        String id = this.custForm.getId();
//        if(JsonResultUtil.isNull(id)){
//            throw new Exception("服务器错误!");
//        }
//        TrainFileEntity trainFileEntity = trainFileService.find(id);
//        QueryCriterion queryCriterion = new QueryCriterion();
//        queryCriterion.addParam(new QueryHibernatePlaceholderParam("trainFileId",id,null, QueryOperSymbolEnum.eq));
//        List<TrainFileDialogueEntity> trainFileDialogueEntities = trainFileDialogueService.find(queryCriterion);
//
//        List<String> ids = new ArrayList<>();
//        trainFileDialogueEntities.forEach(e->{
//            ids.add(e.getId());
//        });
//
//        queryCriterion = new QueryCriterion();
//        queryCriterion.addParam(new QueryHibernatePlaceholderParam("dialogueId", ids, null, QueryOperSymbolEnum.in, QueryParam.PARAM_PLACEHOLDER_NAME));
//        List<TrainFileDialoguedetailEntity> trainFileDialoguedetailEntities = trainFileDialoguedetailService.find(queryCriterion);
//
//
//
//    }
//
//
//}
