package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.diary.DiaryReq;
import com.example.personalcenter.entity.Diary;
import com.example.personalcenter.mapper.DiaryMapper;
import com.example.personalcenter.service.DiaryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 日记服务实现
 */
@Service
public class DiaryServiceImpl extends ServiceImpl<DiaryMapper, Diary> implements DiaryService {

    @Override
    public void create(DiaryReq req, Long userId) {
        if (!StringUtils.hasText(req.getTitle())) {
            throw new BusinessException("标题不能为空");
        }
        if (req.getDiaryDate() == null) {
            throw new BusinessException("日记日期不能为空");
        }
        Diary diary = new Diary();
        diary.setTitle(req.getTitle());
        diary.setContent(req.getContent());
        diary.setMood(req.getMood());
        diary.setWeather(req.getWeather());
        diary.setDiaryDate(req.getDiaryDate());
        diary.setUserId(userId);
        save(diary);
    }

    @Override
    public void updateOwned(Long id, DiaryReq req, Long userId) {
        Diary diary = getOwned(id, userId);
        diary.setTitle(req.getTitle());
        diary.setContent(req.getContent());
        diary.setMood(req.getMood());
        diary.setWeather(req.getWeather());
        diary.setDiaryDate(req.getDiaryDate());
        updateById(diary);
    }

    @Override
    public void deleteOwned(Long id, Long userId) {
        getOwned(id, userId);
        removeById(id);
    }

    @Override
    public IPage<Diary> pageByUser(long page, long size, String keyword, Long userId) {
        return lambdaQuery()
                .eq(Diary::getUserId, userId)
                .and(StringUtils.hasText(keyword), w -> w.like(Diary::getTitle, keyword)
                        .or().like(Diary::getContent, keyword))
                .orderByDesc(Diary::getDiaryDate)
                .page(new Page<>(page, size));
    }

    @Override
    public Diary getOwned(Long id, Long userId) {
        Diary diary = getById(id);
        if (diary == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!diary.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return diary;
    }
}