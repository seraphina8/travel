package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.UserFootprint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserFootprintMapper extends BaseMapper<UserFootprint> {
    
    @Select("SELECT province FROM user_footprint WHERE user_id = #{userId}")
    List<String> getVisitedProvinces(@Param("userId") Long userId);
}
