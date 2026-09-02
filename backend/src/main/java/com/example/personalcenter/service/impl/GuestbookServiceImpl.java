package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.entity.Guestbook;
import com.example.personalcenter.mapper.GuestbookMapper;
import com.example.personalcenter.service.GuestbookService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 留言板服务实现：游客留言（无鉴权）+ 公开分页
 */
@Service
public class GuestbookServiceImpl extends ServiceImpl<GuestbookMapper, Guestbook> implements GuestbookService {

    private static final String DEFAULT_NICKNAME = "匿名旅人";

    @Override
    public Guestbook leave(String nickname, String content, String ip) {
        String nick = nickname == null ? "" : nickname.trim();
        String text = content == null ? "" : content.trim();
        if (!StringUtils.hasText(nick)) {
            nick = DEFAULT_NICKNAME;
        }
        if (nick.length() > 20) {
            throw new BusinessException("昵称最多 20 个字");
        }
        if (!StringUtils.hasText(text)) {
            throw new BusinessException("留言内容不能为空");
        }
        if (text.length() > 300) {
            throw new BusinessException("留言最多 300 个字");
        }
        Guestbook g = new Guestbook();
        g.setNickname(nick);
        g.setContent(text);
        g.setIp(ip);
        save(g);
        return g;
    }

    @Override
    public IPage<Guestbook> pageList(long page, long size) {
        return lambdaQuery()
                .orderByDesc(Guestbook::getCreateTime)
                .orderByDesc(Guestbook::getId)
                .page(new Page<>(page, size));
    }
}
