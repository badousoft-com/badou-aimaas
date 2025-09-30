package com.badou.project.gpucalc.model;

import com.badou.project.exception.DataEmptyException;
import com.badou.project.gpucalc.GpuCalcCardModel;
import com.badou.project.kubernetes.util.StringHandlerUtil;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.trainplan.model.TrainPlanEntity;
import com.badou.project.maas.tuningmodeln.model.TuningModelnEntity;
import com.badou.project.server.model.K8sServerConfEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

//由于K8s是主节点的形式.这里只会有主节点的KubernetesApiClient客户端.就能操作全部服务器

/**
 * K8s 服务器配置实体
 */
@Slf4j
public class MultipleServersConfig {
    //任务方案信息
    private TrainPlanEntity trainPlanEntity;
    //已分配的显卡序号 多个用,拼接
    private String useGpuCardNos = "";
    private Map<Integer,GpuCalcCardModel> useGpuCardNoMap = new HashMap<>();
    //本次可分配的显卡序号
    private String canGpuCardNos = "";
    private Map<Integer,GpuCalcCardModel> canGpuCardNoMap = new HashMap<>();
    // 未分配的显存序号
    private String emptyGpuCardNos = "";
    private Map<Integer,GpuCalcCardModel> emptyGpuCardNoMap = new HashMap<>();
    //需要的GPU显存 单位GB
    private Integer needGpuNum;
    //是否是主节点
    private boolean masterFlag;
    //是否分配结束
    private boolean calcDoneFlag;
    //服务器配置信息
    private K8sServerConfEntity k8sServerConfEntity;
    //实际物理显卡信息数组
    private Map<Integer,GpuCalcCardModel> gpuCalcCardModels = new HashMap<>();

    public MultipleServersConfig() {
    }

    public void calcGpuCard(TrainPlanEntity trainPlanEntity,K8sServerConfEntity k8sServerConfEntity, Map<Integer, GpuCalcCardModel> gpuCalcCardModels) throws DataEmptyException {
        //自动计算物理显卡情况 先移除本次服务不可用的显卡

        for (Integer key : gpuCalcCardModels.keySet()) {
            GpuCalcCardModel cardStatus = gpuCalcCardModels.get(key);
            //检查是否满足显存要求
            int hasCache = (cardStatus.getMaxVMemory() - cardStatus.getCurrentVMemory()) / 1000;
            //该显卡满足分配需求
            if (calcDoneFlag ==false && cardStatus.getCurrentVMemory() == 0 && hasCache>trainPlanEntity.getPreGpucache()) {
                log.info("方案需要显卡显存"+trainPlanEntity.getPreGpucache()+"G,显卡"+cardStatus.getOrderNo()+"可用显存"+cardStatus.getCurrentVMemory()+"/"+cardStatus.getMaxVMemory()+"G,可分配");
                canGpuCardNos += cardStatus.getOrderNo() + ",";
                canGpuCardNoMap.put(cardStatus.getOrderNo(),cardStatus);
                continue;
            }
            //可使用
            if (cardStatus.getCurrentVMemory() == 0 || cardStatus.getCurrentVMemory() < 5){
                canGpuCardNos += cardStatus.getOrderNo() + ",";
                canGpuCardNoMap.put(cardStatus.getOrderNo(),cardStatus);
                continue;
            }
            //满足
            if (hasCache > trainPlanEntity.getPreGpucache()){
                log.info("方案需要显卡显存"+trainPlanEntity.getPreGpucache()+"G,显卡"+cardStatus.getOrderNo()+"可用显存"+cardStatus.getCurrentVMemory()+"/"+cardStatus.getMaxVMemory()+"G,可分配");
                canGpuCardNos += cardStatus.getOrderNo() + ",";
                canGpuCardNoMap.put(cardStatus.getOrderNo(),cardStatus);
                continue;
            }
            //已分配
            if (cardStatus.getCurrentVMemory() > 5){
                useGpuCardNos += cardStatus.getOrderNo() + ",";
                useGpuCardNoMap.put(cardStatus.getOrderNo(),cardStatus);
                continue;
            }
            //代表已经被用完了
            if (hasCache == 0) {
                useGpuCardNos += cardStatus.getOrderNo() + ",";
                useGpuCardNoMap.put(cardStatus.getOrderNo(),cardStatus);
                continue;
            }

            //该显卡满足分配需求
            if (calcDoneFlag ==false && hasCache >= trainPlanEntity.getPreGpucache()) {
                log.info("方案需要显卡显存"+trainPlanEntity.getPreGpucache()+"G,显卡"+cardStatus.getOrderNo()+"可用显存"+cardStatus.getCurrentVMemory()+"/"+cardStatus.getMaxVMemory()+"G,可分配");
                canGpuCardNos += cardStatus.getOrderNo() + ",";
                canGpuCardNoMap.put(cardStatus.getOrderNo(),cardStatus);
            }
        }
        //移除最后一个,
        this.emptyGpuCardNos = StringHandlerUtil.removeLastComma(emptyGpuCardNos);
        this.useGpuCardNos = StringHandlerUtil.removeLastComma(useGpuCardNos);
        this.canGpuCardNos = StringHandlerUtil.removeLastComma(canGpuCardNos);
    }

    public String reBuildCardNos(Map<Integer,GpuCalcCardModel> cardNoMap){
        String cardStr = "";
        if (cardNoMap.size() == 0){
            return cardStr;
        }
        for (Integer key : cardNoMap.keySet()) {
            GpuCalcCardModel cardStatus = cardNoMap.get(key);
            cardStr += cardStatus.getOrderNo() + ",";
        }
        return StringHandlerUtil.removeLastComma(cardStr);
    }

    public MultipleServersConfig(TrainPlanEntity trainPlanEntity,K8sServerConfEntity k8sServerConfEntity, Map<Integer, GpuCalcCardModel> gpuCalcCardModels) throws DataEmptyException {
        //        if (JsonResultUtil.isNull(trainPlanEntity,tuningModelnEntity,trainPlanEntity.getPreGpucache(),trainPlanEntity.getGpuCount())){
//            throw new DataEmptyException("存在不合法的数据!");
//        }
        this.k8sServerConfEntity = k8sServerConfEntity;
        needGpuNum = trainPlanEntity.getPreGpucache();
        //数量必须>0 外面判断
        this.gpuCalcCardModels = gpuCalcCardModels;
        //初始化显卡信息
        calcGpuCard(trainPlanEntity,k8sServerConfEntity,gpuCalcCardModels);
    }

    public String printCardMsg(){
        return "服务器: "+k8sServerConfEntity.getAddress()+",\n可分配显卡: "+canGpuCardNos+"\n已分配显卡: "+useGpuCardNos+"\n未分配显卡: "+emptyGpuCardNos;
    }

//    public static void main(String[] args) throws DataEmptyException {
//        TrainPlanEntity trainPlanEntity = new TrainPlanEntity();
//        trainPlanEntity.setPreGpucache(22);
//        trainPlanEntity.setGpuCount(1);
//
//        //模拟显卡未用过
//        Map<Integer,GpuCalcCardModel> gpuCalcCardModels = new HashMap<>();
//        gpuCalcCardModels.put(0,new GpuCalcCardModel(0,0,22));
//        gpuCalcCardModels.put(1,new GpuCalcCardModel(1,0,22));
//        gpuCalcCardModels.put(2,new GpuCalcCardModel(2,0,22));
//        gpuCalcCardModels.put(3,new GpuCalcCardModel(3,0,22));
//        gpuCalcCardModels.put(4,new GpuCalcCardModel(4,0,22));
//
//        MultipleServersConfig multipleServersConfig = new MultipleServersConfig(trainPlanEntity, null, gpuCalcCardModels);
//        System.out.println("模拟显卡未用过");
//        System.out.println("需要显存"+multipleServersConfig.getNeedGpuNum());
//        System.out.println("可分配"+multipleServersConfig.getCanGpuCardNos());
//        System.out.println("可分配"+multipleServersConfig.getCanGpuCardNoMap());
//        System.out.println("已分配"+multipleServersConfig.getUseGpuCardNos());
//        System.out.println("已分配"+multipleServersConfig.getUseGpuCardNoMap());
//        System.out.println("未分配"+multipleServersConfig.getEmptyGpuCardNos());
//        System.out.println("未分配"+multipleServersConfig.getEmptyGpuCardNoMap());
//        //模拟显卡用了一张
//        gpuCalcCardModels = new HashMap<>();
//        gpuCalcCardModels.put(0,new GpuCalcCardModel(0,12,22));
//        gpuCalcCardModels.put(1,new GpuCalcCardModel(1,0,22));
//        gpuCalcCardModels.put(2,new GpuCalcCardModel(2,0,22));
//        gpuCalcCardModels.put(3,new GpuCalcCardModel(3,0,22));
//        gpuCalcCardModels.put(4,new GpuCalcCardModel(4,0,22));
//        System.out.println("模拟显卡用过一张 规则");
//        multipleServersConfig = new MultipleServersConfig(trainPlanEntity, null, gpuCalcCardModels);
//        System.out.println("需要显存"+multipleServersConfig.getNeedGpuNum());
//        System.out.println("可分配"+multipleServersConfig.getCanGpuCardNos());
//        System.out.println("可分配"+multipleServersConfig.getCanGpuCardNoMap());
//        System.out.println("已分配"+multipleServersConfig.getUseGpuCardNos());
//        System.out.println("已分配"+multipleServersConfig.getUseGpuCardNoMap());
//        System.out.println("未分配"+multipleServersConfig.getEmptyGpuCardNos());
//        System.out.println("未分配"+multipleServersConfig.getEmptyGpuCardNoMap());
//
//        gpuCalcCardModels = new HashMap<>();
//        gpuCalcCardModels.put(0,new GpuCalcCardModel(0,0,22));
//        gpuCalcCardModels.put(1,new GpuCalcCardModel(1,12,22));
//        gpuCalcCardModels.put(2,new GpuCalcCardModel(2,0,22));
//        gpuCalcCardModels.put(3,new GpuCalcCardModel(3,0,22));
//        gpuCalcCardModels.put(4,new GpuCalcCardModel(4,0,22));
//        System.out.println("模拟显卡用过一张 不规则");
//        multipleServersConfig = new MultipleServersConfig(trainPlanEntity, null, gpuCalcCardModels);
//        System.out.println("需要显存"+multipleServersConfig.getNeedGpuNum());
//        System.out.println("可分配"+multipleServersConfig.getCanGpuCardNos());
//        System.out.println("可分配"+multipleServersConfig.getCanGpuCardNoMap());
//        System.out.println("已分配"+multipleServersConfig.getUseGpuCardNos());
//        System.out.println("已分配"+multipleServersConfig.getUseGpuCardNoMap());
//        System.out.println("未分配"+multipleServersConfig.getEmptyGpuCardNos());
//        System.out.println("未分配"+multipleServersConfig.getEmptyGpuCardNoMap());
//        //模拟显卡用了两张 不规则
//        gpuCalcCardModels = new HashMap<>();
//        gpuCalcCardModels.put(0,new GpuCalcCardModel(0,0,22));
//        gpuCalcCardModels.put(1,new GpuCalcCardModel(1,12,22));
//        gpuCalcCardModels.put(2,new GpuCalcCardModel(2,0,22));
//        gpuCalcCardModels.put(3,new GpuCalcCardModel(3,12,22));
//        gpuCalcCardModels.put(4,new GpuCalcCardModel(4,0,22));
//        System.out.println("模拟显卡用过两张 不规则");
//        trainPlanEntity.setGpuCount(2);
//        multipleServersConfig = new MultipleServersConfig(trainPlanEntity, null, gpuCalcCardModels);
//        System.out.println("需要显存"+multipleServersConfig.getNeedGpuNum());
//        System.out.println("可分配"+multipleServersConfig.getCanGpuCardNos());
//        System.out.println("可分配"+multipleServersConfig.getCanGpuCardNoMap());
//        System.out.println("已分配"+multipleServersConfig.getUseGpuCardNos());
//        System.out.println("已分配"+multipleServersConfig.getUseGpuCardNoMap());
//        System.out.println("未分配"+multipleServersConfig.getEmptyGpuCardNos());
//        System.out.println("未分配"+multipleServersConfig.getEmptyGpuCardNoMap());
//
//        //模拟显卡用了两张 规则
//        gpuCalcCardModels = new HashMap<>();
//        gpuCalcCardModels.put(0,new GpuCalcCardModel(0,12,22));
//        gpuCalcCardModels.put(1,new GpuCalcCardModel(1,12,22));
//        gpuCalcCardModels.put(2,new GpuCalcCardModel(2,0,22));
//        gpuCalcCardModels.put(3,new GpuCalcCardModel(3,0,22));
//        gpuCalcCardModels.put(4,new GpuCalcCardModel(4,0,22));
//        System.out.println("模拟显卡用过两张 规则");
//        trainPlanEntity.setGpuCount(2);
//        multipleServersConfig = new MultipleServersConfig(trainPlanEntity, null, gpuCalcCardModels);
//        System.out.println("需要显存"+multipleServersConfig.getNeedGpuNum());
//        System.out.println("可分配"+multipleServersConfig.getCanGpuCardNos());
//        System.out.println("可分配"+multipleServersConfig.getCanGpuCardNoMap());
//        System.out.println("已分配"+multipleServersConfig.getUseGpuCardNos());
//        System.out.println("已分配"+multipleServersConfig.getUseGpuCardNoMap());
//        System.out.println("未分配"+multipleServersConfig.getEmptyGpuCardNos());
//        System.out.println("未分配"+multipleServersConfig.getEmptyGpuCardNoMap());
//    }

    public TrainPlanEntity getTrainPlanEntity() {
        return trainPlanEntity;
    }

    public void setTrainPlanEntity(TrainPlanEntity trainPlanEntity) {
        this.trainPlanEntity = trainPlanEntity;
    }

    public String getEmptyGpuCardNos() {
        return emptyGpuCardNos;
    }

    public void setEmptyGpuCardNos(String emptyGpuCardNos) {
        this.emptyGpuCardNos = emptyGpuCardNos;
    }

    public Map<Integer, GpuCalcCardModel> getEmptyGpuCardNoMap() {
        return emptyGpuCardNoMap;
    }

    public void setEmptyGpuCardNoMap(Map<Integer, GpuCalcCardModel> emptyGpuCardNoMap) {
        this.emptyGpuCardNoMap = emptyGpuCardNoMap;
    }

    public String getUseGpuCardNos() {
        return useGpuCardNos;
    }

    public void setUseGpuCardNos(String useGpuCardNos) {
        this.useGpuCardNos = useGpuCardNos;
    }

    public Map<Integer, GpuCalcCardModel> getUseGpuCardNoMap() {
        return useGpuCardNoMap;
    }

    public void setUseGpuCardNoMap(Map<Integer, GpuCalcCardModel> useGpuCardNoMap) {
        this.useGpuCardNoMap = useGpuCardNoMap;
    }

    public Map<Integer, GpuCalcCardModel> getCanGpuCardNoMap() {
        return canGpuCardNoMap;
    }

    public void setCanGpuCardNoMap(Map<Integer, GpuCalcCardModel> canGpuCardNoMap) {
        this.canGpuCardNoMap = canGpuCardNoMap;
    }

    public boolean isCalcDoneFlag() {
        return calcDoneFlag;
    }

    public void setCalcDoneFlag(boolean calcDoneFlag) {
        this.calcDoneFlag = calcDoneFlag;
    }

    public boolean isMasterFlag() {
        return masterFlag;
    }

    public void setMasterFlag(boolean masterFlag) {
        this.masterFlag = masterFlag;
    }

    public K8sServerConfEntity getK8sServerConfEntity() {
        return k8sServerConfEntity;
    }

    public void setK8sServerConfEntity(K8sServerConfEntity k8sServerConfEntity) {
        this.k8sServerConfEntity = k8sServerConfEntity;
    }

    public String getCanGpuCardNos() {
        return canGpuCardNos;
    }

    public void setCanGpuCardNos(String canGpuCardNos) {
        this.canGpuCardNos = canGpuCardNos;
    }

    public Integer getNeedGpuNum() {
        return needGpuNum;
    }

    public void setNeedGpuNum(Integer needGpuNum) {
        this.needGpuNum = needGpuNum;
    }

    public Map<Integer, GpuCalcCardModel> getGpuCalcCardModels() {
        return gpuCalcCardModels;
    }

    public void setGpuCalcCardModels(Map<Integer, GpuCalcCardModel> gpuCalcCardModels) {
        this.gpuCalcCardModels = gpuCalcCardModels;
    }
}
