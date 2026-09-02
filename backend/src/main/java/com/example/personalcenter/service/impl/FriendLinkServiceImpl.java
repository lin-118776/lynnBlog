package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.entity.FriendLink;
import com.example.personalcenter.mapper.FriendLinkMapper;
import com.example.personalcenter.service.FriendLinkService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FriendLinkServiceImpl extends ServiceImpl<FriendLinkMapper, FriendLink> implements FriendLinkService {

    @Override
    public List<FriendLink> listVisible() {
        return lambdaQuery()
                .eq(FriendLink::getVisible, 1)
                .orderByAsc(FriendLink::getSort)
                .orderByAsc(FriendLink::getId)
                .list();
    }

    @Override
    public List<FriendLink> listAll() {
        return lambdaQuery()
                .orderByDesc(FriendLink::getCreateTime)
                .orderByAsc(FriendLink::getId)
                .list();
    }

    @Override
    public FriendLink apply(FriendLink link) {
        validate(link);
        link.setId(null);
        link.setVisible(0);      // 待审核
        link.setSort(0);
        link.setUserId(null);    // 公开申请无归属
        save(link);
        return link;
    }

    @Override
    public FriendLink create(FriendLink link, Long userId) {
        validate(link);
        link.setId(null);
        if (link.getVisible() == null) {
            link.setVisible(1);
        }
        if (link.getSort() == null) {
            link.setSort(0);
        }
        link.setUserId(userId);
        save(link);
        return link;
    }

    @Override
    public void updateOwned(Long id, FriendLink link, Long userId) {
        FriendLink owned = getOwned(id, userId);
        validate(link);
        owned.setName(link.getName());
        owned.setUrl(link.getUrl());
        owned.setAvatar(link.getAvatar());
        owned.setDescription(link.getDescription());
        owned.setSort(link.getSort() == null ? 0 : link.getSort());
        owned.setVisible(link.getVisible() == null ? 1 : link.getVisible());
        updateById(owned);
    }

    @Override
    public void deleteOwned(Long id, Long userId) {
        getOwned(id, userId);
        removeById(id);
    }

    private void validate(FriendLink link) {
        if (!StringUtils.hasText(link.getName())) {
            throw new BusinessException("站点名不能为空");
        }
        if (!StringUtils.hasText(link.getUrl())) {
            throw new BusinessException("友链地址不能为空");
        }
        link.setName(link.getName().trim());
        link.setUrl(link.getUrl().trim());
    }

    private FriendLink getOwned(Long id, Long userId) {
        FriendLink link = getById(id);
        if (link == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!link.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return link;
    }
}
