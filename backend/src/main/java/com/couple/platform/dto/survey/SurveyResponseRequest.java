package com.couple.platform.dto.survey;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SurveyResponseRequest {

    @NotNull(message = "问卷ID不能为空")
    private Long surveyId;

    @NotBlank(message = "回答数据不能为空")
    private String responseData;

    private Integer completionTime;
}
