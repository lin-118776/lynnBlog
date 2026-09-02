package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作品集（对应表 project）
 */
@Data
@TableName("project")
public class Project {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 项目名称 */
    private String name;

    /** 项目简介 */
    private String description;

    /** 在线地址 */
    private String url;

    /** GitHub链接 */
    private String githubUrl;

    /** 技术栈（逗号分隔） */
    private String tech;

    /** 封面图URL */
    private String coverImage;

    /** 排序（越小越靠前） */
    private Integer sort;

    /** 1显示 0隐藏 */
    private Integer visible;

    private Long userId;

    private LocalDateTime createTime;
}
