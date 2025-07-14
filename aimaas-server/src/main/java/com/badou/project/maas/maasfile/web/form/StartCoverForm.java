package com.badou.project.maas.maasfile.web.form;

import com.badou.project.maas.maasfile.model.MaasTreeFileEntity;

import java.util.List;

public class StartCoverForm {
    //本次转换的类型
    private int type;
    //本次需要转换的数据
    List<MaasTreeFileEntity> data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<MaasTreeFileEntity> getData() {
        return data;
    }

    public void setData(List<MaasTreeFileEntity> data) {
        this.data = data;
    }
}
