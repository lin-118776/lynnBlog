package com.example.personalcenter.common;

import lombok.Getter;

/**
 * 自定义业务异常：Service 层抛出，由 GlobalExceptionHandler 统一捕获
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 状态码 */
    private final Integer code;

    /** 默认按请求参数错误（400）处理 */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BAD_REQUEST.getCode();
    }

    /** 自定义状态码 */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /** 使用状态码枚举 */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
    }
}