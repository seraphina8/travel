package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Admin;
import com.travel.service.AdminService;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    // 管理员服务
    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtils jwtUtils;

    // 管理员登录
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Admin admin) {
        Admin loginAdmin = adminService.login(admin.getUsername(), admin.getPassword());
        if (loginAdmin == null) {
            return Result.error("用户名或密码错误");
        }
        // 生成token
        String token = jwtUtils.generateToken(loginAdmin.getId(), loginAdmin.getUsername(), "admin");
        hidePassword(loginAdmin);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("admin", loginAdmin);
        return Result.success("登录成功", data);
    }

    // 分页查询管理员列表
    @GetMapping("/list")
    public Result<Page<Admin>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username) {
        Page<Admin> page = adminService.getAdminList(pageNum, pageSize, username);
        page.getRecords().forEach(this::hidePassword);
        return Result.success(page);
    }

    // 根据ID查询管理员
    @GetMapping("/{id}")
    public Result<Admin> getById(@PathVariable Long id) {
        Admin admin = adminService.getAdminById(id);
        if (admin == null) {
            return Result.error("管理员不存在");
        }
        hidePassword(admin);
        return Result.success(admin);
    }

    // 新增管理员
    @PostMapping
    public Result<String> add(@RequestBody Admin admin) {
        boolean success = adminService.addAdmin(admin);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    // 更新管理员
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Admin admin) {
        admin.setId(id);
        boolean success = adminService.updateAdmin(admin);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    // 删除管理员
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = adminService.deleteAdmin(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    // 更新管理员状态（启用/禁用）
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = adminService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    private void hidePassword(Admin admin) {
        if (admin != null) {
            admin.setPassword(null);
        }
    }
}
