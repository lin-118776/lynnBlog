package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.article.ArticleNeighborsResp;
import com.example.personalcenter.dto.article.ArticleReq;
import com.example.personalcenter.dto.article.LikeResp;
import com.example.personalcenter.entity.Article;
import com.example.personalcenter.entity.ArticleLike;
import com.example.personalcenter.entity.Category;
import com.example.personalcenter.entity.User;
import com.example.personalcenter.mapper.ArticleLikeMapper;
import com.example.personalcenter.mapper.ArticleMapper;
import com.example.personalcenter.mapper.CategoryMapper;
import com.example.personalcenter.mapper.UserMapper;
import com.example.personalcenter.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 文章服务实现
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final ArticleLikeMapper articleLikeMapper;

    @Override
    public void create(ArticleReq req, Long userId) {
        if (!StringUtils.hasText(req.getTitle())) {
            throw new BusinessException("标题不能为空");
        }
        Article article = new Article();
        article.setTitle(req.getTitle());
        article.setSummary(req.getSummary());
        article.setContent(req.getContent());
        article.setCoverImage(req.getCoverImage());
        article.setCategoryId(req.getCategoryId());
        article.setTags(req.getTags());
        article.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        article.setUserId(userId);
        save(article);
    }

    @Override
    public void updateOwned(Long id, ArticleReq req, Long userId) {
        Article article = getOwnedArticle(id, userId);
        article.setTitle(req.getTitle());
        article.setSummary(req.getSummary());
        article.setContent(req.getContent());
        article.setCoverImage(req.getCoverImage());
        article.setCategoryId(req.getCategoryId());
        article.setTags(req.getTags());
        article.setStatus(req.getStatus());
        updateById(article);
    }

    @Override
    public void deleteOwned(Long id, Long userId) {
        getOwnedArticle(id, userId);
        removeById(id);
    }

    @Override
    public IPage<Article> pageList(long page, long size, Long categoryId, String tag, String keyword, String sort, Integer status, Long userId) {
        // 可见性规则：
        //   匿名：仅已发布
        //   作者：status=null 时 已发布 + 自己的草稿；status=0 时 仅自己的草稿；status=1 时 全部已发布
        LambdaQueryChainWrapper<Article> wrapper = lambdaQuery()
                .eq(categoryId != null, Article::getCategoryId, categoryId)
                .apply(StringUtils.hasText(tag), "FIND_IN_SET({0}, tags)", tag)
                .and(StringUtils.hasText(keyword), w -> w.like(Article::getTitle, keyword)
                        .or().like(Article::getSummary, keyword)
                        .or().like(Article::getContent, keyword));
        if (userId == null) {
            wrapper.eq(Article::getStatus, 1);
        } else if (status != null) {
            if (status == 0) {
                wrapper.eq(Article::getStatus, 0).eq(Article::getUserId, userId);
            } else {
                wrapper.eq(Article::getStatus, 1);
            }
        } else {
            wrapper.and(w -> w.eq(Article::getStatus, 1).or().eq(Article::getUserId, userId));
        }
        // 排序：latest 最新 / hot 最热 / liked 点赞最多，默认最新；同值按创建时间倒序
        switch (sort == null ? "latest" : sort) {
            case "hot" -> wrapper.orderByDesc(Article::getViewCount);
            case "liked" -> wrapper.orderByDesc(Article::getLikeCount);
            default -> wrapper.orderByDesc(Article::getCreateTime);
        }
        wrapper.orderByDesc(Article::getCreateTime);
        IPage<Article> result = wrapper.page(new Page<>(page, size));
        result.getRecords().forEach(this::fillArticle);
        return result;
    }

    @Override
    public Article getDetail(Long id, Long userId) {
        Article article = getById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        // 草稿仅作者可见
        if (Objects.equals(article.getStatus(), 0)
                && (userId == null || !article.getUserId().equals(userId))) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        // 浏览量原子 +1
        update(null, new UpdateWrapper<Article>().eq("id", id).setSql("view_count = view_count + 1"));
        article.setViewCount(article.getViewCount() == null ? 1 : article.getViewCount() + 1);
        // 当前用户是否已点赞 + 补全分类名/作者信息
        article.setIsLiked(userId != null && isLiked(id, userId));
        fillArticle(article);
        return article;
    }

    private void fillArticle(Article article) {
        User user = userMapper.selectById(article.getUserId());
        if (user != null) {
            article.setAuthorNickname(user.getNickname());
            article.setAuthorAvatar(user.getAvatar());
        }
        if (article.getCategoryId() != null) {
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                article.setCategoryName(category.getName());
            }
        }
    }

    /** 获取文章并校验作者归属 */
    private Article getOwnedArticle(Long id, Long userId) {
        Article article = getById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!article.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return article;
    }

    @Override
    public LikeResp toggleLike(Long id, Long userId) {
        Article article = getById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        // 已赞则取消，未赞则点赞（UNIQUE(article_id, user_id) 兜底并发）
        Long liked = articleLikeMapper.selectCount(new QueryWrapper<ArticleLike>()
                .eq("article_id", id)
                .eq("user_id", userId));
        boolean nowLiked;
        if (liked != null && liked > 0) {
            // 取消点赞
            articleLikeMapper.delete(new QueryWrapper<ArticleLike>()
                    .eq("article_id", id)
                    .eq("user_id", userId));
            update(null, new UpdateWrapper<Article>()
                    .eq("id", id)
                    .setSql("like_count = GREATEST(like_count - 1, 0)"));
            nowLiked = false;
        } else {
            ArticleLike like = new ArticleLike();
            like.setArticleId(id);
            like.setUserId(userId);
            try {
                articleLikeMapper.insert(like);
            } catch (DuplicateKeyException e) {
                // 并发重复插入：视为已赞，仅同步计数
                nowLiked = true;
                int count = getById(id).getLikeCount() == null ? 0 : getById(id).getLikeCount();
                return new LikeResp(true, count);
            }
            update(null, new UpdateWrapper<Article>().eq("id", id).setSql("like_count = like_count + 1"));
            nowLiked = true;
        }
        int count = getById(id).getLikeCount() == null ? 0 : getById(id).getLikeCount();
        return new LikeResp(nowLiked, count);
    }

    /** 判断用户是否已点赞某篇文章 */
    private boolean isLiked(Long articleId, Long userId) {
        Long liked = articleLikeMapper.selectCount(new QueryWrapper<ArticleLike>()
                .eq("article_id", articleId)
                .eq("user_id", userId));
        return liked != null && liked > 0;
    }

    @Override
    public ArticleNeighborsResp getNeighbors(Long id) {
        Article current = getById(id);
        if (current == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        ArticleNeighborsResp resp = new ArticleNeighborsResp();
        Article prev = lambdaQuery()
                .eq(Article::getStatus, 1)
                .lt(Article::getId, id)
                .orderByDesc(Article::getId)
                .last("limit 1")
                .one();
        Article next = lambdaQuery()
                .eq(Article::getStatus, 1)
                .gt(Article::getId, id)
                .orderByAsc(Article::getId)
                .last("limit 1")
                .one();
        if (prev != null) {
            resp.setPrevId(prev.getId());
            resp.setPrevTitle(prev.getTitle());
        }
        if (next != null) {
            resp.setNextId(next.getId());
            resp.setNextTitle(next.getTitle());
        }
        return resp;
    }
}