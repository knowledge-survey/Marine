package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("medication_record")
public class MedicationRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("disease_id")
    private Long diseaseId;

    @TableField("medicine_name")
    private String medicineName;

    @TableField("dosage")
    private BigDecimal dosage;

    @TableField("dosage_unit")
    private String dosageUnit;

    @TableField("medication_date")
    private LocalDate medicationDate;

    @TableField("effect")
    private String effect;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
