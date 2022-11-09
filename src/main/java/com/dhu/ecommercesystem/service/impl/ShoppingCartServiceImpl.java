package com.dhu.ecommercesystem.service.impl;

import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.GoodsInfo;
import com.dhu.ecommercesystem.entity.ShoppingCartItem;
import com.dhu.ecommercesystem.entity.vo.ShoppingCartItemVO;
import com.dhu.ecommercesystem.mapper.GoodsInfoMapper;
import com.dhu.ecommercesystem.mapper.ShoppingCartItemMapper;
import com.dhu.ecommercesystem.service.ShoppingCartService;
import com.dhu.ecommercesystem.util.BeanUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author:WuShangke
 * @create:2022/7/22-20:26
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Resource
    ShoppingCartItemMapper shoppingCartItemMapper;
    @Resource
    GoodsInfoMapper goodsInfoMapper;

    @Override
    public String saveShoppingCartItem(ShoppingCartItem shoppingCartItem) {
        //判断用户的购物车中是否已经存在该商品
        ShoppingCartItem temp = shoppingCartItemMapper.selectByUserIdAndGoodsId(shoppingCartItem.getUserId(), shoppingCartItem.getGoodsId());
        if(temp!=null){
            //存在则直接更新
            temp.setGoodsCount(shoppingCartItem.getGoodsCount()+1);
            temp.setUpdateTime(new Date());
            return updateShoppingCartItem(temp);
        }
        //判断商品状态，是否下架，或者不存在
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(shoppingCartItem.getGoodsId());
        if(goodsInfo==null){
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        if(goodsInfo.getGoodsSellStatus() == Constants.SELL_STATUS_DOWN){
            return ServiceResultEnum.GOODS_PUT_DOWN.getResult();
        }
        //判断商品库存
        if(goodsInfo.getStockNum()<shoppingCartItem.getGoodsCount()){
            return ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult();
        }
        //单个商品的数量不能超过指定值
        if(shoppingCartItem.getGoodsCount()> Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //用户购物车能存放的数量不能超过指定值
        int total = shoppingCartItemMapper.selectCountByUserId(shoppingCartItem.getUserId());
        if(total>Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        if(shoppingCartItemMapper.insertSelective(shoppingCartItem)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean deleteShoppingCartItem(Long CartItemId, Long userId) {
        ShoppingCartItem shoppingCartItem = shoppingCartItemMapper.selectByPrimaryKey(CartItemId);
        //不存在则无法删除
        if(shoppingCartItem==null){
            return false;
        }
        //用户ID不同则无法删除
        if(shoppingCartItem.getUserId()!=userId){
            return false;
        }
        int i = shoppingCartItemMapper.deleteByPrimaryKey(CartItemId);
        if(i>0){
            return true;
        }
        return false;
    }

    @Override
    public String updateShoppingCartItem(ShoppingCartItem shoppingCartItem) {
        //如果不存在数据则无法更新
        ShoppingCartItem temp = shoppingCartItemMapper.selectByUserIdAndGoodsId(shoppingCartItem.getUserId(),shoppingCartItem.getGoodsId());
        if(temp==null){
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if(shoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //用户ID不匹配则无法更新
        if(shoppingCartItem.getUserId()!= temp.getUserId()){
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        //购物车内数据相同则不更新
        if(shoppingCartItem.getGoodsCount()==temp.getGoodsCount()){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        temp.setGoodsCount(shoppingCartItem.getGoodsCount());
        temp.setUpdateTime(new Date());
        if(shoppingCartItemMapper.updateByPrimaryKeySelective(temp)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public ShoppingCartItem getShoppingCartItemById(Long Id) {
        return shoppingCartItemMapper.selectByPrimaryKey(Id);
    }

    @Override
    public List<ShoppingCartItemVO> getMyShoppingCartItems(Long userId) {
        List<ShoppingCartItemVO> shoppingCartItemVOS = new ArrayList<>();
        List<ShoppingCartItem> shoppingCartItems = shoppingCartItemMapper.selectByUserId(userId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if(!CollectionUtils.isEmpty(shoppingCartItems)){
            //查询购物车中的商品信息
            List<Long> goodsIds = shoppingCartItems.stream().map(ShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<GoodsInfo> goodsInfos = goodsInfoMapper.selectByPrimaryKeys(goodsIds);
            //转换为以goodsId为主键的map
            Map<Long,GoodsInfo> goodsInfoMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(goodsInfos)){
                goodsInfoMap=goodsInfos.stream().collect(Collectors.toMap(GoodsInfo::getGoodsId, Function.identity(),(entity1,entity2)->entity1));
            }
            //遍历购物车
            for(ShoppingCartItem shoppingCartItem:shoppingCartItems){
                ShoppingCartItemVO vo = new ShoppingCartItemVO();
                BeanUtil.copyProperties(shoppingCartItem,vo);
                if(goodsInfoMap.containsKey(shoppingCartItem.getGoodsId())){
                    //存在商品，则对VO的信息进行更新：名称，封面，售价
                    GoodsInfo temp = goodsInfoMap.get(shoppingCartItem.getGoodsId());
                    vo.setGoodsCoverImg(temp.getGoodsCoverImg());//封面
                    vo.setSellingPrice(temp.getSellingPrice());//售价
                    String goodsName = temp.getGoodsName();
                    //商品名称太长则进行修改
                    if(goodsName.length()>28){
                        goodsName = goodsName.substring(0,28)+"...";
                    }
                    vo.setGoodsName(goodsName);
                    shoppingCartItemVOS.add(vo);
                }
            }
        }
        return shoppingCartItemVOS;
    }
}
