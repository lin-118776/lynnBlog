package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.entity.Music;
import com.example.personalcenter.mapper.MusicMapper;
import com.example.personalcenter.service.MusicService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {

    @Override
    public List<Music> listAll() {
        return lambdaQuery().orderByAsc(Music::getSort).orderByAsc(Music::getId).list();
    }

    @Override
    public Music create(Music music) {
        if (!StringUtils.hasText(music.getTitle())) {
            throw new BusinessException("歌曲标题不能为空");
        }
        if (!StringUtils.hasText(music.getUrl())) {
            throw new BusinessException("音频地址不能为空");
        }
        music.setTitle(music.getTitle().trim());
        if (!StringUtils.hasText(music.getArtist())) {
            music.setArtist("Lynn");
        }
        if (music.getSort() == null) {
            music.setSort(0);
        }
        music.setId(null);
        save(music);
        return music;
    }

    @Override
    public void delete(Long id) {
        if (getById(id) == null) {
            throw new BusinessException("歌曲不存在");
        }
        removeById(id);
    }
}
