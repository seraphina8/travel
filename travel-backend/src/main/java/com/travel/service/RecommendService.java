package com.travel.service;

import com.travel.entity.ScenicArea;
import java.util.List;

public interface RecommendService {
    
    List<ScenicArea> getRecommendScenics(Long userId, int limit);
    
    List<ScenicArea> getHotScenics(int limit);
    
    void recordBehavior(Long userId, Long scenicId, int behaviorType);

    // 多类型推荐
    List<com.travel.entity.Strategy> getRecommendStrategies(Long userId, int limit);

    List<com.travel.entity.Trip> getRecommendTrips(Long userId, int limit);

    List<com.travel.entity.Food> getRecommendFoods(Long userId, int limit);

    List<com.travel.entity.Attraction> getRecommendAttractions(Long userId, int limit);

    void recordBehavior(Long userId, Long targetId, int targetType, int behaviorType);
    
    List<java.util.Map<String, Object>> getUserFootprints(Long userId, int limit);
}
