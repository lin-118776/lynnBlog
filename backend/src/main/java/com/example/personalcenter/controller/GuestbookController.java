package com.example.personalcenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.entity.Guestbook;
import com.example.personalcenter.service.GuestbookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 留言板：公开读取 + 游客留言（无需登录）
 */
@RestController
@RequestMapping("/api/guestbook")
@RequiredArgsConstructor
public class GuestbookController {

    private final GuestbookService guestbookService;

    /** 公开分页列表（时间倒序） */
    @GetMapping("/list")
    public Result<IPage<Guestbook>> list(@RequestParam(defaultValue = "1") long page,
                                         @RequestParam(defaultValue = "10") long size) {
        return Result.success(guestbookService.pageList(page, size));
    }

    /** 游客留言（公开） */
    @PostMapping
    public Result<Guestbook> leave(@RequestBody Guestbook req, HttpServletRequest request) {
        return Result.success(guestbookService.leave(req.getNickname(), req.getContent(), clientIp(request)));
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
