package com.example.personalcenter.interceptor;

import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 登录拦截器：
 * - 请求头携带 Bearer Token 时进行校验，无效或过期返回 401
 * - 未携带 Token 时放行匿名请求（公开接口可用），由各模块 Service 自行判断是否需要登录
 * - 校验通过后将用户ID写入 request 属性 "userId"，供 Controller/Service 使用
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /** request 中存放当前用户ID的属性名 */
    public static final String USER_ID_ATTR = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行跨域预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头获取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 匿名请求：放行，不设置用户ID
            return true;
        }

        String token = authHeader.substring(7);
        if (jwtUtil.validateToken(token)) {
            // 校验通过：写入当前用户ID
            Long userId = jwtUtil.getUserId(token);
            request.setAttribute(USER_ID_ATTR, userId);
            return true;
        }

        // Token 无效或过期：返回 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(ResultCode.UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(result));
        return false;
    }
}