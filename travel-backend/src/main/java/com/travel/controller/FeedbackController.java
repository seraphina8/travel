package com.travel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Feedback;
import com.travel.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // 分页查询反馈列表（后台管理）
    @GetMapping
    public Result<Page<Feedback>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        Page<Feedback> page = feedbackService.getFeedbackList(pageNum, pageSize, status);
        return Result.success(page);
    }

    // 根据ID查询反馈详情
    @GetMapping("/{id}")
    public Result<Feedback> getById(@PathVariable Long id) {
        Feedback feedback = feedbackService.getFeedbackById(id);
        if (feedback == null) {
            return Result.error("反馈不存在");
        }
        return Result.success(feedback);
    }

    // 回复反馈（管理员功能）
    @PostMapping("/{id}/reply")
    public Result<String> reply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        boolean success = feedbackService.reply(id, reply);
        return success ? Result.success("回复成功") : Result.error("回复失败");
    }

    // 删除反馈（管理员功能）
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = feedbackService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
