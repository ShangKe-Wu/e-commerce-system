package com.dhu.ecommercesystem.interceptor;

import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.entity.vo.UserVO;
import com.dhu.ecommercesystem.mapper.ShoppingCartItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author:WuShangke
 * @create:2022/7/26-15:39
 * 购物车拦截
 */
@Component
public class CartNumberInterceptor implements HandlerInterceptor {
    @Autowired
    ShoppingCartItemMapper shoppingCartItemMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //购物车中的数量会改变
        if(null != request.getSession() && null != request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY)){
            //如果用户已经登录，则修改购物车中的商品的数量
            UserVO user = (UserVO) request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY);
            int count = shoppingCartItemMapper.selectCountByUserId(user.getUserId());
            //更新用户信息
            user.setShopCartItemCount(count);
            request.getSession().setAttribute(Constants.MALL_USER_SESSION_KEY,user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
