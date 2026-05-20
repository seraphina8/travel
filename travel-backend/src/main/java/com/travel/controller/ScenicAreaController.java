package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.ScenicArea;
import com.travel.mapper.ScenicAreaMapper;
import com.travel.service.ScenicAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/scenic")
@CrossOrigin
public class ScenicAreaController {

    @Autowired
    private ScenicAreaService scenicAreaService;

    @Autowired
    private ScenicAreaMapper scenicAreaMapper;

    @GetMapping
    public Result<Page<ScenicArea>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String tag) {
        return Result.success(scenicAreaService.getScenicList(pageNum, pageSize, name, province, tag));
    }

    @GetMapping("/{id}")
    public Result<ScenicArea> getById(@PathVariable Long id) {
        ScenicArea scenic = scenicAreaService.getById(id);
        if (scenic != null) {
            scenicAreaMapper.incrementViewCount(id);
            scenic.setViewCount((scenic.getViewCount() == null ? 0 : scenic.getViewCount()) + 1);
        }
        return scenic == null ? Result.error("景区不存在") : Result.success(scenic);
    }

    @PostMapping
    public Result<String> add(@RequestBody ScenicArea scenic) {
        scenic.setStatus(1);
        fillDefaults(scenic);
        return scenicAreaService.save(scenic) ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody ScenicArea scenic) {
        scenic.setId(id);
        fillDefaults(scenic);
        return scenicAreaService.updateById(scenic) ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        return scenicAreaService.removeById(id) ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return scenicAreaService.updateStatus(id, status) ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }

    private void fillDefaults(ScenicArea scenic) {
        scenic.setRating(scenic.getRating() == null ? BigDecimal.ZERO : scenic.getRating());
        scenic.setRatingCount(scenic.getRatingCount() == null ? 0 : scenic.getRatingCount());
        scenic.setViewCount(scenic.getViewCount() == null ? 0 : scenic.getViewCount());
        scenic.setCollectCount(scenic.getCollectCount() == null ? 0 : scenic.getCollectCount());
    }
}
