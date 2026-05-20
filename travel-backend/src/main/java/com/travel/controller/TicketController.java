package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Ticket;
import com.travel.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // 分页查询门票列表（支持景区ID和名称筛选）
    @GetMapping
    public Result<Page<Ticket>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long scenicId,
            @RequestParam(required = false) String name) {
        Page<Ticket> page = ticketService.getTicketList(pageNum, pageSize, scenicId, name);
        return Result.success(page);
    }

    // 根据景区ID获取门票列表
    @GetMapping("/scenic/{scenicId}")
    public Result<List<Ticket>> getByScenicId(@PathVariable Long scenicId) {
        List<Ticket> tickets = ticketService.getByScenicId(scenicId);
        return Result.success(tickets);
    }

    // 根据ID查询门票详情
    @GetMapping("/{id}")
    public Result<Ticket> getById(@PathVariable Long id) {
        Ticket ticket = ticketService.getById(id);
        if (ticket == null) {
            return Result.error("门票不存在");
        }
        return Result.success(ticket);
    }

    // 新增门票
    @PostMapping
    public Result<String> add(@RequestBody Ticket ticket) {
        ticket.setStatus(1);
        boolean success = ticketService.save(ticket);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    // 更新门票信息
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Ticket ticket) {
        ticket.setId(id);
        boolean success = ticketService.updateById(ticket);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    // 删除门票
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = ticketService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    // 更新门票状态
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = ticketService.updateStatus(id, status);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
}
