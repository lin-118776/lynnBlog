package com.example.personalcenter.controller;

import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.entity.FriendLink;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.FriendLinkService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 好友链接：公开读取，登录后管理
 */
@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendLinkController {

    private final FriendLinkService friendLinkService;

    /** 公开友链列表 */
    @GetMapping("/list")
    public Result<List<FriendLink>> list() {
        return Result.success(friendLinkService.listVisible());
    }

    /** 管理端全部列表（含待审核，需登录） */
    @GetMapping("/all")
    public Result<List<FriendLink>> listAll(HttpServletRequest request) {
        getCurrentUserId(request);
        return Result.success(friendLinkService.listAll());
    }

    /** 访客提交友链申请（无需登录，蜜罐字段 website 非空视为机器人） */
    @PostMapping("/apply")
    public Result<FriendLink> apply(@RequestBody FriendLink req,
                                    @RequestParam(value = "website", required = false) String website) {
        if (StringUtils.hasText(website)) {
            throw new BusinessException("提交失败，请稍后再试");
        }
        return Result.success(friendLinkService.apply(req));
    }

    /** 新增（需登录） */
    @PostMapping
    public Result<FriendLink> create(@RequestBody FriendLink req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return Result.success(friendLinkService.create(req, userId));
    }

    /** 更新（校验归属） */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody FriendLink req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        friendLinkService.updateOwned(id, req, userId);
        return Result.success();
    }

    /** 删除（校验归属） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        friendLinkService.deleteOwned(id, userId);
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
