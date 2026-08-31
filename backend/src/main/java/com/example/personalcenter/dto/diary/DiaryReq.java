package com.example.personalcenter.dto.diary;

import lombok.Data;

import java.time.LocalDate;

/**
 * 日记新增/更新请求
 */
@Data
public class DiaryReq {

    /** 标题 */
    private String title;

    /** 内容（支持 Markdown） */
    private String content;

    /** 心情 */
    private String mood;

    /** 天气 */
    private String weather;

    /** 日记日期 */
    private LocalDate diaryDate;
}