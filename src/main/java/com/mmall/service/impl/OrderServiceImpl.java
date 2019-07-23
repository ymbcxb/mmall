package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.Product;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.service.impl
 * @date 2019/7/19 1:00
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;


    @Override
    public ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId) {
        //获取下订单的购物车
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);
        if (cartList == null) {
            return ServerResponse.createByErrorMessage("购物车没有数据");
        }
        //组装订单,缺失payment
        Order order = assembleOrder(userId, shippingId);
        //获得订单详情
        List<OrderItem> orderItemList = getOrderItemList(order, cartList);
        //获取实际付款金额payment
        BigDecimal payment = getPayment(orderItemList);
        order.setPayment(payment);
        //新增订单
        int resultCount = orderMapper.insert(order);
        if (order == null) {
            return ServerResponse.createByErrorMessage("创建订单失败");
        }
        orderItemMapper.batchInsert(orderItemList);
        //清空购物车
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        // 获得OrderVo对象
        OrderVo orderVo = assembleOrderVo(order, orderItemList, shippingId);
        orderVo.setOrderItemVoList(orderItemList);
        orderVo.setShippingId(shippingId);
        return ServerResponse.createBySuccess(orderVo);
    }


    @Override
    public ServerResponse cancelOrder(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("该用户没有此订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerResponse.createByErrorMessage("此订单已付款，无法被取消");
        }
        order.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int resultCount = orderMapper.updateByPrimaryKeySelective(order);
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("取消订单成功");
        }
        return ServerResponse.createByErrorMessage("取消订单失败");
    }

    @Override
    public ServerResponse getOrderCartProduct(Integer userId,Long orderNo) {
        //获取订单
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order == null) {
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(order.getOrderNo(),userId);
        if(orderItemList == null){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        List<OrderItemVo> orderItemVoList = assembleOrderItemVo(orderItemList, order.getOrderNo());
        Map<String,Object> map = Maps.newHashMap();
        map.put("orderItemVoList",orderItemVoList);
        map.put("productTotalPrice",order.getPayment());
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<PageInfo> orderList(Integer userId,Integer pageNum,Integer pageSize){
        //取出所有订单
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orders = orderMapper.selectByUserId(userId);
        if(orders == null){
            return ServerResponse.createByErrorMessage("没有订单");
        }
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orders){
            //获取orderItemList
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(order.getOrderNo(), userId);
            //获取orderItemVoList
            List<OrderItemVo> orderItemVoList = assembleOrderItemVo(orderItemList, order.getOrderNo());
            Integer shippingId = order.getShippingId();
            OrderVo orderVo = assembleOrderVo(order, orderItemList, shippingId);
            if(orderVo != null){
                orderVoList.add(orderVo);
            }
        }
        PageInfo pageInfo = new PageInfo(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse orderDetail(Integer userId,Long orderNo){
        //获取订单
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
        OrderVo orderVo = assembleOrderVo(order, orderItemList, order.getShippingId());
        return ServerResponse.createBySuccess(orderVo);
    }

    private List<OrderItemVo> assembleOrderItemVo(List<OrderItem> orderItemList,Long orderNo){
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = new OrderItemVo();
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            if(product != null){
                orderItemVo.setOrderNo(orderNo);
                orderItemVo.setProductId(product.getId());
                orderItemVo.setProductName(product.getName());
                orderItemVo.setProductImage(product.getMainImage());
                orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
                orderItemVo.setQuantity(orderItem.getQuantity());
                orderItemVo.setTotalPrice(orderItem.getTotalPrice());
                orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
            }
            orderItemVoList.add(orderItemVo);
        }
        return orderItemVoList;
    }

    private long generateOrderNo() {
        long orderNo = System.currentTimeMillis();
        return orderNo + new Random().nextInt(100);
    }

    private Order assembleOrder(Integer userId, Integer shippingId) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPaymentType(Const.PayPlatformEnum.ALIPAY.getCode());
        order.setPostage(0);
        order.setStatus(Const.OrderStatusEnum.PAID.getCode());
        order.setCreateTime(new Date());
        return order;
    }


    private BigDecimal getPayment(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = payment.add(orderItem.getTotalPrice());
        }
        return payment;
    }

    private List<OrderItem> getOrderItemList(Order order, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        for (Cart cartItem : cartList) {
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            //组装OrderItem
            Integer quantity = cartItem.getQuantity();
            if (product.getStock() < cartItem.getQuantity()) {
                quantity = product.getStock();
            }
            //组装orderItem
            OrderItem orderItem = assembleOrderItem(order, product, quantity);
            orderItemList.add(orderItem);
            //更新商品
            product.setStock(product.getStock() - quantity);
            productMapper.updateByPrimaryKeySelective(product);
        }
        return orderItemList;
    }

    private OrderItem assembleOrderItem(Order order, Product product, Integer quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setUserId(order.getUserId());
        orderItem.setOrderNo(order.getOrderNo());
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductImage(product.getMainImage());
        orderItem.setCurrentUnitPrice(product.getPrice());
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(BigDecimalUtil.mul(quantity, product.getPrice().doubleValue()));
        orderItem.setCreateTime(new Date());
        orderItem.setUpdateTime(new Date());
        return orderItem;
    }


    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList, Integer shippingId) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setCreateTime(order.getCreateTime());
        orderVo.setOrderItemVoList(orderItemList);
        orderVo.setShippingId(shippingId);
        return orderVo;
    }

}
