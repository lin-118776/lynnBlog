package com.example.personalcenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.personalcenter.entity.Guestbook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言板 Mapper
 */
@Mapper
public interface GuestbookMapper extends BaseMapper<Guestbook> {
}
