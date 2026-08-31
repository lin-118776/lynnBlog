package com.example.personalcenter.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码工具类：BCrypt 加密与校验
 */
@Component
public class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * BCrypt 加密明文密码
     */
    public String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 校验明文密码与加密密码是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}