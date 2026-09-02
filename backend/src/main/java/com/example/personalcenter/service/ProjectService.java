package com.example.personalcenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.personalcenter.entity.Project;

import java.util.List;

public interface ProjectService extends IService<Project> {

    /** 公开作品列表（仅启用，按 sort, id 升序） */
    List<Project> listVisible();

    /** 新增（需登录） */
    Project create(Project project, Long userId);

    /** 更新（校验归属） */
    void updateOwned(Long id, Project project, Long userId);

    /** 删除（校验归属） */
    void deleteOwned(Long id, Long userId);
}
