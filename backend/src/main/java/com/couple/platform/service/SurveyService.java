package com.couple.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.couple.platform.dto.survey.*;
import com.couple.platform.entity.Survey;
import com.couple.platform.entity.SurveyQuestion;
import com.couple.platform.entity.SurveyResponse;
import com.couple.platform.entity.User;
import com.couple.platform.enums.ErrorCode;
import com.couple.platform.exception.BusinessException;
import com.couple.platform.mapper.SurveyMapper;
import com.couple.platform.mapper.SurveyQuestionMapper;
import com.couple.platform.mapper.SurveyResponseMapper;
import com.couple.platform.mapper.UserMapper;
import com.couple.platform.utils.SecurityUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService extends ServiceImpl<SurveyMapper, Survey> {

    private final SurveyQuestionMapper surveyQuestionMapper;
    private final SurveyResponseMapper surveyResponseMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public Long createSurvey(SurveyCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        Long targetId = request.getTargetId();
        if (targetId.equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能给自己发送问卷");
        }

        Survey survey = new Survey();
        BeanUtils.copyProperties(request, survey);
        survey.setCreatorId(userId);
        survey.setStatus("draft");
        survey.setCurrentResponses(0);

        baseMapper.insert(survey);

        saveSurveyQuestions(survey.getId(), request.getQuestions());

        log.info("用户 {} 创建问卷 {}", userId, survey.getId());
        return survey.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSurvey(Long surveyId, SurveyUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        Survey survey = baseMapper.selectById(surveyId);
        if (survey == null) {
            throw new BusinessException(ErrorCode.SURVEY_NOT_FOUND);
        }

        if (!survey.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限修改此问卷");
        }

        if ("published".equals(survey.getStatus())) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_PUBLISHED);
        }

        BeanUtils.copyProperties(request, survey);
        baseMapper.updateById(survey);

        surveyQuestionMapper.delete(new LambdaQueryWrapper<SurveyQuestion>()
                .eq(SurveyQuestion::getSurveyId, surveyId));

        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            saveSurveyQuestions(surveyId, request.getQuestions());
        }

        log.info("用户 {} 更新问卷 {}", userId, surveyId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void publishSurvey(Long surveyId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Survey survey = baseMapper.selectById(surveyId);
        if (survey == null) {
            throw new BusinessException(ErrorCode.SURVEY_NOT_FOUND);
        }

        if (!survey.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限发布此问卷");
        }

        if ("published".equals(survey.getStatus())) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_PUBLISHED);
        }

        List<SurveyQuestion> questions = surveyQuestionMapper.selectList(
                new LambdaQueryWrapper<SurveyQuestion>()
                        .eq(SurveyQuestion::getSurveyId, surveyId)
        );

        if (questions.isEmpty()) {
            throw new BusinessException(ErrorCode.SURVEY_QUESTION_EMPTY);
        }

        survey.setStatus("published");
        if (survey.getStartTime() == null) {
            survey.setStartTime(LocalDateTime.now());
        }
        baseMapper.updateById(survey);

        log.info("用户 {} 发布问卷 {}", userId, surveyId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void closeSurvey(Long surveyId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Survey survey = baseMapper.selectById(surveyId);
        if (survey == null) {
            throw new BusinessException(ErrorCode.SURVEY_NOT_FOUND);
        }

        if (!survey.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限关闭此问卷");
        }

        if ("closed".equals(survey.getStatus())) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_CLOSED);
        }

        survey.setStatus("closed");
        survey.setEndTime(LocalDateTime.now());
        baseMapper.updateById(survey);

        log.info("用户 {} 关闭问卷 {}", userId, surveyId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteSurvey(Long surveyId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Survey survey = baseMapper.selectById(surveyId);
        if (survey == null) {
            throw new BusinessException(ErrorCode.SURVEY_NOT_FOUND);
        }

        if (!survey.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限删除此问卷");
        }

        surveyQuestionMapper.delete(new LambdaQueryWrapper<SurveyQuestion>()
                .eq(SurveyQuestion::getSurveyId, surveyId));

        surveyResponseMapper.delete(new LambdaQueryWrapper<SurveyResponse>()
                .eq(SurveyResponse::getSurveyId, surveyId));

        baseMapper.deleteById(surveyId);

        log.info("用户 {} 删除问卷 {}", userId, surveyId);
    }

    public SurveyDetailResponse getSurveyDetail(Long surveyId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Survey survey = baseMapper.selectById(surveyId);
        if (survey == null) {
            throw new BusinessException(ErrorCode.SURVEY_NOT_FOUND);
        }

        if (!survey.getCreatorId().equals(userId) && !survey.getTargetId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限查看此问卷");
        }

        SurveyDetailResponse response = new SurveyDetailResponse();
        BeanUtils.copyProperties(survey, response);

        User creator = userMapper.selectById(survey.getCreatorId());
        if (creator != null) {
            response.setCreatorName(creator.getNickname());
            response.setCreatorAvatar(creator.getAvatarUrl());
        }

        User target = userMapper.selectById(survey.getTargetId());
        if (target != null) {
            response.setTargetName(target.getNickname());
            response.setTargetAvatar(target.getAvatarUrl());
        }

        List<SurveyQuestion> questions = surveyQuestionMapper.selectList(
                new LambdaQueryWrapper<SurveyQuestion>()
                        .eq(SurveyQuestion::getSurveyId, surveyId)
                        .orderByAsc(SurveyQuestion::getSortOrder)
        );

        List<SurveyQuestionResponse> questionResponses = questions.stream()
                .map(q -> {
                    SurveyQuestionResponse qr = new SurveyQuestionResponse();
                    BeanUtils.copyProperties(q, qr);
                    return qr;
                })
                .collect(Collectors.toList());

        response.setQuestions(questionResponses);

        return response;
    }

    public List<SurveyDetailResponse> getMyCreatedSurveys() {
        Long userId = SecurityUtil.getCurrentUserId();

        List<Survey> surveys = baseMapper.selectList(
                new LambdaQueryWrapper<Survey>()
                        .eq(Survey::getCreatorId, userId)
                        .orderByDesc(Survey::getCreatedTime)
        );

        return surveys.stream()
                .map(this::convertToDetailResponse)
                .collect(Collectors.toList());
    }

    public List<SurveyDetailResponse> getMyTargetSurveys() {
        Long userId = SecurityUtil.getCurrentUserId();

        List<Survey> surveys = baseMapper.selectList(
                new LambdaQueryWrapper<Survey>()
                        .eq(Survey::getTargetId, userId)
                        .eq(Survey::getStatus, "published")
                        .orderByDesc(Survey::getCreatedTime)
        );

        return surveys.stream()
                .map(this::convertToDetailResponse)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void submitSurveyResponse(SurveyResponseRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();

        Survey survey = baseMapper.selectById(request.getSurveyId());
        if (survey == null) {
            throw new BusinessException(ErrorCode.SURVEY_NOT_FOUND);
        }

        if (!survey.getTargetId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限回答此问卷");
        }

        if (!"published".equals(survey.getStatus())) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_CLOSED, "问卷已关闭，无法回答");
        }

        if (survey.getEndTime() != null && LocalDateTime.now().isAfter(survey.getEndTime())) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_CLOSED, "问卷已过期，无法回答");
        }

        if (survey.getMaxResponses() != null && survey.getCurrentResponses() >= survey.getMaxResponses()) {
            throw new BusinessException(ErrorCode.SURVEY_ALREADY_CLOSED, "问卷回复数已达上限");
        }

        if (!survey.getAllowMultiple()) {
            SurveyResponse existingResponse = surveyResponseMapper.selectOne(
                    new LambdaQueryWrapper<SurveyResponse>()
                            .eq(SurveyResponse::getSurveyId, survey.getId())
                            .eq(SurveyResponse::getRespondentId, userId)
            );
            if (existingResponse != null) {
                throw new BusinessException(ErrorCode.SURVEY_ALREADY_RESPONDED);
            }
        }

        SurveyResponse response = new SurveyResponse();
        response.setSurveyId(survey.getId());
        response.setRespondentId(userId);
        response.setResponseData(request.getResponseData());
        response.setCompletionTime(request.getCompletionTime());
        response.setIsCompleted(true);
        surveyResponseMapper.insert(response);

        survey.setCurrentResponses(survey.getCurrentResponses() + 1);
        baseMapper.updateById(survey);

        log.info("用户 {} 回答问卷 {}", userId, survey.getId());
    }

    public List<SurveyResponse> getSurveyResponses(Long surveyId) {
        Long userId = SecurityUtil.getCurrentUserId();

        Survey survey = baseMapper.selectById(surveyId);
        if (survey == null) {
            throw new BusinessException(ErrorCode.SURVEY_NOT_FOUND);
        }

        if (!survey.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限查看问卷回复");
        }

        return surveyResponseMapper.selectList(
                new LambdaQueryWrapper<SurveyResponse>()
                        .eq(SurveyResponse::getSurveyId, surveyId)
                        .orderByDesc(SurveyResponse::getSubmittedTime)
        );
    }

    private void saveSurveyQuestions(Long surveyId, List<SurveyQuestionRequest> questions) {
        for (int i = 0; i < questions.size(); i++) {
            SurveyQuestion question = new SurveyQuestion();
            BeanUtils.copyProperties(questions.get(i), question);
            question.setSurveyId(surveyId);
            question.setSortOrder(i);
            surveyQuestionMapper.insert(question);
        }
    }

    private SurveyDetailResponse convertToDetailResponse(Survey survey) {
        SurveyDetailResponse response = new SurveyDetailResponse();
        BeanUtils.copyProperties(survey, response);

        User creator = userMapper.selectById(survey.getCreatorId());
        if (creator != null) {
            response.setCreatorName(creator.getNickname());
            response.setCreatorAvatar(creator.getAvatarUrl());
        }

        User target = userMapper.selectById(survey.getTargetId());
        if (target != null) {
            response.setTargetName(target.getNickname());
            response.setTargetAvatar(target.getAvatarUrl());
        }

        return response;
    }
}
