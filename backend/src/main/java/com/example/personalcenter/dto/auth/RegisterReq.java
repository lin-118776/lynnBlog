package com.example.personalcenter.dto.auth;

import lombok.Data;

/**
 * 注册请求
 */
@Data
public class RegisterReq {

    /** 用户名 */
    private String username;

    /** 密码（明文，入库前 BCrypt 加密） */
    private String password;

    /** 昵称（必填，用于展示用户名字） */
    private String nickname;
}