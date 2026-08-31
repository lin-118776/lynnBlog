package com.example.personalcenter.utils;

import com.example.personalcenter.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 本地文件存储工具：按 年/月 目录存储，UUID 重命名
 */
@Component
public class FileUploadUtil {

    /** 本地文件存储根目录（取自 application.yml） */
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 保存文件到本地磁盘，返回可访问的 URL（如 /uploads/2026/08/xxx.png）
     */
    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 提取原始扩展名（可能为空）
        String originalName = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(originalName);

        // 按 年/月 生成相对目录
        LocalDate now = LocalDate.now();
        String yearMonth = String.format("%d/%02d", now.getYear(), now.getMonthValue());

        // UUID 重命名，避免文件名冲突
        String filename = UUID.randomUUID().toString().replace("-", "")
                + (ext != null ? "." + ext : "");

        // 目标绝对目录，不存在则创建
        Path dir = Paths.get(uploadPath, yearMonth).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(filename).toAbsolutePath().normalize();
            file.transferTo(target);
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败：" + e.getMessage(), e);
        }

        // 返回可通过 /uploads/** 静态映射访问的 URL
        return "/uploads/" + yearMonth + "/" + filename;
    }
}