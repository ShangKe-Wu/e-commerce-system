package com.dhu.ecommercesystem.service;

import com.dhu.ecommercesystem.entity.Carousel;
import com.dhu.ecommercesystem.entity.vo.IndexCarouselVO;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/19-21:49
 * 轮播图
 */
public interface CarouselService {
    // 增
    String saveCarousel(Carousel carousel);
    // 删
    Boolean deleteBatch(Integer[] ids);
    // 改
    String updateCarousel(Carousel carousel);
    // 查
    Carousel getCarouselById(Integer id);
    //后台分页
    PageResult getCarouselPage(PageQueryUtil pageQueryUtil);
    //首页轮播图(返回number个)
    List<IndexCarouselVO> getCarouseForIndex(int number);
}
