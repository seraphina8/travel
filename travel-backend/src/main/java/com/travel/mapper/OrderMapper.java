package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

//    查询订单详情，同时带出用户名、景区名、门票名、景区封面。
    @Select("SELECT o.*, u.username, s.name as scenic_name, t.name as ticket_name, s.cover_image as scenic_cover " +
            "FROM `order` o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "LEFT JOIN scenic_area s ON o.scenic_id = s.id " +
            "LEFT JOIN ticket t ON o.ticket_id = t.id " +
            "WHERE o.id = #{id}")
    Order selectWithDetails(Long id);

//    分页查询当前用户的订单。
    IPage<Order> selectMyOrdersWithDetails(Page<Order> page, @Param("userId") Long userId, @Param("status") Integer status);
    
    @Select("SELECT COUNT(*) FROM `order`")
    Long countOrders();
    
    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM `order` WHERE status = 1")
    BigDecimal sumPaidAmount();

//    查询最近 6 个月订单数量，用于后台图表。
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as month, COUNT(*) as count " +
            "FROM `order` WHERE create_time >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
            "GROUP BY month ORDER BY month")
    List<Map<String, Object>> getMonthlyStats();
}
