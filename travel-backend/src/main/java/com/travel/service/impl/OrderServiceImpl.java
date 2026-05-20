package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.Order;
import com.travel.entity.ScenicArea;
import com.travel.entity.Ticket;
import com.travel.entity.User;
import com.travel.mapper.OrderMapper;
import com.travel.mapper.ScenicAreaMapper;
import com.travel.mapper.TicketMapper;
import com.travel.mapper.UserMapper;
import com.travel.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ScenicAreaMapper scenicAreaMapper;
    
    @Autowired
    private TicketMapper ticketMapper;

    // 分页查询订单列表
    @Override
    public Page<Order> getOrderList(Integer pageNum, Integer pageSize, String orderNo, Integer status) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> result = this.page(page, wrapper);
        
        // 填充关联信息
        for (Order order : result.getRecords()) {
            if (order.getUserId() != null) {
                User user = userMapper.selectById(order.getUserId());
                if (user != null) {
                    order.setUsername(user.getNickname() != null ? user.getNickname() : user.getUsername());
                }
            }
            if (order.getScenicId() != null) {
                ScenicArea scenic = scenicAreaMapper.selectById(order.getScenicId());
                if (scenic != null) {
                    order.setScenicName(scenic.getName());
                }
            }
            if (order.getTicketId() != null) {
                Ticket ticket = ticketMapper.selectById(order.getTicketId());
                if (ticket != null) {
                    order.setTicketName(ticket.getName());
                }
            }
        }
        
        return result;
    }

    // 根据ID查询订单详情
    @Override
    public Order getOrderById(Long id) {
        return baseMapper.selectWithDetails(id);
    }

    // 更新订单状态
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(status);
        return this.updateById(order);
    }
}
