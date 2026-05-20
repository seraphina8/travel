package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.Feedback;
import com.travel.service.FeedbackService;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Feedback feedback,
                                 @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = getUserId(token);
        if (userId == null) {
            return Result.error(401, "请先登录后再提交反馈");
        }
        if (feedback == null || feedback.getContent() == null || feedback.getContent().trim().isEmpty()) {
            return Result.error("反馈内容不能为空");
        }

        feedback.setUserId(userId);
        feedback.setContent(feedback.getContent().trim());
        feedback.setStatus(0);
        feedback.setCreateTime(LocalDateTime.now());
        return feedbackService.save(feedback) ? Result.success("反馈提交成功") : Result.error("反馈提交失败");
    }

    @GetMapping("/my")
    public Result<List<Feedback>> myFeedback(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = getUserId(token);
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Feedback::getUserId, userId);
        wrapper.orderByDesc(Feedback::getCreateTime);
        return Result.success(feedbackService.list(wrapper));
    }

    @GetMapping
    public Result<Page<Feedback>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        Page<Feedback> page = feedbackService.getFeedbackList(pageNum, pageSize, status);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<Feedback> getById(@PathVariable Long id) {
        Feedback feedback = feedbackService.getFeedbackById(id);
        return feedback == null ? Result.error("反馈不存在") : Result.success(feedback);
    }

    @PostMapping("/{id}/reply")
    public Result<String> reply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        boolean success = feedbackService.reply(id, reply);
        return success ? Result.success("回复成功") : Result.error("回复失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = feedbackService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    private Long getUserId(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
            return jwtUtils.getUserId(tokenValue);
        } catch (Exception e) {
            return null;
        }
    }
}
