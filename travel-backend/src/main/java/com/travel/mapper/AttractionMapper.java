package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Attraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface AttractionMapper extends BaseMapper<Attraction> {
    
    @Select("SELECT a.*, s.name as scenic_name FROM attraction a " +
            "LEFT JOIN scenic_area s ON a.scenic_id = s.id WHERE a.id = #{id}")
    Attraction selectWithScenic(Long id);

//    获取浏览量前 10 的热门景点。
    @Select("SELECT name, view_count AS viewCount FROM attraction ORDER BY view_count DESC LIMIT 10")
    List<Map<String, Object>> getHotAttractions();

    @Update("UPDATE attraction SET rating = #{rating}, rating_count = #{ratingCount} WHERE id = #{id}")
    int updateRating(@Param("id") Long id, @Param("rating") java.math.BigDecimal rating, @Param("ratingCount") Integer ratingCount);
}
