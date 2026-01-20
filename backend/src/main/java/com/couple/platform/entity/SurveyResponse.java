package com.couple.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("survey_responses")
public class SurveyResponse {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long surveyId;

    private Long respondentId;

    private String responseData;

    private Integer completionTime;

    private String ipAddress;

    private String userAgent;

    private Boolean isCompleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime submittedTime;
}
