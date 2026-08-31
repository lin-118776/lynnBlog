package com.example.personalcenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.lolita.LolitaReq;
import com.example.personalcenter.entity.LolitaGarment;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.LolitaGarmentService;
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
 * Lolita 衣橱管理模块：公开/私有混合查询
 */
@Slf4j
@RestController
@RequestMapping("/api/lolita")
@RequiredArgsConstructor
public class LolitaController {

    private final LolitaGarmentService lolitaGarmentService;

    /** 新增（需登录） */
    @PostMapping
    public Result<Void> create(@RequestBody LolitaReq req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("新增 Lolita 服饰：userId={}, name={}", userId, req.getName());
        lolitaGarmentService.create(req, userId);
        return Result.success();
    }

    /** 更新（校验归属） */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody LolitaReq req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("更新 Lolita 服饰：id={}, userId={}", id, userId);
        lolitaGarmentService.updateOwned(id, req, userId);
        return Result.success();
    }

    /** 删除（校验归属） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("删除 Lolita 服饰：id={}, userId={}", id, userId);
        lolitaGarmentService.deleteOwned(id, userId);
        return Result.success();
    }

    /** 混合分页列表（category/status 筛选 + keyword 搜索） */
    @GetMapping("/list")
    public Result<IPage<LolitaGarment>> list(@RequestParam(defaultValue = "1") long page,
                                             @RequestParam(defaultValue = "10") long size,
                                             @RequestParam(required = false) String category,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(required = false) String keyword,
                                             HttpServletRequest request) {
        Long userId = getOptionalUserId(request);
        return Result.success(lolitaGarmentService.pageList(page, size, category, status, keyword, userId));
    }

    /** 详情（公开直接访问，私有校验归属） */
    @GetMapping("/{id}")
    public Result<LolitaGarment> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getOptionalUserId(request);
        return Result.success(lolitaGarmentService.getDetail(id, userId));
    }

    /** 穿着次数原子 +1（校验归属），返回最新次数 */
    @PutMapping("/wear/{id}")
    public Result<Integer> wear(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("Lolita 穿着次数 +1：id={}, userId={}", id, userId);
        return Result.success(lolitaGarmentService.incrementWearCount(id, userId));
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