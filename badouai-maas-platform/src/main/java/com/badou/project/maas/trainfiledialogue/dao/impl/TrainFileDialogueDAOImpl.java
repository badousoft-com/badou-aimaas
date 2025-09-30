package com.badou.project.maas.trainfiledialogue.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.badou.project.common.webparams.handler.WebParamHandler;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.badou.brms.base.support.hibernate.BaseHibernateDAO;
import com.badou.project.maas.trainfiledialogue.dao.ITrainFileDialogueDAO;
import com.badou.project.maas.trainfiledialogue.model.TrainFileDialogueEntity;

 
/**
 * @author badousoft
 * @date 2024-05-16 14:39:38.124
 * @todo 训练集文件对话dao接口实现类
 **/
@Repository
public class TrainFileDialogueDAOImpl extends BaseHibernateDAO<TrainFileDialogueEntity, Serializable> implements ITrainFileDialogueDAO {

    @Override
    public List<TrainFileDialogueEntity> getListByEvaluation(String evaluationId) {
        String sql = "SELECT\n" +
                "\t:fields \n" +
                "FROM\n" +
                "\tmaas_fine_tuning_evaluation evamain\n" +
                "\tINNER JOIN maas_fine_tuning_evaluation_tfile tfile ON tfile.evaluation_id = evamain.id \n" +
                "\tAND evamain.id = :evaluationId \n" +
                "\tAND evamain.flg_deleted = 0 \n" +
                "\tAND tfile.flg_deleted = 0\n" +
                "\tINNER JOIN maas_train_file trainfile ON trainfile.id = tfile.train_file_id \n" +
                "\tAND trainfile.flg_deleted = 0\n" +
                "\tINNER JOIN maas_train_file_dialogue trainfiled ON trainfiled.train_file_id = trainfile.id \n" +
                "\tAND trainfiled.flg_deleted = 0";
        SQLQuery autoQuery = WebParamHandler.createAutoQuery(sql, TrainFileDialogueEntity.class, getSession(), 0,"trainfiled");
        autoQuery.setString("evaluationId",evaluationId);
        return autoQuery.list();
    }

    @Override
    public int getTotalDataCount(String trainFileId) {
        Object count = getSession().createSQLQuery("select count(1) from maas_train_file_dialogue where train_file_id = ?;").setString(0, trainFileId).list().get(0);
        return Integer.parseInt(count.toString());
    }

    @Override
    public Integer bacthUpdateSystemRole(ArrayList ids, String newSystemRole) {
        SQLQuery sqlQuery = getSession().createSQLQuery("update maas_train_file_dialogue set system =  :newSystemRole where id in (:ids)");
        sqlQuery.setParameterList("ids",ids);
        sqlQuery.setString("newSystemRole",newSystemRole);
        return sqlQuery.executeUpdate();
    }

}