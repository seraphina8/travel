package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {
    
    @Select("SELECT s.*, u.nickname as target_name, u.avatar as target_avatar " +
            "FROM chat_session s LEFT JOIN user u ON s.target_id = u.id " +
            "WHERE s.user_id = #{userId} ORDER BY s.update_time DESC")
    List<ChatSession> getSessionsByUserId(@Param("userId") Long userId);
}
