package com.badou.project.maas.modelapp.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;
import com.badou.project.maas.modelsync.service.IModelSyncTaskService;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.TimeUnit;

/**
 * @author badousoft
 * @created 2024-05-27 11:33:46.513
 * @todo 模型应用管理 保存实现类
 */
@Controller
public class ModelAppSaveAction extends BaseCommonSaveAction {

    @Autowired
    private IModelAppService modelAppService;
    @Autowired
    private IModelSyncTaskService modelSyncTaskService;

    @RequestMapping
    public JsonReturnBean stopApp(String id){
        try {
            String s = modelAppService.stopApp(id, true, null);
            if (StringUtils.isNotBlank(s)){
                return JsonResultUtil.errorMsg(s);
            }
        }catch (Exception e){
            return JsonResultUtil.error();
        }
        return JsonResultUtil.success();
    }

    public static void main(String[] args) {
        System.out.println("Downloading [diffusion_pytorch_model-00006-of-00007.safetensors]:  34%|███▍      | 2.54G/7.37G [05:39<39:48, 2.17MB/s]AAAAA\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00002-of-00007.safetensors]:  38%|███▊      | 3.50G/9.16G [05:39<55:29, 1.83MB/s]A\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00005-of-00007.safetensors]:  27%|██▋       | 2.44G/9.16G [05:40<1:03:34, 1.89MB/s]AAAAAAAA\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00003-of-00007.safetensors]:  22%|██▏       | 2.02G/9.16G [05:40<2:10:50, 977kB/s]AA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00007-of-00007.safetensors]:  34%|███▍      | 1.94G/5.68G [05:40<1:03:30, 1.05MB/s]AAAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00006-of-00007.safetensors]:  34%|███▍      | 2.54G/7.37G [05:40<39:59, 2.16MB/s]AAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [models_t5_umt5-xxl-enc-bf16.pth]:  19%|█▉        | 2.04G/10.6G [05:40<1:53:22, 1.35MB/s]AAAAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00004-of-00007.safetensors]:  26%|██▌       | 2.38G/9.16G [05:40<1:43:35, 1.17MB/s]AAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00001-of-00007.safetensors]:  31%|███       | 2.83G/9.21G [05:40<1:28:19, 1.29MB/s]AAAA\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00002-of-00007.safetensors]:  38%|███▊      | 3.50G/9.16G [05:40<54:43, 1.85MB/s]A\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00005-of-00007.safetensors]:  27%|██▋       | 2.44G/9.16G [05:40<1:03:57, 1.88MB/s]AAAAAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00006-of-00007.safetensors]:  34%|███▍      | 2.54G/7.37G [05:40<39:59, 2.16MB/s]AAAAA\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00002-of-00007.safetensors]:  38%|███▊      | 3.50G/9.16G [05:41<55:17, 1.83MB/s]A\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00003-of-00007.safetensors]:  22%|██▏       | 2.02G/9.16G [05:41<2:08:11, 997kB/s]AA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [models_t5_umt5-xxl-enc-bf16.pth]:  19%|█▉        | 2.04G/10.6G [05:41<1:53:46, 1.34MB/s]AAAAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00007-of-00007.safetensors]:  34%|███▍      | 1.94G/5.68G [05:41<1:04:10, 1.04MB/s]AAAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00005-of-00007.safetensors]:  27%|██▋       | 2.45G/9.16G [05:41<1:05:07, 1.85MB/s]AAAAAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00006-of-00007.safetensors]:  34%|███▍      | 2.54G/7.37G [05:41<39:04, 2.21MB/s]AAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00004-of-00007.safetensors]:  26%|██▌       | 2.38G/9.16G [05:41<1:42:02, 1.19MB/s]AAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00001-of-00007.safetensors]:  31%|███       | 2.83G/9.21G [05:41<1:28:07, 1.30MB/s]AAAA\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00002-of-00007.safetensors]:  38%|███▊      | 3.51G/9.16G [05:41<54:31, 1.86MB/s]A\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00006-of-00007.safetensors]:  34%|███▍      | 2.54G/7.37G [05:41<39:09, 2.21MB/s]AAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00005-of-00007.safetensors]:  27%|██▋       | 2.45G/9.16G [05:41<1:05:59, 1.82MB/s]AAAAAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [models_t5_umt5-xxl-enc-bf16.pth]:  19%|█▉        | 2.04G/10.6G [05:41<1:53:17, 1.35MB/s]AAAAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00001-of-00007.safetensors]:  31%|███       | 2.83G/9.21G [05:42<1:27:22, 1.31MB/s]AAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00004-of-00007.safetensors]:  26%|██▌       | 2.38G/9.16G [05:42<1:42:40, 1.18MB/s]AAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00006-of-00007.safetensors]:  34%|███▍      | 2.54G/7.37G [05:42<39:24, 2.19MB/s]AAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00007-of-00007.safetensors]:  34%|███▍      | 1.94G/5.68G [05:42<1:04:19, 1.04MB/s]AAAAAA\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00003-of-00007.safetensors]:  22%|██▏       | 2.02G/9.16G [05:42<2:09:26, 987kB/s]AA\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00002-of-00007.safetensors]:  38%|███▊      | 3.51G/9.16G [05:42<54:14, 1.87MB/s]A\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00005-of-00007.safetensors]:  27%|██▋       | 2.45G/9.16G [05:42<1:05:56, 1.82MB/s]AAAAAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "Downloading [diffusion_pytorch_model-00006-of-00007.safetensors]:  34%|███▍      | 2.54G/7.37G [05:42<39:24, 2.19MB/s]AAAAA\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n".replaceAll("\u001B\\[[0-?]*[ -/]*[@-~]", ""));
    }

    @RequestMapping
    public JsonReturnBean getAppLogs(String id, @RequestParam String logType, Integer size, Integer flushTime,Boolean modelSyncFlag){
        if (modelSyncFlag!=null && modelSyncFlag){
            ModelAppEntity appEntity = new ModelAppEntity();
            ModelSyncTaskEntity modelSyncTaskEntity = modelSyncTaskService.find(id);
            appEntity.setModelServerId(modelSyncTaskEntity.getCode());
            appEntity.setServerId(modelSyncTaskEntity.getServerIds());
            if (MaasConst.DOPLAN_SUCCESS_STATUS == modelSyncTaskEntity.getStatus() ||
            MaasConst.DOPLAN_FAIL_STATUS == modelSyncTaskEntity.getStatus()){
                return JsonResultUtil.errorMsg(modelSyncTaskEntity.getExecMsg());
            }
            return JsonResultUtil.success(modelAppService.getLogs(null,appEntity,logType,size,flushTime, MaasConst.MODEL_SYNC_NS));
        }
        return JsonResultUtil.success(modelAppService.getLogs(id,null,logType,size,flushTime,null));
    }

}
