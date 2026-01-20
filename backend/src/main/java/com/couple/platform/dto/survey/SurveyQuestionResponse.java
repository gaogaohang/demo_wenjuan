package com.couple.platform.dto.survey;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SurveyQuestionResponse {

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

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
