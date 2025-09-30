package com.badou.project.mq;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.dboperation.query.QueryCriterion;
import com.badou.brms.dboperation.query.QueryOperSymbolEnum;
import com.badou.brms.dboperation.query.support.QueryHibernatePlaceholderParam;
import com.badou.brms.dictionary.DictionaryLib;
import com.badou.brms.dictionary.form.DictionaryCacheObject;
import com.badou.core.runtime.thread.local.LogonCertificate;
import com.badou.core.runtime.thread.local.LogonCertificateHolder;
import com.badou.project.CommonConst;
import com.badou.project.GlobalConsts;
import com.badou.project.common.webparams.GlobalConstans;
import com.badou.project.common.webparams.util.DateUtil;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesPodHandler;
import com.badou.project.kubernetes.handler.KubernetesServiceHandler;
import com.badou.project.kubernetes.vo.DeployAppVo;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.StopCenter;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanqEntity;
import com.badou.project.maas.evaluationinstan.model.EvaluationMqEntity;
import com.badou.project.maas.evaluationinstan.service.IEvaluationInstanService;
import com.badou.project.maas.evaluationinstan.service.IEvaluationInstanqService;
import com.badou.project.maas.k8sport.service.IK8sPortService;
import com.badou.project.maas.modelapp.model.ModelAppEntity;
import com.badou.project.maas.modelapp.model.TalkEntity;
import com.badou.project.maas.modelapp.service.IModelAppService;
import com.badou.project.maas.tuningevaluationn.service.ITuningEvaluationnService;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.maas.tuningmodeln.service.ITuningModelnService;
import com.badou.project.maas.tuningprogramqueue.service.ITuningProgramQueueService;
import com.badou.project.mq.util.RabbitMqUtil;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class EvaluationMqReceiver implements IBaseTaskMqReceiver{

    @Autowired
    private IEvaluationInstanqService evaluationInstanqService;
    @Autowired
    private IEvaluationInstanService evaluationInstanService;
    @Autowired
    private IModelAppService modelAppService;
    @Autowired
    private ITuningModelnService tuningModelnService;
    @Autowired
    private ITuningProgramQueueService tuningProgramQueueService;
    @Autowired
    private ModelPlanTaskMqReceiver modelPlanTaskMqReceiver;
    private static final Map<String,Integer> calcCount = new HashMap<>();
    private static final Map<String,Double> calcScore = new HashMap<>();

    public void clearTaskIdx(String id){
        calcCount.remove(id);
        calcScore.remove(id);
    }

    /**
     * 模型应用建立服务的处理类
     * @param message
     */
    @RabbitListener(queues = {"${spring.rabbitmq.evaluation-queue}"})
    @RabbitHandler
    @Override
    public synchronized void process(String message){
        EvaluationMqEntity evaluationMqEntity = null;
        EvaluationInstanEntity evaluationInstanEntity = null;
        try {
            evaluationMqEntity = JSONObject.parseObject(message, EvaluationMqEntity.class);
            setUpdate();
            //获取当前助手并访问
            evaluationInstanEntity = evaluationMqEntity.getEvaluationInstanEntity();
            boolean stop = StopCenter.checkStopFlag(evaluationInstanEntity.getTunModelId(), "EvaluationMqReceiver-模型评价任务-03停止");
            if (stop) {
                return;
            }
            Integer nowCount = calcCount.get(evaluationInstanEntity.getId());
            Double nowScore = calcScore.get(evaluationInstanEntity.getId());
            if (nowCount == null){
                nowCount = 1;
                calcCount.put(evaluationInstanEntity.getId(),nowCount);
            }else if (nowScore==null){
                calcScore.put(evaluationInstanEntity.getId(),0.0);
            }else {
                calcCount.put(evaluationInstanEntity.getId(),nowCount+1);
            }
//            log.info("计算执行次数"+nowCount);
            QueryCriterion queryCriterion = new QueryCriterion();
            queryCriterion.addParam(new QueryHibernatePlaceholderParam("id",evaluationInstanEntity.getId(),null, QueryOperSymbolEnum.eq));
            List<EvaluationInstanEntity> evaluationInstanEntities = evaluationInstanService.find(queryCriterion);
            evaluationInstanEntity = evaluationInstanEntities.get(0);
            //失败或者成功 跳过!
            //变成未理中
            if(evaluationInstanEntity.getStatus() == null){
                evaluationInstanEntity.setStatus(0);
            }
            if (evaluationInstanEntity.getStatus()==MaasConst.DOPLAN_FAIL_STATUS || evaluationInstanEntity.getStatus() == MaasConst.DOPLAN_SUCCESS_STATUS){
                StopCenter.startStop(evaluationInstanEntity.getTunModelId(),"本次评价已成功或者失败!任务取消");
                log.error(evaluationInstanEntity.getId()+"已成功或者失败!任务取消");
                return;
            }
            EvaluationInstanqEntity evaluationInstanqEntity = evaluationMqEntity.getEvaluationInstanqEntity();

            String evaModelId = evaluationInstanEntity.getEvaModelId();
            if(StringUtils.isEmpty(evaModelId)){
                updateFail(evaluationInstanEntity,"执行评价模型失败!evaModelId评价模型主键是空的!");
                return;
            }
            //如果是第一次执行
            if(evaluationInstanEntity.getEvaStartTime()==null){
                evaluationInstanEntity.setEvaStartTime(new Date());
            }
            evaluationInstanqEntity.setSubmitTime(new Date());
            String question = evaluationInstanqEntity.getQuestion();
            String standardAnswer = evaluationInstanqEntity.getStandardAnswer();
            String feedback = null;
            //获取当前模型进行对话 回答答案
            String modelAppId = evaluationMqEntity.getEvaluationInstanEntity().getModelAppId();
            if(StringUtils.isEmpty(modelAppId)){
                updateFail(evaluationInstanEntity,"未设置模型应用主键");
                return;
            }
            TalkEntity talkEntity = new TalkEntity();
            talkEntity.setContent(question+"\n.请用中文回答.不要返回任何的表情、特殊符号");
            talkEntity.setId(modelAppId);
            talkEntity.setNowTalks(new ArrayList<>());
            talkEntity.setTrainModelId(evaluationInstanEntity.getTunModelId());
            talkEntity.setNowTalks(new ArrayList<>());
            talkEntity.setMaxTokens("2048");
            String talkResult = modelAppService.talkToAi(talkEntity);
            if (talkResult.contains("finish_reason:stop")){
                updateFail(evaluationInstanEntity,"评分失败!Token中断!"+talkResult);
                return;
            }
            stop = StopCenter.checkStopFlagQuiet(evaluationInstanEntity.getTunModelId(), "EvaluationMqReceiver-模型评价任务-03停止");
            if (stop) {
                return;
            }
            JSONObject talkResultJson = JSONObject.parseObject(talkResult);
            if(talkResultJson.getJSONArray("choices")!=null && talkResultJson.getJSONArray("choices").size()==1
                    && talkResultJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message")!=null){
                feedback = talkResultJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                if(StringUtils.isEmpty(feedback)){
//                    log.info("对话数据不合法!");
//                    updateFail(evaluationInstanEntity);
//                    return;
                    feedback = "模型无法回答!";
                }
            }else {
                updateFail(evaluationInstanEntity,"对话数据异常!");
                return;
            }
            JSONObject params = new JSONObject();
            /**
             * [
             *   {
             *     "问题": "植物进行光合作用的主要场所是哪里？",
             *     "标准答案": "叶绿体。叶绿体含有叶绿素等光合色素，能够吸收光能，将二氧化碳和水转化为有机物并释放氧气，是光合作用的关键场所。",
             *     "随机答案": "线粒体。"
             *   },
             *   {
             *     "问题": "秦始皇统一六国后使用的货币是什么形状？",
             *     "标准答案": "圆形方孔钱。这种货币形式被称为半两钱，在全国范围内统一使用，促进了经济交流和统一市场的形成。",
             *     "随机答案": "刀币形状。"
             *   },
             *   {
             *     "问题": "物理学中，牛顿第一定律的内容是什么？",
             *     "标准答案": "任何物体都要保持匀速直线运动或静止的状态，直到外力迫使它改变运动状态为止。它揭示了物体的惯性这一基本属性。",
             *     "随机答案": "物体受力就会运动，不受力就静止。"
             *   }
             * ]
             */
            JSONObject row = new JSONObject();
            row.put("问题",question);
            row.put("标准答案",standardAnswer);
            row.put("随机答案",feedback);
            //要求JSONArray数组
            params.put("contents",row);

//            JSONObject jsonObject = apiHelperService.talkWithAi(TaskMqConst.AI_TIP_ANSWER_QUSTION, params, evaluationInstanEntity.getAssistantId());
            JSONObject jsonObject = null;
            //判断是否对话失败
            if (Objects.isNull(jsonObject) ||
                    !jsonObject.containsKey("hasOk") ||
                    !jsonObject.getBoolean("hasOk") || jsonObject.getJSONObject("bean") == null) {
                updateFail(evaluationInstanEntity,"执行对话失败!"+jsonObject);
                return;
            }
            JSONObject bean = jsonObject.getJSONObject("bean");
            String replyContent = bean.getString("replyContent");
            replyContent = replyContent.replace("]","");
            replyContent = replyContent.replace("[","");
            Double score = 0.0;
            System.out.println(calcScore);
            System.out.println(evaluationInstanEntity.getId());
            if(StringUtils.isNotEmpty(replyContent)){
                try {
                    score = Double.parseDouble(replyContent);
                    nowScore = calcScore.get(evaluationInstanEntity.getId());
                    if (nowScore==null){
                        nowScore = 0.0;
                    }
                    calcScore.put(evaluationInstanEntity.getId(),nowScore+score);
                }catch (Exception e){
                    System.out.println(replyContent);
                    e.printStackTrace();
                    updateFail(evaluationInstanEntity,"转换出错!"+e.getMessage()+",问题:"+question);
                    return;
                }
            }
            //如果是null就直接结束  允许空结果
            if(replyContent == null){
                updateFail(evaluationInstanEntity,"存在空结果");
                return;
            }
            log.info("问题:"+question+",标准答案:"+standardAnswer+",回复:"+feedback+",评分:"+score);
            evaluationInstanqEntity.setFeedback(feedback);
            evaluationInstanqEntity.setAnswerScore(score);
            evaluationInstanqEntity.setInstanq(evaluationInstanEntity.getId());
            evaluationInstanqEntity.setEvaFlag(GlobalConsts.ONE);
            evaluationInstanqService.update(evaluationInstanqEntity);
        }catch (Exception e){
            e.printStackTrace();
            updateFail(evaluationInstanEntity,"评价失败!"+e.getMessage());
            return;
        }finally {
            if (evaluationInstanEntity.getStatus()==MaasConst.DOPLAN_FAIL_STATUS || evaluationInstanEntity.getStatus() == MaasConst.DOPLAN_SUCCESS_STATUS){
                return;
            }
            boolean stop = StopCenter.checkStopFlag(evaluationInstanEntity.getTunModelId(), "EvaluationMqReceiver-模型评价任务-03停止");
            if (stop) {
                return;
            }
            //计算总分和总数
            int nowCount = calcCount.get(evaluationInstanEntity.getId());
            evaluationInstanEntity.setExexCount(nowCount);
            evaluationInstanEntity.setTotalScore(calcScore.get(evaluationInstanEntity.getId()));
            log.info("目前进度信息:");
            System.out.println("目前已执行数量"+evaluationInstanEntity.getExexCount());
            System.out.println("目前已获得分数"+evaluationInstanEntity.getTotalScore());
            //修改进度
            evaluationInstanService.startUpdate(evaluationInstanEntity);
            updateToModelTask(evaluationInstanEntity);
            //判断是不是最后
            if(nowCount >= evaluationInstanEntity.getQustionCount()){
                log.info("模型评价任务结束!统计评价情况");
                //计算总数
                evaluationInstanEntity.setStatus(MaasConst.DOPLAN_SUCCESS_STATUS);
                evaluationInstanEntity.setEvaEndTime(new Date());
                evaluationInstanEntity.setEvaTotalSeconds(DateUtil.getTimeDiff(evaluationInstanEntity.getEvaStartTime(),evaluationInstanEntity.getEvaEndTime()));
                evaluationInstanService.calcFinishStatus(evaluationInstanEntity.getId());
                clearTaskIdx(evaluationInstanEntity.getId());
                tuningProgramQueueService.updateStatusByPlanId(evaluationInstanEntity.getTaskId(),MaasConst.DOPLAN_SUCCESS_STATUS);
                //更新到model
                TuningModelnEntity tuningModelnEntity = tuningModelnService.find(evaluationInstanEntity.getTunModelId());
                tuningModelnService.setSucccessStatus(tuningModelnEntity,"模型微调->模型自动启动->模型评价->成功!");
            }
        }
    }

    public void updateToModelTask(EvaluationInstanEntity evaluationInstanEntity){
        //更新到model
        TuningModelnEntity tuningModelnEntity = tuningModelnService.find(evaluationInstanEntity.getTunModelId());
        tuningModelnEntity.setEvaTotalCount(evaluationInstanEntity.getExexCount()+"/"+evaluationInstanEntity.getQustionCount());
        tuningModelnEntity.setEvaTotalScore(evaluationInstanEntity.getTotalScore()+"/"+evaluationInstanEntity.getQustionCount());
        tuningModelnService.update(tuningModelnEntity);
    }

    private EvaluationInstanEntity updateFail(EvaluationInstanEntity evaluationInstanEntity,String msg){
        setUpdate();
        ModelAppEntity modelAppEntity = modelAppService.find(evaluationInstanEntity.getModelAppId());
        TuningModelnEntity tuningModelnEntity = tuningModelnService.find(evaluationInstanEntity.getTunModelId());
        evaluationInstanEntity.setStatus(MaasConst.DOPLAN_FAIL_STATUS);
        tuningProgramQueueService.updateStatusByPlanId(evaluationInstanEntity.getTaskId(),MaasConst.DOPLAN_FAIL_STATUS);
        evaluationInstanService.update(evaluationInstanEntity);
        clearTaskIdx(evaluationInstanEntity.getId());
        try {
            modelAppService.stopApp(evaluationInstanEntity.getModelAppId(),true,null);
            modelAppService.delete(modelAppEntity.getId());
        } catch (Exception e) {
            e.printStackTrace();
            //更新到model
            tuningModelnService.setFailStatus(tuningModelnEntity,"移除应用失败!"+msg);
            return evaluationInstanEntity;
        }
        //更新到model
        tuningModelnService.setFailStatus(tuningModelnEntity,"评价失败!"+msg);
        return evaluationInstanEntity;
    }

    public static void setUpdate(){
        LogonCertificate logonCertificate = LogonCertificateHolder.getLogonCertificate();
        if(logonCertificate==null){
            logonCertificate = new LogonCertificate();
            logonCertificate.setUserName("system");
            logonCertificate.setUserId("system");
            LogonCertificateHolder.setLogonCertificate(logonCertificate);
        }
    }

}
