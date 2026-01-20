package com.couple.platform.controller;

import com.couple.platform.dto.survey.*;
import com.couple.platform.entity.SurveyResponse;
import com.couple.platform.service.SurveyService;
import com.couple.platform.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "问卷管理", description = "问卷相关接口")
@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @Operation(summary = "创建问卷")
    @PostMapping
    public ApiResponse<Long> createSurvey(@Valid @RequestBody SurveyCreateRequest request) {
        Long surveyId = surveyService.createSurvey(request);
        return ApiResponse.success("问卷创建成功", surveyId);
    }

    @Operation(summary = "更新问卷")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateSurvey(
            @Parameter(description = "问卷ID") @PathVariable Long id,
            @Valid @RequestBody SurveyUpdateRequest request) {
        surveyService.updateSurvey(id, request);
        return ApiResponse.success("问卷更新成功", null);
    }

    @Operation(summary = "发布问卷")
    @PostMapping("/{id}/publish")
    public ApiResponse<Void> publishSurvey(
            @Parameter(description = "问卷ID") @PathVariable Long id) {
        surveyService.publishSurvey(id);
        return ApiResponse.success("问卷发布成功", null);
    }

    @Operation(summary = "关闭问卷")
    @PostMapping("/{id}/close")
    public ApiResponse<Void> closeSurvey(
            @Parameter(description = "问卷ID") @PathVariable Long id) {
        surveyService.closeSurvey(id);
        return ApiResponse.success("问卷关闭成功", null);
    }

    @Operation(summary = "删除问卷")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSurvey(
            @Parameter(description = "问卷ID") @PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return ApiResponse.success("问卷删除成功", null);
    }

    @Operation(summary = "获取问卷详情")
    @GetMapping("/{id}")
    public ApiResponse<SurveyDetailResponse> getSurveyDetail(
            @Parameter(description = "问卷ID") @PathVariable Long id) {
        SurveyDetailResponse response = surveyService.getSurveyDetail(id);
        return ApiResponse.success(response);
    }

    @Operation(summary = "获取我创建的问卷列表")
    @GetMapping("/created")
    public ApiResponse<List<SurveyDetailResponse>> getMyCreatedSurveys() {
        List<SurveyDetailResponse> surveys = surveyService.getMyCreatedSurveys();
        return ApiResponse.success(surveys);
    }

    @Operation(summary = "获取我的目标问卷列表")
    @GetMapping("/target")
    public ApiResponse<List<SurveyDetailResponse>> getMyTargetSurveys() {
        List<SurveyDetailResponse> surveys = surveyService.getMyTargetSurveys();
        return ApiResponse.success(surveys);
    }

    @Operation(summary = "提交问卷回答")
    @PostMapping("/{id}/responses")
    public ApiResponse<Void> submitSurveyResponse(
            @Parameter(description = "问卷ID") @PathVariable Long id,
            @Valid @RequestBody SurveyResponseRequest request) {
        request.setSurveyId(id);
        surveyService.submitSurveyResponse(request);
        return ApiResponse.success("问卷回答提交成功", null);
    }

    @Operation(summary = "获取问卷回复列表")
    @GetMapping("/{id}/responses")
    public ApiResponse<List<SurveyResponse>> getSurveyResponses(
            @Parameter(description = "问卷ID") @PathVariable Long id) {
        List<SurveyResponse> responses = surveyService.getSurveyResponses(id);
        return ApiResponse.success(responses);
    }
}
