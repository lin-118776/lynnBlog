package com.example.personalcenter.utils;

import com.example.personalcenter.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 文件存储工具：优先 OSS（图片托管），未启用时回退本地磁盘。
 * 存储路径：{bizType}/{年}/{月}/{UUID}.{ext}
 */
@Component
@RequiredArgsConstructor
public class FileUploadUtil {

    /** 本地文件存储根目录（取自 application.yml） */
    @Value("${upload.path}")
    private String uploadPath;

    private final OssService ossService;

    /**
     * 保存文件，返回可访问的完整 URL：
     * - OSS 启用：https://lynnblog.oss-cn-beijing.aliyuncs.com/article/2026/09/xxx.jpg
     * - 本地回退：/uploads/2026/09/xxx.jpg
     */
    public String save(MultipartFile file, String bizType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        LocalDate now = LocalDate.now();
        String yearMonth = String.format("%d/%02d", now.getYear(), now.getMonthValue());
        String filename = UUID.randomUUID().toString().replace("-", "")
                + (ext != null ? "." + ext : "");
        String type = StringUtils.hasText(bizType) ? bizType : "COMMON";
        // 按用途分顶层文件夹：lolita_img → lolita/，article → article/，其余 → home/
        String folder;
        if ("lolita_img".equalsIgnoreCase(type)) {
            folder = "lolita";
        } else if ("article".equalsIgnoreCase(type)) {
            folder = "article";
        } else {
            folder = "home";
        }
        String objectKey = folder + "/" + yearMonth + "/" + filename;

        // 优先 OSS
        if (ossService.isEnabled()) {
            try (InputStream in = file.getInputStream()) {
                ossService.upload(in, objectKey, file.getContentType());
                return ossService.buildUrl(objectKey);
            } catch (IOException e) {
                throw new RuntimeException("文件读取失败：" + e.getMessage(), e);
            }
        }

        // 本地回退
        Path dir = Paths.get(uploadPath, yearMonth).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(filename).toAbsolutePath().normalize();
            file.transferTo(target);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败：" + e.getMessage(), e);
        }
        return "/uploads/" + yearMonth + "/" + filename;
    }
}
