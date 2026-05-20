package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("trip")
public class Trip {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private String description;
    
    private String coverImage;

    private String tags;
    
    private Integer days;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String routeData;
    
    private Integer status;
    
    private Integer isPublic; // 0私有 1公开
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String username;
}
