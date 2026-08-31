package com.example.personalcenter.common;

import lombok.Data;

/**
 * 统一响应结果封装
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    /** 状态码 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 响应数据 */
    private T data;

    /** 私有构造方法，禁止外部直接 new */
    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 成功（无数据） */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    /** 成功（携带数据） */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    /** 成功（自定义提示 + 数据） */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /** 失败（使用状态码枚举） */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null);
    }

    /** 失败（自定义状态码与提示） */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /** 失败（自定义提示，默认 400） */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ResultCode.BAD_REQUEST.getCode(), msg, null);
    }
}