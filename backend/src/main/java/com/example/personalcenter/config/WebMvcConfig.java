package com.example.personalcenter.config;

import com.example.personalcenter.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Web MVC 配置：注册 JWT 拦截器 + 本地上传目录静态资源映射
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    /** 本地文件存储根目录（取自 application.yml） */
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有 /api/** 请求
                .addPathPatterns("/api/**")
                // 放行认证模块（注册/登录）
                .excludePathPatterns("/api/auth/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/** 映射到本地磁盘目录，使上传文件可直接通过 URL 访问
        String absolutePath = Paths.get(uploadPath).toAbsolutePath().normalize().toString().replace('\\', '/');
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}