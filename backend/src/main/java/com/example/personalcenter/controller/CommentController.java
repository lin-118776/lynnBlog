package com.example.personalcenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.comment.CommentReq;
import com.example.personalcenter.entity.Comment;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/article/{articleId}")
    public Result<IPage<Comment>> list(@PathVariable Long articleId,
                                       @RequestParam(defaultValue = "1") long page,
                                       @RequestParam(defaultValue = "10") long size) {
        return Result.success(commentService.pageByArticle(page, size, articleId));
    }

    @PostMapping("/article/{articleId}")
    public Result<Comment> add(@PathVariable Long articleId,
                               @RequestBody CommentReq req,
                               HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return Result.success(commentService.addArticleComment(articleId, req.getContent(), userId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        commentService.deleteOwned(id, userId);
        return Result.success();
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }
}