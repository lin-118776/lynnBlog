package com.example.personalcenter.service.impl;

import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.dto.github.GithubContributionsResp;
import com.example.personalcenter.service.GithubStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GitHub 贡献数据服务：抓取公开 profile 的 contributions 页面，
 * 解析每个 <rect data-date data-level> 为热力图格子（无官方 JSON 接口，自行实现数据管道）
 *
 * TLS 说明：默认严格证书校验。若本机处于抓包/加速代理（如 Watt）的 MITM 环境下，
 * 通过配置 github.insecure-tls=true（application.yml 或启动参数）对抓取放行自签中间证书（仅本地开发用，生产勿开）。
 */
@Slf4j
@Service
public class GithubStatsServiceImpl implements GithubStatsService {

    /** 匹配任意含贡献格子的 HTML 标签（2026 版 GitHub 由 <rect> 改成了 <td class="ContributionCalendar-day">） */
    private static final Pattern RECT_TAG = Pattern.compile("<[a-zA-Z][^>]*>");
    private static final Pattern DATE_ATTR = Pattern.compile("data-date=\"(\\d{4}-\\d{2}-\\d{2})\"");
    private static final Pattern LEVEL_ATTR = Pattern.compile("data-level=\"([0-4])\"");
    /** 页面文案中的年度总提交数，如 "365 contributions in the last year" */
    private static final Pattern TOTAL_TEXT = Pattern.compile("([\\d,]+)\\s+contributions?\\s+in the last year");
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36";

    private final boolean insecureTls;
    private final HttpClient httpClient;

    public GithubStatsServiceImpl(@Value("${github.insecure-tls:false}") boolean insecureTls) {
        this.insecureTls = insecureTls;
        this.httpClient = buildClient(insecureTls);
    }

    private static HttpClient buildClient(boolean insecure) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .followRedirects(HttpClient.Redirect.NORMAL);
        if (insecure) {
            try {
                TrustManager[] trustAll = { new X509TrustManager() {
                    @Override public void checkClientTrusted(X509Certificate[] c, String a) { }
                    @Override public void checkServerTrusted(X509Certificate[] c, String a) { }
                    @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                } };
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAll, new SecureRandom());
                builder.sslContext(sc);
            } catch (Exception ignored) {
                // 初始化失败则退回严格校验
            }
        }
        return builder.build();
    }

    @Override
    public GithubContributionsResp contributions(String user) {
        String name = (user == null || user.isBlank()) ? "lin-118776" : user.trim();
        String html;
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://github.com/users/" + name + "/contributions"))
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "text/html")
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new BusinessException("GitHub 返回状态码 " + resp.statusCode());
            }
            html = resp.body();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("抓取 GitHub 贡献页失败 (insecureTls={})", insecureTls, e);
            throw new BusinessException("连接 GitHub 失败，请稍后重试");
        }

        List<GithubContributionsResp.Cell> cells = new ArrayList<>(372);
        long total = 0;
        Matcher totalMatcher = TOTAL_TEXT.matcher(html);
        if (totalMatcher.find()) {
            total = Long.parseLong(totalMatcher.group(1).replace(",", ""));
        }
        Matcher rectMatcher = RECT_TAG.matcher(html);
        while (rectMatcher.find()) {
            String tag = rectMatcher.group();
            Matcher dm = DATE_ATTR.matcher(tag);
            if (!dm.find()) continue;
            Matcher lm = LEVEL_ATTR.matcher(tag);
            int level = lm.find() ? Integer.parseInt(lm.group(1)) : 0;
            cells.add(new GithubContributionsResp.Cell(dm.group(1), level));
        }
        if (cells.isEmpty()) {
            throw new BusinessException("GitHub 贡献数据解析失败");
        }
        if (total <= 0) {
            total = cells.stream().filter(c -> c.getL() > 0).count();
        }
        GithubContributionsResp respDto = new GithubContributionsResp();
        respDto.setLogin(name);
        respDto.setTotal(total);
        respDto.setCells(cells);
        return respDto;
    }
}
