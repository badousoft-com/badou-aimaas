package com.badou.project.gpucalc.impl;

import com.badou.project.gpucalc.BaseGpuCalcHandler;
import com.badou.project.gpucalc.GpuCalcHandler;
import com.badou.project.gpucalc.model.MultipleServersConfig;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.model.WareHouseVllmParamEntity;
import io.kubernetes.client.openapi.models.V1EnvVar;
import java.util.List;

public class VllmGpuHandler extends BaseGpuCalcHandler implements GpuCalcHandler {

    @Override
    public Object exec(Object... params) throws Exception {
        if (params==null && params.length!=4){
            throw new Exception("未存在有效的显卡配置!请联系管理员!");
        }
        ModelWarehouseEntity modelWarehouseEntity = (ModelWarehouseEntity) params[0];
        List<V1EnvVar> envVarList = (List<V1EnvVar>) params[1];
        List<MultipleServersConfig> multipleServersConfigs = (List<MultipleServersConfig>) params[2];

        //判断本次启动是否是多机模式 集群模式暂时不支持 暂时最多支持单机多卡
        envVarList.add(new V1EnvVar().name(MaasConst.NVIDIA_VISIBLE_DEVICES).value(multipleServersConfigs.get(0).getCanGpuCardNos()));
        //当需要配置VLLM单机多卡的时候
        String moreCardParams = "";
        for (MultipleServersConfig multipleServersConfig:multipleServersConfigs){
            int gpuCount = multipleServersConfig.getCanGpuCardNoMap().size();
            if (gpuCount>1){
                //只有当前显卡数量是模型注意力头的倍数和2的倍数时 可以启用最佳的张量并行推理.否则就是换弱性能管线并行
                if (MaasConst.TRANSFORMER_ATTENTION_HEADS_COUNT % gpuCount == 0 && gpuCount % 2 == 0){
                    moreCardParams+=" "+MaasConst.TENSOR_PARALLEL_SIZE_FIELD+"="+gpuCount;
                }else {
                    moreCardParams+=" "+MaasConst.PIPELINE_PARALLEL_SIZE+"="+gpuCount;
                }
            }
        }
        moreCardParams+=" --served-model-name "+modelWarehouseEntity.getModelName();
        if (params[3]!=null){
            List<WareHouseVllmParamEntity> vllmParams = (List<WareHouseVllmParamEntity>) params[3];
            String currentExecParams = StringHandlerUtil.buildVllmParams(vllmParams) + " --disable-log-stats  "+moreCardParams;
            envVarList.add(new V1EnvVar().name(MaasConst.VLLM_EXEC_PARAMS_NAME).value(currentExecParams));
        }
        //增加VLLM需要使用到的参数
        envVarList.add(new V1EnvVar().name("VLLM_ALLOW_LONG_MAX_MODEL_LEN").value("1"));
        envVarList.add(new V1EnvVar().name("TORCH_DISTRIBUTED_DEBUG").value("INFO"));
        envVarList.add(new V1EnvVar().name("NCCL_DEBUG_SUBSYS").value("TRACE"));
        envVarList.add(new V1EnvVar().name("NCCL_DEBUG").value("info"));
        envVarList.add(new V1EnvVar().name("LOAR_PATHS1").value("output"));
        envVarList.add(new V1EnvVar().name("VLLM_ENGINE_ITERATION_TIMEOUT_S").value("600"));
        envVarList.add(new V1EnvVar().name("VLLM_IMAGE_FETCH_TIMEOUT").value("600"));
        envVarList.add(new V1EnvVar().name("VLLM_RPC_TIMEOUT").value("10000"));
        //优化参数 启动专家模式等内容
//        envVarList.add(new V1EnvVar().name("VLLM_ATTENTION_BACKEND").value("FLASHMLA"));
//        envVarList.add(new V1EnvVar().name("VLLM_TEST_ENABLE_EP").value("1"));
//        envVarList.add(new V1EnvVar().name("VLLM_USE_V1").value("1"));

        return null;
    }

}
