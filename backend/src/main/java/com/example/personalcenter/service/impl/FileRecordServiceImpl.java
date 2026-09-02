package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.dto.file.UploadResp;
import com.example.personalcenter.entity.FileRecord;
import com.example.personalcenter.mapper.FileRecordMapper;
import com.example.personalcenter.service.FileRecordService;
import com.example.personalcenter.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件记录服务实现
 */
@Service
@RequiredArgsConstructor
public class FileRecordServiceImpl extends ServiceImpl<FileRecordMapper, FileRecord> implements FileRecordService {

    private final FileUploadUtil fileUploadUtil;

    @Override
    public UploadResp upload(MultipartFile file, String bizType, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 保存文件（优先 OSS，回退本地磁盘）
        String url = fileUploadUtil.save(file, bizType);

        // 记录文件元数据
        FileRecord record = new FileRecord();
        record.setOriginalName(file.getOriginalFilename());
        record.setFileUrl(url);
        record.setFileSize(file.getSize());
        record.setFileType(file.getContentType());
        record.setBizType(StringUtils.hasText(bizType) ? bizType : "COMMON");
        record.setUserId(userId);
        save(record);

        return new UploadResp(url, record.getOriginalName(), record.getFileSize());
    }

    @Override
    public IPage<FileRecord> pageByUser(long page, long size, String bizType, Long userId) {
        // 仅查询当前用户的文件，可选按业务类型筛选，按上传时间倒序
        return lambdaQuery()
                .eq(FileRecord::getUserId, userId)
                .eq(StringUtils.hasText(bizType), FileRecord::getBizType, bizType)
                .orderByDesc(FileRecord::getCreateTime)
                .page(new Page<>(page, size));
    }

    @Override
    public void deleteOwned(Long id, Long userId) {
        FileRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("文件记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该文件");
        }
        // 按需求：先只删除记录，物理文件暂不删除
        removeById(id);
    }
}