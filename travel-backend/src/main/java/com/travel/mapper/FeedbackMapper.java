package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
    
    @Select("SELECT f.*, u.username FROM feedback f " +
            "LEFT JOIN user u ON f.user_id = u.id WHERE f.id = #{id}")
    Feedback selectWithUser(Long id);
}
