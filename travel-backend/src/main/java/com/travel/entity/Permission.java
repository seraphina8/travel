package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.List;

@Data
@TableName("permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String code;
    
    private Integer type;
    
    private Long parentId;
    
    private String path;
    
    private String icon;
    
    private Integer sortOrder;
    
    private Integer status;
    
    @TableField(exist = false)
    private List<Permission> children;
}
