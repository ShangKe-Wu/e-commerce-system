package com.dhu.ecommercesystem.mapper;

import com.dhu.ecommercesystem.entity.Carousel;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/15-21:56
 */
@Mapper
public interface CarouselMapper {
    // 增
    int insert(Carousel record);
    int insertSelective(Carousel record);
    // 删
    int deleteByPrimaryKey(@Param("id") Integer id);
    int deleteBatch(Integer[] ids);
    // 改
    int updateByPrimaryKey(Carousel record);
    int updateSelective(Carousel record);
    // 查
    Carousel selectByPrimaryKey(@Param("id") Integer id);
    List<Carousel> findCarouselList(PageQueryUtil pageUtil);
    int getTotalCarousels(PageQueryUtil pageUtil);
    //返回number个轮播图
    List<Carousel> findCarouselsByNum(@Param("number") int number);
}
