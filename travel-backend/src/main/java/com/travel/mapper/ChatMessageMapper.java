package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    
    @Select("SELECT m.*, u.nickname as sender_name, u.avatar as sender_avatar " +
            "FROM chat_message m LEFT JOIN user u ON m.sender_id = u.id " +
            "WHERE (m.sender_id = #{userId} AND m.receiver_id = #{targetId}) " +
            "OR (m.sender_id = #{targetId} AND m.receiver_id = #{userId}) " +
            "ORDER BY m.create_time ASC LIMIT #{limit}")
    List<ChatMessage> getChatHistory(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("limit") int limit);
    
    @Update("UPDATE chat_message SET status = 1 WHERE sender_id = #{senderId} AND receiver_id = #{receiverId} AND status = 0")
    int markAsRead(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}
