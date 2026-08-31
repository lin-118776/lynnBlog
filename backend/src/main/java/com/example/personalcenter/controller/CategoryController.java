package com.example.personalcenter.controller;

import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.category.CategoryReq;
import com.example.personalcenter.entity.Category;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.CategoryService;
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

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.listAll());
    }

    @PostMapping
    public Result<Category> create(@RequestBody CategoryReq req, HttpServletRequest request) {
        getCurrentUserId(request);
        return Result.success(categoryService.create(req.getName()));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        getCurrentUserId(request);
        categoryService.delete(id);
        return Result.success();
    }

    /** 重命名分类（需登录） */
    @PutMapping("/{id}")
    public Result<Category> rename(@PathVariable Long id, @RequestBody CategoryReq req, HttpServletRequest request) {
        getCurrentUserId(request);
        return Result.success(categoryService.rename(id, req.getName()));
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }
}