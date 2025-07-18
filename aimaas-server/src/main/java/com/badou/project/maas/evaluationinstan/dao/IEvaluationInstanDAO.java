package com.badou.project.maas.evaluationinstan.dao;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.evaluationinstan.model.EvaluationInstanEntity;

/**
 * @author badousoft
 * @created 2024-06-06 15:58:38.064
 * @todo 模型评价实例dao接口
 */
public interface IEvaluationInstanDAO extends IBaseHibernateDAO<EvaluationInstanEntity, Serializable> {
    /**
     * 计算本次评价任务需要评价的总数
     * @param id 主键
     * @return 总数
     */
    int calcExecTotalCount(String id);

    int getExecCount(String id);

    void startUpdate(EvaluationInstanEntity evaluationInstanEntity);

    /***
     * 创建对象
     * @param o
     */
    void createObj(EvaluationInstanEntity o);

    /**
     * 计算完成的状态 比如最大最小平均
     * @param id 主键
     * @return
     */
    JSONObject calcFinishStatus(String id);
}
