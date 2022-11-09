package com.dhu.ecommercesystem.mapper;

import com.dhu.ecommercesystem.entity.ShoppingCartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/18-21:33
 */
@Mapper
public interface ShoppingCartItemMapper {
    // 增
    int insert(ShoppingCartItem record);
    int insertSelective(ShoppingCartItem record);
    // 删
    int deleteByPrimaryKey(Long cartItemId);
    int deleteBatch(List<Long> ids);
    // 改
    int updateByPrimaryKeySelective(ShoppingCartItem record);
    int updateByPrimaryKey(ShoppingCartItem record);
    // 查
    ShoppingCartItem selectByPrimaryKey(Long cartItemId);
    ShoppingCartItem selectByUserIdAndGoodsId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("goodsId") Long goodsId);
    List<ShoppingCartItem> selectByUserId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("number") int number);
    int selectCountByUserId(Long newBeeMallUserId);
}
