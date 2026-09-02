package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.entity.Project;
import com.example.personalcenter.mapper.ProjectMapper;
import com.example.personalcenter.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Override
    public List<Project> listVisible() {
        return lambdaQuery()
                .eq(Project::getVisible, 1)
                .orderByAsc(Project::getSort)
                .orderByAsc(Project::getId)
                .list();
    }

    @Override
    public Project create(Project project, Long userId) {
        validate(project);
        project.setId(null);
        if (project.getVisible() == null) {
            project.setVisible(1);
        }
        if (project.getSort() == null) {
            project.setSort(0);
        }
        project.setUserId(userId);
        save(project);
        return project;
    }

    @Override
    public void updateOwned(Long id, Project project, Long userId) {
        Project owned = getOwned(id, userId);
        validate(project);
        owned.setName(project.getName());
        owned.setDescription(project.getDescription());
        owned.setUrl(project.getUrl());
        owned.setGithubUrl(project.getGithubUrl());
        owned.setTech(project.getTech());
        owned.setCoverImage(project.getCoverImage());
        owned.setSort(project.getSort() == null ? 0 : project.getSort());
        owned.setVisible(project.getVisible() == null ? 1 : project.getVisible());
        updateById(owned);
    }

    @Override
    public void deleteOwned(Long id, Long userId) {
        getOwned(id, userId);
        removeById(id);
    }

    private void validate(Project project) {
        if (!StringUtils.hasText(project.getName())) {
            throw new BusinessException("项目名称不能为空");
        }
        project.setName(project.getName().trim());
    }

    private Project getOwned(Long id, Long userId) {
        Project project = getById(id);
        if (project == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!project.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return project;
    }
}
