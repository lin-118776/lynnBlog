package com.example.personalcenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.personalcenter.entity.Diary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日记 Mapper
 */
@Mapper
public interface DiaryMapper extends BaseMapper<Diary> {
}