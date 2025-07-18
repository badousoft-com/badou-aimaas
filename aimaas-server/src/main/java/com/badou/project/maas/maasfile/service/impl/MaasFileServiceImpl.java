package com.badou.project.maas.maasfile.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.badou.brms.attach.AttachConfig;
import com.badou.brms.attach.AttachFactory;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;

import com.badou.project.maas.problemdata.service.IProblemDataService;
import com.badou.project.maas.problemdatadetail.service.IProblemDataDetailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.badou.brms.base.support.spring.BaseSpringService;

import com.badou.project.maas.maasfile.dao.IMaasFileDAO;
import com.badou.project.maas.maasfile.model.MaasFileEntity;
import com.badou.project.maas.maasfile.service.IMaasFileService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author badousoft
 * @date 2024-07-15 15:48:36.55
 * @todo 文件管理 Service接口实现类
 **/
@Service
public class MaasFileServiceImpl extends BaseSpringService<MaasFileEntity, Serializable> implements IMaasFileService {

    @Autowired
    private IMaasFileDAO maasFileDAO;
    @Autowired
    private AttachFactory attachFactory;
//    @Autowired
//    private MaasFileAsyncTask maasFileAsyncTask;
    @Autowired
    private IProblemDataService problemDataService;
    @Autowired
    private IProblemDataDetailService problemDataDetailService;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    public static final String[] ableEndFix = new String[]{".txt", ".zip", ".pdf", ".docx", ".doc"};

    @Autowired
    public void setMaasFileDAO(IMaasFileDAO maasFileDAO) {
        this.maasFileDAO = maasFileDAO;
        super.setBaseDAO(maasFileDAO);
    }

    @Override
    public String coverPreTrain(ArrayList<MultipartFile> files,String modeName) throws Exception {
        String[] ableEndFix = new String[]{".docx", ".doc"};
        for (MultipartFile multipartFile : files) {
            boolean valid = false;
            for (String endFix : ableEndFix) {
                //如果不是合法的文件名字
                if (multipartFile.getOriginalFilename().endsWith(endFix)) {
                    valid = true;
                    break;
                }
            }
            if(valid == false){
                return multipartFile.getOriginalFilename() + "的后缀不在合法的格式内.只支持doc、docx";
            }
        }
        DictionaryCacheObject fileTypeDic = DictionaryLib.getDictionaryByCode("FILE_TYPE");
        List<MaasFileEntity> maasFileEntities = new ArrayList<>();
        //格式合法 开始生成数据
        for (MultipartFile multipartFile : files) {
            MaasFileEntity maasFileEntity = new MaasFileEntity();
            maasFileEntity.setFlgDeleted(0);
            maasFileEntity.setUpdateTime(new Date());
            maasFileEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
            maasFileEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
            maasFileEntity.setName(multipartFile.getOriginalFilename());

            AtomicReference<String> fileType = new AtomicReference<>();
            //获取文件类型并设置
            fileTypeDic.getItems().forEach(e -> {
                if (multipartFile.getOriginalFilename().endsWith(e.getCode())) {
                    fileType.set(e.getCode());
                    maasFileEntity.setType(Integer.parseInt(e.getValue()));
                }
            });
            if (maasFileEntity.getType() == null || fileType.get() == null) {
                return "未找到" + multipartFile.getOriginalFilename() + "的文件类型!";
            }
            // 保存文件到数据库
            Attach attach = new Attach();
            LogonCertificate logonCertificate = LogonCertificateHolder.getLogonCertificate();
            attach.setResourceId(maasFileEntity.getId());
            attach.setExtendName(fileType.get());
            attach.setFileSize(multipartFile.getSize());
            attach.setGenPersonId(logonCertificate.getUserId());
            attach.setGenPersonName(logonCertificate.getUserName());
            attach.setGenDate(new Date());
            attach.setName(multipartFile.getOriginalFilename());
            attach.setFileContent(multipartFile.getBytes());
            attachFactory.uploadFile(attach);
            String filePath = AttachConfig.getInstance().getAttachSavePath() + "/" + attach.getFileName();
            String fileExtension = attach.getName().substring(attach.getName().lastIndexOf('.') + 1).toLowerCase();
            String fileName = attach.getName().substring(0,attach.getName().lastIndexOf('.'));
//            DocumentParser parser = DocumentParserFactory.getParser(fileExtension, filePath);
//            if (parser!= null) {
//                List<KnowledgePoint> knowledgePoints = null;
//                if ("doc".equals(fileExtension)) {
//                    knowledgePoints = parser.parse(filePath,modeName,DocumentParserFactory.convertHWPFToXWPF(new HWPFDocument(new FileInputStream(filePath))));
//                }else {
//                    knowledgePoints  = parser.parse(filePath,modeName,null);
//                }
//                //设置预训练集
//                ProblemDataEntity problemDataEntity = problemDataService.buildInitEntity();
//                problemDataEntity.setName(fileName);
//                problemDataEntity.setDataFormat(1);
//                problemDataEntity.setSamplePermission(0);
//                problemDataEntity.setApplicableIndustry("0");
//                problemDataService.create(problemDataEntity);
//                for (KnowledgePoint point : knowledgePoints) {
//                    ProblemDataDetailEntity problemDataDetailEntity = new ProblemDataDetailEntity();
//                    problemDataDetailEntity.setQuestion(point.getContent());
//                    problemDataDetailEntity.setProblemDataId(problemDataEntity.getId());
//                    problemDataDetailEntity.setFlgDeleted(0);
//                    problemDataDetailEntity.setUpdateTime(new Date());
//                    problemDataDetailEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
//                    problemDataDetailEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
//                    problemDataDetailService.createEntity(problemDataDetailEntity);
//                }
//                problemDataEntity.setSampleCount(knowledgePoints.size());
//                problemDataService.update(problemDataEntity);
//                maasFileEntity.setFileId(attach.getId());
//                maasFileEntity.setCreateTime(new Date());
//                maasFileEntity.setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
//                maasFileEntity.setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
//                maasFileEntity.setAnalyzeFlg(1);
//                maasFileEntity.setAnalyzeTime(new Date());
//                maasFileEntity.setAnalyzeCount(knowledgePoints.size());
//                maasFileDAO.create(maasFileEntity);
//            } else {
//                throw new RuntimeException("不支持的文件类型或排版特征");
//            }
//
//            maasFileEntities.add(maasFileEntity);
        }
        return null;
    }

    @Override
    public String uploadFiles(ArrayList<MultipartFile> files,String tipContent) throws Exception {
        return null;
    }

    @Override
//    public String uploadFiles(ArrayList<MultipartFile> files,String tipContent) throws Exception {
//        for (MultipartFile multipartFile : files) {
//            boolean valid = false;
//            for (String endFix : ableEndFix) {
//                //如果不是合法的文件名字
//                if (multipartFile.getOriginalFilename().endsWith(endFix)) {
//                    valid = true;
//                    break;
//                }
//            }
//            if(valid == false){
//                return multipartFile.getOriginalFilename() + "的后缀不在合法的格式内.只支持doc、docx";
//            }
//        }
//        DictionaryCacheObject fileTypeDic = DictionaryLib.getDictionaryByCode("FILE_TYPE");
//        List<MaasFileEntity> maasFileEntities = new ArrayList<>();
//        //格式合法 开始生成数据
//        for (MultipartFile multipartFile : files) {
//            MaasFileEntity maasFileEntity = new MaasFileEntity();
//            maasFileEntity.setFlgDeleted(0);
//            maasFileEntity.setUpdateTime(new Date());
//            maasFileEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
//            maasFileEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
//            maasFileEntity.setName(multipartFile.getOriginalFilename());
//
//            AtomicReference<String> fileType = new AtomicReference<>();
//            //获取文件类型并设置
//            fileTypeDic.getItems().forEach(e -> {
//                if (multipartFile.getOriginalFilename().endsWith(e.getCode())) {
//                    fileType.set(e.getCode());
//                    maasFileEntity.setType(Integer.parseInt(e.getValue()));
//                }
//            });
//            if (maasFileEntity.getType() == null || fileType.get() == null) {
//                return "未找到" + multipartFile.getOriginalFilename() + "的文件类型!";
//            }
//            // 保存文件到数据库
//            Attach attach = new Attach();
//            LogonCertificate logonCertificate = LogonCertificateHolder.getLogonCertificate();
//            attach.setResourceId(maasFileEntity.getId());
//            attach.setExtendName(fileType.get());
//            attach.setFileSize(multipartFile.getSize());
//            attach.setGenPersonId(logonCertificate.getUserId());
//            attach.setGenPersonName(logonCertificate.getUserName());
//            attach.setGenDate(new Date());
//            attach.setName(multipartFile.getOriginalFilename());
//            attach.setFileContent(multipartFile.getBytes());
//            attachFactory.uploadFile(attach);
//            maasFileEntity.setFileId(attach.getId());
//            maasFileEntity.setCreateTime(new Date());
//            maasFileEntity.setCreatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
//            maasFileEntity.setCreator(LogonCertificateHolder.getLogonCertificate().getUserId());
//            maasFileEntity.setAnalyzeFlg(0);
//            maasFileEntity.setAnalyzeTime(new Date());
//            maasFileEntity.setAnalyzeCount(0);
//            maasFileDAO.create(maasFileEntity);
//            maasFileEntities.add(maasFileEntity);
//        }
//        // 事务手动提交 毁报错但是不用管
//        platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
//        maasFileAsyncTask.startBuildFiles(maasFileEntities,LogonCertificateHolder.getLogonCertificate(),tipContent);
//        return null;
//    }

    public String startBuildFiles(ArrayList<MaasFileEntity> values,LogonCertificate logonCertificate) {
        try {
//            maasFileAsyncTask.startBuildFiles(values,logonCertificate);
            logger.info("启动任务成功!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
 
 