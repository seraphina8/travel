package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.entity.Order;
import com.travel.entity.ScenicArea;
import com.travel.entity.User;
import com.travel.mapper.AttractionMapper;
import com.travel.mapper.OrderMapper;
import com.travel.mapper.ScenicAreaMapper;
import com.travel.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin/stats")
@CrossOrigin
public class StatisticsController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScenicAreaMapper scenicAreaMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AttractionMapper attractionMapper;

    // 获取总览统计数据（用户数、景区数、订单数、销售总额）
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> data = new HashMap<>();
        
        // 用户总数
        data.put("userCount", userMapper.selectCount(null));
        
        // 景区总数
        data.put("scenicCount", scenicAreaMapper.selectCount(null));
        
        // 订单总数
        data.put("orderCount", orderMapper.selectCount(null));
        
        // 销售总额（只统计已支付和已使用的订单）
        LambdaQueryWrapper<Order> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.in(Order::getStatus, 1, 2);
        List<Order> paidOrders = orderMapper.selectList(paidWrapper);
        BigDecimal totalAmount = paidOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalAmount", totalAmount);
        
        return Result.success(data);
    }

    // 获取热门景点统计（收藏量排名）
    @GetMapping("/attraction")
    public Result<List<Map<String, Object>>> getAttractionStats() {
        List<Map<String, Object>> data = attractionMapper.getHotAttractions();
        return Result.success(data);
    }

    // 获取订单状态统计（各状态订单数量）
    @GetMapping("/order")
    public Result<Map<String, Object>> getOrderStats() {
        Map<String, Object> data = new HashMap<>();
        
        // 各状态订单数
        data.put("pending", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 0)));
        data.put("paid", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 1)));
        data.put("used", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 2)));
        data.put("cancelled", orderMapper.selectCount(new LambdaQueryWrapper<Order>().eq(Order::getStatus, 3)));
        
        return Result.success(data);
    }

    // 获取近30天订单趋势（订单数和金额）
    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrendStats() {
        Map<String, Object> data = new HashMap<>();
        
        List<String> dates = new ArrayList<>();
        List<Long> orderCounts = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        // 遍历最近30天
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));
            
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(Order::getCreateTime, startOfDay);
            wrapper.lt(Order::getCreateTime, endOfDay);
            
            List<Order> dayOrders = orderMapper.selectList(wrapper);
            orderCounts.add((long) dayOrders.size());
            
            BigDecimal dayAmount = dayOrders.stream()
                    .filter(o -> o.getStatus() != null && o.getStatus() >= 1)
                    .map(Order::getTotalAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            amounts.add(dayAmount);
        }
        
        data.put("dates", dates);
        data.put("orderCounts", orderCounts);
        data.put("amounts", amounts);
        
        return Result.success(data);
    }
}
