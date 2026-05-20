package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_session")
public class ChatSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long targetId;
    
    private String lastMessage;
    
    private Integer unreadCount;
    
    private LocalDateTime updateTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String targetName;
    
    @TableField(exist = false)
    private String targetAvatar;
}
