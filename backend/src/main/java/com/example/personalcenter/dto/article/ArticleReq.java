package com.example.personalcenter.dto.article;

import lombok.Data;

/**
 * 文章新增/更新请求
 */
@Data
public class ArticleReq {

    /** 标题 */
    private String title;

    /** 摘要 */
    private String summary;

    /** Markdown 内容 */
    private String content;

    /** 分类ID */
    private Long categoryId;

    /** 标签（英文逗号分隔，如：AI,折腾,教程） */
    private String tags;

    /** 封面图URL */
    private String coverImage;

    /** 状态：0草稿 1已发布（为空默认已发布） */
    private Integer status;
}