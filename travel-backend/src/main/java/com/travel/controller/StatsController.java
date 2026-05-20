package com.travel.controller;

import com.travel.common.Result;
import com.travel.mapper.AttractionMapper;
import com.travel.mapper.OrderMapper;
import com.travel.mapper.StrategyMapper;
import com.travel.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin
public class StatsController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ScenicAreaService scenicAreaService;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private AttractionMapper attractionMapper;
    
    @Autowired
    private StrategyMapper strategyMapper;

    // 获取总览统计数据（用户数、景区数、订单数、销售总额）
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.count());
        stats.put("scenicCount", scenicAreaService.count());
        stats.put("orderCount", orderMapper.countOrders());
        stats.put("totalAmount", orderMapper.sumPaidAmount());
        return Result.success(stats);
    }

    // 获取月度订单统计
    @GetMapping("/order/monthly")
    public Result<List<Map<String, Object>>> getOrderMonthlyStats() {
        List<Map<String, Object>> stats = orderMapper.getMonthlyStats();
        return Result.success(stats);
    }

    // 获取热门景点统计（收藏量排名）
    @GetMapping("/attraction/hot")
    public Result<List<Map<String, Object>>> getHotAttractions() {
        List<Map<String, Object>> stats = attractionMapper.getHotAttractions();
        return Result.success(stats);
    }

    // 获取攻略标签统计
    @GetMapping("/strategy/tags")
    public Result<List<Map<String, Object>>> getStrategyTagStats() {
        List<Map<String, Object>> stats = strategyMapper.getTagStats();
        return Result.success(stats);
    }
}
