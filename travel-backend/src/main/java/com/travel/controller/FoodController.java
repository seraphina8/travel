package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Food;
import com.travel.mapper.FoodMapper;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/food")
@CrossOrigin
public class FoodController {

    @Autowired
    private FoodMapper foodMapper;
    
    @Autowired
    private JwtUtils jwtUtils;

    // 分页查询美食列表（后台管理）
    @GetMapping
    public Result<Page<Food>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Integer status,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Page<Food> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        if (province != null && !province.isEmpty()) {
            wrapper.eq(Food::getProvince, province);
        }
        if (city != null && !city.isEmpty()) {
            wrapper.eq(Food::getCity, city);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w
                    .like(Food::getName, kw)
                    .or()
                    .like(Food::getProvince, kw)
                    .or()
                    .like(Food::getCity, kw)
            );
        }

        if (status != null) {
            wrapper.eq(Food::getStatus, status);
        } else if (!isAdminToken(token)) {
            wrapper.eq(Food::getStatus, 1);
        }
        if (tag != null && !tag.trim().isEmpty()) {
            String tagKeyword = tag.trim();
            wrapper.and(w -> w
                    .like(Food::getTags, tagKeyword)
                    .or()
                    .like(Food::getName, tagKeyword)
                    .or()
                    .like(Food::getDescription, tagKeyword)
                    .or()
                    .like(Food::getProvince, tagKeyword)
                    .or()
                    .like(Food::getCity, tagKeyword)
            );
        }
        // 排序
        if ("popular".equals(sortBy)) {
            wrapper.orderByDesc(Food::getId);
        } else {
            wrapper.orderByDesc(Food::getCreateTime);
        }
        return Result.success(foodMapper.selectPage(page, wrapper));
    }

    private boolean isAdminToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return false;
        }
        String tokenValue = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
        try {
            return !jwtUtils.isTokenExpired(tokenValue) && "admin".equals(jwtUtils.getTokenType(tokenValue));
        } catch (Exception e) {
            return false;
        }
    }

    // 美食详情
    @GetMapping("/{id}")
    public Result<Food> detail(@PathVariable Long id) {
        return Result.success(foodMapper.selectById(id));
    }

    // 推荐美食（前台首页展示）
    @GetMapping("/recommend")
    public Result<List<Food>> recommend(@RequestParam(defaultValue = "6") int limit) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getStatus, 1);
        wrapper.orderByDesc(Food::getCreateTime);
        wrapper.last("LIMIT " + limit);
        return Result.success(foodMapper.selectList(wrapper));
    }

//    管理员功能
//    新增美食
    @PostMapping
    public Result<String> add(@RequestBody Food food) {
        food.setCreateTime(LocalDateTime.now());
        if (food.getStatus() == null) {
            food.setStatus(1);
        }
        foodMapper.insert(food);
        return Result.success("添加成功");
    }

    // 更新美食信息
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Food food) {
        food.setId(id);
        foodMapper.updateById(food);
        return Result.success("更新成功");
    }

    // 删除美食
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        foodMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // 更新美食状态
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Food food = new Food();
        food.setId(id);
        food.setStatus(status);
        foodMapper.updateById(food);
        return Result.success(status == 1 ? "审核通过" : "已取消审核");
    }

    // 用户功能
    // 用户分享美食
    @PostMapping("/share")
    public Result<String> share(@RequestBody Food food,
                                @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        try {
            String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtUtils.getUserId(tokenValue);
            if (userId == null) {
                return Result.error("登录已过期，请重新登录");
            }
            food.setUserId(userId);
            food.setStatus(0); // 待审核
            food.setCreateTime(LocalDateTime.now());
            foodMapper.insert(food);
            return Result.success("分享成功，等待审核");
        } catch (Exception e) {
            return Result.error("分享失败: " + e.getMessage());
        }
    }

    // 获取我分享的美食列表
    @GetMapping("/my")
    public Result<List<Food>> myFoods(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        try {
            String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtUtils.getUserId(tokenValue);
            if (userId == null) {
                return Result.error("登录已过期");
            }
            LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Food::getUserId, userId);
            wrapper.orderByDesc(Food::getCreateTime);
            return Result.success(foodMapper.selectList(wrapper));
        } catch (Exception e) {
            return Result.error("获取失败");
        }
    }

    // 获取用户分享的美食（查看他人主页）
    @GetMapping("/user/{userId}")
    public Result<List<Food>> getUserFoods(@PathVariable Long userId) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getUserId, userId);
        wrapper.eq(Food::getStatus, 1); // 只返回已审核的
        wrapper.orderByDesc(Food::getCreateTime);
        return Result.success(foodMapper.selectList(wrapper));
    }

    // 删除自己分享的美食
    @DeleteMapping("/my/{id}")
    public Result<String> deleteMyFood(@PathVariable Long id,
                                       @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        try {
            String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtUtils.getUserId(tokenValue);
            if (userId == null) {
                return Result.error("登录已过期");
            }
            Food food = foodMapper.selectById(id);
            if (food == null) {
                return Result.error("美食不存在");
            }
            if (!food.getUserId().equals(userId)) {
                return Result.error("只能删除自己分享的美食");
            }
            foodMapper.deleteById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败");
        }
    }
}
