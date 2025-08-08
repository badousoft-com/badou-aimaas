package com.badou.project.maas.trainfile.dao.impl;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.trainfile.dao.ITrainMultiVideoDAO;
import com.badou.project.maas.trainfile.model.TrainMultiVideoEntity;


/**
 * @author badousoft
 * @date 2025-03-26 15:26:00.791
 *  多模态视频训练文件管理dao接口实现类
 **/
@Repository
public class TrainMultiVideoDAOImpl extends BaseHibernateDAO<TrainMultiVideoEntity, Serializable> implements ITrainMultiVideoDAO {

}