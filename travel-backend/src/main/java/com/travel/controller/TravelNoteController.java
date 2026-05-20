package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.common.Result;
import com.travel.entity.NoteComment;
import com.travel.entity.NoteLike;
import com.travel.entity.TravelNote;
import com.travel.entity.UserFootprint;
import com.travel.mapper.NoteCommentMapper;
import com.travel.mapper.NoteLikeMapper;
import com.travel.mapper.TravelNoteMapper;
import com.travel.mapper.UserFootprintMapper;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/note")
@CrossOrigin
public class TravelNoteController {

    @Autowired
    private TravelNoteMapper travelNoteMapper;

    @Autowired
    private UserFootprintMapper userFootprintMapper;
    
    @Autowired
    private NoteCommentMapper noteCommentMapper;
    
    @Autowired
    private NoteLikeMapper noteLikeMapper;

    @Autowired
    private JwtUtils jwtUtils;

    // 分页查询游记列表（支持省份筛选）
    @GetMapping
    public Result<Page<TravelNote>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String tag) {
        Page<TravelNote> page = new Page<>(pageNum, pageSize);
        return Result.success(travelNoteMapper.selectNotesWithUser(page, province, tag));
    }

    // 查询景区相关游记
    @GetMapping("/related")
    public Result<List<TravelNote>> relatedNotes(
            @RequestParam(required = false) Long scenicId,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "4") Integer limit) {

        LambdaQueryWrapper<TravelNote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNote::getStatus, 1);

        boolean hasCondition = scenicId != null
                || StringUtils.hasText(province)
                || StringUtils.hasText(city);

        if (hasCondition) {
            wrapper.and(w -> {
                boolean first = true;

                if (scenicId != null) {
                    w.eq(TravelNote::getScenicId, scenicId);
                    first = false;
                }

                if (StringUtils.hasText(province)) {
                    if (!first) {
                        w.or();
                    }
                    w.eq(TravelNote::getProvince, province);
                    first = false;
                }

                if (StringUtils.hasText(city)) {
                    if (!first) {
                        w.or();
                    }
                    w.eq(TravelNote::getCity, city);
                }
            });
        }

        int safeLimit = Math.min(Math.max(limit, 1), 8);
        wrapper.orderByDesc(TravelNote::getViewCount);
        wrapper.orderByDesc(TravelNote::getCreateTime);
        wrapper.last("LIMIT " + safeLimit);

        return Result.success(travelNoteMapper.selectList(wrapper));
    }

    // 游记详情（访问时自动增加浏览量）
    @GetMapping("/{id}")
    public Result<TravelNote> detail(@PathVariable Long id) {
        TravelNote note = travelNoteMapper.selectNoteWithUser(id);
        if (note != null) {
            Integer viewCount = note.getViewCount() == null ? 0 : note.getViewCount();
            note.setViewCount(viewCount + 1);
            travelNoteMapper.updateById(note);
        }
        return Result.success(note);
    }

    // 发布游记
    @PostMapping
    public Result<String> create(
            @RequestHeader("Authorization") String token,
            @RequestBody TravelNote note) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            note.setUserId(userId);
            note.setStatus(1);
            note.setViewCount(0);
            note.setLikeCount(0);
            note.setCreateTime(LocalDateTime.now());
            travelNoteMapper.insert(note);
            
            if (note.getProvince() != null && !note.getProvince().isEmpty()) {
                LambdaQueryWrapper<UserFootprint> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserFootprint::getUserId, userId);
                wrapper.eq(UserFootprint::getProvince, note.getProvince());
                if (userFootprintMapper.selectCount(wrapper) == 0) {
                    UserFootprint footprint = new UserFootprint();
                    footprint.setUserId(userId);
                    footprint.setProvince(note.getProvince());
                    footprint.setFirstVisitTime(LocalDateTime.now());
                    footprint.setCreateTime(LocalDateTime.now());
                    userFootprintMapper.insert(footprint);
                }
            }
            
            return Result.success("发布成功");
        } catch (Exception e) {
            return Result.error("发布失败");
        }
    }

    // 获取我发布的游记列表
    @GetMapping("/my")
    public Result<List<TravelNote>> myNotes(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<TravelNote> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TravelNote::getUserId, userId);
            wrapper.orderByDesc(TravelNote::getCreateTime);
            return Result.success(travelNoteMapper.selectList(wrapper));
        } catch (Exception e) {
            return Result.error("获取失败");
        }
    }

    // 删除自己发布的游记
    @DeleteMapping("/{id}")
    public Result<String> delete(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<TravelNote> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TravelNote::getId, id);
            wrapper.eq(TravelNote::getUserId, userId);
            travelNoteMapper.delete(wrapper);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败");
        }
    }

    // 获取用户已打卡的省份足迹
    @GetMapping("/footprint")
    public Result<List<String>> getFootprint(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            List<String> provinces = userFootprintMapper.getVisitedProvinces(userId);
            return Result.success(provinces);
        } catch (Exception e) {
            return Result.error("获取失败");
        }
    }
    
    // 点赞功能
    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> toggleLike(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            // 查询是否已点赞
            LambdaQueryWrapper<NoteLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(NoteLike::getNoteId, id);
            wrapper.eq(NoteLike::getUserId, userId);
            NoteLike existingLike = noteLikeMapper.selectOne(wrapper);
            
            Map<String, Object> result = new HashMap<>();
            if (existingLike != null) {
                noteLikeMapper.delete(wrapper);
                // 减少点赞数
                LambdaUpdateWrapper<TravelNote> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(TravelNote::getId, id);
                updateWrapper.setSql("like_count = like_count - 1");
                travelNoteMapper.update(null, updateWrapper);
                result.put("liked", false);
                result.put("message", "取消点赞");
            } else {
                NoteLike like = new NoteLike();
                like.setNoteId(id);
                like.setUserId(userId);
                like.setCreateTime(LocalDateTime.now());
                noteLikeMapper.insert(like);
                // 增加点赞数
                LambdaUpdateWrapper<TravelNote> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(TravelNote::getId, id);
                updateWrapper.setSql("like_count = like_count + 1");
                travelNoteMapper.update(null, updateWrapper);
                result.put("liked", true);
                result.put("message", "点赞成功");
            }
            
            TravelNote note = travelNoteMapper.selectById(id);
            result.put("likeCount", note != null ? note.getLikeCount() : 0);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("操作失败");
        }
    }

    // 检查是否已点赞
    @GetMapping("/{id}/like/check")
    public Result<Boolean> checkLike(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<NoteLike> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(NoteLike::getNoteId, id);
            wrapper.eq(NoteLike::getUserId, userId);
            return Result.success(noteLikeMapper.selectCount(wrapper) > 0);
        } catch (Exception e) {
            return Result.success(false);
        }
    }

    // 获取游记的评论列表
    @GetMapping("/{id}/comments")
    public Result<List<NoteComment>> getComments(@PathVariable Long id) {
        List<NoteComment> comments = noteCommentMapper.selectCommentsWithUser(id);
        return Result.success(comments);
    }

    // 添加评论
    @PostMapping("/{id}/comment")
    public Result<String> addComment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Map<String, Object> params) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            String content = (String) params.get("content");
            Long parentId = params.get("parentId") != null ? Long.valueOf(params.get("parentId").toString()) : null;
            
            if (content == null || content.trim().isEmpty()) {
                return Result.error("评论内容不能为空");
            }
            
            NoteComment comment = new NoteComment();
            comment.setNoteId(id);
            comment.setUserId(userId);
            comment.setContent(content.trim());
            comment.setParentId(parentId);
            comment.setLikeCount(0);
            comment.setStatus(1);
            comment.setCreateTime(LocalDateTime.now());
            noteCommentMapper.insert(comment);
            
            return Result.success("评论成功");
        } catch (Exception e) {
            return Result.error("评论失败");
        }
    }

    // 删除自己的评论
    @DeleteMapping("/comment/{commentId}")
    public Result<String> deleteComment(
            @RequestHeader("Authorization") String token,
            @PathVariable Long commentId) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<NoteComment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(NoteComment::getId, commentId);
            wrapper.eq(NoteComment::getUserId, userId);
            NoteComment comment = noteCommentMapper.selectOne(wrapper);
            if (comment != null) {
                comment.setStatus(0);
                noteCommentMapper.updateById(comment);
                return Result.success("删除成功");
            }
            return Result.error("评论不存在或无权删除");
        } catch (Exception e) {
            return Result.error("删除失败");
        }
    }
}
