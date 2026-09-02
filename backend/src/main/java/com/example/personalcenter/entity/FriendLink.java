package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 好友链接（对应表 friend_link）
 */
@Data
@TableName("friend_link")
public class FriendLink {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 站点名 */
    private String name;

    /** 友链地址 */
    private String url;

    /** 头像URL */
    private String avatar;

    /** 一句话介绍 */
    private String description;

    /** 排序（越小越靠前） */
    private Integer sort;

    /** 1显示 0隐藏 */
    private Integer visible;

    private Long userId;

    private LocalDateTime createTime;
}
