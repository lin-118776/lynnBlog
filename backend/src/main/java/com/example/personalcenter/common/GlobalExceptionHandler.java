package com.example.personalcenter.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理器：统一捕获异常并转换为 Result 响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：直接返回业务状态码与提示 */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：code={}, msg={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /** 缺少请求参数 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数：{}", e.getParameterName());
        return Result.error(ResultCode.BAD_REQUEST.getCode(), "缺少参数：" + e.getParameterName());
    }

    /** 请求体无法解析（如 JSON 格式错误） */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体格式错误：{}", e.getMessage());
        return Result.error(ResultCode.BAD_REQUEST.getCode(), "请求体格式错误");
    }

    /** 参数类型不匹配 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型错误：{}", e.getName());
        return Result.error(ResultCode.BAD_REQUEST.getCode(), "参数类型错误：" + e.getName());
    }

    /** 兜底异常：记录完整堆栈，避免泄露细节 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.ERROR);
    }
}