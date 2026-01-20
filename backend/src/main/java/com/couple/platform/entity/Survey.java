package com.couple.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("surveys")
public class Survey {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String description;

    private Long creatorId;

    private Long targetId;

    private String type;

    private String status;

    private Boolean isAnonymous;

    private Boolean allowMultiple;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer maxResponses;

    private Integer currentResponses;

    private String settings;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
