package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("disease_knowledge")
public class DiseaseKnowledge {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("disease_name")
    private String diseaseName;

    @TableField("symptoms")
    private String symptoms;

    @TableField("causes")
    private String causes;

    @TableField("prevention")
    private String prevention;

    @TableField("treatment")
    private String treatment;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
