package com.badou.project.maas.trainplan.web;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.badou.brms.attach.AttachFactory;
import com.badou.brms.attach.service.IAttachService;
import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.brms.dictionary.form.DictionaryItemCacheObject;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.GlobalConsts;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.exception.DataValidException;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.project.maas.modelwarehouse.service.IModelWarehouseService;
import com.badou.project.maas.planlink.model.PlanLinkEntity;
import com.badou.project.maas.planlink.service.IPlanLinkService;
import com.badou.project.maas.pregpucache.model.PreGpucacheEntityEntity;
import com.badou.project.maas.pregpucache.service.IPreGpucacheEntityService;
import com.badou.project.maas.trainfile.event.CalcFileSizeEvent;
import com.badou.project.maas.trainfile.model.TrainFileEntity;
import com.badou.project.maas.trainfile.service.ITrainFileService;
import com.badou.project.maas.trainplan.service.ITrainPlanService;
import com.badou.project.maas.tuningevaluationn.model.TuningEvaluationnEntity;
import com.badou.project.maas.tuningevaluationn.service.ITuningEvaluationnService;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.maas.tuningplanparams.model.TuningPlanParamsEntity;
import com.badou.project.maas.tuningplanparams.service.ITuningPlanParamsService;
import com.badou.tools.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author badousoft
 * @created 2024-04-17 15:10:34.232
 * @todo 微调方案 保存实现类
 */
@Controller
public class TrainPlanSaveAction extends BaseCommonSaveAction {

    @Autowired
    private ITuningPlanParamsService tuningPlanParamsService;
    @Autowired
    private IPlanLinkService planLinkService;
    @Autowired
    private ITrainPlanService trainPlanService;
    @Autowired
    private ITuningEvaluationnService tuningEvaluationnService;
    @Autowired
    private IModelWarehouseService modelWarehouseService;
    @Autowired
    private IPreGpucacheEntityService preGpucacheEntityService;
    @Autowired
    private ITuningModelnService tuningModelnService;


    public static final List<String[]> allowModel = new ArrayList<>();

    {
//        0不限制 1限制模型必须llama3和qwen 2.限制模型必须是chatglm3
        String[] allowModel1 = new String[]{"llama3", "qianwen", "qwen"};
        String[] allowModel2 = new String[]{"chatglm3","glm-4-9b-chat"};
        allowModel.add(new String[]{});
        allowModel.add(allowModel1);
        allowModel.add(allowModel2);
    }

    /**
     * 判断框架是否能跑对应的模型
     * 非常重要 如果框架和模型不对应 微调模型是起不来的
     * @param frame 框架
     * @param modelName 模型名字
     * @return
     */
    private static String validFrameModel(int frame,String modelName){
        if (frame>=allowModel.size()){
            return "不在有效范围的框架类型！请联系管理员！";
        }
        String[] allows = allowModel.get(frame);
        String join = String.join("、", allows);
        //长度为0则不限制
        if (allows.length ==0 ){
            return null;
        }
        for (String allow : allows) {
            if (allow.equals(modelName)){
                return null;
            }
        }
        return "基础模型文件"+modelName+"与当前选择的框架不适配！当前框架适配的模型有"+join;
    }

    @PostMapping
    public Integer getPreGpuCache(String modelId,Integer tunFrame,Integer mode){
//        QueryCriterion queryCriterion1 = new QueryCriterion();
//        queryCriterion1.addParam(new QueryHibernatePlaceholderParam("flg_deleted",0,null, QueryOperSymbolEnum.eq));
//        queryCriterion1.addParam(new QueryHibernatePlaceholderParam("createTime",DateUtil.coverDate("2024-12-18 00:00:00"),null, QueryOperSymbolEnum.gt));
//        List<TrainFileEntity> trainFileEntities = SpringHelper.getBean(ITrainFileService.class).find(queryCriterion1);
//        for (TrainFileEntity trainFileEntity : trainFileEntities) {
//            SpringHelper.getBean(CalcFileSizeEvent.class).saveAfter(Arrays.asList(new String[]{trainFileEntity.getId()}),null);
//            System.out.println(trainFileEntity);
//        }
        if (tunFrame == null || tunFrame == null){
            return 0;
        }
        ModelWarehouseEntity modelWarehouseEntity = modelWarehouseService.find(modelId);
        if (StringUtils.isEmpty(modelId)){
            return 0;
        }
        //微调模型 要拿它基础模型的GPU配置
        if (modelWarehouseEntity.getSource().equals(MaasConst.MODEL_SOURCE_TUN)){
            //优先通过父主键找
            if (StringUtils.isNotBlank(modelWarehouseEntity.getParentId())){
                modelId = modelWarehouseEntity.getParentId();
            }else {
                modelId = tuningModelnService.find(modelWarehouseEntity.getTunModelId()).getModelId();
            }
        }
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("flg_deleted",0,null,QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("tun_frame",tunFrame,null,QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("model_id",modelId,null,QueryOperSymbolEnum.eq));
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("doWay",mode,null,QueryOperSymbolEnum.eq));
        List<PreGpucacheEntityEntity> preGpucacheEntityEntities = preGpucacheEntityService.find(queryCriterion);
        if (preGpucacheEntityEntities.size() == 0){
            return 22;
        }
        return preGpucacheEntityEntities.get(0).getPreGpucache();
    }

    @PostMapping
    public JsonReturnBean copyObject(String id){
        trainPlanService.copyObject(id);
        return JsonResultUtil.success();
    }

    @Override
    protected void exeBeforeSave() throws Exception {
//        String evaluationName = this.custForm.getDetails().get("evaluationName")[0];
//        String preGpucache = this.custForm.getDetails().get("preGpucache")[0];
//        if (StringUtils.isEmpty(preGpucache) || "0".equals(preGpucache)){
//            throw new Exception("无效的GPU显存预估!请变更模型刷新!");
//        }
        this.custForm.getDetails().put("modelAllName",new String[]{this.custForm.getDetails().get("modelName")[0]});
        String[] modelAllName = this.custForm.getDetails().get("modelAllName");
        if (modelAllName == null || StringUtils.isEmpty(modelAllName[0])){
            throw new Exception("请选择基础模型!");
        }

        String serverGpuMode = this.custForm.getDetails().get("serverGpuMode")[0];
        String modelId = this.custForm.getDetails().get("modelId")[0];
//        Integer gpuCount = this.custForm.getDetails().get("gpuCount").length==0?0:Integer.parseInt(this.custForm.getDetails().get("gpuCount")[0]);
        Integer gpuCount = 1;
        String name = this.custForm.getDetails().get("name")[0];

        String serverId = this.custForm.getDetails().get("serverId").length==0?"":this.custForm.getDetails().get("serverId")[0];
        String[] serverIdSplit = serverId.split(",");
        if (Integer.parseInt(serverGpuMode) == GlobalConsts.ZERO){
            //单机的情况下 只有有一个服务器
            if (serverIdSplit.length !=1){
                throw new Exception("单机只能选择一台服务器");
            }
        }
        //判断是否合法
        if (serverIdSplit.length > 1){
            if (!GlobalConsts.TWO.equals(Integer.parseInt(serverGpuMode))){
                throw new Exception(name+"方案配置错误!只有多机多卡模式才允许选择多个服务器!");
            }
        }
        if (StringUtils.isEmpty(modelId)){
            throw new Exception("无效的模型信息");
        }
        this.custForm.getDetails().put("modelName",new String[]{modelAllName[0]});
        if (!JsonResultUtil.arrayOneElement(this.custForm.getDetails().get("doWay"))
                || StringUtils.isEmpty(this.custForm.getDetails().get("doWay")[0])){
            throw new DataValidException("微调方法为必填项");
        }

        //如果微调方法是偏好类型 则需要判断偏好对齐方法是否有选择
        if ("2".equals(this.custForm.getDetails().get("doWay")[0])){
            if (!JsonResultUtil.arrayOneElement(this.custForm.getDetails().get("rlhfWay"))
            || StringUtils.isEmpty(this.custForm.getDetails().get("rlhfWay")[0])) {
                throw new DataValidException("当微调方法为偏好对齐时,必须选择偏好对齐方法");
            }
        }

//        if (codes!=null){
//            throw new Exception("自动设置失败!请联系管理员!");
//        }

        String modelAllNameHandler = modelAllName[0].toLowerCase();
        this.custForm.getDetails().put("code",new String[]{(modelAllNameHandler)
                .replace(".","-")
                .replace(":","")+System.currentTimeMillis()+""});


        logger.info("生成方案编码:"+this.custForm.getDetails().get("code")[0]);
//        String regex = "^[a-z0-9-]+$";
//        boolean matches = codes[0].matches(".*" + regex + ".*");
//        if (!matches) {
//            throw new Exception("微调方案编码只允许输入数字、小写字母和-");
//        }
        //自动生成code
        //如果是要评价模型 检查助理是否可行
//        if(StringUtils.isNotEmpty(evaluationName)){
//            if(this.custForm.getDetails().get("evaluationId").length!=1){
//                throw new Exception("选择了开启评价必须选择评价的模型");
//            }
//            String evaluationId = this.custForm.getDetails().get("evaluationId").length==0?null:this.custForm.getDetails().get("evaluationId")[0];
//            String autoStartFlg = this.custForm.getDetails().get("autoStartFlg")[0].length()==0?null:this.custForm.getDetails().get("autoStartFlg")[0];
//            if(StringUtils.isEmpty(evaluationId)){
//                throw new Exception("开启了评价必须选择评价模型");
//            }
//            if(StringUtils.isEmpty(autoStartFlg) || "0".equals(autoStartFlg)){
//                throw new Exception("开启了评价必须开启自动启动");
//            }
//            //查询评价相关信息
//            QueryCriterion queryCriterion = new QueryCriterion();
//            queryCriterion.addParam(new QueryHibernatePlaceholderParam("id",evaluationId,null, QueryOperSymbolEnum.eq));
//            queryCriterion.addParam(new QueryHibernatePlaceholderParam("flgDeleted",0,null,QueryOperSymbolEnum.eq));
//            List<TuningEvaluationnEntity> tuningEvaluationnEntities = tuningEvaluationnService.find(queryCriterion);
//            if(tuningEvaluationnEntities.size()==0){
//                throw new Exception("无效的评价模型!请重新选择");
//            }
//            TuningEvaluationnEntity tuningEvaluationnEntity = tuningEvaluationnEntities.get(0);
//            JSONObject keyWord = new JSONObject();
//            try {
//                keyWord.put("who","你是");
////                JSONObject jsonObject = apiHelperService.talkWithAi("default-who", keyWord, tuningEvaluationnEntity.getAssistantId());
//                JSONObject jsonObject = null;
//                logger.error("结果"+jsonObject.toJSONString());
//                if (Objects.isNull(jsonObject) ||
//                        !jsonObject.containsKey("hasOk") ||
//                        !jsonObject.getBoolean("hasOk")) {
//                    logger.error("查询失败!" + jsonObject.toJSONString());
//                }
//                JSONObject bean = jsonObject.getJSONObject("bean");
//                String replyContent = bean.getString("replyContent");
//            }catch (Exception e){
//                e.printStackTrace();
//                throw new Exception("评价模型服务无响应!请联系管理员!");
//            }
//            tuningEvaluationnService.find(queryCriterion);
//        }
        if(this.custForm!=null && this.custForm.getDetails()!=null){
            String id = this.custForm.getDetails().get("id")[0];
            String tunFrame = this.custForm.getDetails().get("tunFrame")[0];
            String modelName = this.custForm.getDetails().get("modelName")[0];
            if (gpuCount!=null && gpuCount>1){
                String[] gpuFrames = this.custForm.getDetails().get("gpuFrame");
                if (gpuFrames == null || gpuFrames.length!=1 || StringUtils.isEmpty(gpuFrames[0])  || Integer.parseInt(gpuFrames[0])<0){
                    throw new DataEmptyException("显卡数>1时请选择多卡执行框架!");
                }
            }
            if (StringUtils.isNotEmpty(tunFrame) && StringUtils.isNotEmpty(modelName)){
                String result = validFrameModel(Integer.parseInt(tunFrame), modelName);
                if (result!=null){
//                    throw new Exception(result);
                }
            }
            DictionaryCacheObject dictionaryByCode = null;
            if ("0".equals(tunFrame)){
                dictionaryByCode = DictionaryLib.getDictionaryByCode(MaasConst.LLAMA_FACTORY_DIC);
            }else {
                dictionaryByCode  = DictionaryLib.getDictionaryByCode(MaasConst.getTuningAllName(
                        tunFrame,modelName));
            }
            if(dictionaryByCode.getItems().size()==0){
                throw new Exception("模型"+modelName+"未设置任务信息!请联系管理员设置!");
            }
            if(StringUtils.isNotEmpty(id)){
                //编辑 检查是否配置了微调参数、训练集文件
                QueryCriterion queryCriterion = new QueryCriterion();
                queryCriterion.addParam(new QueryHibernatePlaceholderParam("planLinkId",id,null, QueryOperSymbolEnum.eq));
                List<PlanLinkEntity> planLinkEntities = planLinkService.find(queryCriterion);
                if(JsonResultUtil.arrayEmpty(planLinkEntities)){
                    throw new Exception("请至少绑定一个训练集文件");
                }
//                queryCriterion = new QueryCriterion();
//                queryCriterion.addParam(new QueryHibernatePlaceholderParam("planId",id,null,QueryOperSymbolEnum.eq));
//                List<TuningPlanParamsEntity> tuningPlanParamsEntities = tuningPlanParamsService.find(queryCriterion);
//                if(JsonResultUtil.arrayEmpty(tuningPlanParamsEntities)){
//                    throw new Exception("请至少添加一个训练集参数");
//                }
            }
        }
        return;
    }

    @Override
    protected void exeAfterSave() throws Exception {
        QueryCriterion queryCriterion = new QueryCriterion();
        queryCriterion.addParam(new QueryHibernatePlaceholderParam("planId",this.custForm.getId(),null,QueryOperSymbolEnum.eq));
        List<TuningPlanParamsEntity> tuningPlanParamsEntities = tuningPlanParamsService.find(queryCriterion);
        if(JsonResultUtil.arrayEmpty(tuningPlanParamsEntities)){
            String[] modelNames = this.custForm.getDetails().get("modelName");
            String[] frames = this.custForm.getDetails().get("tunFrame");
            if(modelNames!=null && modelNames.length>0){
                String modelName = ""+modelNames[0];
                String frame = frames[0];
                DictionaryCacheObject dictionaryByCode = null;
                if ("0".equals(frame)){
                    dictionaryByCode = DictionaryLib.getDictionaryByCode(MaasConst.LLAMA_FACTORY_DIC);
                }else {
                    dictionaryByCode  = DictionaryLib.getDictionaryByCode(MaasConst.getTuningAllName(frame,modelName));
                }
                if(dictionaryByCode.getItems().size()==0){
                    throw new Exception("模型"+modelName+"未设置任务信息!请联系管理员设置!");
                }

                if (Integer.parseInt(frame)!=0){
                    // 获取模型路径等信息
                    String value = dictionaryByCode.getRemark();
                    String modelPath=null;
                    String dataPath=null;
                    String outPut=null;
                    String serverModelPath=null;

                    try {
                        JSONObject taskMsg = JSONObject.parseObject(value);
                        modelPath =taskMsg.getString("modelPath");
                        dataPath =taskMsg.getString("dataPath");
                        outPut =taskMsg.getString("outPut");
                        serverModelPath =taskMsg.getString("serverModelPath");
                    }catch (Exception e){
                        throw new Exception("模型"+modelName+"未设置合法的任务本体信息!请联系管理员修复!");
                    }
                    if(JsonResultUtil.isNull(modelPath,dataPath,outPut,serverModelPath)){
                        throw new Exception("模型"+modelName+"任务信息无效!请联系管理员");
                    }
                }

                if(JsonResultUtil.arrayNotEmpty(dictionaryByCode.getItems())){
                    DictionaryCacheObject planParamsGroupDic = DictionaryLib.getDictionaryByCode(MaasConst.DIC_PLAN_PARAMS_GROUP);
                    Map<String,String> groupNames = new HashMap<>();
                    Map<String,String> grouCodes = new HashMap<>();
                    for (DictionaryItemCacheObject item : planParamsGroupDic.getItems()) {
                        groupNames.put(item.getValue(),item.getName());
                        grouCodes.put(item.getValue(),item.getValue());
                    }
                    List<TuningPlanParamsEntity> insertData = new ArrayList<>();
                    dictionaryByCode.getItems().forEach(e->{
                        TuningPlanParamsEntity tuningPlanParamsEntity = new TuningPlanParamsEntity();
                        tuningPlanParamsEntity.setCode(e.getCode());
                        tuningPlanParamsEntity.setExplainName(e.getName());
                        tuningPlanParamsEntity.setValue(e.getValue());
                        tuningPlanParamsEntity.setPlanId(this.getCustForm().getId());
                        tuningPlanParamsEntity.setFlgDeleted(0);
                        tuningPlanParamsEntity.setUpdateTime(new Date());
                        tuningPlanParamsEntity.setUpdator(LogonCertificateHolder.getLogonCertificate().getUserId());
                        tuningPlanParamsEntity.setUpdatorName(LogonCertificateHolder.getLogonCertificate().getUserName());
                        tuningPlanParamsEntity.setGroupCode(grouCodes.get(e.getFlgDefault()));
                        tuningPlanParamsEntity.setGroupName(groupNames.get(e.getName()));
                        tuningPlanParamsEntity.setGroupPriority(e.getPriority().intValue());
                        tuningPlanParamsEntity.setGroupNo(e.getFlgDefault());
                        insertData.add(tuningPlanParamsEntity);
                    });
                    tuningPlanParamsService.batchCreate(insertData);
                }
            }

        }
    }

}