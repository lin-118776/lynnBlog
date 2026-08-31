package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.entity.Comment;
import com.example.personalcenter.entity.User;
import com.example.personalcenter.mapper.CommentMapper;
import com.example.personalcenter.mapper.UserMapper;
import com.example.personalcenter.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final UserMapper userMapper;

    @Override
    public IPage<Comment> pageByArticle(long page, long size, Long articleId) {
        IPage<Comment> result = lambdaQuery()
                .eq(Comment::getBizType, "ARTICLE")
                .eq(Comment::getBizId, articleId)
                .orderByAsc(Comment::getCreateTime)
                .page(new Page<>(page, size));
        result.getRecords().forEach(this::fillAuthor);
        return result;
    }

    @Override
    public Comment addArticleComment(Long articleId, String content, Long userId) {
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("评论内容不能为空");
        }
        Comment comment = new Comment();
        comment.setBizType("ARTICLE");
        comment.setBizId(articleId);
        comment.setContent(content.trim());
        comment.setUserId(userId);
        comment.setParentId(0L);
        save(comment);
        fillAuthor(comment);
        return comment;
    }

    @Override
    public void deleteOwned(Long id, Long userId) {
        Comment comment = getById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该评论");
        }
        removeById(id);
    }

    private void fillAuthor(Comment comment) {
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            comment.setAuthorNickname(user.getNickname());
            comment.setAuthorAvatar(user.getAvatar());
        }
    }
}