package com.badou.project.maas.maasfile.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.maasfile.model.MaasFileEntity;
import com.badou.project.maas.maasfile.service.IMaasFileService;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

/**
 * @author badousoft
 * @created 2024-07-15 15:48:36.55
 * @todo 文件管理 保存实现类
 */
@Controller
public class MaasFileSaveAction extends BaseCommonSaveAction {

    @Autowired
    private IMaasFileService maasFileService;

    @PostMapping
    public JsonReturnBean coverPreTrain(ArrayList<MultipartFile> fileId,Integer coverRule){
        try {
            String modeName = "";
            if (coverRule == 0){
                modeName = "按章节";
            }else if(coverRule == 1){
                modeName = "按字数切割";
            }else {
                modeName = "按段落";
            }
            String result = maasFileService.coverPreTrain(fileId,modeName);
            if(StringUtils.isNotEmpty(result)){
                return JsonResultUtil.errorMsg(result);
            }
        }catch (Exception e){
            return JsonResultUtil.errorMsg("上传文件失败!请联系管理员!");
        }
        return JsonResultUtil.success();
    }

    @PostMapping
    public JsonReturnBean uploadFiles(ArrayList<MultipartFile> fileId,String tipContent){
        try {
            String result = maasFileService.uploadFiles(fileId,tipContent);
            if(StringUtils.isNotEmpty(result)){
                return JsonResultUtil.errorMsg(result);
            }
        }catch (IllegalTransactionStateException e){
//            e.printStackTrace();
        }catch (Exception e){
            return JsonResultUtil.errorMsg("上传文件失败!请联系管理员!");
        }
        return JsonResultUtil.success();
    }

    @PostMapping
    public JsonReturnBean startBuildFiles(@RequestBody ArrayList<MaasFileEntity> values){
        try {
//            maasFileAsyncTask.startBuildFiles(values, LogonCertificateHolder.getLogonCertificate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JsonResultUtil.success();
    }

    @PostMapping
    public JsonReturnBean updateTagMsg(String id,String applicableIndustry,String applicableSubject){
        MaasFileEntity maasFileEntity = maasFileService.find(id);
        maasFileEntity.setApplicableIndustry(applicableIndustry);
        maasFileEntity.setApplicableSubject(applicableSubject);
        maasFileService.update(maasFileEntity);
        return JsonResultUtil.success();
    }

}
