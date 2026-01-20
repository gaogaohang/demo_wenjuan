package com.couple.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("survey_questions")
public class SurveyQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long surveyId;

    private String questionText;

    private String questionType;

    private Boolean isRequired;

    private Integer sortOrder;

    private String options;

    private String validationRules;

    private String description;

    private String imageUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
