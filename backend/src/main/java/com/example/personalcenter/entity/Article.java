package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 技术文章实体（对应表 article）
 */
@Data
@TableName("article")
public class Article {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 摘要 */
    private String summary;

    /** Markdown 内容 */
    private String content;

    /** 封面图URL */
    private String coverImage;

    /** 浏览量 */
    private Integer viewCount;

    /** 点赞数 */
    private Integer likeCount;

    /** 分类ID */
    private Long categoryId;

    /** 标签（英文逗号分隔，如：AI,折腾,教程） */
    private String tags;

    /** 作者ID */
    private Long userId;

    @TableField(exist = false)
    private String authorNickname;

    @TableField(exist = false)
    private String authorAvatar;

    @TableField(exist = false)
    private String categoryName;

    /** 状态：0草稿 1已发布 */
    private Integer status;

    /** 当前用户是否已点赞（非表字段，详情接口填充） */
    @TableField(exist = false)
    private Boolean isLiked;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}