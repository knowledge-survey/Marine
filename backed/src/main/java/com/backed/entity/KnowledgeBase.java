package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("knowledge_base")
public class KnowledgeBase {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("category")
    private String category;

    @TableField("title")
    private String title;

    @TableField("summary")
    private String summary;

    @TableField("content")
    private String content;

    @TableField("tags")
    private String tags;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("is_published")
    private Boolean isPublished;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
