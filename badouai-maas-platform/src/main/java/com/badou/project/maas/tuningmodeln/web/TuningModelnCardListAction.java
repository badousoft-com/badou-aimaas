package com.badou.project.maas.tuningmodeln.web;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesExecHandler;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;
import com.badou.project.maas.trainfiledialogue.service.ITrainFileDialogueService;
import com.badou.project.server.service.IServerGpuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author badousoft
 * @created 2024-04-30 16:20:58.82
 * @todo 微调模型管理 列表实现类
 */
@Controller
@Slf4j
public class TuningModelnCardListAction extends BaseCommonListAction {

    @Autowired
    private IServerGpuService serverGpuService;
    @Autowired
    private KubernetesExecHandler kubernetesExecHandler;

    @Autowired
    private ITrainFileDialogueService trainFileDialogueService;


    @Override
    protected void exeAfterList() {
        List<String> strings = FileUtil.readLines(new File("C:\\Users\\16532\\Desktop\\distill_r1_110k_sft.jsonl"), "UTF-8");
        List<TrainFileDialogueEntity> trainFileDialogueEntities = new ArrayList<>();
        for (String string : strings) {
            TrainFileDialogueEntity trainFileDialogueEntity = trainFileDialogueService.buildTrainDiaFile("54cacbc1bcaf4e42a79185d061c12cc474422ea2bea849acb4d0d4b2c6041009");
            JSONObject jsonObject = JSONObject.parseObject(string);
            trainFileDialogueEntity.setQuestion(jsonObject.getString("instruction"));
            trainFileDialogueEntity.setInput(jsonObject.getString("input"));
            trainFileDialogueEntity.setFeedback(jsonObject.getString("output"));
            trainFileDialogueEntities.add(trainFileDialogueEntity);
        }
        trainFileDialogueService.batchCreate(trainFileDialogueEntities);
        System.out.println("123");
        this.pagelet.getDatas().forEach(e->{
            JSONObject row = (JSONObject)e;
            try {
                row.put("usedCardNos", serverGpuService.buildTunTaskCard(row.getString("execGpuCard"),row.getString("serverId")));
                try {
                    KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(row.getString("serverId"));
                    row.put("threadMsg",JSONObject.toJSONString(kubernetesExecHandler.getPodProcessStatus(row.getString("code"),
                            MaasConst.TRIAN_PLAN_NSPACE,kubernetesApiClient,"/startup,python")));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (DataEmptyException ex) {
                ex.printStackTrace();
            }
        });
    }


}
