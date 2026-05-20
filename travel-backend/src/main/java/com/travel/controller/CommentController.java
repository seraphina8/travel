package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.entity.Comment;
import com.travel.mapper.AttractionMapper;
import com.travel.mapper.CommentMapper;
import com.travel.mapper.ScenicAreaMapper;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ScenicAreaMapper scenicAreaMapper;

    @Autowired
    private AttractionMapper attractionMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/{targetType}/{targetId}")
    public Result<List<Comment>> getComments(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        return Result.success(commentMapper.selectCommentsWithUser(targetType, targetId));
    }

    @PostMapping
    public Result<String> addComment(@RequestBody Comment comment,
                                     @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }

        try {
            String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtUtils.getUserId(tokenValue);
            if (userId == null) {
                return Result.error("登录已过期，请重新登录");
            }
            if (comment.getRating() != null && (comment.getRating() < 1 || comment.getRating() > 5)) {
                return Result.error("评分必须在1到5之间");
            }

            comment.setUserId(userId);
            comment.setLikeCount(0);
            comment.setStatus(1);
            commentMapper.insert(comment);
            refreshTargetRating(comment.getTargetType(), comment.getTargetId());
            return Result.success("评论成功");
        } catch (Exception e) {
            return Result.error("评论失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteComment(@PathVariable Long id,
                                        @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }

        try {
            String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long userId = jwtUtils.getUserId(tokenValue);
            if (userId == null) {
                return Result.error("登录已过期");
            }

            Comment comment = commentMapper.selectById(id);
            if (comment == null) {
                return Result.error("评论不存在");
            }

            if (!comment.getUserId().equals(userId)) {
                return Result.error("只能删除自己的评论");
            }

            comment.setStatus(0);
            commentMapper.updateById(comment);
            refreshTargetRating(comment.getTargetType(), comment.getTargetId());
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/count/{targetType}/{targetId}")
    public Result<Long> getCommentCount(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getTargetType, targetType)
                .eq(Comment::getTargetId, targetId)
                .eq(Comment::getStatus, 1);
        return Result.success(commentMapper.selectCount(wrapper));
    }

    private void refreshTargetRating(String targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            return;
        }
        BigDecimal rating = commentMapper.selectAverageRating(targetType, targetId);
        Integer ratingCount = commentMapper.selectRatingCount(targetType, targetId);
        if ("scenic".equals(targetType)) {
            scenicAreaMapper.updateRating(targetId, rating, ratingCount);
        } else if ("attraction".equals(targetType)) {
            attractionMapper.updateRating(targetId, rating, ratingCount);
        }
    }
}
