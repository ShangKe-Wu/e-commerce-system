package com.dhu.ecommercesystem.mapper;

import com.dhu.ecommercesystem.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/18-21:05
 */
@Mapper
public interface OrderItemMapper {
    // 增
    int insert(OrderItem record);
    int insertSelective(OrderItem record);
    int insertBatch(@Param("orderItems") List<OrderItem> orderItems);
    // 删
    int deleteByPrimaryKey(Long orderItemId);
    // 改
    int updateByPrimaryKeySelective(OrderItem record);
    int updateByPrimaryKey(OrderItem record);
    // 查
    OrderItem selectByPrimaryKey(Long orderItemId);
    List<OrderItem> selectByOrderId(Long orderId);
    List<OrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);
}
