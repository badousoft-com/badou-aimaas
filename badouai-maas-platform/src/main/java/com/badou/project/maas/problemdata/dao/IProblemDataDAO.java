package com.badou.project.maas.problemdata.dao;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;

/**
 * @author badousoft
 * @created 2024-05-15 17:37:11.964
 * @todo 样本数据集管理dao接口
 */
public interface IProblemDataDAO extends IBaseHibernateDAO<ProblemDataEntity, Serializable> {
    /**
     * 更新最新的样本数据
     * @param id 样本数据导入
     */
    void updateNewestCount(String id);

}
