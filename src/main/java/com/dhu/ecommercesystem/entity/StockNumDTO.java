package com.dhu.ecommercesystem.entity;

import lombok.Data;

/**
 * @author:WuShangke
 * @create:2022/7/15-20:43
 * 用户下单后，库存修改所需实体
 */
@Data
public class StockNumDTO {
    private Long goodsId;//用户购买的商品的ID
    private Integer goodsCount;//用户购买的商品的数量
}
