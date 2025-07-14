package com.badou.project.maas.mongo.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Data
@Document(collection = "train_data")
public class TrainData {
    @Id
    private String id;
    private String title;
    private String trainContent;
    private String tag;
    private String version;
    private Integer sourceType;
    private String sourceDesc;
    private Date createTime;
    private String creator;
    private String creatorName;

}
