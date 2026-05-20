package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    @Select("SELECT c.*, u.username, u.avatar as user_avatar " +
            "FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.target_type = #{targetType} AND c.target_id = #{targetId} AND c.status = 1 " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectCommentsWithUser(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    @Select("SELECT COALESCE(ROUND(AVG(rating), 1), 0) FROM comment " +
            "WHERE target_type = #{targetType} AND target_id = #{targetId} AND status = 1 AND rating IS NOT NULL")
    BigDecimal selectAverageRating(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    @Select("SELECT COUNT(*) FROM comment " +
            "WHERE target_type = #{targetType} AND target_id = #{targetId} AND status = 1 AND rating IS NOT NULL")
    Integer selectRatingCount(@Param("targetType") String targetType, @Param("targetId") Long targetId);
}
