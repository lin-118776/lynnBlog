package com.example.personalcenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.entity.Comment;

public interface CommentService extends IService<Comment> {

    IPage<Comment> pageByArticle(long page, long size, Long articleId);

    Comment addArticleComment(Long articleId, String content, Long userId);

    void deleteOwned(Long id, Long userId);
}