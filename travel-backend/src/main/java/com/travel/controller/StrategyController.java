package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Strategy;
import com.travel.service.StrategyService;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/strategy")
@CrossOrigin
public class StrategyController {

    @Autowired
    private StrategyService strategyService;
    
    @Autowired
    private JwtUtils jwtUtils;

    // 分页查询攻略列表（支持标题、标签、状态筛选）
    @GetMapping
    public Result<Page<Strategy>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) Integer status) {
        Page<Strategy> page = strategyService.getStrategyList(pageNum, pageSize, title, tag, sortBy, status);
        return Result.success(page);
    }

    // 查询景区相关攻略
    @GetMapping("/related")
    public Result<List<Strategy>> relatedStrategies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "4") Integer limit) {

        LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Strategy::getStatus, 1);

        boolean hasKeyword = StringUtils.hasText(keyword);
        boolean hasCity = StringUtils.hasText(city);

        if (hasKeyword || hasCity) {
            wrapper.and(w -> {
                if (hasKeyword) {
                    String kw = keyword.trim();
                    w.like(Strategy::getTitle, kw)
                            .or()
                            .like(Strategy::getContent, kw)
                            .or()
                            .like(Strategy::getTags, kw);
                }

                if (hasCity) {
                    String ct = city.trim();
                    if (hasKeyword) {
                        w.or();
                    }
                    w.like(Strategy::getTitle, ct)
                            .or()
                            .like(Strategy::getContent, ct)
                            .or()
                            .like(Strategy::getTags, ct);
                }
            });
        }

        int safeLimit = Math.min(Math.max(limit, 1), 8);
        wrapper.orderByDesc(Strategy::getViewCount);
        wrapper.orderByDesc(Strategy::getCreateTime);
        wrapper.last("LIMIT " + safeLimit);

        return Result.success(strategyService.list(wrapper));
    }

    // 根据ID查询攻略详情
    @GetMapping("/{id}")
    public Result<Strategy> getById(@PathVariable Long id) {
        Strategy strategy = strategyService.getStrategyById(id);
        if (strategy == null) {
            return Result.error("攻略不存在");
        }
        return Result.success(strategy);
    }

    // 更新攻略状态
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = strategyService.updateStatus(id, status);
        String msg = status == 1 ? "审核通过" : (status == 2 ? "已拒绝" : "状态更新成功");
        return success ? Result.success(msg) : Result.error("操作失败");
    }

    // 删除攻略
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = strategyService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    // 用户发布攻略
    @PostMapping("/publish")
    public Result<String> publish(@RequestBody Strategy strategy,
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
            strategy.setUserId(userId);
            strategy.setStatus(0); // 待审核
            strategy.setViewCount(0);
            strategy.setLikeCount(0);
            strategy.setCreateTime(LocalDateTime.now());
            strategyService.save(strategy);
            return Result.success("发布成功，等待审核");
        } catch (Exception e) {
            return Result.error("发布失败: " + e.getMessage());
        }
    }

    // 获取我发布的攻略列表
    @GetMapping("/my")
    public Result<List<Strategy>> myStrategies(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        try {
            String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtUtils.getUserId(tokenValue);
            if (userId == null) {
                return Result.error("登录已过期");
            }
            LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Strategy::getUserId, userId);
            wrapper.orderByDesc(Strategy::getCreateTime);
            return Result.success(strategyService.list(wrapper));
        } catch (Exception e) {
            return Result.error("获取失败");
        }
    }
}
