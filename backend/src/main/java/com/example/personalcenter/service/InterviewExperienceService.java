package com.example.personalcenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.dto.interview.InterviewReq;
import com.example.personalcenter.entity.InterviewExperience;

/**
 * 面试经验服务接口
 */
public interface InterviewExperienceService extends IService<InterviewExperience> {

    /** 新增面试经验 */
    void create(InterviewReq req, Long userId);

    /** 校验归属后更新 */
    void updateOwned(Long id, InterviewReq req, Long userId);

    /** 校验归属后删除 */
    void deleteOwned(Long id, Long userId);

    /** 混合分页：未登录仅公开；已登录公开或自己的 */
    IPage<InterviewExperience> pageList(long page, long size, String keyword, Long userId);

    /** 详情：公开直接访问，私有必须校验归属 */
    InterviewExperience getDetail(Long id, Long userId);
}