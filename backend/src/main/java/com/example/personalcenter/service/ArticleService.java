package com.example.personalcenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.dto.article.ArticleNeighborsResp;
import com.example.personalcenter.dto.article.ArticleReq;
import com.example.personalcenter.entity.Article;

/**
 * 文章服务接口
 */
public interface ArticleService extends IService<Article> {

    /** 新增文章 */
    void create(ArticleReq req, Long userId);

    /** 校验作者归属后更新文章 */
    void updateOwned(Long id, ArticleReq req, Long userId);

    /** 校验作者归属后删除文章 */
    void deleteOwned(Long id, Long userId);

    /** 公开分页列表：未登录仅已发布；已登录可按 status 筛选（0草稿仅本人 / 1已发布 / null 全部可见）；sort: latest/hot/liked */
    IPage<Article> pageList(long page, long size, Long categoryId, String keyword, String sort, Integer status, Long userId);

    /** 文章详情：浏览量 +1；草稿仅作者可见 */
    Article getDetail(Long id, Long userId);


    /** 点赞（一人一赞，需登录）；返回最新点赞数 */
    int like(Long id, Long userId);

    ArticleNeighborsResp getNeighbors(Long id);
}