package com.travel.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.Admin;
import com.travel.mapper.AdminMapper;
import com.travel.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    // 分页查询管理员列表
    @Override
    public Page<Admin> getAdminList(Integer pageNum, Integer pageSize, String username) {
        Page<Admin> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(Admin::getUsername, username);
        }
        wrapper.orderByDesc(Admin::getCreateTime);
        return this.page(page, wrapper);
    }

    // 根据ID查询管理员
    @Override
    public Admin getAdminById(Long id) {
        return baseMapper.selectWithRole(id);
    }

    // 新增管理员
    @Override
    public boolean addAdmin(Admin admin) {
        admin.setPassword(DigestUtil.md5Hex(admin.getPassword()));
        admin.setStatus(1);
        admin.setCreateTime(LocalDateTime.now());
        return this.save(admin);
    }

    // 更新管理员信息
    @Override
    public boolean updateAdmin(Admin admin) {
        if (StringUtils.hasText(admin.getPassword())) {
            admin.setPassword(DigestUtil.md5Hex(admin.getPassword()));
        } else {
            admin.setPassword(null);
        }
        admin.setUpdateTime(LocalDateTime.now());
        return this.updateById(admin);
    }

    // 删除管理员
    @Override
    public boolean deleteAdmin(Long id) {
        return this.removeById(id);
    }

    // 更新管理员状态
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setStatus(status);
        return this.updateById(admin);
    }

    // 管理员登录
    @Override
    public Admin login(String username, String password) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        wrapper.eq(Admin::getPassword, DigestUtil.md5Hex(password));
        wrapper.eq(Admin::getStatus, 1);
        Admin admin = this.getOne(wrapper);
        if (admin != null) {
            admin.setLastLoginTime(LocalDateTime.now());
            this.updateById(admin);
        }
        return admin;
    }
}
