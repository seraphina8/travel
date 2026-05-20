package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.travel.common.Result;
import com.travel.entity.Attraction;
import com.travel.entity.UserCollection;
import com.travel.entity.ScenicArea;
import com.travel.entity.Strategy;
import com.travel.mapper.AttractionMapper;
import com.travel.mapper.CollectionMapper;
import com.travel.mapper.ScenicAreaMapper;
import com.travel.mapper.StrategyMapper;
import com.travel.service.RecommendService;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collect")
@CrossOrigin
public class CollectionController {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private AttractionMapper attractionMapper;

    @Autowired
    private ScenicAreaMapper scenicAreaMapper;

    @Autowired
    private StrategyMapper strategyMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RecommendService recommendService;

    // 收藏/取消收藏（切换状态）
    @PostMapping
    public Result<String> collect(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            Long targetId = Long.valueOf(params.get("targetId").toString());
            Integer type = Integer.valueOf(params.get("type").toString());
            
            LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserCollection::getUserId, userId);
            wrapper.eq(UserCollection::getTargetId, targetId);
            wrapper.eq(UserCollection::getType, type);
            
            UserCollection existing = collectionMapper.selectOne(wrapper);
            if (existing != null) {
                collectionMapper.deleteById(existing.getId());
                updateCollectCount(targetId, type, -1);
                return Result.success("取消收藏成功");
            } else {
                UserCollection collection = new UserCollection();
                collection.setUserId(userId);
                collection.setTargetId(targetId);
                collection.setType(type);
                collection.setCreateTime(LocalDateTime.now());
                collectionMapper.insert(collection);
                updateCollectCount(targetId, type, 1);
                recordCollectBehavior(userId, targetId, type);
                return Result.success("收藏成功");
            }
        } catch (Exception e) {
            return Result.error("操作失败");
        }
    }

    // 检查是否已收藏
    @GetMapping("/check")
    public Result<Boolean> check(
            @RequestHeader("Authorization") String token,
            @RequestParam Long targetId,
            @RequestParam Integer type) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserCollection::getUserId, userId);
            wrapper.eq(UserCollection::getTargetId, targetId);
            wrapper.eq(UserCollection::getType, type);
            return Result.success(collectionMapper.selectCount(wrapper) > 0);
        } catch (Exception e) {
            return Result.success(false);
        }
    }

    // 获取我的收藏列表
    @GetMapping("/my")
    public Result<List<UserCollection>> myCollections(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer type) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserCollection::getUserId, userId);
            if (type != null) {
                wrapper.eq(UserCollection::getType, type);
            }
            wrapper.orderByDesc(UserCollection::getCreateTime);
            List<UserCollection> collections = collectionMapper.selectList(wrapper);
            
            // 填充收藏目标的名称和图片
            for (UserCollection c : collections) {
                if (c.getType() == 1) {
                    // 景区
                    ScenicArea scenic = scenicAreaMapper.selectById(c.getTargetId());
                    if (scenic != null) {
                        c.setTargetName(scenic.getName());
                        c.setTargetImage(scenic.getCoverImage());
                    }
                } else if (c.getType() == 2) {
                    // 攻略
                    Strategy strategy = strategyMapper.selectById(c.getTargetId());
                    if (strategy != null) {
                        c.setTargetName(strategy.getTitle());
                        c.setTargetImage(strategy.getCoverImage());
                    }
                } else if (c.getType() == 5) {
                    // 景点
                    Attraction attraction = attractionMapper.selectById(c.getTargetId());
                    if (attraction != null) {
                        c.setTargetName(attraction.getName());
                        c.setTargetImage(attraction.getCoverImage());
                    }
                }
            }
            
            return Result.success(collections);
        } catch (Exception e) {
            return Result.error("获取失败");
        }
    }

    // 删除收藏（按收藏ID）
    @DeleteMapping("/{id}")
    public Result<String> delete(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<UserCollection> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserCollection::getId, id);
            wrapper.eq(UserCollection::getUserId, userId);
            collectionMapper.delete(wrapper);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败");
        }
    }

    /** 收藏行为写入 user_behavior，供协同过滤使用（behaviorType=2 收藏） */
    private void recordCollectBehavior(Long userId, Long targetId, Integer collectionType) {
        int targetType = switch (collectionType) {
            case 1 -> 1; // 景区
            case 2 -> 2; // 攻略
            case 5 -> 5; // 景点
            default -> 0;
        };
        if (targetType > 0) {
            recommendService.recordBehavior(userId, targetId, targetType, 2);
        }
    }

    // 更新目标实体的收藏数量
    private void updateCollectCount(Long targetId, Integer type, int delta) {
        try {
            if (type == 1) {
                // 景区收藏
                ScenicArea scenic = scenicAreaMapper.selectById(targetId);
                if (scenic != null) {
                    Integer count = scenic.getCollectCount() == null ? 0 : scenic.getCollectCount();
                    scenic.setCollectCount(Math.max(0, count + delta));
                    scenicAreaMapper.updateById(scenic);
                }
            } else if (type == 5) {
                // 景点收藏
                Attraction attraction = attractionMapper.selectById(targetId);
                if (attraction != null) {
                    Integer count = attraction.getCollectCount() == null ? 0 : attraction.getCollectCount();
                    attraction.setCollectCount(Math.max(0, count + delta));
                    attractionMapper.updateById(attraction);
                }
            }
        } catch (Exception e) {
            // 忽略计数更新失败
        }
    }
}
