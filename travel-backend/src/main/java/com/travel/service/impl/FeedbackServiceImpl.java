package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.Feedback;
import com.travel.mapper.FeedbackMapper;
import com.travel.service.FeedbackService;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    // 分页查询反馈列表
    @Override
    public Page<Feedback> getFeedbackList(Integer pageNum, Integer pageSize, Integer status) {
        Page<Feedback> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.orderByDesc(Feedback::getCreateTime);
        return this.page(page, wrapper);
    }

    // 根据ID查询反馈详情
    @Override
    public Feedback getFeedbackById(Long id) {
        return baseMapper.selectWithUser(id);
    }

    // 回复反馈
    @Override
    public boolean reply(Long id, String reply) {
        Feedback feedback = new Feedback();
        feedback.setId(id);
        feedback.setReply(reply);
        feedback.setStatus(1);
        return this.updateById(feedback);
    }
}
