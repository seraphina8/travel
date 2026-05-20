package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_footprint")
public class UserFootprint {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String province;
    
    private LocalDateTime firstVisitTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
