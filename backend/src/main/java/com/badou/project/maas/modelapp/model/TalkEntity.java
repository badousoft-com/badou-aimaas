package com.badou.project.maas.modelapp.model;

import cn.hutool.core.io.FileUtil;
import com.badou.project.maas.modelapp.web.ModelAppListAction;

import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class TalkEntity {
    private String id;
    private String content;
    private String maxTokens;
    private ArrayList<TalkMsgEntity> nowTalks;
    //训练结果主键
    private String trainModelId;

    public TalkEntity() {
    }

    public String getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(String maxTokens) {
        this.maxTokens = maxTokens;
    }

    public String getTrainModelId() {
        return trainModelId;
    }

    public void setTrainModelId(String trainModelId) {
        this.trainModelId = trainModelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<TalkMsgEntity> getNowTalks() {
        return nowTalks;
    }

    public void setNowTalks(ArrayList<TalkMsgEntity> nowTalks) {
        this.nowTalks = nowTalks;
    }
}
