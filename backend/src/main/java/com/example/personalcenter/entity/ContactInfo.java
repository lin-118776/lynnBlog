package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 联系方式配置（左侧 Connect 卡片）
 * 公开读取，登录后可更新
 */
@Data
@TableName("contact_info")
public class ContactInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 键：bilibili / github / tiktok / red / qq / mail */
    private String contactKey;

    /** 联系方式内容 */
    private String contactValue;

    private LocalDateTime updateTime;
}
