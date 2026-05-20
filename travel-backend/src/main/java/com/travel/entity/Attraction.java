package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("attraction")
public class Attraction {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long scenicId;
    
    private String name;
    
    private String description;
    
    private String coverImage;
    
    private String province;
    
    private String city;

    private String tags;
    
    private BigDecimal longitude;
    
    private BigDecimal latitude;
    
    private Integer viewCount;
    
    private Integer collectCount;

    private BigDecimal rating;

    private Integer ratingCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String scenicName;
}
