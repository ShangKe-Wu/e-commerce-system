package com.dhu.ecommercesystem.controller.mall;

import com.dhu.ecommercesystem.common.Constants;
import com.dhu.ecommercesystem.common.NewBeeMallException;
import com.dhu.ecommercesystem.common.NewBeeMallOrderStatusEnum;
import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.MallUser;
import com.dhu.ecommercesystem.entity.Order;
import com.dhu.ecommercesystem.entity.ShoppingCartItem;
import com.dhu.ecommercesystem.entity.vo.OrderDetailVO;
import com.dhu.ecommercesystem.entity.vo.ShoppingCartItemVO;
import com.dhu.ecommercesystem.entity.vo.UserVO;
import com.dhu.ecommercesystem.service.OrderService;
import com.dhu.ecommercesystem.service.ShoppingCartService;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;
import com.dhu.ecommercesystem.util.Result;
import com.dhu.ecommercesystem.util.ResultGenerator;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author:WuShangke
 * @create:2022/7/23-18:26
 */
@Controller
public class OrderController {
    @Resource
    OrderService orderService;
    @Resource
    ShoppingCartService shoppingCartService;

    //获取订单详情
    @GetMapping("/orders/{orderNo}")
    public String OrderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession session){
        UserVO user =(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        OrderDetailVO orderDetailVO = orderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        request.setAttribute("orderDetailVO",orderDetailVO);
        return "mall/order-detail";
    }

    //获取用户订单列表
    @GetMapping("/orders")
    public String myOrderList(@RequestParam Map<String,Object> params, HttpServletRequest request,HttpSession session){
        UserVO user=(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId",user.getUserId());
        if(StringUtils.isEmpty(params.get("page"))){
            params.put("page",1);
        }
        params.put("limit",Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageQueryUtil=new PageQueryUtil(params);
        PageResult myOrderPage = orderService.getMyOrderPage(pageQueryUtil);
        request.setAttribute("orderPageResult",myOrderPage);
        request.setAttribute("path","orders");
        return "mall/my-orders";
    }

    //保存订单
    @GetMapping("/saveOrder")
    public String saveOrders(HttpSession session){
        UserVO userVO =(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        //用户地址为空
        if(StringUtils.isEmpty(userVO.getAddress())){
            NewBeeMallException.fail("收货地址为空，请先填写地址");
        }
        //获取用户购物车中的数据
        List<ShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(userVO.getUserId());
        if(CollectionUtils.isEmpty(myShoppingCartItems)){
            NewBeeMallException.fail("购物车数据为空");
        }
        String orderNo = orderService.saveOrder(userVO, myShoppingCartItems);//生成订单号
        //重定向
        return "redirect:/orders/"+orderNo;
    }

    //取消订单（用户自己关闭）
    @ResponseBody
    @PutMapping("/orders/{orderNo}/cancel")
    public Result cancelOrders(HttpSession session,@PathVariable("orderNo") String orderNo){
        UserVO user=(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String result = orderService.cancelOrder(orderNo, user.getUserId());
        if(result.equals(ServiceResultEnum.SUCCESS.getResult())){
            return ResultGenerator.genSuccessResult();
        }
        else{
            return ResultGenerator.genFailResult(result);
        }
    }

    //完成订单
    @ResponseBody
    @PutMapping("/orders/{orderNo}/finish")
    public Result finishOrders(HttpSession session,@PathVariable("orderNo") String orderNo){
        UserVO userVO=(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String result = orderService.finishOrder(orderNo, userVO.getUserId());
        if(result.equals(ServiceResultEnum.SUCCESS.getResult())){
            return ResultGenerator.genSuccessResult();
        }
        else{
            return ResultGenerator.genFailResult(result);
        }
    }

    //选择支付方式
    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request,HttpSession session,@RequestParam String orderNo){
        UserVO userVO=(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order order = orderService.getOrderByOrderNo(orderNo);
        //判断order的用户id是否一致
        if(order.getUserId()!=userVO.getUserId()){
            NewBeeMallException.fail("用户不匹配");
        }
        //判断订单状态
        if(order.getOrderStatus()!= NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
            NewBeeMallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        }
        //保存到session中
        request.setAttribute("orderNo",orderNo);
        request.setAttribute("totalPrice",order.getTotalPrice());
        return "mall/pay-select";
    }

    //支付页面
    @GetMapping("/payPage")
    public String payPage(HttpSession session,HttpServletRequest request,@RequestParam("orderNo") String orderNo,@RequestParam("payType") int payType){
        UserVO userVO=(UserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Order order = orderService.getOrderByOrderNo(orderNo);
        //判断用户ID
        if(userVO.getUserId()!= order.getUserId()){
            NewBeeMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        //判断订单状态
        if(order.getOrderStatus()!=NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
            NewBeeMallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        }
        request.setAttribute("totalPrice",order.getTotalPrice());
        request.setAttribute("orderNo",orderNo);
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }

    //支付成功
    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo,@RequestParam("payType") int payType){
        String result = orderService.payOrder(orderNo, payType);
        if(result.equals(ServiceResultEnum.SUCCESS.getResult())){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }
}
