package com.travel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.Ticket;

import java.util.List;

public interface TicketService extends IService<Ticket> {
    
    Page<Ticket> getTicketList(Integer pageNum, Integer pageSize, Long scenicId, String name);
    
    List<Ticket> getByScenicId(Long scenicId);
    
    boolean updateStatus(Long id, Integer status);
}
