package com.example.personalcenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.article.ArticleNeighborsResp;
import com.example.personalcenter.dto.article.ArticleReq;
import com.example.personalcenter.entity.Article;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.ArticleService;
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
 * 技术文章模块：列表/详情公开，写操作需登录且校验作者
 */
@Slf4j
@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /** 新增文章（需登录） */
    @PostMapping
    public Result<Void> create(@RequestBody ArticleReq req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("新增文章：userId={}, title={}", userId, req.getTitle());
        articleService.create(req, userId);
        return Result.success();
    }

    /** 更新文章（校验作者） */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ArticleReq req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("更新文章：id={}, userId={}", id, userId);
        articleService.updateOwned(id, req, userId);
        return Result.success();
    }

    /** 删除文章（校验作者） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("删除文章：id={}, userId={}", id, userId);
        articleService.deleteOwned(id, userId);
        return Result.success();
    }

    /** 公开分页列表：支持分类筛选、关键字搜索、排序（latest 最新 / hot 最热 / liked 点赞最多）、登录后按状态筛选 */
    @GetMapping("/list")
    public Result<IPage<Article>> list(@RequestParam(defaultValue = "1") long page,
                                       @RequestParam(defaultValue = "10") long size,
                                       @RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(defaultValue = "latest") String sort,
                                       @RequestParam(required = false) Integer status,
                                       HttpServletRequest request) {
        Long userId = getOptionalUserId(request);
        return Result.success(articleService.pageList(page, size, categoryId, keyword, sort, status, userId));
    }

    /** 公开详情（浏览量 +1） */
    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getOptionalUserId(request);
        return Result.success(articleService.getDetail(id, userId));
    }


    /** 点赞（一人一赞，需登录） */
    @PostMapping("/{id}/like")
    public Result<Integer> like(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return Result.success(articleService.like(id, userId));
    }

    @GetMapping("/neighbors/{id}")
    public Result<ArticleNeighborsResp> neighbors(@PathVariable Long id) {
        return Result.success(articleService.getNeighbors(id));
    }

    /** 获取当前登录用户ID，未登录抛 401 */
    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    /** 获取当前用户ID，未登录返回 null（公开接口用） */
    private Long getOptionalUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
    }
}