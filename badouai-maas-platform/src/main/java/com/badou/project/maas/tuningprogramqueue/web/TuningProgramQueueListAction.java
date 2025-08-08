package com.badou.project.maas.tuningprogramqueue.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.attach.AttachFactory;
import com.badou.brms.attach.model.Attach;
import com.badou.brms.attach.service.IAttachService;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;
import com.badou.project.maas.trainfile.model.TrainMultiDetailFileEntity;
import com.badou.project.maas.trainfile.service.ITrainMultiDetailFileService;
import com.badou.project.maas.trainfile.service.ITrainMultiFileService;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.tools.common.util.SpringHelper;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author badousoft
 * @created 2024-07-22 09:58:48.624
 * @todo 计划任务列表 列表实现类
 */
@Controller
public class TuningProgramQueueListAction extends BaseCommonListAction {

    public static String convertImageToBase64(String imagePath) throws IOException {
        File file = new File(imagePath);
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            // 创建一个字节数组来存储图片数据
            byte[] imageData = new byte[(int) file.length()];
            // 读取图片数据到字节数组中
            imageInFile.read(imageData);
            // 使用Base64编码器对图片数据进行编码
            return Base64.getEncoder().encodeToString(imageData);
        }
    }

}
