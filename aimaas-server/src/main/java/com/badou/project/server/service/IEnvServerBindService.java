package com.badou.project.server.service;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.server.model.EnvServerBindEntity;
import com.badou.project.server.model.K8sServerConfEntity;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author lm
 * @Description
 * @Date 2022/12/3 0001 2:38
 * @Version 1.0
 */
public interface IEnvServerBindService extends IBaseSpringService<EnvServerBindEntity, Serializable> {

    //获取环境所属的服务器信息
    Map<String, Object> getEnvServers(int pageIndex, int pageSize, String projectId, String envId);

    K8sServerConfEntity getOneEnvServer(String projectId, String envId);

}
