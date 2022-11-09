package com.dhu.ecommercesystem.service;

import com.dhu.ecommercesystem.entity.Order;
import com.dhu.ecommercesystem.entity.vo.OrderDetailVO;
import com.dhu.ecommercesystem.entity.vo.OrderItemVO;
import com.dhu.ecommercesystem.entity.vo.ShoppingCartItemVO;
import com.dhu.ecommercesystem.entity.vo.UserVO;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/21-14:05
 */
public interface OrderService {
    // 增，保存订单
    String saveOrder(UserVO userVO, List<ShoppingCartItemVO> shoppingCartItemVOS);
    // 删，用户手动取消订单
    String cancelOrder(String orderNo,Long userId);
    // 改，修改订单信息
    String updateOrderInfo(Order order);
    // 查,获取订单详情
    OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);
    Order getOrderByOrderNo(String orderNo);
    // 后台分页
    PageResult getOrderPage(PageQueryUtil pageQueryUtil);
    //订单配货
    String checkDone(List<Long> orderIds);
    //出库
    String checkOut(List<Long> orderIds);
    //商家关闭订单
    String closeOrder(List<Long> orderIds);
    //我的订单列表
    PageResult getMyOrderPage(PageQueryUtil pageQueryUtil);
    //完成订单，确认收货
    String finishOrder(String orderNo,Long userId);
    //订单支付
    String payOrder(String orderNo,int payType);
    //获取订单项VO
    List<OrderItemVO> getOrderItems(Long OrderId);

}
