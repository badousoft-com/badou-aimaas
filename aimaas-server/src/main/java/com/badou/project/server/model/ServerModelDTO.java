package com.badou.project.server.model;


public class ServerModelDTO {
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * 模型路径
     */
    private String modelPath;

    public ServerModelDTO() {
    }

    public ServerModelDTO(String modelName, String modelPath) {
        this.modelName = modelName;
        this.modelPath = modelPath;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }
}
