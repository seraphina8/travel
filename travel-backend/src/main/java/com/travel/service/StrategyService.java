package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.Strategy;

public interface StrategyService extends IService<Strategy> {
    
    Page<Strategy> getStrategyList(Integer pageNum, Integer pageSize, String title, String tag, String sortBy, Integer status);
    
    Strategy getStrategyById(Long id);
    
    boolean updateStatus(Long id, Integer status);
}
