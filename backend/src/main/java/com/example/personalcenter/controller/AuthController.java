package com.example.personalcenter.controller;

import com.example.personalcenter.common.Result;
import com.example.personalcenter.dto.auth.LoginReq;
import com.example.personalcenter.dto.auth.LoginResp;
import com.example.personalcenter.dto.auth.RegisterReq;
import com.example.personalcenter.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证模块：注册 / 登录
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /** 注册 */
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterReq req) {
        log.info("用户注册：username={}", req.getUsername());
        userService.register(req);
        return Result.success("注册成功");
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<LoginResp> login(@RequestBody LoginReq req) {
        log.info("用户登录：username={}", req.getUsername());
        LoginResp resp = userService.login(req);
        return Result.success(resp);
    }
}