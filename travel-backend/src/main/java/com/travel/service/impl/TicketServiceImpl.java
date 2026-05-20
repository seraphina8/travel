package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.ScenicArea;
import com.travel.entity.Ticket;
import com.travel.mapper.ScenicAreaMapper;
import com.travel.mapper.TicketMapper;
import com.travel.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

    @Autowired
    private ScenicAreaMapper scenicAreaMapper;

    // 分页查询门票列表（支持景区ID和名称筛选）
    @Override
    public Page<Ticket> getTicketList(Integer pageNum, Integer pageSize, Long scenicId, String name) {
        Page<Ticket> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        if (scenicId != null) {
            wrapper.eq(Ticket::getScenicId, scenicId);
        }
        if (StringUtils.hasText(name)) {
            wrapper.like(Ticket::getName, name);
        }
        wrapper.orderByDesc(Ticket::getCreateTime);
        Page<Ticket> result = this.page(page, wrapper);
        
        // 填充景区名称
        if (!result.getRecords().isEmpty()) {
            Set<Long> scenicIds = result.getRecords().stream()
                    .map(Ticket::getScenicId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
            if (!scenicIds.isEmpty()) {
                List<ScenicArea> scenics = scenicAreaMapper.selectBatchIds(scenicIds);
                Map<Long, String> scenicNameMap = scenics.stream()
                        .collect(Collectors.toMap(ScenicArea::getId, ScenicArea::getName));
                result.getRecords().forEach(ticket -> {
                    if (ticket.getScenicId() != null) {
                        ticket.setScenicName(scenicNameMap.get(ticket.getScenicId()));
                    }
                });
            }
        }
        return result;
    }

    // 根据景区ID获取门票列表
    @Override
    public List<Ticket> getByScenicId(Long scenicId) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ticket::getScenicId, scenicId);
        wrapper.eq(Ticket::getStatus, 1);
        return this.list(wrapper);
    }

    // 更新门票状态
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setStatus(status);
        return this.updateById(ticket);
    }
}
