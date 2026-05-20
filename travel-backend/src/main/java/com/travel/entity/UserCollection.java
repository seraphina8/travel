package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("collection")
public class UserCollection {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long targetId;
    
    private Integer type;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String targetName;
    
    @TableField(exist = false)
    private String targetImage;
}
