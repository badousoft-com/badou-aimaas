package com.badou.project.kubernetes.handler.impl;

import com.badou.project.kubernetes.client.KubernetesApiClient;
import com.badou.project.kubernetes.handler.KubernetesErrorHandler;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ReplicaSet;
import io.kubernetes.client.openapi.models.V1ReplicaSetCondition;
import io.kubernetes.client.openapi.models.V1ReplicaSetList;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName KubernetesErrorHandlerImpl
 * @Description TODO
 * @date 2023/3/3 9:58
 * @Version 1.0
 */
@Component
public class KubernetesErrorHandlerImpl implements KubernetesErrorHandler {
    @Override
    public String getResetErrorMessage(KubernetesApiClient kubernetesApiClient, String nameSpace, String appCode) throws ApiException {
        V1ReplicaSetList v1ReplicaSetList = kubernetesApiClient.getAppsV1Api().listNamespacedReplicaSet(nameSpace,
                null, null, null, null, "app="+appCode,
                null, null, null, null, null);
        if(v1ReplicaSetList.getItems() != null && v1ReplicaSetList.getItems().size()==1){
            V1ReplicaSet v1ReplicaSet = v1ReplicaSetList.getItems().get(0);
            //状态数据只有一条 可能存在问题了
            List<V1ReplicaSetCondition> conditions = v1ReplicaSet.getStatus().getConditions();
            if(conditions!= null && conditions.size() == 1){
                V1ReplicaSetCondition v1ReplicaSetCondition = conditions.get(0);
                if("FailedCreate".equals(v1ReplicaSetCondition.getReason())){
                    if(v1ReplicaSetCondition.getMessage().contains(nameSpace)){
                        String[] split = v1ReplicaSetCondition.getMessage().split(nameSpace);
                        if(split.length == 2){
                            String msg = split[1];
                            msg = msg.replace(",","");
                            msg = msg.replace("requested","已申请");
                            msg = msg.replace("used","\n 已使用");
                            msg = msg.replace("limited","\n 已限制");
                            msg = msg.replace("limits."," 限制 ");
                            msg = "可申请的资源不足!请变更资源限制大小或使用其他的资源空间\n"+msg;
                            return msg;
                        }
                    } else {
                        return v1ReplicaSetCondition.getMessage();
                    }
                }
            }
        }
        return null;
    }
}
