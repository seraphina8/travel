package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.Feedback;

public interface FeedbackService extends IService<Feedback> {
    
    Page<Feedback> getFeedbackList(Integer pageNum, Integer pageSize, Integer status);
    
    Feedback getFeedbackById(Long id);
    
    boolean reply(Long id, String reply);
}
