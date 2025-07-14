package com.badou.project.maas.modelapp.web;

import com.alibaba.fastjson.JSONObject;
import com.badou.project.exception.DataEmptyException;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesExecHandler;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.server.service.IServerGpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author badousoft
 * @created 2024-05-27 11:33:46.513
 * @todo 模型应用管理 列表实现类
 */
@Controller
public class ModelAppListGpuAction extends ModelAppListAction {

    @Autowired
    private KubernetesExecHandler kubernetesExecHandler;
    @Autowired
    private IServerGpuService serverGpuService;

    @Override
    protected void exeAfterList() {
        super.exeAfterList();
        this.pagelet.getDatas().forEach(e->{
            JSONObject row = (JSONObject)e;
            try {
                row.put("usedCardNos", serverGpuService.buildTunTaskCard(row.getString("execGpuCard"),row.getString("serverId")));
                try {
                    KubernetesApiClient kubernetesApiClient = FileControllerService.getCustomClient(row.getString("serverId"));
                    row.put("threadMsg",JSONObject.toJSONString(kubernetesExecHandler.getPodProcessStatus(row.getString("modelServerId"),
                            MaasConst.MODEL_APP_NSAPCE,kubernetesApiClient,"/startup,python")));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } catch (DataEmptyException ex) {
                ex.printStackTrace();
            }
        });
    }


}
