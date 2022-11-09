package com.dhu.ecommercesystem.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:WuShangke
 * @create:2022/7/19-21:03
 * 购物车页面VO
 */
@Data
public class ShoppingCartItemVO implements Serializable {
    private Long cartItemId;

    private Long goodsId;

    private Integer goodsCount;

    private String goodsName;

    private String goodsCoverImg;

    private Integer sellingPrice;
}
