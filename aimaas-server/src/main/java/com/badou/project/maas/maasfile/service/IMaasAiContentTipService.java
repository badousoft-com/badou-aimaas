package com.badou.project.maas.maasfile.service;

import java.io.Serializable;

import com.badou.brms.base.support.spring.IBaseSpringService;
import com.badou.project.maas.maasfile.model.MaasAiContentTipEntity;


/**
 * @author badousoft
 * @date 2025-02-05 17:51:01.635
 *  ai内容生成提示词 service接口
 **/
public interface IMaasAiContentTipService extends IBaseSpringService<MaasAiContentTipEntity, Serializable> {
    /**
     * 根据提示词获得数据类型
     * @param type
     * @return
     */
    String getTipByContent(int type);

}