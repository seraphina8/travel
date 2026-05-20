package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("note_comment")
public class NoteComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long noteId;
    
    private Long userId;
    
    private String content;
    
    private Long parentId;
    
    private Integer likeCount;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String userAvatar;
    
    @TableField(exist = false)
    private String replyToUsername;
}
