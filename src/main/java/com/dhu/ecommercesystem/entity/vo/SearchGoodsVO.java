package com.dhu.ecommercesystem.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:WuShangke
 * @create:2022/7/19-21:03
 * 搜索结果页面VO
 */
@Data
public class SearchGoodsVO implements Serializable {
    private Long goodsId;

    private String goodsName;

    private String goodsIntro;

    private String goodsCoverImg;

    private Integer sellingPrice;
}
