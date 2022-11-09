package com.dhu.ecommercesystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author:WuShangke
 * @create:2022/7/15-20:07
 * 轮播图实体类
 */
@Data
@ToString
public class Carousel {
    private Integer carouselId;
    private String carouselUrl;
    private String redirectUrl;
    private Integer carouselRank;
    private Byte isDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    private Integer updateUser;

}
