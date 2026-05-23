package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {

//    查询当前用户对不同资源的行为总分。
    @Select("SELECT target_id, SUM(score) as total_score FROM user_behavior " +
            "WHERE user_id = #{userId} AND target_type = #{targetType} GROUP BY target_id ORDER BY total_score DESC")
    List<Map<String, Object>> getUserTargetScores(@Param("userId") Long userId, @Param("targetType") Integer targetType);

//    根据共同操作过的资源查找相似用户。
    @Select("SELECT DISTINCT user_id FROM user_behavior WHERE target_id = #{targetId} AND target_type = #{targetType} AND user_id != #{userId}")
    List<Long> getSimilarUsers(@Param("targetId") Long targetId, @Param("targetType") Integer targetType, @Param("userId") Long userId);
    
    @Select({
            "<script>",
            "SELECT target_id, SUM(score) as total_score FROM user_behavior",
            "WHERE user_id IN",
            "<foreach collection='userIds' item='userId' open='(' separator=',' close=')'>",
            "#{userId}",
            "</foreach>",
            "AND target_type = #{targetType}",
            "AND target_id NOT IN",
            "(SELECT target_id FROM user_behavior WHERE user_id = #{currentUserId} AND target_type = #{targetType})",
            "GROUP BY target_id ORDER BY total_score DESC LIMIT #{limit}",
            "</script>"
    })

//    从相似用户行为中找出当前用户没操作过的资源，生成推荐 ID。
    List<Map<String, Object>> getRecommendTargetIds(@Param("userIds") List<Long> userIds,
                                                     @Param("currentUserId") Long currentUserId,
                                                     @Param("targetType") Integer targetType,
                                                     @Param("limit") int limit);

//    当用户没有行为数据时，返回热门资源。
    @Select("SELECT target_id, SUM(score) as total_score, COUNT(*) as cnt FROM user_behavior " +
            "WHERE target_type = #{targetType} GROUP BY target_id ORDER BY total_score DESC, cnt DESC LIMIT #{limit}")
    List<Map<String, Object>> getHotTargetIds(@Param("targetType") Integer targetType, @Param("limit") int limit);
}
