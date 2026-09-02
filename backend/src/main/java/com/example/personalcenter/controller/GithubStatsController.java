package com.example.personalcenter.controller;

import com.example.personalcenter.common.Result;
import com.example.personalcenter.dto.github.GithubContributionsResp;
import com.example.personalcenter.service.GithubStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * GitHub 仪表盘：贡献热力图数据（服务端代理抓取 github.com 公开页，规避跨域与网络限制）
 */
@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GithubStatsController {

    private final GithubStatsService githubStatsService;

    /** 获取用户最近一年贡献格子 */
    @GetMapping("/contributions")
    public Result<GithubContributionsResp> contributions(@RequestParam(required = false) String user) {
        return Result.success(githubStatsService.contributions(user));
    }
}
