package com.example.personalcenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.entity.Music;

import java.util.List;

public interface MusicService extends IService<Music> {

    /** 全部歌曲（公开，按 sort, id 升序） */
    List<Music> listAll();

    /** 新增歌曲（需登录） */
    Music create(Music music);

    /** 删除歌曲（需登录） */
    void delete(Long id);
}
