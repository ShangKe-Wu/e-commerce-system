package com.dhu.ecommercesystem.service;

import com.dhu.ecommercesystem.entity.ShoppingCartItem;
import com.dhu.ecommercesystem.entity.vo.ShoppingCartItemVO;

import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/22-20:25
 */
public interface ShoppingCartService {
    // 增
    String saveShoppingCartItem(ShoppingCartItem shoppingCartItem);
    // 删
    //要不要来个批量删除？
    Boolean deleteShoppingCartItem(Long CartItemId,Long userId);
    // 改
    String updateShoppingCartItem(ShoppingCartItem shoppingCartItem);
    // 查
    ShoppingCartItem getShoppingCartItemById(Long Id);
    // 获取用户购物车中的列表数据
    List<ShoppingCartItemVO> getMyShoppingCartItems(Long userId);
}
