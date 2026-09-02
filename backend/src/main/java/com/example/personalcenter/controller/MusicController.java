package com.example.personalcenter.controller;

import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.entity.Music;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.MusicService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 首页播放器歌曲接口：公开读取，登录后新增/删除
 */
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    /** 歌曲列表（公开） */
    @GetMapping("/list")
    public Result<List<Music>> list() {
        return Result.success(musicService.listAll());
    }

    /** 新增歌曲（需登录） */
    @PostMapping
    public Result<Music> create(@RequestBody Music req, HttpServletRequest request) {
        getCurrentUserId(request);
        return Result.success(musicService.create(req));
    }

    /** 删除歌曲（需登录） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        getCurrentUserId(request);
        musicService.delete(id);
        return Result.success();
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }
}
