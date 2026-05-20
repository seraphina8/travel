package com.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.entity.Attraction;
import com.travel.entity.Food;
import com.travel.entity.ScenicArea;
import com.travel.entity.Strategy;
import com.travel.entity.Trip;
import com.travel.entity.UserBehavior;
import com.travel.mapper.AttractionMapper;
import com.travel.mapper.FoodMapper;
import com.travel.mapper.ScenicAreaMapper;
import com.travel.mapper.StrategyMapper;
import com.travel.mapper.TripMapper;
import com.travel.mapper.UserBehaviorMapper;
import com.travel.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private ScenicAreaMapper scenicAreaMapper;

    @Autowired
    private StrategyMapper strategyMapper;

    @Autowired
    private TripMapper tripMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private AttractionMapper attractionMapper;

    private static final Map<Integer, Double> BEHAVIOR_WEIGHTS = new HashMap<>() {{
        put(1, 1.0); // view
        put(2, 2.0); // collect
        put(3, 3.0); // like
        put(4, 5.0); // comment
    }};

    private static final int TARGET_TYPE_SCENIC = 1;
    private static final int TARGET_TYPE_STRATEGY = 2;
    private static final int TARGET_TYPE_TRIP = 3;
    private static final int TARGET_TYPE_FOOD = 4;
    private static final int TARGET_TYPE_ATTRACTION = 5;

    @Override
    public List<ScenicArea> getRecommendScenics(Long userId, int limit) {
        int size = normalizeLimit(limit);
        List<Long> ids = mergeRecommendIds(userId, size, TARGET_TYPE_SCENIC);
        List<ScenicArea> result = loadScenicsByIds(ids);
        appendMissing(result, getScenicFallback(size * 2), ScenicArea::getId, size);
        return limitList(result, size);
    }

    @Override
    public List<ScenicArea> getHotScenics(int limit) {
        int size = normalizeLimit(limit);
        List<ScenicArea> result = loadScenicsByIds(hotTargetIds(size * 2, TARGET_TYPE_SCENIC));
        appendMissing(result, getScenicFallback(size * 2), ScenicArea::getId, size);
        return limitList(result, size);
    }

    @Override
    public List<Strategy> getRecommendStrategies(Long userId, int limit) {
        int size = normalizeLimit(limit);
        List<Strategy> result = loadStrategiesByIds(mergeRecommendIds(userId, size, TARGET_TYPE_STRATEGY));
        appendMissing(result, getStrategyFallback(size * 2), Strategy::getId, size);
        return limitList(result, size);
    }

    @Override
    public List<Trip> getRecommendTrips(Long userId, int limit) {
        int size = normalizeLimit(limit);
        List<Trip> result = loadTripsByIds(mergeRecommendIds(userId, size, TARGET_TYPE_TRIP));
        appendMissing(result, getTripFallback(size * 2), Trip::getId, size);
        return limitList(result, size);
    }

    @Override
    public List<Food> getRecommendFoods(Long userId, int limit) {
        int size = normalizeLimit(limit);
        List<Food> result = loadFoodsByIds(mergeRecommendIds(userId, size, TARGET_TYPE_FOOD));
        appendMissing(result, getFoodFallback(size * 2), Food::getId, size);
        return limitList(result, size);
    }

    @Override
    public List<Attraction> getRecommendAttractions(Long userId, int limit) {
        int size = normalizeLimit(limit);
        List<Attraction> result = loadAttractionsByIds(mergeRecommendIds(userId, size, TARGET_TYPE_ATTRACTION));
        appendMissing(result, getAttractionFallback(size * 2), Attraction::getId, size);
        return limitList(result, size);
    }

    @Override
    public void recordBehavior(Long userId, Long scenicId, int behaviorType) {
        recordBehavior(userId, scenicId, TARGET_TYPE_SCENIC, behaviorType);
    }

    @Override
    public void recordBehavior(Long userId, Long targetId, int targetType, int behaviorType) {
        if (userId == null || targetId == null || targetType < 1 || targetType > 5) {
            return;
        }

        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setTargetId(targetId);
        behavior.setTargetType(targetType);
        behavior.setBehaviorType(behaviorType);
        behavior.setScore(BEHAVIOR_WEIGHTS.getOrDefault(behaviorType, 1.0));
        behavior.setCreateTime(LocalDateTime.now());
        userBehaviorMapper.insert(behavior);

        if (targetType == TARGET_TYPE_SCENIC && behaviorType == 1) {
            scenicAreaMapper.incrementViewCount(targetId);
        }
    }

    @Override
    public List<Map<String, Object>> getUserFootprints(Long userId, int limit) {
        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehavior::getUserId, userId);
        wrapper.eq(UserBehavior::getBehaviorType, 1);
        wrapper.orderByDesc(UserBehavior::getCreateTime);
        wrapper.last("LIMIT " + normalizeLimit(limit));
        List<UserBehavior> behaviors = userBehaviorMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (UserBehavior behavior : behaviors) {
            Map<String, Object> item = buildFootprintItem(behavior);
            if (!item.isEmpty()) {
                result.add(item);
            }
        }
        return result;
    }

    private List<Long> mergeRecommendIds(Long userId, int limit, int targetType) {
        LinkedHashSet<Long> ids = new LinkedHashSet<>(collaborativeTargetIds(userId, limit * 2, targetType));
        ids.addAll(hotTargetIds(limit * 2, targetType));
        return new ArrayList<>(ids);
    }

    private List<Long> collaborativeTargetIds(Long userId, int limit, int targetType) {
        if (userId == null) {
            return List.of();
        }

        List<Map<String, Object>> userScores = userBehaviorMapper.getUserTargetScores(userId, targetType);
        if (userScores.isEmpty()) {
            return List.of();
        }

        Set<Long> similarUserIds = new HashSet<>();
        for (Map<String, Object> score : userScores) {
            Long targetId = toLong(score.get("target_id"));
            if (targetId != null) {
                similarUserIds.addAll(userBehaviorMapper.getSimilarUsers(targetId, targetType, userId));
            }
        }
        if (similarUserIds.isEmpty()) {
            return List.of();
        }

        List<Long> similarUsers = similarUserIds.stream().limit(50).collect(Collectors.toList());
        return userBehaviorMapper.getRecommendTargetIds(similarUsers, userId, targetType, limit).stream()
                .map(row -> toLong(row.get("target_id")))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Long> hotTargetIds(int limit, int targetType) {
        return userBehaviorMapper.getHotTargetIds(targetType, normalizeLimit(limit)).stream()
                .map(row -> toLong(row.get("target_id")))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<ScenicArea> loadScenicsByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ScenicArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ScenicArea::getId, ids);
        wrapper.eq(ScenicArea::getStatus, 1);
        return orderByIds(scenicAreaMapper.selectList(wrapper), ids, ScenicArea::getId);
    }

    private List<Strategy> loadStrategiesByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Strategy::getId, ids);
        wrapper.eq(Strategy::getStatus, 1);
        return orderByIds(strategyMapper.selectList(wrapper), ids, Strategy::getId);
    }

    private List<Trip> loadTripsByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Trip> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Trip::getId, ids);
        wrapper.eq(Trip::getStatus, 1);
        wrapper.eq(Trip::getIsPublic, 1);
        return orderByIds(tripMapper.selectList(wrapper), ids, Trip::getId);
    }

    private List<Food> loadFoodsByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Food::getId, ids);
        wrapper.eq(Food::getStatus, 1);
        return orderByIds(foodMapper.selectList(wrapper), ids, Food::getId);
    }

    private List<Attraction> loadAttractionsByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Attraction::getId, ids);
        return orderByIds(attractionMapper.selectList(wrapper), ids, Attraction::getId);
    }

    private List<ScenicArea> getScenicFallback(int limit) {
        LambdaQueryWrapper<ScenicArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScenicArea::getStatus, 1);
        wrapper.orderByDesc(ScenicArea::getViewCount);
        wrapper.orderByDesc(ScenicArea::getCollectCount);
        wrapper.orderByDesc(ScenicArea::getCreateTime);
        wrapper.last("LIMIT " + normalizeLimit(limit));
        return scenicAreaMapper.selectList(wrapper);
    }

    private List<Strategy> getStrategyFallback(int limit) {
        LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Strategy::getStatus, 1);
        wrapper.orderByDesc(Strategy::getViewCount);
        wrapper.orderByDesc(Strategy::getLikeCount);
        wrapper.orderByDesc(Strategy::getCreateTime);
        wrapper.last("LIMIT " + normalizeLimit(limit));
        return strategyMapper.selectList(wrapper);
    }

    private List<Trip> getTripFallback(int limit) {
        LambdaQueryWrapper<Trip> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Trip::getStatus, 1);
        wrapper.eq(Trip::getIsPublic, 1);
        wrapper.orderByDesc(Trip::getCreateTime);
        wrapper.last("LIMIT " + normalizeLimit(limit));
        return tripMapper.selectList(wrapper);
    }

    private List<Food> getFoodFallback(int limit) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getStatus, 1);
        wrapper.orderByDesc(Food::getCreateTime);
        wrapper.last("LIMIT " + normalizeLimit(limit));
        return foodMapper.selectList(wrapper);
    }

    private List<Attraction> getAttractionFallback(int limit) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Attraction::getViewCount);
        wrapper.orderByDesc(Attraction::getCollectCount);
        wrapper.orderByDesc(Attraction::getCreateTime);
        wrapper.last("LIMIT " + normalizeLimit(limit));
        return attractionMapper.selectList(wrapper);
    }

    private <T> List<T> orderByIds(List<T> rows, List<Long> ids, Function<T, Long> idGetter) {
        Map<Long, T> map = rows.stream().collect(Collectors.toMap(idGetter, Function.identity(), (a, b) -> a));
        return ids.stream().map(map::get).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
    }

    private <T> void appendMissing(List<T> target, List<T> candidates, Function<T, Long> idGetter, int limit) {
        Set<Long> exists = target.stream().map(idGetter).collect(Collectors.toSet());
        for (T candidate : candidates) {
            if (target.size() >= limit) {
                return;
            }
            Long id = idGetter.apply(candidate);
            if (id != null && exists.add(id)) {
                target.add(candidate);
            }
        }
    }

    private <T> List<T> limitList(List<T> list, int limit) {
        return list.stream().limit(limit).collect(Collectors.toList());
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return 6;
        }
        return Math.min(limit, 50);
    }

    private Long toLong(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }

    private Map<String, Object> buildFootprintItem(UserBehavior behavior) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", behavior.getId());
        item.put("targetId", behavior.getTargetId());
        item.put("targetType", behavior.getTargetType());
        item.put("createTime", behavior.getCreateTime());

        String typeName;
        String title = "";
        String cover = "";
        String routePath = "";

        switch (behavior.getTargetType()) {
            case TARGET_TYPE_SCENIC -> {
                typeName = "景区";
                ScenicArea scenic = scenicAreaMapper.selectById(behavior.getTargetId());
                if (scenic != null) {
                    title = scenic.getName();
                    cover = scenic.getCoverImage();
                    routePath = "/scenic/" + behavior.getTargetId();
                }
            }
            case TARGET_TYPE_STRATEGY -> {
                typeName = "攻略";
                Strategy strategy = strategyMapper.selectById(behavior.getTargetId());
                if (strategy != null) {
                    title = strategy.getTitle();
                    cover = strategy.getCoverImage();
                    routePath = "/strategy/" + behavior.getTargetId();
                }
            }
            case TARGET_TYPE_TRIP -> {
                typeName = "行程";
                Trip trip = tripMapper.selectById(behavior.getTargetId());
                if (trip != null) {
                    title = trip.getTitle();
                    cover = trip.getCoverImage();
                    routePath = "/trip/" + behavior.getTargetId();
                }
            }
            case TARGET_TYPE_FOOD -> {
                typeName = "美食";
                Food food = foodMapper.selectById(behavior.getTargetId());
                if (food != null) {
                    title = food.getName();
                    cover = food.getCoverImage();
                    routePath = "/food/" + behavior.getTargetId();
                }
            }
            case TARGET_TYPE_ATTRACTION -> {
                typeName = "景点";
                Attraction attraction = attractionMapper.selectById(behavior.getTargetId());
                if (attraction != null) {
                    title = attraction.getName();
                    cover = attraction.getCoverImage();
                    routePath = "/attraction/" + behavior.getTargetId();
                }
            }
            default -> {
                return Map.of();
            }
        }

        if (title == null || title.isBlank()) {
            return Map.of();
        }
        item.put("typeName", typeName);
        item.put("title", title);
        item.put("cover", cover);
        item.put("routePath", routePath);
        return item;
    }
}
