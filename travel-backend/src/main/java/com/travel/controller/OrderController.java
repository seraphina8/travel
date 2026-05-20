package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Order;
import com.travel.entity.Ticket;
import com.travel.service.OrderService;
import com.travel.service.TicketService;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private com.travel.service.ScenicAreaService scenicAreaService;

//    创建订单
    @PostMapping
    public Result<Order> create(@RequestBody Map<String, Object> params,
                                @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        try {
            String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId;
            try {
                userId = jwtUtils.getUserId(tokenValue);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                return Result.error("登录已过期，请重新登录");
            }
            if (userId == null) {
                return Result.error("登录已过期，请重新登录");
            }
            
            Long scenicId = Long.valueOf(params.get("scenicId").toString());
            Long ticketId = Long.valueOf(params.get("ticketId").toString());
            Integer quantity = Integer.valueOf(params.get("quantity").toString());
            String visitDateStr = params.get("visitDate").toString();
            LocalDate visitDate = LocalDate.parse(visitDateStr);
            
            // 获取门票信息
            Ticket ticket = ticketService.getById(ticketId);
            if (ticket == null) {
                return Result.error("门票不存在");
            }
            
            // 计算总金额
            BigDecimal totalAmount = ticket.getPrice().multiply(BigDecimal.valueOf(quantity));
            
            // 生成订单号
            String orderNo = "T" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            
            // 创建订单
            Order order = new Order();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setScenicId(scenicId);
            order.setTicketId(ticketId);
            order.setQuantity(quantity);
            order.setTotalAmount(totalAmount);
            order.setVisitDate(visitDate);
            order.setStatus(0); // 待支付
            order.setCreateTime(LocalDateTime.now());
            
            orderService.save(order);
            return Result.success(order);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }

//    后台管理：订单列表
    @GetMapping
    public Result<Page<Order>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer status) {
        Page<Order> page = orderService.getOrderList(pageNum, pageSize, orderNo, status);
        return Result.success(page);
    }

//    我的订单列表（用户端）
    @GetMapping("/my")
    public Result<Page<Order>> myOrders(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        try {
            String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtUtils.getUserId(tokenValue);
            if (userId == null) {
                return Result.error("登录已过期");
            }
            Page<Order> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getUserId, userId);
            if (status != null) {
                wrapper.eq(Order::getStatus, status);
            }
            wrapper.orderByDesc(Order::getCreateTime);
            Page<Order> result = orderService.page(page, wrapper);
            
            // 填充景区和门票信息
            for (Order order : result.getRecords()) {
                if (order.getScenicId() != null) {
                    var scenic = scenicAreaService.getById(order.getScenicId());
                    if (scenic != null) {
                        order.setScenicName(scenic.getName());
                        order.setScenicCover(scenic.getCoverImage());
                    }
                }
                if (order.getTicketId() != null) {
                    var ticket = ticketService.getById(order.getTicketId());
                    if (ticket != null) {
                        order.setTicketName(ticket.getName());
                    }
                }
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取订单失败");
        }
    }

//    订单详情
    @GetMapping("/{id}")
    public Result<Order> getById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    // 取消订单接口
    @PutMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        // 只有待支付才能取消
        if (order.getStatus() != 0) {
            return Result.error("只有待支付订单可以取消");
        }
        order.setStatus(3); // 3 = 已取消
        orderService.updateById(order);
        return Result.success("取消成功");
    }

//    更新订单状态
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = orderService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
//    删除订单
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = orderService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
