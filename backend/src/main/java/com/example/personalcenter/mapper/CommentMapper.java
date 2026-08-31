package com.example.personalcenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.personalcenter.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}