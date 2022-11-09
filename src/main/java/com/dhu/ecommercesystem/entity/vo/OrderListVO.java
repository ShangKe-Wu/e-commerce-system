package com.dhu.ecommercesystem.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/19-20:57
 * 订单列表页面VO
 */
@Data
public class OrderListVO implements Serializable {
    private Long orderId;

    private String orderNo;

    private Integer totalPrice;

    private Byte payType;

    private Byte orderStatus;

    private String orderStatusString;

    private String userAddress;

    private Date createTime;

    private List<OrderItemVO> newBeeMallOrderItemVOS;
}
