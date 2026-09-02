package com.example.personalcenter.dto.github;

import lombok.Data;

import java.util.List;

/**
 * GitHub 贡献热力图响应：login + 每日格子（按日期升序）
 */
@Data
public class GithubContributionsResp {

    /** GitHub 用户名 */
    private String login;

    /** 过去一年贡献总数（页面文案提取，无则按活跃天数） */
    private long total;

    /** 每日格子：d=日期(yyyy-MM-dd) l=贡献级别 0~4 */
    private List<Cell> cells;

    @Data
    public static class Cell {
        private String d;
        private int l;

        public Cell(String d, int l) {
            this.d = d;
            this.l = l;
        }
    }
}
