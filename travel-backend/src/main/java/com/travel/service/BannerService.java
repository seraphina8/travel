package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.Banner;

import java.util.List;

public interface BannerService extends IService<Banner> {
    
    Page<Banner> getBannerList(Integer pageNum, Integer pageSize);
    
    List<Banner> getActiveBanners();
    
    boolean updateStatus(Long id, Integer status);
}
