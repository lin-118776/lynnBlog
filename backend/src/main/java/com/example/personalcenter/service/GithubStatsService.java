package com.example.personalcenter.service;

import com.example.personalcenter.dto.github.GithubContributionsResp;

/**
 * GitHub 集成服务：抓取并解析公开贡献数据（服务端代理，规避浏览器跨域与网络限制）
 */
public interface GithubStatsService {

    /** 获取用户最近一年的贡献格子（解析 github.com/users/{user}/contributions 页面） */
    GithubContributionsResp contributions(String user);
}
