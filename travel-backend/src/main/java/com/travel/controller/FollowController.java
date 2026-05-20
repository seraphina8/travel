package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.entity.Follow;
import com.travel.entity.User;
import com.travel.mapper.FollowMapper;
import com.travel.mapper.UserMapper;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
@CrossOrigin
public class FollowController {

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    // 关注/取消关注（切换状态）
    @PostMapping("/{targetUserId}")
    public Result<String> follow(
            @RequestHeader("Authorization") String token,
            @PathVariable Long targetUserId) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            if (userId.equals(targetUserId)) {
                return Result.error("不能关注自己");
            }
            
            LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Follow::getUserId, userId);
            wrapper.eq(Follow::getFollowUserId, targetUserId);
            
            Follow existing = followMapper.selectOne(wrapper);
            // 已关注 -> 取消关注
            if (existing != null) {
                followMapper.deleteById(existing.getId());
                return Result.success("取消关注成功");
            } else {
                Follow follow = new Follow();
                follow.setUserId(userId);
                follow.setFollowUserId(targetUserId);
                follow.setCreateTime(LocalDateTime.now());
                followMapper.insert(follow);
                return Result.success("关注成功");
            }
        } catch (Exception e) {
            return Result.error("操作失败");
        }
    }

    // 检查是否已关注某用户
    @GetMapping("/check/{targetUserId}")
    public Result<Boolean> checkFollow(
            @RequestHeader("Authorization") String token,
            @PathVariable Long targetUserId) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Follow::getUserId, userId);
            wrapper.eq(Follow::getFollowUserId, targetUserId);
            return Result.success(followMapper.selectCount(wrapper) > 0);
        } catch (Exception e) {
            return Result.success(false);
        }
    }

    // 获取我的关注列表（我关注的人）
    @GetMapping("/list")
    public Result<List<Follow>> followList(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Follow::getUserId, userId);
            wrapper.orderByDesc(Follow::getCreateTime);
            List<Follow> list = followMapper.selectList(wrapper);
            // 关联用户信息
            for (Follow f : list) {
                User u = userMapper.selectById(f.getFollowUserId());
                if (u != null) {
                    f.setFollowUserName(u.getNickname() != null ? u.getNickname() : u.getUsername());
                    f.setFollowUserAvatar(u.getAvatar());
                }
            }
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取失败");
        }
    }

    // 获取我的粉丝列表（关注我的人）
    @GetMapping("/fans")
    public Result<List<Follow>> fansList(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<Follow> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Follow::getFollowUserId, userId);
            wrapper.orderByDesc(Follow::getCreateTime);
            List<Follow> list = followMapper.selectList(wrapper);
            // 关联用户信息
            for (Follow f : list) {
                User u = userMapper.selectById(f.getUserId());
                if (u != null) {
                    f.setFollowUserName(u.getNickname() != null ? u.getNickname() : u.getUsername());
                    f.setFollowUserAvatar(u.getAvatar());
                }
            }
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("获取失败");
        }
    }

    // 获取当前用户的关注数和粉丝数
    @GetMapping("/count")
    public Result<Map<String, Long>> count(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            
            LambdaQueryWrapper<Follow> followWrapper = new LambdaQueryWrapper<>();
            followWrapper.eq(Follow::getUserId, userId);
            Long followCount = followMapper.selectCount(followWrapper);
            
            LambdaQueryWrapper<Follow> fansWrapper = new LambdaQueryWrapper<>();
            fansWrapper.eq(Follow::getFollowUserId, userId);
            Long fansCount = followMapper.selectCount(fansWrapper);
            
            return Result.success(Map.of("followCount", followCount, "fansCount", fansCount));
        } catch (Exception e) {
            return Result.error("获取失败");
        }
    }

    // 获取指定用户的关注数和粉丝数（查看他人主页时使用）
    @GetMapping("/count/{userId}")
    public Result<Map<String, Long>> countByUserId(@PathVariable Long userId) {
        try {
            LambdaQueryWrapper<Follow> followWrapper = new LambdaQueryWrapper<>();
            followWrapper.eq(Follow::getUserId, userId);
            Long followCount = followMapper.selectCount(followWrapper);
            
            LambdaQueryWrapper<Follow> fansWrapper = new LambdaQueryWrapper<>();
            fansWrapper.eq(Follow::getFollowUserId, userId);
            Long fansCount = followMapper.selectCount(fansWrapper);
            
            return Result.success(Map.of("followCount", followCount, "fansCount", fansCount));
        } catch (Exception e) {
            return Result.error("获取失败");
        }
    }
}
