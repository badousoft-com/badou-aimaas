package com.badou.project.maas.problemdatadetail.dao;

import java.io.Serializable;
import java.util.List;

import com.badou.brms.base.support.hibernate.IBaseHibernateDAO;
import com.badou.brms.base.support.page.model.Pagelet;
import com.badou.project.maas.problemdata.model.ProblemDataEntity;
import com.badou.project.maas.problemdatadetail.model.ProblemDataDetailEntity;

/**
 * @author badousoft
 * @created 2024-05-15 17:37:43.24
 * @todo 样本数据集详情管理dao接口
 */
public interface IProblemDataDetailDAO extends IBaseHibernateDAO<ProblemDataDetailEntity, Serializable> {

    /**
     * 获取指定范围的数据
     * @param id
     * @param range
     * @return
     */
    List<Object> getRangeData(String id, double range);
	
}
