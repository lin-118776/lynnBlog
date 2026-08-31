package com.example.personalcenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.personalcenter.entity.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章 Mapper
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}