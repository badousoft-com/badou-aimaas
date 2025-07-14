package com.badou.project.maas.maasfile.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.maasfile.model.MaasTreeFileEntity;
import com.badou.project.maas.maasfile.service.IMaasTreeFileService;
import com.badou.project.maas.maasfile.util.FileContentExtractor;
import com.badou.project.maas.maasfile.web.form.StartCoverForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author badousoft
 * @created 2025-02-05 14:13:28.676
 *  系统文件夹 保存实现类
 */
@Controller
public class MaasTreeFileSaveAction extends BaseCommonSaveAction {

    @Autowired
    private IMaasTreeFileService maasTreeFileService;
//    @Autowired
//    private MaasFileAsyncTask maasFileAsyncTask;
//
//    @PostMapping
//    public JsonReturnBean startCoverData(@RequestBody StartCoverForm startCoverForm){
//        try {
//            List<MaasTreeFileEntity> data = startCoverForm.getData();
//            //检查参数无误 则开始解析转换的任务
//            if (data == null || data.size() == 0){
//                return JsonResultUtil.errorData();
//            }
//            //检查文件类型是否合法
//            try {
//                for (MaasTreeFileEntity maasTreeFileEntity : data) {
//                    String fileExtension = maasTreeFileEntity.getName().substring(maasTreeFileEntity.getName().indexOf(".") + 1);
//                    String result = FileContentExtractor.extractContent(null,fileExtension,null);
//                }
//
//            }catch (UnsupportedOperationException e){
//                return JsonResultUtil.errorMsg(e.getMessage());
//            }
//            maasTreeFileService.changeAllRun(startCoverForm.getData());
//            maasFileAsyncTask.startCoverData(startCoverForm,LogonCertificateHolder.getLogonCertificate());
//        }catch (Exception e){
//            e.printStackTrace();
//            return JsonResultUtil.errorMsg(e.getMessage());
//        }
//        return JsonResultUtil.success("已成功提交生成任务!");
//    }


    @PostMapping
    public JsonReturnBean saveFile(List<MultipartFile> name,String parentId,String parentName){
        try {
            maasTreeFileService.batchCreateEntity(name,parentId,parentName);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg(e.getMessage());
        }
        return JsonResultUtil.success();
    }

}
