package com.couple.platform.dto.survey;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class SurveyQuestionRequest {

    @NotBlank(message = "问题内容不能为空")
    private String questionText;

    @NotBlank(message = "问题类型不能为空")
    private String questionType;

    private Boolean isRequired;

    private Integer sortOrder;

    private String options;

    private String validationRules;

    private String description;

    private String imageUrl;
}
