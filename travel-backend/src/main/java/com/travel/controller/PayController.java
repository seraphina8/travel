package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.entity.Order;
import com.travel.mapper.OrderMapper;
import com.travel.service.AlipayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
@CrossOrigin
public class PayController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AlipayService alipayService;

    /**
     * 发起支付宝沙箱支付
     * 返回支付表单HTML，前端需要渲染并自动提交
     */
    @PostMapping("/alipay")
    public Result<Map<String, Object>> createAlipayPayment(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Order order = orderMapper.selectById(orderId);

        if (order == null) {
            return Result.error("订单不存在");
        }

        if (order.getStatus() != 0) {
            return Result.error("订单状态异常");
        }

        // 使用订单号+时间戳作为支付宝交易号，避免重复提交时出现E02错误
        String outTradeNo = order.getOrderNo() + "_" + System.currentTimeMillis();
        String subject = "旅游景点门票-" + order.getOrderNo();
        String form = alipayService.createPayForm(
                outTradeNo,
                order.getTotalAmount().toString(),
                subject
        );

        if (form == null) {
            return Result.error("创建支付失败");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("form", form);
        data.put("orderNo", order.getOrderNo());
        data.put("amount", order.getTotalAmount());

        return Result.success(data);
    }

    /**
     * 支付宝异步通知回调（服务器端）
     * 支付宝服务器会POST请求这个地址，通知支付结果
     * 必须返回"success"字符串，否则支付宝会重复发送通知
     */
    @PostMapping("/alipay/notify")
    public String alipayNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }

        boolean signVerified = alipayService.verifyNotify(params);

        if (signVerified) {
            String tradeStatus = params.get("trade_status");
            String outTradeNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            // 从out_trade_no中提取原始订单号（去掉_timestamp后缀）
            String orderNo = outTradeNo.contains("_") ? outTradeNo.substring(0, outTradeNo.lastIndexOf("_")) : outTradeNo;

            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Order::getOrderNo, orderNo);
                Order order = orderMapper.selectOne(wrapper);

                if (order != null && order.getStatus() == 0) {
                    // 已支付
                    order.setStatus(1);
                    order.setPayTime(LocalDateTime.now());
                    order.setPayType("支付宝");
                    order.setAlipayTradeNo(tradeNo);
                    orderMapper.updateById(order);
                }
            }
            // 告诉支付宝已收到通知
            return "success";
        }
        return "failure";
    }

    /**
     * 支付宝同步回调（用户支付完成后页面跳转）
     * 用户支付成功后会重定向到return-url
     */
    @GetMapping("/alipay/return")
    public Result<Map<String, Object>> alipayReturn(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }

        String outTradeNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String totalAmount = params.get("total_amount");
        // 从out_trade_no中提取原始订单号（去掉_timestamp后缀）
        String orderNo = outTradeNo != null && outTradeNo.contains("_") ? outTradeNo.substring(0, outTradeNo.lastIndexOf("_")) : outTradeNo;

        // 查询订单并更新状态（如果异步通知还没到）
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);

        if (order != null && order.getStatus() == 0) {
            order.setStatus(1);
            order.setPayTime(LocalDateTime.now());
            order.setPayType("支付宝");
            order.setAlipayTradeNo(tradeNo);
            orderMapper.updateById(order);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", orderNo);
        data.put("tradeNo", tradeNo);
        data.put("totalAmount", totalAmount);
        data.put("status", order != null ? order.getStatus() : -1);

        return Result.success(data);
    }

//    模拟支付
    @PostMapping("/mock")
    public Result<Map<String, Object>> createMockPayment(@RequestBody Map<String, Object> params) {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        Order order = orderMapper.selectById(orderId);
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            return Result.error("订单状态异常");
        }
        
        String payUrl = "/pay/mock?orderNo=" + order.getOrderNo() 
                      + "&amount=" + order.getTotalAmount()
                      + "&subject=门票订单";
        
        Map<String, Object> data = new HashMap<>();
        data.put("payUrl", payUrl);
        data.put("orderNo", order.getOrderNo());
        data.put("amount", order.getTotalAmount());
        
        return Result.success(data);
    }

    /**
     * 确认支付（模拟支付专用）
     * 前端调用此接口完成支付
     */
    @PostMapping("/confirm")
    public Result<String> confirmPayment(@RequestBody Map<String, Object> params) {
        String orderNo = params.get("orderNo").toString();
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);
        
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            return Result.error("订单已支付或已取消");
        }
        
        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        order.setPayType(params.getOrDefault("payType", "模拟支付").toString());
        orderMapper.updateById(order);
        
        return Result.success("支付成功");
    }

//    取消支付
    @PostMapping("/cancel")
    public Result<String> cancelPayment(@RequestBody Map<String, Object> params) {
        String orderNo = params.get("orderNo").toString();
        return Result.success("已取消支付");
    }
}
