package com.example.personalcenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.dto.file.UploadResp;
import com.example.personalcenter.entity.FileRecord;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件记录服务接口
 */
public interface FileRecordService extends IService<FileRecord> {

    /** 上传文件并保存记录，返回 URL 等元数据 */
    UploadResp upload(MultipartFile file, String bizType, Long userId);

    /** 分页查询当前用户上传的文件 */
    IPage<FileRecord> pageByUser(long page, long size, String bizType, Long userId);

    /** 校验归属后删除文件记录 */
    void deleteOwned(Long id, Long userId);
}