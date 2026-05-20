package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("tag")
public class Tag {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private Integer type;
    
    private Integer sortOrder;
    
    private Integer status;
}
