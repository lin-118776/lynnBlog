package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件记录实体（对应表 file_record，只存元数据）
 */
@Data
@TableName("file_record")
public class FileRecord {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 原始文件名 */
    private String originalName;

    /** 访问URL */
    private String fileUrl;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件类型（如 image/png） */
    private String fileType;

    /** 关联业务（avatar/article_cover/diary_img/lolita_img 等） */
    private String bizType;

    /** 关联业务主键ID */
    private Long bizId;

    /** 上传者ID */
    private Long userId;

    /** 创建时间 */
    private LocalDateTime createTime;
}