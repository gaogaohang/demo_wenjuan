package com.couple.platform.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "操作成功"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USER_DISABLED(1003, "用户已被禁用"),
    INVALID_CREDENTIALS(1004, "用户名或密码错误"),
    PHONE_ALREADY_REGISTERED(1005, "手机号已注册"),
    PAIR_CODE_INVALID(1006, "配对码无效"),
    USER_ALREADY_PAIRED(1007, "用户已配对"),
    PARTNER_NOT_FOUND(1008, "配对对象不存在"),

    ORDER_NOT_FOUND(2001, "订单不存在"),
    ORDER_STATUS_INVALID(2002, "订单状态无效"),
    ORDER_ALREADY_ACCEPTED(2003, "订单已被接受"),
    ORDER_CANNOT_CANCEL(2004, "订单无法取消"),
    ORDER_ITEM_EMPTY(2005, "订单项不能为空"),

    EVALUATION_ALREADY_EXISTS(3001, "评价已存在"),
    EVALUATION_NOT_FOUND(3002, "评价不存在"),

    SURVEY_NOT_FOUND(4001, "问卷不存在"),
    SURVEY_ALREADY_PUBLISHED(4002, "问卷已发布"),
    SURVEY_ALREADY_CLOSED(4003, "问卷已关闭"),
    SURVEY_QUESTION_EMPTY(4004, "问卷问题不能为空"),
    SURVEY_RESPONSE_EMPTY(4005, "问卷回复不能为空"),
    SURVEY_ALREADY_RESPONDED(4006, "问卷已回复"),
    SURVEY_QUESTION_TYPE_INVALID(4007, "问题类型无效"),

    FILE_UPLOAD_FAILED(5001, "文件上传失败"),
    FILE_SIZE_EXCEEDED(5002, "文件大小超出限制"),
    FILE_TYPE_NOT_SUPPORTED(5003, "文件类型不支持"),
    FILE_NOT_FOUND(5004, "文件不存在"),

    MESSAGE_NOT_FOUND(6001, "消息不存在");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
