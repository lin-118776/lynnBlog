package com.example.personalcenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.diary.DiaryReq;
import com.example.personalcenter.entity.Diary;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.DiaryService;
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
 * 私密日记模块：绝对私有，所有接口需登录
 */
@Slf4j
@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    /** 新增日记 */
    @PostMapping
    public Result<Void> create(@RequestBody DiaryReq req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("新增日记：userId={}, title={}", userId, req.getTitle());
        diaryService.create(req, userId);
        return Result.success();
    }

    /** 更新（校验归属） */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DiaryReq req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("更新日记：id={}, userId={}", id, userId);
        diaryService.updateOwned(id, req, userId);
        return Result.success();
    }

    /** 删除（校验归属） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("删除日记：id={}, userId={}", id, userId);
        diaryService.deleteOwned(id, userId);
        return Result.success();
    }

    /** 分页查询当前用户所有日记 */
    @GetMapping("/list")
    public Result<IPage<Diary>> list(@RequestParam(defaultValue = "1") long page,
                                     @RequestParam(defaultValue = "10") long size,
                                     @RequestParam(required = false) String keyword,
                                     HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return Result.success(diaryService.pageByUser(page, size, keyword, userId));
    }

    /** 详情（校验归属） */
    @GetMapping("/{id}")
    public Result<Diary> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return Result.success(diaryService.getOwned(id, userId));
    }

    /** 获取当前登录用户ID，未登录抛 401 */
    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }
}