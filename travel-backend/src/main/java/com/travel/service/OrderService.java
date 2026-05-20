package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.Order;

public interface OrderService extends IService<Order> {
    
    Page<Order> getOrderList(Integer pageNum, Integer pageSize, String orderNo, Integer status);
    
    Order getOrderById(Long id);
    
    boolean updateStatus(Long id, Integer status);
}
