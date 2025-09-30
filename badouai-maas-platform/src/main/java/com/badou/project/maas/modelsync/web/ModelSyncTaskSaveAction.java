package com.badou.project.maas.modelsync.web;

import com.badou.brms.base.support.struts.JsonReturnBean;
import com.badou.project.common.webparams.util.JsonResultUtil;
import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.maas.MaasConst;
import com.badou.project.maas.modelsync.model.ModelSyncTaskEntity;
import com.badou.project.maas.modelsync.service.IModelSyncTaskService;
import com.badou.project.maas.modelwarehouse.model.ModelWarehouseEntity;
import com.badou.tools.common.util.StringUtils;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.badou.designer.jdbc.common.web.BaseCommonSaveAction;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author badousoft
 * @created 2025-04-11 10:33:39.208
 *  模型同步任务 保存实现类
 */
@Controller
public class ModelSyncTaskSaveAction extends BaseCommonSaveAction {

    @Autowired
    private IModelSyncTaskService modelSyncTaskService;

    @PostMapping
    public JsonReturnBean startByMsPage(String serverId,String serverName,String modelPath){
        try {
            Object o = modelSyncTaskService.loadModelByModelScope(serverId, serverName, modelPath);
            if (o instanceof String){
                return JsonResultUtil.errorMsg(o.toString());
            }
            ModelWarehouseEntity modelWarehouseEntity = (ModelWarehouseEntity) o;
            return JsonResultUtil.success((modelWarehouseEntity.getId()));
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.error();
        }
    }

    @PostMapping
    public JsonReturnBean closeTask(String id){
        if (StringUtils.isEmpty(id)){
            return JsonResultUtil.errorMsg("请选择要停止的模型同步任务!");
        }
        try {
            ModelSyncTaskEntity modelSyncTaskEntity = modelSyncTaskService.find(id);
            if (modelSyncTaskEntity.getStatus() != MaasConst.DOPLAN_RUN_STATUS){
                return JsonResultUtil.errorMsg("任务未处于处理状态!");
            }
            modelSyncTaskService.closeTask(modelSyncTaskEntity);
        }catch (Exception e){
            e.printStackTrace();
            return JsonResultUtil.errorMsg("停止失败!");
        }
        return JsonResultUtil.success();
    }

}
