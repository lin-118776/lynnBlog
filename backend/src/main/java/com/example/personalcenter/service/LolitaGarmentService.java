package com.example.personalcenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.dto.lolita.LolitaReq;
import com.example.personalcenter.entity.LolitaGarment;

/**
 * Lolita 服饰服务接口
 */
public interface LolitaGarmentService extends IService<LolitaGarment> {

    /** 新增 */
    void create(LolitaReq req, Long userId);

    /** 校验归属后更新 */
    void updateOwned(Long id, LolitaReq req, Long userId);

    /** 校验归属后删除 */
    void deleteOwned(Long id, Long userId);

    /** 混合分页：未登录仅公开；已登录公开或自己的（支持 category/status 筛选 + keyword） */
    IPage<LolitaGarment> pageList(long page, long size, String category, String status, String keyword, Long userId);

    /** 详情：公开直接访问，私有校验归属 */
    LolitaGarment getDetail(Long id, Long userId);

    /** 穿着次数原子 +1，返回最新次数 */
    Integer incrementWearCount(Long id, Long userId);
}