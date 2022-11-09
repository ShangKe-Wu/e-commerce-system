package com.dhu.ecommercesystem.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author:WuShangke
 * @create:2022/7/15-20:33
 * 订单里面的商品信息
 */
@Data
@ToString
public class OrderItem {
    private Long orderItemId;

    private Long orderId;

    private Long goodsId;

    private String goodsName;

    private String goodsCoverImg;

    private Integer sellingPrice;

    private Integer goodsCount;

    private Date createTime;
}
