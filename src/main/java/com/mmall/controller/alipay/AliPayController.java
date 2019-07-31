package com.mmall.controller.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IAliService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.controller.alipay
 * @date 2019/7/23 20:43
 */
@RestController
@RequestMapping("/order/")
public class AliPayController {

    private static Logger log = LoggerFactory.getLogger(AliPayController.class);
    @Autowired
    private IAliService iAliService;
    /**
     * 支付
     *
     * @param session
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("pay")
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iAliService.pay(user.getId(), orderNo, path);
    }

    /**
     * 阿里支付回调接口
     *
     * @param request
     * @return
     */
    @RequestMapping("alipay_callback")
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        if(params == null || params.size() == 0){
            return Const.AlipayCallback.RESPONSE_FAILED;
        }
        log.info("支付宝回调,sign: {}, trade_status: {}, 参数: {}", params.get("sign"), params.get("trade_status"), params.toString());

        //验证回调的正确性
        params.remove("sign_type");
        try {
            boolean alipayRSACheck = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheck) {
                return ServerResponse.createByErrorMessage("非法请求，验证不通过");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常", e);
            e.printStackTrace();
        }

        //todo 验证各种数据
        ServerResponse serverResponse = iAliService.aliCallback(params);
        if (serverResponse.success()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }


    @RequestMapping("query_order_pay_status")
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iAliService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.success()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
