package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.User;

public interface UserService extends IService<User> {
    
    Page<User> getUserList(Integer pageNum, Integer pageSize, String username, Integer status);
    
    boolean updateStatus(Long id, Integer status);
}
