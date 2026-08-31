package com.example.personalcenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.Result;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.file.UploadResp;
import com.example.personalcenter.entity.FileRecord;
import com.example.personalcenter.interceptor.JwtInterceptor;
import com.example.personalcenter.service.FileRecordService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件模块：上传 / 列表 / 删除（所有接口需登录）
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileRecordService fileRecordService;

    /** 上传文件，存储至本地 ./uploads/年/月/ 目录 */
    @PostMapping("/upload")
    public Result<UploadResp> upload(@RequestParam("file") MultipartFile file,
                                     @RequestParam(value = "bizType", required = false) String bizType,
                                     HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("文件上传：userId={}, fileName={}, size={}", userId, file.getOriginalFilename(), file.getSize());
        return Result.success(fileRecordService.upload(file, bizType, userId));
    }

    /** 分页查询当前用户上传的文件 */
    @GetMapping("/list")
    public Result<IPage<FileRecord>> list(@RequestParam(defaultValue = "1") long page,
                                          @RequestParam(defaultValue = "10") long size,
                                          @RequestParam(value = "bizType", required = false) String bizType,
                                          HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return Result.success(fileRecordService.pageByUser(page, size, bizType, userId));
    }

    /** 删除文件记录（校验归属） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("删除文件记录：id={}, userId={}", id, userId);
        fileRecordService.deleteOwned(id, userId);
        return Result.success();
    }

    /** 从拦截器写入的 request 属性中获取当前用户ID */
    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }
}