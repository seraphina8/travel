package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.entity.TravelNote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TravelNoteMapper extends BaseMapper<TravelNote> {
    
    @Select("<script>" +
            "SELECT n.*, u.nickname as username, u.avatar as user_avatar " +
            "FROM travel_note n LEFT JOIN user u ON n.user_id = u.id " +
            "WHERE n.status = 1 " +
            "<if test='province != null and province != \"\"'> AND n.province = #{province} </if>" +
            "<if test='tag != null and tag != \"\"'> " +
            "AND (n.tags LIKE CONCAT('%', #{tag}, '%') " +
            "OR n.title LIKE CONCAT('%', #{tag}, '%') " +
            "OR n.content LIKE CONCAT('%', #{tag}, '%') " +
            "OR n.province LIKE CONCAT('%', #{tag}, '%') " +
            "OR n.city LIKE CONCAT('%', #{tag}, '%')) " +
            "</if>" +
            "ORDER BY n.create_time DESC" +
            "</script>")
    Page<TravelNote> selectNotesWithUser(Page<TravelNote> page, @Param("province") String province, @Param("tag") String tag);
    
    @Select("SELECT n.*, u.nickname as username, u.avatar as user_avatar " +
            "FROM travel_note n LEFT JOIN user u ON n.user_id = u.id " +
            "WHERE n.id = #{id}")
    TravelNote selectNoteWithUser(@Param("id") Long id);
}
