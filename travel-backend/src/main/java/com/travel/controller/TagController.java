package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.entity.Tag;
import com.travel.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
@CrossOrigin
public class TagController {

    @Autowired
    private TagMapper tagMapper;

    // 获取标签列表（支持按类型筛选）
    @GetMapping
    public Result<List<Tag>> list(
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "false") Boolean includeDisabled) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        if (!Boolean.TRUE.equals(includeDisabled)) {
            wrapper.eq(Tag::getStatus, 1);
        }
        if (type != null) {
            wrapper.eq(Tag::getType, type);
        }
        wrapper.orderByAsc(Tag::getSortOrder);
        return Result.success(tagMapper.selectList(wrapper));
    }

    // 根据ID查询标签详情
    @GetMapping("/{id}")
    public Result<Tag> detail(@PathVariable Long id) {
        return Result.success(tagMapper.selectById(id));
    }

    @PostMapping
    public Result<String> add(@RequestBody Tag tag) {
        String error = validateTag(tag);
        if (error != null) {
            return Result.error(error);
        }
        if (tag.getStatus() == null) {
            tag.setStatus(1);
        }
        if (tag.getSortOrder() == null) {
            tag.setSortOrder(0);
        }
        tagMapper.insert(tag);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Tag tag) {
        String error = validateTag(tag);
        if (error != null) {
            return Result.error(error);
        }
        tag.setId(id);
        tagMapper.updateById(tag);
        return Result.success("更新成功");
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setStatus(status);
        tagMapper.updateById(tag);
        return Result.success("状态已更新");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        tagMapper.deleteById(id);
        return Result.success("删除成功");
    }

    private String validateTag(Tag tag) {
        if (tag == null || !StringUtils.hasText(tag.getName())) {
            return "标签名称不能为空";
        }
        if (tag.getType() == null) {
            return "标签类型不能为空";
        }
        tag.setName(tag.getName().trim());
        return null;
    }
}
