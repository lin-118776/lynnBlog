package com.example.personalcenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.entity.Guestbook;

/**
 * 留言板服务接口（公开读取 + 游客留言，无需登录）
 */
public interface GuestbookService extends IService<Guestbook> {

    /** 留言：校验昵称/内容，返回落库记录 */
    Guestbook leave(String nickname, String content, String ip);

    /** 公开分页列表（时间倒序） */
    IPage<Guestbook> pageList(long page, long size);
}
