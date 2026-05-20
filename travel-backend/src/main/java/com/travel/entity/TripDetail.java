package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("trip_detail")
public class TripDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long tripId;
    
    private Integer dayNum;
    
    private Long attractionId;
    
    private String attractionName;
    
    private String description;
    
    private Integer sortOrder;
}
