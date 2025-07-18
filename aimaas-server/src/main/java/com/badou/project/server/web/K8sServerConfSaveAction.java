package com.badou.project.server.web;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.client.KubernetesApiClientFactory;
import com.badou.project.maas.common.FileControllerService;
import com.badou.project.server.model.K8sServerConfEntity;
import com.badou.project.server.service.IK8sServerConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author badousoft
 * @created 2025-03-17 14:30:08.365
 *  显卡资源管理 保存实现类
 */
@Controller
public class K8sServerConfSaveAction extends BaseCommonSaveAction {

    @Autowired
    private IK8sServerConfService k8sServerConfService;
    @Autowired
    private FileControllerService fileControllerService;

    @Override
    public void exeAfterSave() throws Exception {
        K8sServerConfEntity k8sServerConfEntity = k8sServerConfService.find(this.getCustForm().getId());
        //每次保存都刷新服务器内部的KS客户端
        KubernetesApiClient build = KubernetesApiClientFactory.build(k8sServerConfEntity);

        //每次保存都确认一次K8S容器操作客户端
        fileControllerService.initServerServe(k8sServerConfEntity);
    }



}
