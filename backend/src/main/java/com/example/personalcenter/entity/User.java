package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体（对应表 user）
 */
@Data
@TableName("`user`")
public class User {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名（唯一） */
    private String username;

    /** 密码（BCrypt 加密存储） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 邮箱 */
    private String email;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}