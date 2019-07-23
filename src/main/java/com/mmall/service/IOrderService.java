package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.service
 * @date 2019/7/19 1:00
 */
public interface IOrderService {
    ServerResponse<OrderVo> createOrder(Integer userId, Integer shippingId);
    ServerResponse cancelOrder(Integer userId, Long orderNo);
    ServerResponse getOrderCartProduct(Integer userId,Long orderNo);
    ServerResponse<PageInfo> orderList(Integer userId, Integer pageNum, Integer pageSize);
    ServerResponse orderDetail(Integer userId,Long orderNo);
}
