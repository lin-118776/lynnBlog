package com.example.personalcenter.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应
 */
@Data
@AllArgsConstructor
public class LoginResp {

    /** JWT Token */
    private String token;

    /** 用户信息 */
    private UserInfoResp userInfo;
}