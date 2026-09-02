package com.example.personalcenter.controller;

import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.entity.Project;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 作品集：公开读取，登录后管理
 */
@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /** 公开作品列表 */
    @GetMapping("/list")
    public Result<List<Project>> list() {
        return Result.success(projectService.listVisible());
    }

    /** 新增（需登录） */
    @PostMapping
    public Result<Project> create(@RequestBody Project req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return Result.success(projectService.create(req, userId));
    }

    /** 更新（校验归属） */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Project req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        projectService.updateOwned(id, req, userId);
        return Result.success();
    }

    /** 删除（校验归属） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        projectService.deleteOwned(id, userId);
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
