package com.example.personalcenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.interview.InterviewReq;
import com.example.personalcenter.entity.InterviewExperience;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.InterviewExperienceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 面试经验模块：公开/私有混合查询
 */
@Slf4j
@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewExperienceService interviewExperienceService;

    /** 新增面试经验（需登录） */
    @PostMapping
    public Result<Void> create(@RequestBody InterviewReq req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("新增面试经验：userId={}, company={}, position={}", userId, req.getCompanyName(), req.getJobPosition());
        interviewExperienceService.create(req, userId);
        return Result.success();
    }

    /** 更新（校验归属） */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody InterviewReq req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("更新面试经验：id={}, userId={}", id, userId);
        interviewExperienceService.updateOwned(id, req, userId);
        return Result.success();
    }

    /** 删除（校验归属） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("删除面试经验：id={}, userId={}", id, userId);
        interviewExperienceService.deleteOwned(id, userId);
        return Result.success();
    }

    /** 混合分页列表 */
    @GetMapping("/list")
    public Result<IPage<InterviewExperience>> list(@RequestParam(defaultValue = "1") long page,
                                                   @RequestParam(defaultValue = "10") long size,
                                                   @RequestParam(required = false) String keyword,
                                                   HttpServletRequest request) {
        Long userId = getOptionalUserId(request);
        return Result.success(interviewExperienceService.pageList(page, size, keyword, userId));
    }

    /** 详情（公开直接访问，私有校验归属） */
    @GetMapping("/{id}")
    public Result<InterviewExperience> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getOptionalUserId(request);
        return Result.success(interviewExperienceService.getDetail(id, userId));
    }

    /** 获取当前登录用户ID，未登录抛 401 */
    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    /** 获取当前用户ID，未登录返回 null */
    private Long getOptionalUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
    }
}