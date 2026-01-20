package com.couple.platform.dto.survey;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SurveyDetailResponse {

    private Long id;

    private String title;

    private String description;

    private Long creatorId;

    private String creatorName;

    private String creatorAvatar;

    private Long targetId;

    private String targetName;

    private String targetAvatar;

    private String type;

    private String status;

    private Boolean isAnonymous;

    private Boolean allowMultiple;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer maxResponses;

    private Integer currentResponses;

    private String settings;

    private List<SurveyQuestionResponse> questions;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
