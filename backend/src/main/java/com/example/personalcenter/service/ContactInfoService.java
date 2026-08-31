package com.example.personalcenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.entity.ContactInfo;

import java.util.List;

public interface ContactInfoService extends IService<ContactInfo> {

    /** 查询全部联系方式（公开，访客可见） */
    List<ContactInfo> listAll();

    /** 更新某个联系方式的值（不存在则创建），返回更新后的记录 */
    ContactInfo updateValue(String key, String value);
}
