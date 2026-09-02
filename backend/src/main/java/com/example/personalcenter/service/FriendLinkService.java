package com.example.personalcenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.entity.FriendLink;

import java.util.List;

public interface FriendLinkService extends IService<FriendLink> {

    /** 公开友链列表（仅启用，按 sort, id 升序） */
    List<FriendLink> listVisible();

    /** 管理端全部列表（含隐藏/待审核，按创建时间倒序，新申请在前） */
    List<FriendLink> listAll();

    /** 访客提交友链申请（无需登录，visible=0 待审核） */
    FriendLink apply(FriendLink link);

    /** 新增（需登录） */
    FriendLink create(FriendLink link, Long userId);

    /** 更新（校验归属） */
    void updateOwned(Long id, FriendLink link, Long userId);

    /** 删除（校验归属） */
    void deleteOwned(Long id, Long userId);
}
