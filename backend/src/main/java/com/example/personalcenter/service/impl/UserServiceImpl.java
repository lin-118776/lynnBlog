package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.auth.LoginReq;
import com.example.personalcenter.dto.auth.LoginResp;
import com.example.personalcenter.dto.auth.RegisterReq;
import com.example.personalcenter.dto.auth.UserInfoResp;
import com.example.personalcenter.entity.User;
import com.example.personalcenter.mapper.UserMapper;
import com.example.personalcenter.service.UserService;
import com.example.personalcenter.utils.JwtUtil;
import com.example.personalcenter.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterReq req) {
        // 参数基本校验
        if (!StringUtils.hasText(req.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(req.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        if (!StringUtils.hasText(req.getNickname())) {
            throw new BusinessException("昵称不能为空");
        }

        // 用户名唯一性校验
        long count = lambdaQuery().eq(User::getUsername, req.getUsername()).count();
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 组装并保存用户（昵称必填，直接使用用户填写的名字）
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordUtil.encode(req.getPassword()));
        user.setNickname(req.getNickname().trim());
        save(user);
    }

    @Override
    public LoginResp login(LoginReq req) {
        // 按用户名查询用户
        User user = lambdaQuery().eq(User::getUsername, req.getUsername()).one();
        // 用户不存在或密码不匹配，统一提示，避免暴露账号是否存在
        if (user == null || !passwordUtil.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }

        // 生成 Token 并组装响应
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        UserInfoResp userInfo = new UserInfoResp(user.getId(), user.getUsername(), user.getNickname(), user.getAvatar());
        return new LoginResp(token, userInfo);
    }
}