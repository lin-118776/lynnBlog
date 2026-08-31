package com.example.personalcenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.personalcenter.entity.FileRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件记录 Mapper
 */
@Mapper
public interface FileRecordMapper extends BaseMapper<FileRecord> {
}