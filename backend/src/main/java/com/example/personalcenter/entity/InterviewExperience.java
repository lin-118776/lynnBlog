package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 面试经验实体（对应表 interview_experience）
 */
@Data
@TableName("interview_experience")
public class InterviewExperience {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 公司名称 */
    private String companyName;

    /** 应聘岗位 */
    private String jobPosition;

    /** 面试轮次（一面/二面/HR面） */
    private String interviewRound;

    /** 面试问题 */
    private String questionText;

    /** 我的回答 */
    private String myAnswer;

    /** 复盘反思/踩坑点 */
    private String reflection;

    /** 结果：0待定 1通过 2未过 */
    private Integer isSuccess;

    /** 面试日期 */
    private LocalDate interviewDate;

    /** 是否公开：0仅自己 1公开 */
    private Integer isPublic;

    /** 所属用户ID */
    private Long userId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}