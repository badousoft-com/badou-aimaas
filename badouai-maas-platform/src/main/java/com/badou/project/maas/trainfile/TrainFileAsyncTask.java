package com.badou.project.maas.trainfile;

import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfile.service.ITrainFileService;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TrainFileAsyncTask {
    @Autowired
    private ITrainFileService trainFileService;
    @Autowired
    private ITrainFileDialogueService trainFileDialogueService;

    @Async
    public void task1() throws InterruptedException{
        long currentTimeMillis = System.currentTimeMillis();
        Thread.sleep(1000);
        long currentTimeMillis1 = System.currentTimeMillis();
        System.out.println("task1任务耗时:"+(currentTimeMillis1-currentTimeMillis)+"ms");
    }

    @Transactional
    @Async
//    public void startAnycTask(List<MultipartFile> importFile, int type, String[] ids, LogonCertificate LogonCertificate) throws Exception {
//        LogonCertificateHolder.setLogonCertificate(LogonCertificate);
//        List<TrainFileEntity> trainFileEntities = new ArrayList<>();
//        //数据导入到旧文件
//        if (type == 0) {
//            for (int i = 0; i < ids.length; i++) {
//                String id = ids[i];
//                TrainFileEntity trainFileEntity = trainFileService.find(id);
//                trainFileEntities.add(trainFileEntity);
//            }
//        }
//        for (int i = 0; i < importFile.size(); i++) {
//            File file = new File(System.getProperty("user.dir")+ File.separator + "tmp" + File.separator + importFile.get(i).getOriginalFilename());
//            new File(System.getProperty("user.dir")+File.separator + "tmp" ).mkdir();
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            int flag = 0;
//            while (flag != 0){
//                try{
//                    importFile.get(i).transferTo(file);
//                    flag = 1;
//                }catch (Exception e){
//                    System.out.println("报错");
//                    e.printStackTrace();
//                }
//            }
//            String value = null;
//            BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
//            if(is.available() == 0 ){
//                log.info("错误的文件！！！");
//                continue;
//            }
//            if (FileMagic.valueOf(is) == FileMagic.OLE2 || file.getName().endsWith(".doc")) {
////                HWPFDocument hwpfDocument = new HWPFDocument(is);
////                String s = hwpfDocument.getText().toString();
////                System.out.println(s);
//
////                ex.close();
//            } else if (FileMagic.valueOf(is) == FileMagic.OOXML) {
//                XWPFDocument doc = new XWPFDocument(is);
//                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
//                value = extractor.getText();
//                extractor.close();
//            }
//            if (ids == null || ids.length == 0) {
//                ids = new String[1];
//                TrainFileEntity trainFileEntity = new TrainFileEntity();
//                trainFileEntity.setFlgDeleted(0);
//                trainFileEntity.setRoleDesc("你是一个中文小助手.擅长把收集来的问题分析得出结论");
//                trainFileEntity.setFilePath("/home/test");
//                trainFileEntity.setNumCount(0);
//                trainFileEntity.setName("新导入的训练集文件"+System.currentTimeMillis());
//                trainFileEntity.setUpdateTime(new Date());
//                trainFileEntity.setApplicableIndustry(10);
//                trainFileEntity.setUpdator(LogonCertificate.getUserId());
//                trainFileEntity.setUpdatorName(LogonCertificate.getUserName());
//                trainFileService.create(trainFileEntity);
//                ids[0] = trainFileEntity.getId();
//            }
////            apiHelperService.createQustion(value, ids);
//        }
//        updateTotalCount(ids);
//    }

    public void updateTotalCount(String[] ids) {
        trainFileService.flushAndGetTotalCount(ids);
        //问题创建完成 统计数量
        // 1数量=80B
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            int totalDataCount = trainFileDialogueService.getTotalDataCount(id);
            TrainFileEntity trainFileEntity = trainFileService.find(id);
            trainFileEntity.setNumCount(totalDataCount);
            trainFileService.update(trainFileEntity);
        }
    }

}