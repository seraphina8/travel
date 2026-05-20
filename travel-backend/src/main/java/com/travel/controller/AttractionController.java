package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Attraction;
import com.travel.entity.Tag;
import com.travel.mapper.AttractionMapper;
import com.travel.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/attraction")
@CrossOrigin
public class AttractionController {

    @Autowired
    private AttractionMapper attractionMapper;

    @Autowired
    private TagMapper tagMapper;

    @GetMapping
    public Result<Page<Attraction>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long scenicId,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String tagName) {

        Page<Attraction> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();

        if (scenicId != null) {
            wrapper.eq(Attraction::getScenicId, scenicId);
        }
        if (province != null && !province.isBlank()) {
            wrapper.eq(Attraction::getProvince, province);
        }
        if (city != null && !city.isBlank()) {
            wrapper.eq(Attraction::getCity, city);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w
                    .like(Attraction::getName, kw)
                    .or().like(Attraction::getProvince, kw)
                    .or().like(Attraction::getCity, kw)
                    .or().like(Attraction::getDescription, kw));
        }
        String tagKeyword = getTagKeyword(tagId, tagName);
        if (tagKeyword != null && !tagKeyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(Attraction::getTags, tagKeyword)
                    .or().like(Attraction::getName, tagKeyword)
                    .or().like(Attraction::getDescription, tagKeyword)
                    .or().like(Attraction::getProvince, tagKeyword)
                    .or().like(Attraction::getCity, tagKeyword));
        }

        wrapper.orderByDesc(Attraction::getRating);
        wrapper.orderByDesc(Attraction::getViewCount);
        return Result.success(attractionMapper.selectPage(page, wrapper));
    }

    @GetMapping("/{id}")
    public Result<Attraction> detail(@PathVariable Long id) {
        Attraction attraction = attractionMapper.selectById(id);
        if (attraction != null) {
            attraction.setViewCount(attraction.getViewCount() == null ? 1 : attraction.getViewCount() + 1);
            attractionMapper.updateById(attraction);
        }
        return Result.success(attraction);
    }

    @GetMapping("/recommend")
    public Result<List<Attraction>> recommend(@RequestParam(defaultValue = "6") int limit) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Attraction::getRating);
        wrapper.orderByDesc(Attraction::getCollectCount);
        wrapper.last("LIMIT " + Math.max(1, Math.min(limit, 50)));
        return Result.success(attractionMapper.selectList(wrapper));
    }

    @GetMapping("/hot")
    public Result<List<Attraction>> hot(@RequestParam(defaultValue = "6") int limit) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Attraction::getViewCount);
        wrapper.last("LIMIT " + Math.max(1, Math.min(limit, 50)));
        return Result.success(attractionMapper.selectList(wrapper));
    }

    @PostMapping
    public Result<String> add(@RequestBody Attraction attraction) {
        fillDefaults(attraction);
        attractionMapper.insert(attraction);
        return Result.success("添加成功");
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Attraction attraction) {
        attraction.setId(id);
        fillDefaults(attraction);
        attractionMapper.updateById(attraction);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        int rows = attractionMapper.deleteById(id);
        return rows > 0 ? Result.success("删除成功") : Result.error("景点不存在");
    }

    private void fillDefaults(Attraction attraction) {
        attraction.setViewCount(attraction.getViewCount() == null ? 0 : attraction.getViewCount());
        attraction.setCollectCount(attraction.getCollectCount() == null ? 0 : attraction.getCollectCount());
        attraction.setRating(attraction.getRating() == null ? BigDecimal.ZERO : attraction.getRating());
        attraction.setRatingCount(attraction.getRatingCount() == null ? 0 : attraction.getRatingCount());
    }

    private String getTagKeyword(Long tagId, String tagName) {
        if (tagName != null && !tagName.trim().isEmpty()) {
            return tagName.trim();
        }
        if (tagId == null) {
            return null;
        }
        Tag tag = tagMapper.selectById(tagId);
        return tag == null ? null : tag.getName();
    }
}
