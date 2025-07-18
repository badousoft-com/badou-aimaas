package com.badou.project.maas.problemdata.web.form;

import com.badou.project.maas.trainfile.model.TrainFileEntity;

import java.util.ArrayList;

public class ExportTrainDataForm {

    private String[] ids;
    private int type;
    private ArrayList<TrainFileEntity> valueList;
    private String fileName;
    private int exportRange ;

    public int getExportRange() {
        return exportRange;
    }

    public void setExportRange(int exportRange) {
        this.exportRange = exportRange;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<TrainFileEntity> getValueList() {
        return valueList;
    }

    public void setValueList(ArrayList<TrainFileEntity> valueList) {
        this.valueList = valueList;
    }
}
