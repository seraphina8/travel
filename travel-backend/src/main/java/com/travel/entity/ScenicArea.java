package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("scenic_area")
public class ScenicArea {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String description;
    
    private String address;
    
    private String province;
    
    private String city;
    
    private String coverImage;
    
    private String images;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    private String openTime;
    
    private String level;

    private String tags;
    
    private Integer status;
    
    private Integer viewCount;
    
    private Integer collectCount;

    private BigDecimal rating;

    private Integer ratingCount;
    
    private BigDecimal price;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
