package com.dhu.ecommercesystem.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:WuShangke
 * @create:2022/7/19-20:56
 * 订单详情页面的订单项
 */
@Data
public class OrderItemVO implements Serializable {
    private Long goodsId;

    private Integer goodsCount;

    private String goodsName;

    private String goodsCoverImg;

    private Integer sellingPrice;
}
