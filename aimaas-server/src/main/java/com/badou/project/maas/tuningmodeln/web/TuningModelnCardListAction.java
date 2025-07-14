package com.badou.project.maas.tuningmodeln.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.designer.jdbc.common.web.BaseCommonListAction;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesExecHandler;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.server.service.IServerGpuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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

    @Override
    protected void exeAfterList() {
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
