package com.example.personalcenter.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 上传响应
 */
@Data
@AllArgsConstructor
public class UploadResp {

    /** 文件访问URL */
    private String url;

    /** 原始文件名 */
    private String originalName;

    /** 文件大小（字节） */
    private Long size;
}