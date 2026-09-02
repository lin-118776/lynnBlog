package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言板实体（对应表 guestbook，游客可直接留言）
 */
@Data
@TableName("guestbook")
public class Guestbook {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 昵称（游客自填，空则由服务端兜底为「匿名旅人」） */
    private String nickname;

    /** 留言内容 */
    private String content;

    /** 留言IP（防滥用记录，不对外展示） */
    private String ip;

    /** 创建时间 */
    private LocalDateTime createTime;
}
