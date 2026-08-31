package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.lolita.LolitaReq;
import com.example.personalcenter.entity.LolitaGarment;
import com.example.personalcenter.mapper.LolitaGarmentMapper;
import com.example.personalcenter.service.LolitaGarmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Lolita 服饰服务实现
 */
@Service
public class LolitaGarmentServiceImpl extends ServiceImpl<LolitaGarmentMapper, LolitaGarment>
        implements LolitaGarmentService {

    @Override
    public void create(LolitaReq req, Long userId) {
        if (!StringUtils.hasText(req.getName())) {
            throw new BusinessException("服饰名称不能为空");
        }
        if (!StringUtils.hasText(req.getCategory())) {
            throw new BusinessException("分类不能为空");
        }
        LolitaGarment garment = new LolitaGarment();
        applyReq(garment, req);
        garment.setIsPublic(req.getIsPublic() == null ? 0 : req.getIsPublic());
        garment.setUserId(userId);
        save(garment);
    }

    @Override
    public void updateOwned(Long id, LolitaReq req, Long userId) {
        LolitaGarment garment = getOwned(id, userId);
        applyReq(garment, req);
        updateById(garment);
    }

    @Override
    public void deleteOwned(Long id, Long userId) {
        getOwned(id, userId);
        removeById(id);
    }

    @Override
    public IPage<LolitaGarment> pageList(long page, long size, String category, String status, String keyword, Long userId) {
        return lambdaQuery()
                .eq(StringUtils.hasText(category), LolitaGarment::getCategory, category)
                .eq(StringUtils.hasText(status), LolitaGarment::getStatus, status)
                .and(StringUtils.hasText(keyword), w -> w.like(LolitaGarment::getName, keyword)
                        .or().like(LolitaGarment::getBrand, keyword))
                // 已登录：公开 OR 自己的；未登录：仅公开
                .and(w -> w.eq(LolitaGarment::getIsPublic, 1)
                        .or().eq(userId != null, LolitaGarment::getUserId, userId))
                .orderByDesc(LolitaGarment::getCreateTime)
                .page(new Page<>(page, size));
    }

    @Override
    public LolitaGarment getDetail(Long id, Long userId) {
        LolitaGarment garment = getById(id);
        if (garment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        // 私有数据必须校验归属
        if (!Objects.equals(garment.getIsPublic(), 1)
                && (userId == null || !garment.getUserId().equals(userId))) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return garment;
    }

    @Override
    public Integer incrementWearCount(Long id, Long userId) {
        getOwned(id, userId);
        // 穿着次数原子自增
        update(null, new UpdateWrapper<LolitaGarment>().eq("id", id).setSql("wear_count = wear_count + 1"));
        LolitaGarment garment = getById(id);
        return garment == null ? 0 : garment.getWearCount();
    }

    /** 将请求字段拷贝到实体（新增/更新共用） */
    private void applyReq(LolitaGarment garment, LolitaReq req) {
        garment.setName(req.getName());
        garment.setBrand(req.getBrand());
        garment.setSeries(req.getSeries());
        garment.setCategory(req.getCategory());
        garment.setColor(req.getColor());
        garment.setSize(req.getSize());
        garment.setPurchaseDate(req.getPurchaseDate());
        garment.setPurchasePrice(req.getPurchasePrice());
        garment.setStatus(StringUtils.hasText(req.getStatus()) ? req.getStatus() : "现货");
        garment.setLocation(req.getLocation());
        garment.setCoverImage(req.getCoverImage());
        garment.setImages(req.getImages());
        garment.setNote(req.getNote());
        garment.setIsPublic(req.getIsPublic());
    }

    /** 获取服饰并校验归属 */
    private LolitaGarment getOwned(Long id, Long userId) {
        LolitaGarment garment = getById(id);
        if (garment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!garment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return garment;
    }
}