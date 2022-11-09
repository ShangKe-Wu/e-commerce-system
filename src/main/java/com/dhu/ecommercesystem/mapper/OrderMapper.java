package com.dhu.ecommercesystem.mapper;

import com.dhu.ecommercesystem.entity.Order;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/17-22:01
 */
@Mapper
public interface OrderMapper {
    // 增
    int insert(Order record);
    int insertSelective(Order record);
    // 删
    int deleteByPrimaryKey(Long orderId);
    // 改
    int updateByPrimaryKeySelective(Order record);
    int updateByPrimaryKey(Order record);

    int checkOut(@Param("orderIds") List<Long> orderIds);//商品出库，修改订单状态

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);//关闭订单，有3种可能

    int checkDone(@Param("orderIds") List<Long> asList);//配货完成，修改订单状态

    // 查
    Order selectByPrimaryKey(Long orderId);
    Order selectByOrderNo(String orderNo);

    List<Order> findNewBeeMallOrderList(PageQueryUtil pageUtil);
    int getTotalNewBeeMallOrders(PageQueryUtil pageUtil);

    List<Order> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

}
