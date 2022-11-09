package com.dhu.ecommercesystem.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author:WuShangke
 * @create:2022/7/15-20:39
 */
@Data
public class ShoppingCartItem {
    private Long cartItemId;

    private Long userId;

    private Long goodsId;

    private Integer goodsCount;

    private Byte isDeleted;

    private Date createTime;

    private Date updateTime;
}
