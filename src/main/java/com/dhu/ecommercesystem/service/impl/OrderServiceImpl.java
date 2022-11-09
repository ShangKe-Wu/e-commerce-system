package com.dhu.ecommercesystem.service.impl;

import com.dhu.ecommercesystem.common.*;
import com.dhu.ecommercesystem.entity.GoodsInfo;
import com.dhu.ecommercesystem.entity.Order;
import com.dhu.ecommercesystem.entity.OrderItem;
import com.dhu.ecommercesystem.entity.StockNumDTO;
import com.dhu.ecommercesystem.entity.vo.*;
import com.dhu.ecommercesystem.mapper.*;
import com.dhu.ecommercesystem.service.OrderService;
import com.dhu.ecommercesystem.util.BeanUtil;
import com.dhu.ecommercesystem.util.NumberUtil;
import com.dhu.ecommercesystem.util.PageQueryUtil;
import com.dhu.ecommercesystem.util.PageResult;
import org.apache.catalina.valves.ErrorReportValve;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author:WuShangke
 * @create:2022/7/21-14:05
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderItemMapper orderItemMapper;
    @Resource
    ShoppingCartItemMapper shoppingCartItemMapper;
    @Resource
    GoodsInfoMapper goodsInfoMapper;

    @Override
    @Transactional
    public String saveOrder(UserVO userVO, List<ShoppingCartItemVO> shoppingCartItemVOS) {
        List<Long> goodsIdList = shoppingCartItemVOS.stream().map(ShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<Long> cartItemIdList = shoppingCartItemVOS.stream().map(ShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<GoodsInfo> goodsInfos = goodsInfoMapper.selectByPrimaryKeys(goodsIdList);
        //判断购物车中的商品是否有已经下架的商品
        List<GoodsInfo> downGoods = goodsInfos.stream().filter(goodsTemp -> goodsTemp.getGoodsSellStatus() == Constants.SELL_STATUS_DOWN).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(downGoods)){
            //list非空，说明存在已经下架的商品
            NewBeeMallException.fail(downGoods.get(0).getGoodsName()+"已下架，无法生成订单");
        }
        Map<Long, GoodsInfo> goodsInfoMap = goodsInfos.stream().collect(Collectors.toMap(GoodsInfo::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断库存
        for(ShoppingCartItemVO vo :shoppingCartItemVOS){
            if(!goodsInfoMap.containsKey(vo.getGoodsId())){
                //商品不存在
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            if(goodsInfoMap.get(vo.getGoodsId()).getStockNum() < vo.getGoodsCount()){
                //库存不足
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //生成订单
        if(!CollectionUtils.isEmpty(goodsIdList) && !CollectionUtils.isEmpty(cartItemIdList) && !CollectionUtils.isEmpty(goodsInfos)){
            //删除购物车中的内容
            if(shoppingCartItemMapper.deleteBatch(cartItemIdList)>0){
                //修改商品库存
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(shoppingCartItemVOS,StockNumDTO.class);
                int update = goodsInfoMapper.updateStockNum(stockNumDTOS);
                if(update<1){
                    NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                Order order = new Order(); //这里没有自动生成orderId
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                order.setOrderNo(orderNo);
                Long userId = userVO.getUserId();
                String address = userVO.getAddress();
                String nickName = userVO.getNickName();
                order.setUserId(userId);
                order.setUserAddress(address);
                order.setUserName(nickName);
                //计算订单的总价格
                int total=0;
                for(ShoppingCartItemVO vo:shoppingCartItemVOS){
                    total+=vo.getGoodsCount()*vo.getSellingPrice();
                }
                if(total<1){
                    //商品价格异常
                    NewBeeMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                order.setTotalPrice(total);
                //订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo="";
                order.setExtraInfo(extraInfo);
                //保存订单项记录
                if(orderMapper.insertSelective(order)>0){
                    //加上这个试试
                    order = orderMapper.selectByOrderNo(orderNo);
                    List<OrderItem> orderItemList= new ArrayList<>();
                    for(ShoppingCartItemVO vo:shoppingCartItemVOS){
                        OrderItem orderItem = new OrderItem();
                        BeanUtil.copyProperties(vo,orderItem);
                        //OrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        //为啥我这没有获取到
                        orderItem.setOrderId(order.getOrderId());
                        orderItem.setCreateTime(new Date());
                        orderItemList.add(orderItem);
                    }
                    if(orderItemMapper.insertBatch(orderItemList)>0){
                        //操作成功后返回订单号，供controller跳转到订单详情
                        return orderNo;
                    }
                    else{
                        NewBeeMallException.fail(ServiceResultEnum.ORDER_GENERATE_ERROR.getResult());
                    }
                }
                else{
                    NewBeeMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
                }
            }
            else{
                NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
        }
        NewBeeMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //判断订单是否存在
        if(order!=null){
            //判断用户是否有权限进行操作
            if(!order.getUserId().equals(userId)){
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //判断订单状态,已成功或者已取消的订单不能进行关闭
            if(order.getOrderStatus().intValue()==NewBeeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
            ||  order.getOrderStatus().intValue()==NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
            || order.getOrderStatus().intValue()==NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
            || order.getOrderStatus().intValue()==NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()){
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            if(orderMapper.closeOrder(Collections.singletonList(order.getOrderId()),NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus())>0){
                //成功关闭
                return ServiceResultEnum.SUCCESS.getResult();
            }
            else{
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String updateOrderInfo(Order order) {
        Order temp = orderMapper.selectByPrimaryKey(order.getOrderId());
        //订单存在，且状态>=0,<3 才能更改（也就是配送前才能更改）
        if(temp!=null && temp.getOrderStatus()>=0 && temp.getOrderStatus()<3){
            temp.setTotalPrice(order.getTotalPrice());
            temp.setUpdateTime(new Date());
            temp.setUserAddress(order.getUserAddress());
            if(orderMapper.updateByPrimaryKeySelective(temp)>0){
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public OrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //订单不存在
        if(order==null){
            NewBeeMallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        //订单用户Id和userId不一样
        if(!order.getUserId().equals(userId)){
            NewBeeMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        //获取订单项
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderId(order.getOrderId());
        if(CollectionUtils.isEmpty(orderItemList)){
            NewBeeMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<OrderItemVO> orderItemVOS = BeanUtil.copyList(orderItemList,OrderItemVO.class);
        OrderDetailVO detailVO = new OrderDetailVO();
        BeanUtil.copyProperties(order,detailVO);
        //设置VO中一些没有赋值的属性
        detailVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(order.getOrderStatus()).getName());
        detailVO.setPayStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(order.getPayStatus()).getName());
        detailVO.setPayTypeString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(order.getPayType()).getName());
        detailVO.setNewBeeMallOrderItemVOS(orderItemVOS);
        return detailVO;
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        return order;
    }

    @Override
    public PageResult getOrderPage(PageQueryUtil pageQueryUtil) {
        List<Order> newBeeMallOrderList = orderMapper.findNewBeeMallOrderList(pageQueryUtil);
        int total = orderMapper.getTotalNewBeeMallOrders(pageQueryUtil);
        PageResult pageResult=new PageResult(newBeeMallOrderList,total,pageQueryUtil.getLimit(), pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    //订单配货
    @Transactional
    public String checkDone(List<Long> orderIds) {
        List<Order> orders = orderMapper.selectByPrimaryKeys(orderIds);
        String error="";
        if(!CollectionUtils.isEmpty(orders)){
            for(Order order :orders){
                if(order.getIsDeleted() == 1){//订单已经被删除
                    error+=order.getOrderNo()+" ";
                    continue;
                }
                if(order.getOrderStatus()!=1){//未支付
                    error+=order.getOrderNo()+" ";
                    continue;
                }
            }
            if(StringUtils.isEmpty(error)){
                //没有错误的订单
                if(orderMapper.checkDone(orderIds)>0){
                    return ServiceResultEnum.SUCCESS.getResult();
                }
                return ServiceResultEnum.DB_ERROR.getResult();
            }else {
                //有错误的订单
                if(error.length()>0 && error.length()<106){
                    return error+"订单未支付，无法配货";
                }
                else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //没找到数据
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    //出库，和上面配货流程一样
    @Transactional
    public String checkOut(List<Long> orderIds) {
        List<Order> orders = orderMapper.selectByPrimaryKeys(orderIds);
        String error="";
        if(!CollectionUtils.isEmpty(orders)){
            for (Order order:orders){
                if(order.getIsDeleted()==1){
                    error+= order.getOrderNo()+",";
                    continue;
                }
                if(order.getOrderStatus()!=1 && order.getOrderStatus()!=2){
                    error+=order.getOrderNo()+",";
                }
            }
            if(StringUtils.isEmpty(error)){
                //没有错误订单
                if(orderMapper.checkOut(orderIds)>0){
                    return ServiceResultEnum.SUCCESS.getResult();
                }
                else{
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            }
            else{
                //存在错误订单
                if(error.length()>0 && error.length()<106){
                    return error+"订单状态为支付或者为配货，请先进行支付或配货";
                }
                else {
                    return "选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //没找到数据
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(List<Long> orderIds) {
        List<Order> orders = orderMapper.selectByPrimaryKeys(orderIds);
        String error="";
        if(!CollectionUtils.isEmpty(orders)){
            for(Order order:orders){
                if(order.getIsDeleted()==1){
                    //已经关闭了的订单
                    error+=order.getOrderNo()+",";
                    continue;
                }
                if(order.getOrderStatus()==4 || order.getOrderStatus()<0){
                    //订单已经完成或者状态为已经关闭，则无法关闭
                    error+=order.getOrderNo()+",";
                }
            }
            //没有错误订单则进行关闭操作
            if(StringUtils.isEmpty(error)){
                if(orderMapper.closeOrder(orderIds, NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus())>0){
                    return ServiceResultEnum.SUCCESS.getResult();
                }
                else{
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            }
            else{
                //存在错误订单,无法关闭
                if(error.length()>0 && error.length()<106){
                    return error+"订单已完成或已经关闭，无法进行关闭操作";
                }
                else {
                    return "选择过多已完成或已关闭的订单，无法关闭订单";
                }
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public PageResult getMyOrderPage(PageQueryUtil pageQueryUtil) {
        List<Order> orderList = orderMapper.findNewBeeMallOrderList(pageQueryUtil);
        int total = orderMapper.getTotalNewBeeMallOrders(pageQueryUtil);
        List<OrderListVO> orderListVOS = new ArrayList<>();
        if(total>0){
            //复制属性
            orderListVOS = BeanUtil.copyList(orderList,OrderListVO.class);
            //设置订单状态字符串
            for(OrderListVO vo:orderListVOS){
                vo.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(vo.getOrderStatus()).getName());
            }
            List<Long> orderIds = orderListVOS.stream().map(OrderListVO::getOrderId).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(orderIds)){
                List<OrderItem> orderItemList = orderItemMapper.selectByOrderIds(orderIds);
                //以orderId为键的map
                Map<Long, List<OrderItem>> orderItemMap = orderItemList.stream().collect(groupingBy(OrderItem::getOrderId));
                //封装OrderListVo中的orderItemVOS
                for(OrderListVO vo:orderListVOS){
                    if(orderItemMap.containsKey(vo.getOrderId())){
                        List<OrderItem> itemList = orderItemMap.get(vo.getOrderId());
                        List<OrderItemVO> orderItemVOS = BeanUtil.copyList(itemList,OrderItemVO.class);
                        vo.setNewBeeMallOrderItemVOS(orderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS,total,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order!=null){
            //判断用户是否有权限
            if(!order.getUserId().equals(userId)){
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //判断订单状态，是否为已出库 3
            if(order.getOrderStatus()!= (byte) NewBeeMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()){
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult()+",该订单还未出库";
            }
            //进行更新操作
            order.setOrderStatus((byte) NewBeeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            order.setUpdateTime(new Date());
            if(orderMapper.updateByPrimaryKeySelective(order)>0){
                return ServiceResultEnum.SUCCESS.getResult();
            }
            else{
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String payOrder(String orderNo, int payType) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order!=null){
            //检查订单状态
            if(order.getOrderStatus() != NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            order.setOrderStatus((byte)NewBeeMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            order.setPayStatus((byte) PayStatus.PAY_SUCCESS.getPayStatus());
            order.setPayType((byte)payType);
            order.setPayTime(new Date());
            order.setUpdateTime(new Date());
            if(orderMapper.updateByPrimaryKeySelective(order)>0){
                return ServiceResultEnum.SUCCESS.getResult();
            }
            else{
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<OrderItemVO> getOrderItems(Long OrderId) {
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderId(OrderId);
        if(CollectionUtils.isEmpty(orderItemList)){
            NewBeeMallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        List<OrderItemVO> orderItemVOS = new ArrayList<>();
        orderItemVOS =BeanUtil.copyList(orderItemList,OrderItemVO.class);
        return orderItemVOS;
    }
}
