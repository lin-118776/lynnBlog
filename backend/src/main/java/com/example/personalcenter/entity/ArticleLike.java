package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章点赞记录（对应表 article_like，一人一赞）
 */
@Data
@TableName("article_like")
public class ArticleLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章ID */
    private Long articleId;

    /** 点赞用户ID */
    private Long userId;

    private LocalDateTime createTime;
}
