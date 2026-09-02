package com.example.personalcenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 首页播放器歌曲（对应表 music）
 */
@Data
@TableName("music")
public class Music {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 歌曲标题 */
    private String title;

    /** 艺术家 */
    private String artist;

    /** 音频URL（本地 /uploads/... 或后续 OSS） */
    private String url;

    /** 封面URL */
    private String coverUrl;

    /** 排序（越小越靠前） */
    private Integer sort;

    private LocalDateTime createTime;
}
