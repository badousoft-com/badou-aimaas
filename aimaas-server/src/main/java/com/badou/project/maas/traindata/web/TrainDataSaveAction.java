package com.badou.project.maas.traindata.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.attach.*;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.util.InstanceFactory;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.traindatalink.model.TrainDataLinkEntity;
import com.badou.project.maas.traindatalink.service.ITrainDataLinkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author badousoft
 * @created 2024-04-18 09:34:05.295
 * @todo 训练集管理 保存实现类
 */
@Controller
@Slf4j
public class TrainDataSaveAction extends BaseCommonSaveAction {
    @Autowired
    private FileControllerService fileControllerService;
    @Autowired
    private ITrainDataLinkService trainDataLinkService;

    @Override
    protected void exeBeforeSave() throws Exception {
        //检查数据是否合法
        String[] codes = this.custForm.getDetails().get("code");

        //根据数据集文件 实际创建文件到容器里面
//        createTrainData();
    }


    public static void main(String[] args) {
        String s = "[{\"conversations\":[{\"role\":\"user\",\"content\":\"形容一只猪的词语\"},{\"role\":\"assistant\",\"content\":\"铛铛信息\"},{\"role\":\"tool\",\"observation\":\"Status Code: 200. Response: 200\",\"name\":\"sendHttpRequest\",\"parameters\":{\"method\":\"GET\",\"url\":\"https://www.baidu.com/s?wd=chatGLM3\"}},{\"role\":\"user\",\"content\":\"形成天气的愿意\"},{\"role\":\"assistant\",\"content\":\"无线吃饭\"},{\"role\":\"user\",\"content\":\"科技科幻\"}]},\n" +
                "{\"conversations\":[{\"role\":\"user\",\"content\":\"这次测试的人是谁呢?\"},{\"role\":\"assistant\",\"content\":\"是我是我 猪猪侠\"}]}]";
//        new TrainDataSaveAction().createTrainData();
        System.out.println(JSONObject.parseArray(s));
    }

    /**
     * 创建数据集
     */
//    public void createTrainData() throws Exception {
//        QueryCriterion queryCriterion = new QueryCriterion();
//        queryCriterion.addParam(new QueryHibernatePlaceholderParam("trainId",this.custForm.getDetails().get("id")[0],null, QueryOperSymbolEnum.eq));
//        List<TrainDataLinkEntity> trainDataLinkEntities = trainDataLinkService.find(queryCriterion);
//        if(JsonResultUtil.arrayEmpty(trainDataLinkEntities)){
//            throw new Exception("未配置训练集!请新增训练集数据关联!");
//        }
//        log.info("开始生成训练集文件给到本地");
//        //1.获取到全部的数据集数据
//        //2.生成字符串
//        //3.
//        try{
//            StringBuilder content = new StringBuilder();
//            for(TrainDataLinkEntity trainDataLinkEntity:trainDataLinkEntities){
//                content.append(trainDataLinkEntity.getTrainDataContent());
//            }
//            String fileName = this.custForm.getDetails().get("code")[0]+".jsonl";
//            if(containsChineseFlg(fileName)){
//                throw new Exception("编码不允许包含中文!");
//            }
//            String path = FileControllerService.class.getResource("/").getPath();
//            String allPath = path+ File.separator+"traindata";
//            File file = new File(allPath);
//            //如果文件不存在则创建
//            if(!file.exists()){
//                file.mkdir();
//            }
//            file = new File(allPath+File.separator+fileName);
//            file.createNewFile();
////            FileWriter fileWritter = new FileWriter(file);
////            fileWritter.write(content.toString());
////            fileWritter.close();
//            byte bytes[]=new byte[512];
////            bytes=content.toString().getBytes();
//            bytes=trainDataLinkEntities.get(0).getTrainDataContent().toString().getBytes();
//            int b=bytes.length;   //是字节的长度，不是字符串的长度
//            FileOutputStream fos=new FileOutputStream(file);
//            fos.write(bytes,0,b);
//            fos.close();
//
//            log.info("生成文件到"+file.getPath());
//            //训练集的格式必须是jsonl 否则会报错 raise JSONDecodeError("Extra data", s, end)
//            Attach json = createAttach(file, fileName, bytes, "jsonl");
//            System.out.println(json);
//////            //调用容器里面的服务 下载这个文件
//            String modelName = "chatglm3";
//            String trainDataPath = MaasConst.TRAIN_MAIN_PATH+modelName+"/"+fileName;
//            fileControllerService.createPath(MaasConst.TRAIN_MAIN_PATH+modelName);
//            JSONObject jsonObject = fileControllerService.downFile(json.getId(), trainDataPath);
//            JSONObject fileExist = fileControllerService.checkFileExist(trainDataPath);
//            //如果结果有数据 代表上传文件
//            String msg = fileExist.getString("msg");
//            if(msg.contains("No such")){
//                throw new Exception("生成训练集失败!请联系管理员!");
//            }
//            log.info("生成数据集成功!生成到"+msg);
//            if(StringUtils.isEmpty(msg)){
//                throw new Exception("创建数据集失败!无法生成实际的数据集文件!");
//            }
//            this.custForm.getDetails().put("path",new String[]{MaasConst.TRAIN_MAIN_PATH+modelName});
//            this.custForm.getDetails().put("fileName",new String[]{fileName});
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public Attach createAttach(File file,String fileName,byte bytes[],String ...fileType){
//        if (file != null && org.apache.commons.lang.StringUtils.isNotBlank(fileName)) {
//            Attach attach = new Attach();
//            attach.setFileContent(bytes);
//            attach.setName(fileName);
//
//            AttachFactory attachFactory = (AttachFactory) InstanceFactory.getInstance(AttachFactory.class);
//            LogonCertificate logon = LogonCertificateHolder.getLogonCertificate();
//            attach.setExtendName(".json");
//            attach.setGenPersonId(logon.getUserId());
//            attach.setGenPersonName(logon.getUserName());
//            attach.setFileSize(bytes.length);
//            if (fileType != null && fileType.length > 0) {
//                attach.setFileType(fileType[0]);
//            }
//
//            try {
//                attachFactory.uploadFile(attach);
//            } catch (Exception var10) {
//                log.error(var10.getMessage(), var10);
//            }
//
//            return attach;
//        } else {
//            return null;
//        }
//    }
//
//    public static boolean containsChineseFlg(String str) {
//        if (str == null) { return false; }
//        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//        Matcher m = p.matcher(str);
//        return m.find();
//    }

}