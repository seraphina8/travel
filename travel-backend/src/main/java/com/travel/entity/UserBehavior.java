package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_behavior")
public class UserBehavior {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long targetId;
    
    private Integer targetType;
    
    private Integer behaviorType;
    
    private Double score;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
