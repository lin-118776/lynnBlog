package com.example.personalcenter.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.personalcenter.common.BusinessException;
import com.example.personalcenter.common.ResultCode;
import com.example.personalcenter.dto.interview.InterviewReq;
import com.example.personalcenter.entity.InterviewExperience;
import com.example.personalcenter.mapper.InterviewExperienceMapper;
import com.example.personalcenter.service.InterviewExperienceService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 面试经验服务实现
 */
@Service
public class InterviewExperienceServiceImpl extends ServiceImpl<InterviewExperienceMapper, InterviewExperience>
        implements InterviewExperienceService {

    @Override
    public void create(InterviewReq req, Long userId) {
        if (!StringUtils.hasText(req.getCompanyName()) || !StringUtils.hasText(req.getJobPosition())) {
            throw new BusinessException("公司名称和岗位不能为空");
        }
        InterviewExperience exp = new InterviewExperience();
        exp.setCompanyName(req.getCompanyName());
        exp.setJobPosition(req.getJobPosition());
        exp.setInterviewRound(req.getInterviewRound());
        exp.setQuestionText(req.getQuestionText());
        exp.setMyAnswer(req.getMyAnswer());
        exp.setReflection(req.getReflection());
        exp.setIsSuccess(req.getIsSuccess());
        exp.setInterviewDate(req.getInterviewDate());
        exp.setIsPublic(req.getIsPublic() == null ? 0 : req.getIsPublic());
        exp.setUserId(userId);
        save(exp);
    }

    @Override
    public void updateOwned(Long id, InterviewReq req, Long userId) {
        InterviewExperience exp = getOwned(id, userId);
        exp.setCompanyName(req.getCompanyName());
        exp.setJobPosition(req.getJobPosition());
        exp.setInterviewRound(req.getInterviewRound());
        exp.setQuestionText(req.getQuestionText());
        exp.setMyAnswer(req.getMyAnswer());
        exp.setReflection(req.getReflection());
        exp.setIsSuccess(req.getIsSuccess());
        exp.setInterviewDate(req.getInterviewDate());
        exp.setIsPublic(req.getIsPublic());
        updateById(exp);
    }

    @Override
    public void deleteOwned(Long id, Long userId) {
        getOwned(id, userId);
        removeById(id);
    }

    @Override
    public IPage<InterviewExperience> pageList(long page, long size, String keyword, Long userId) {
        return lambdaQuery()
                // 已登录：公开 OR 自己的；未登录：仅公开
                .and(w -> w.eq(InterviewExperience::getIsPublic, 1)
                        .or().eq(userId != null, InterviewExperience::getUserId, userId))
                .and(StringUtils.hasText(keyword), w -> w.like(InterviewExperience::getCompanyName, keyword)
                        .or().like(InterviewExperience::getJobPosition, keyword)
                        .or().like(InterviewExperience::getQuestionText, keyword))
                .orderByDesc(InterviewExperience::getCreateTime)
                .page(new Page<>(page, size));
    }

    @Override
    public InterviewExperience getDetail(Long id, Long userId) {
        InterviewExperience exp = getById(id);
        if (exp == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        // 私有数据必须校验归属
        if (!Objects.equals(exp.getIsPublic(), 1)
                && (userId == null || !exp.getUserId().equals(userId))) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return exp;
    }

    /** 获取面试经验并校验归属 */
    private InterviewExperience getOwned(Long id, Long userId) {
        InterviewExperience exp = getById(id);
        if (exp == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (!exp.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return exp;
    }
}