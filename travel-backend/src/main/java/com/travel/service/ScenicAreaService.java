package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.ScenicArea;

public interface ScenicAreaService extends IService<ScenicArea> {
    
    Page<ScenicArea> getScenicList(Integer pageNum, Integer pageSize, String name, String province, String tag);
    
    boolean updateStatus(Long id, Integer status);
}
