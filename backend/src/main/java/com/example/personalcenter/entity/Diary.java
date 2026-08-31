package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 私密日记实体（对应表 diary，绝对私有）
 */
@Data
@TableName("diary")
public class Diary {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 内容（支持 Markdown） */
    private String content;

    /** 心情（开心/平静/emo/奋斗） */
    private String mood;

    /** 天气（晴/雨/阴/雪） */
    private String weather;

    /** 日记日期 */
    private LocalDate diaryDate;

    /** 所属用户ID */
    private Long userId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}