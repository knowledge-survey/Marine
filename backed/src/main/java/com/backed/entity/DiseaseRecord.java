package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("disease_record")
public class DiseaseRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("species")
    private String species;

    @TableField("batch_no")
    private String batchNo;

    @TableField("disease_name")
    private String diseaseName;

    @TableField("occurrence_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate occurrenceDate;

    @TableField("symptoms")
    private String symptoms;

    @TableField("treatment")
    private String treatment;

    @TableField("status")
    private DiseaseStatus status = DiseaseStatus.ACTIVE;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public enum DiseaseStatus {
        ACTIVE, RECOVERED
    }
}
