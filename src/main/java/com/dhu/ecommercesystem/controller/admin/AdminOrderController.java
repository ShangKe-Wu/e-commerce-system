package com.dhu.ecommercesystem.controller.admin;

import com.dhu.ecommercesystem.common.ServiceResultEnum;
import com.dhu.ecommercesystem.entity.Order;
import com.dhu.ecommercesystem.entity.vo.OrderItemVO;
import com.dhu.ecommercesystem.service.OrderService;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.Result;
import com.dhu.ecommercesystem.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author:WuShangke
 * @create:2022/7/26-14:31
 */
@Controller
@RequestMapping("/admin")
public class AdminOrderController {
    @Resource
    OrderService orderService;


    //页面跳转
    @GetMapping("/orders")
    public String orderPage(HttpServletRequest request){
        request.setAttribute("path","orders");
        return "admin/newbee_mall_order";
    }

    //获取订单列表
    @GetMapping("/orders/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(orderService.getOrderPage(pageUtil));
    }

    //查看订单详情
    @GetMapping("/order-items/{orderId}")
    @ResponseBody
    public Result info(@PathVariable("orderId") Long orderId){
        List<OrderItemVO> orderItems = orderService.getOrderItems(orderId);
        //如果为空
        if(CollectionUtils.isEmpty(orderItems)){
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(orderItems);
    }

    //修改订单
    @PostMapping("/orders/update")
    @ResponseBody
    public Result update(@RequestBody Order order){
        if (Objects.isNull(order.getTotalPrice())
                || Objects.isNull(order.getOrderId())
                || order.getOrderId() < 1
                || order.getTotalPrice() < 1
                || StringUtils.isEmpty(order.getUserAddress())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = orderService.updateOrderInfo(order);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    //关闭订单
    @ResponseBody
    @PostMapping("/orders/close")
    public Result closeOrder(@RequestBody Long ids[]){
        if(ids.length<1){
            return ResultGenerator.genFailResult("参数异常!");
        }
        String result = orderService.closeOrder(Arrays.asList(ids));
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    //出库 checkOut
    @PostMapping("/orders/checkOut")
    @ResponseBody
    public Result checkOut(@RequestBody Long ids[]){
        if(ids.length<1){
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = orderService.checkOut(Arrays.asList(ids));
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    //配货 checkDone
    @ResponseBody
    @PostMapping("/orders/checkDone")
    public Result checkDone(@RequestBody Long ids[]){
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = orderService.checkDone(Arrays.asList(ids));
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }
}
