package com.dhu.ecommercesystem.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:WuShangke
 * @create:2022/7/19-20:48
 * 商品详情页
 */
@Data
public class GoodsDetailsVO implements Serializable {
    private Long goodsId;

    private String goodsName;

    private String goodsIntro;

    private String goodsCoverImg;

    private String[] goodsCarouselList;

    private Integer sellingPrice;

    private Integer originalPrice;

    private String goodsDetailContent;
}
