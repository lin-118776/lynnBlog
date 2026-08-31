package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.entity.ContactInfo;
import com.example.personalcenter.mapper.ContactInfoMapper;
import com.example.personalcenter.service.ContactInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ContactInfoServiceImpl extends ServiceImpl<ContactInfoMapper, ContactInfo> implements ContactInfoService {

    @Override
    public List<ContactInfo> listAll() {
        return lambdaQuery().orderByAsc(ContactInfo::getId).list();
    }

    @Override
    public ContactInfo updateValue(String key, String value) {
        if (!StringUtils.hasText(key)) {
            throw new BusinessException("联系方式键不能为空");
        }
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("联系方式内容不能为空");
        }
        if (value.length() > 255) {
            throw new BusinessException("联系方式内容不能超过255字符");
        }
        String k = key.trim();
        String v = value.trim();
        ContactInfo info = lambdaQuery().eq(ContactInfo::getContactKey, k).one();
        if (info == null) {
            info = new ContactInfo();
            info.setContactKey(k);
            info.setContactValue(v);
            save(info);
        } else {
            info.setContactValue(v);
            updateById(info);
        }
        return info;
    }
}
