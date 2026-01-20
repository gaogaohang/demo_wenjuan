package com.couple.platform.dto.survey;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SurveyUpdateRequest {

    @NotBlank(message = "问卷标题不能为空")
    private String title;

    private String description;

    private Long targetId;

    private String type;

    private Boolean isAnonymous;

    private Boolean allowMultiple;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer maxResponses;

    private String settings;

    @Valid
    private List<SurveyQuestionRequest> questions;
}
