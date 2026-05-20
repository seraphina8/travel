package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.Admin;

public interface AdminService extends IService<Admin> {
    
    Page<Admin> getAdminList(Integer pageNum, Integer pageSize, String username);
    
    Admin getAdminById(Long id);
    
    boolean addAdmin(Admin admin);
    
    boolean updateAdmin(Admin admin);
    
    boolean deleteAdmin(Long id);
    
    boolean updateStatus(Long id, Integer status);
    
    Admin login(String username, String password);
}
