package com.dhu.ecommercesystem.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:WuShangke
 * @create:2022/7/19-20:51
 * 首页轮播图
 */
@Data
public class IndexCarouselVO implements Serializable {
    private String carouselUrl;

    private String redirectUrl;
}
