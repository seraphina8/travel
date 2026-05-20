package com.travel.controller;

import com.travel.common.Result;
import com.travel.entity.*;
import com.travel.service.RecommendService;
import com.travel.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private JwtUtils jwtUtils;

    // 获取推荐景区（个性化推荐，未登录用户获取热门推荐）
    @GetMapping("/scenics")
    public Result<List<ScenicArea>> getRecommendScenics(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "6") int limit) {
        Long userId = null;
        if (token != null && !token.isEmpty()) {
            try {
                userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            } catch (Exception ignored) {
            }
        }
        List<ScenicArea> scenics = recommendService.getRecommendScenics(userId, limit);
        return Result.success(scenics);
    }

    // 获取热门景区
    @GetMapping("/hot")
    public Result<List<ScenicArea>> getHotScenics(@RequestParam(defaultValue = "6") int limit) {
        List<ScenicArea> scenics = recommendService.getHotScenics(limit);
        return Result.success(scenics);
    }

    // 记录用户行为
    @PostMapping("/behavior")
    public Result<String> recordBehavior(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            Long targetId = Long.valueOf(params.get("targetId").toString());
            Integer targetType = Integer.valueOf(params.get("targetType").toString());
            Integer behaviorType = Integer.valueOf(params.get("behaviorType").toString());
            recommendService.recordBehavior(userId, targetId, targetType, behaviorType);
            return Result.success("记录成功");
        } catch (Exception e) {
            return Result.error("记录失败");
        }
    }

    // 获取推荐攻略
    @GetMapping("/strategies")
    public Result<List<Strategy>> getRecommendStrategies(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "6") int limit) {
        Long userId = getUserIdFromToken(token);
        return Result.success(recommendService.getRecommendStrategies(userId, limit));
    }

    // 获取推荐游记
    @GetMapping("/trips")
    public Result<List<Trip>> getRecommendTrips(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "6") int limit) {
        Long userId = getUserIdFromToken(token);
        return Result.success(recommendService.getRecommendTrips(userId, limit));
    }

    // 获取推荐美食
    @GetMapping("/foods")
    public Result<List<Food>> getRecommendFoods(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "6") int limit) {
        Long userId = getUserIdFromToken(token);
        return Result.success(recommendService.getRecommendFoods(userId, limit));
    }

    // 获取推荐景点
    @GetMapping("/attractions")
    public Result<List<Attraction>> getRecommendAttractions(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "6") int limit) {
        Long userId = getUserIdFromToken(token);
        return Result.success(recommendService.getRecommendAttractions(userId, limit));
    }

    // 从令牌中提取用户ID（工具方法）
    private Long getUserIdFromToken(String token) {
        if (token != null && !token.isEmpty()) {
            try {
                return jwtUtils.getUserId(token.replace("Bearer ", ""));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    // 获取用户浏览足迹
    @GetMapping("/footprint")
    public Result<List<Map<String, Object>>> getFootprint(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            List<Map<String, Object>> footprints = recommendService.getUserFootprints(userId, limit);
            return Result.success(footprints);
        } catch (Exception e) {
            return Result.error("获取足迹失败");
        }
    }
}
