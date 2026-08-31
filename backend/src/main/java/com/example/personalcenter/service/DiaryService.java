package com.example.personalcenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.dto.diary.DiaryReq;
import com.example.personalcenter.entity.Diary;

/**
 * 日记服务接口（所有操作强制限定当前用户）
 */
public interface DiaryService extends IService<Diary> {

    /** 新增日记，user_id 自动填充为当前用户 */
    void create(DiaryReq req, Long userId);

    /** 校验归属后更新 */
    void updateOwned(Long id, DiaryReq req, Long userId);

    /** 校验归属后删除 */
    void deleteOwned(Long id, Long userId);

    /** 分页查询当前用户所有日记 */
    IPage<Diary> pageByUser(long page, long size, String keyword, Long userId);

    /** 校验归属后返回详情 */
    Diary getOwned(Long id, Long userId);
}