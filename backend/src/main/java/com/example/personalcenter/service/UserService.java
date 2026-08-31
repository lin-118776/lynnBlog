package com.example.personalcenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.dto.auth.LoginReq;
import com.example.personalcenter.dto.auth.LoginResp;
import com.example.personalcenter.dto.auth.RegisterReq;
import com.example.personalcenter.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /** 注册新用户 */
    void register(RegisterReq req);

    /** 登录，返回 Token 与用户信息 */
    LoginResp login(LoginReq req);
}