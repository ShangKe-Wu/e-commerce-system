package com.dhu.ecommercesystem.controller.mall;

import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.common.NewBeeMallException;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.ShoppingCartItem;
import com.dhu.ecommercesystem.entity.vo.ShoppingCartItemVO;
import com.dhu.ecommercesystem.entity.vo.UserVO;
import com.dhu.ecommercesystem.service.ShoppingCartService;
import com.dhu.ecommercesystem.util.Result;
import com.dhu.ecommercesystem.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author:WuShangke
 * @create:2022/7/24-21:07
 */
@Controller
public class ShoppingCartController {
    @Resource
    ShoppingCartService shoppingCartService;

    // 查，获取购物车页面
    @GetMapping("/shop-cart")
    public String cartListPage(HttpSession session, HttpServletRequest request){
        UserVO user=(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<ShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        int itemsTotal =0;//购物车商品数量
        int priceTotal=0;//购物车商品总价格
        if(!CollectionUtils.isEmpty(myShoppingCartItems)){
            //遍历List，计算数量和价格
            for(ShoppingCartItemVO vo : myShoppingCartItems){
                itemsTotal += vo.getGoodsCount();
                priceTotal += vo.getGoodsCount()*vo.getSellingPrice();
            }
            if(itemsTotal<1){
                NewBeeMallException.fail("购物车为空");
            }
            if(priceTotal<1){
                NewBeeMallException.fail("价格异常");
            }
        }
        request.setAttribute("myShoppingCartItems",myShoppingCartItems);
        request.setAttribute("itemsTotal",itemsTotal);
        request.setAttribute("priceTotal",priceTotal);
        return "mall/cart";
    }

    // 增，在商品详情页面点击加入购物车
    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveShoppingCartItem(HttpSession session, @RequestBody ShoppingCartItem shoppingCartItem){
        UserVO user = (UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        //传过来的数据没有userId
        shoppingCartItem.setUserId(user.getUserId());
        String result = shoppingCartService.saveShoppingCartItem(shoppingCartItem);
        //成功
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        //失败
        return ResultGenerator.genFailResult(result);
    }

    // 删
    @DeleteMapping("/shop-cart/{shoppingCartItemId}")
    @ResponseBody
    public Result deleteShoppingCartItem(HttpSession session,
                                         @PathVariable("shoppingCartItemId") Long shoppingCartItemId){
        UserVO user =(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean flag = shoppingCartService.deleteShoppingCartItem(shoppingCartItemId, user.getUserId());
        if(flag){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
    }

    // 改
    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateShoppingCartItem(HttpSession session,@RequestBody ShoppingCartItem shoppingCartItem){
        UserVO userVO=(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        //传过来的数据没有userId
        shoppingCartItem.setUserId(userVO.getUserId());
        String result = shoppingCartService.updateShoppingCartItem(shoppingCartItem);
        //修改成功
        if(ServiceResultEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(result);
    }

    //跳转到结算页面
    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpSession session,HttpServletRequest request){
        UserVO user=(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<ShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        request.setAttribute("myShoppingCartItems",myShoppingCartItems);
        int priceTotal=0;
        //没有数据则返回购物车
        if(CollectionUtils.isEmpty(myShoppingCartItems)){
            return "/cart";
        }
        //计算总价
        for(ShoppingCartItemVO vo:myShoppingCartItems){
            priceTotal += vo.getGoodsCount()*vo.getSellingPrice();
        }
        if(priceTotal<1){
            NewBeeMallException.fail("购物车价格异常");
        }
        request.setAttribute("priceTotal",priceTotal);
        return "mall/order-settle";
    }
}
