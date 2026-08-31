package com.example.personalcenter.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "success"),

    /** 请求参数错误 */
    BAD_REQUEST(400, "请求参数错误"),

    /** 未登录或 Token 已过期 */
    UNAUTHORIZED(401, "未登录或登录已过期"),

    /** 无权限操作 */
    FORBIDDEN(403, "无权访问该资源"),

    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),

    /** 服务器内部错误 */
    ERROR(500, "服务器内部错误");

    /** 状态码 */
    private final Integer code;

    /** 提示信息 */
    private final String msg;
}