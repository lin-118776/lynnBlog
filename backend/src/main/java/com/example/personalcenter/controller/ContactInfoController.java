package com.example.personalcenter.controller;

import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.contact.ContactReq;
import com.example.personalcenter.entity.ContactInfo;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.ContactInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 联系方式接口：公开读取（访客可见），登录后更新（博主维护）
 */
@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactInfoController {

    private final ContactInfoService contactInfoService;

    /** 查询全部联系方式（公开） */
    @GetMapping("/list")
    public Result<List<ContactInfo>> list() {
        return Result.success(contactInfoService.listAll());
    }

    /** 更新某个联系方式（需登录） */
    @PutMapping("/{key}")
    public Result<ContactInfo> update(@PathVariable String key, @RequestBody ContactReq req, HttpServletRequest request) {
        getCurrentUserId(request);
        return Result.success(contactInfoService.updateValue(key, req.getValue()));
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }
}
