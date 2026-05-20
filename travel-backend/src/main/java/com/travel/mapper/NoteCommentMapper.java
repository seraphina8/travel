package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.NoteComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NoteCommentMapper extends BaseMapper<NoteComment> {
    
    @Select("SELECT c.*, u.nickname as username, u.avatar as user_avatar " +
            "FROM note_comment c LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.note_id = #{noteId} AND c.status = 1 " +
            "ORDER BY c.create_time DESC")
    List<NoteComment> selectCommentsWithUser(@Param("noteId") Long noteId);
}
