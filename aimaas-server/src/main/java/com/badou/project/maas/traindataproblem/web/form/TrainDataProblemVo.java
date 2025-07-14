package com.badou.project.maas.traindataproblem.web.form;

import com.badou.project.maas.problemdata.model.ProblemDataEntity;

import java.util.ArrayList;

public class TrainDataProblemVo {
    private ArrayList<ProblemDataEntity> problemDataEntityList;
    private String trainFileId;

    public ArrayList<ProblemDataEntity> getProblemDataEntityList() {
        return problemDataEntityList;
    }

    public void setProblemDataEntityList(ArrayList<ProblemDataEntity> problemDataEntityList) {
        this.problemDataEntityList = problemDataEntityList;
    }

    public String getTrainFileId() {
        return trainFileId;
    }

    public void setTrainFileId(String trainFileId) {
        this.trainFileId = trainFileId;
    }
}
