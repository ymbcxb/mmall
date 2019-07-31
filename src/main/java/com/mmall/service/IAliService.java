package com.mmall.service;

import com.mmall.common.ServerResponse;

import java.util.Map;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.service
 * @date 2019/7/23 20:58
 */
public interface IAliService {
    ServerResponse pay(Integer userId, Long orderNo, String path);
    ServerResponse aliCallback(Map<String,String> params);
    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);
}
