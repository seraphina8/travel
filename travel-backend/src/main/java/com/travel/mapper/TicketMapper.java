package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
    
    @Select("SELECT t.*, s.name as scenic_name FROM ticket t " +
            "LEFT JOIN scenic_area s ON t.scenic_id = s.id WHERE t.id = #{id}")
    Ticket selectWithScenic(Long id);
}
