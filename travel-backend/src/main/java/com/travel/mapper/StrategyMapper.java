package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Strategy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StrategyMapper extends BaseMapper<Strategy> {
    
    @Select("SELECT s.*, u.username FROM strategy s " +
            "LEFT JOIN user u ON s.user_id = u.id WHERE s.id = #{id}")
    Strategy selectWithUser(Long id);
    
    @Select("SELECT tags, COUNT(*) as count FROM strategy " +
            "WHERE status = 1 GROUP BY tags ORDER BY count DESC LIMIT 10")
    List<Map<String, Object>> getTagStats();
}
