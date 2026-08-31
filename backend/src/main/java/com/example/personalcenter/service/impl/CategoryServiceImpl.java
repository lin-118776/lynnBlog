package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.entity.Article;
import com.example.personalcenter.entity.Category;
import com.example.personalcenter.mapper.ArticleMapper;
import com.example.personalcenter.mapper.CategoryMapper;
import com.example.personalcenter.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final ArticleMapper articleMapper;

    @Override
    public List<Category> listAll() {
        List<Category> categories = lambdaQuery().orderByAsc(Category::getCreateTime).list();
        // 按 category_id 分组统计文章数（含草稿），填充 articleCount
        Map<Long, Long> countMap = new HashMap<>();
        for (Map<String, Object> row : articleMapper.selectMaps(new QueryWrapper<Article>()
                .select("category_id", "COUNT(*) AS cnt")
                .groupBy("category_id"))) {
            Object cid = row.get("category_id");
            Object cnt = row.get("cnt");
            if (cid != null && cnt != null) {
                countMap.put(((Number) cid).longValue(), ((Number) cnt).longValue());
            }
        }
        categories.forEach(c -> c.setArticleCount(countMap.getOrDefault(c.getId(), 0L)));
        return categories;
    }

    @Override
    public Category create(String name) {
        String trimmed = checkName(name);
        if (lambdaQuery().eq(Category::getName, trimmed).exists()) {
            throw new BusinessException("分类名称已存在");
        }
        Category category = new Category();
        category.setName(trimmed);
        save(category);
        return category;
    }

    @Override
    public Category rename(Long id, String name) {
        Category category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        String trimmed = checkName(name);
        if (lambdaQuery().eq(Category::getName, trimmed).ne(Category::getId, id).exists()) {
            throw new BusinessException("分类名称已存在");
        }
        category.setName(trimmed);
        updateById(category);
        return category;
    }

    @Override
    public void delete(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        Long count = articleMapper.selectCount(new QueryWrapper<Article>().eq("category_id", id));
        if (count != null && count > 0) {
            throw new BusinessException("该分类下有 " + count + " 篇文章，不能删除");
        }
        removeById(id);
    }

    private String checkName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("分类名称不能为空");
        }
        String trimmed = name.trim();
        if (trimmed.length() > 50) {
            throw new BusinessException("分类名称不能超过50个字符");
        }
        return trimmed;
    }
}
