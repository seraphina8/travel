package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long scenicId;
    
    private Long ticketId;
    
    private Integer quantity;
    
    private BigDecimal totalAmount;
    
    private LocalDate visitDate;
    
    private Integer status;
    
    private LocalDateTime payTime;
    
    private String payType;
    
    private String alipayTradeNo;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(exist = false)
    private String username;
    
    @TableField(exist = false)
    private String scenicName;
    
    @TableField(exist = false)
    private String ticketName;
    
    @TableField(exist = false)
    private String scenicCover;
}
