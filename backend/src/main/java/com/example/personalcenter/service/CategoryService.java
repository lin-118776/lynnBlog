package com.example.personalcenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /** 全部分类（按创建时间升序，含文章数统计） */
    List<Category> listAll();

    /** 新建分类（名称唯一校验） */
    Category create(String name);

    /** 重命名分类（名称唯一校验） */
    Category rename(Long id, String name);

    /** 删除分类（分类下有文章时禁止删除） */
    void delete(Long id);
}
