package com.example.personalcenter.dto.interview;

import lombok.Data;

import java.time.LocalDate;

/**
 * 面试经验新增/更新请求
 */
@Data
public class InterviewReq {

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
}