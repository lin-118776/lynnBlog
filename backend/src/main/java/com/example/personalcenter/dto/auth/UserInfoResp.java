package com.example.personalcenter.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户信息响应
 */
@Data
@AllArgsConstructor
public class UserInfoResp {

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;
}