package com.badou.project.maas.evaluationinstan.model;

public class EvaluationMqEntity {

    private EvaluationInstanqEntity evaluationInstanqEntity;
    private EvaluationInstanEntity evaluationInstanEntity;

    public EvaluationMqEntity() {
    }

    public EvaluationMqEntity(EvaluationInstanqEntity evaluationInstanqEntity, EvaluationInstanEntity evaluationInstanEntity) {
        this.evaluationInstanqEntity = evaluationInstanqEntity;
        this.evaluationInstanEntity = evaluationInstanEntity;
    }

    public EvaluationInstanqEntity getEvaluationInstanqEntity() {
        return evaluationInstanqEntity;
    }

    public void setEvaluationInstanqEntity(EvaluationInstanqEntity evaluationInstanqEntity) {
        this.evaluationInstanqEntity = evaluationInstanqEntity;
    }

    public EvaluationInstanEntity getEvaluationInstanEntity() {
        return evaluationInstanEntity;
    }

    public void setEvaluationInstanEntity(EvaluationInstanEntity evaluationInstanEntity) {
        this.evaluationInstanEntity = evaluationInstanEntity;
    }
}
