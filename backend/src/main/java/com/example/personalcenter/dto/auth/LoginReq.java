package com.example.personalcenter.dto.auth;

import lombok.Data;

/**
 * 登录请求
 */
@Data
public class LoginReq {

    /** 用户名 */
    private String username;

    /** 密码（明文） */
    private String password;
}