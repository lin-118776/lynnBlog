package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String content;

    private String bizType;

    private Long bizId;

    private Long userId;

    private Long parentId;

    private LocalDateTime createTime;

    @TableField(exist = false)
    private String authorNickname;

    @TableField(exist = false)
    private String authorAvatar;
}