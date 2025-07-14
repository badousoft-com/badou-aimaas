package com.badou.project.util;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.mq.ModelPlanTaskMqReceiver;
import com.badou.tools.common.util.SpringHelper;
import com.badou.tools.common.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BuildCustomFileUtil {

    public static void loadPPOTrainFile(TuningModelnEntity tuningModelnEntity, KubernetesApiClient kubernetesApiClient) throws Exception {
        commonDownK8sFile(MaasConst.TUN_CONFIG_FILE_DIC,"ppotrainfile","ppo",tuningModelnEntity,kubernetesApiClient);
    }

    public static void commonDownK8sFile(String dicCode,String dicName,String objMsg,TuningModelnEntity tuningModelnEntity, KubernetesApiClient kubernetesApiClient) throws Exception {
        DictionaryItemCacheObject deepspeeds2config = DictionaryLib.getDictionaryItemByCode(dicCode, dicName);
        if (deepspeeds2config == null){
            throw new Exception("未存在"+objMsg+"配置文件.请联系管理员!");
        }
        String dataInfoJsonId = deepspeeds2config.getValue();
        if (tuningModelnEntity.getDoFrame() == 0){
            String datasetInfoPath = tuningModelnEntity.getCreatePath() + deepspeeds2config.getName();
            //下载配置文件到容器文件服务
            if (StringUtils.isNotBlank(dataInfoJsonId)){
                //下载微调配置文件
                FileControllerService fileControllerService = SpringHelper.getBean(FileControllerService.class);
                ModelPlanTaskMqReceiver modelPlanTaskMqReceiver = SpringHelper.getBean(ModelPlanTaskMqReceiver.class);
                JSONObject dataInfoJsonAttach = fileControllerService.downFile(tuningModelnEntity,dataInfoJsonId, datasetInfoPath, kubernetesApiClient,null);
                if (tuningModelnEntity.getDoStatus() == MaasConst.DOPLAN_FAIL_STATUS){
                    throw new Exception("初始化"+objMsg+"配置失败!"+tuningModelnEntity.getPlanMsg());
                }
            }
        }
    }

    /**
     * 计算文件的md5值 用来判断唯一
     * @param fileContent 文件内容
     * @return
     */
    public static String calcMd5ByBytes(byte[] fileContent){
        try {
            // 创建一个MessageDigest实例，指定算法为MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 将输入的字符串转换为字节数组并更新MessageDigest对象
            md.update(fileContent);
            // 计算哈希值并获取字节数组结果
            byte[] digest = md.digest();
            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
