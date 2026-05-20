package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Banner;
import com.travel.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banner")
@CrossOrigin
public class BannerController {

    @Autowired
    private BannerService bannerService;

    // 分页查询轮播图列表（后台管理）
    @GetMapping
    public Result<Page<Banner>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Banner> page = bannerService.getBannerList(pageNum, pageSize);
        return Result.success(page);
    }

    // 获取启用的轮播图（前端首页展示）
    @GetMapping("/active")
    public Result<List<Banner>> getActiveBanners() {
        List<Banner> banners = bannerService.getActiveBanners();
        return Result.success(banners);
    }

    // 根据ID查询轮播图
    @GetMapping("/{id}")
    public Result<Banner> getById(@PathVariable Long id) {
        Banner banner = bannerService.getById(id);
        if (banner == null) {
            return Result.error("轮播图不存在");
        }
        return Result.success(banner);
    }

    // 新增轮播图（默认状态为启用）
    @PostMapping
    public Result<String> add(@RequestBody Banner banner) {
        banner.setStatus(1);
        boolean success = bannerService.save(banner);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    // 更新轮播图信息
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        boolean success = bannerService.updateById(banner);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    // 删除轮播图
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = bannerService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    // 更新轮播图状态（启用/禁用）
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = bannerService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
}
