package com.badou.project.maas.trainfile.web.form;

import com.alibaba.fastjson.JSONArray;
import com.badou.project.maas.trainfile.model.TrainFileEntity;

import java.util.ArrayList;
import java.util.List;

public class TrainFileQustionForm {

    private String id;
    private JSONArray trainFileEntitys;

    public TrainFileQustionForm() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONArray getTrainFileEntitys() {
        return trainFileEntitys;
    }

    public void setTrainFileEntitys(JSONArray trainFileEntitys) {
        this.trainFileEntitys = trainFileEntitys;
    }
}
