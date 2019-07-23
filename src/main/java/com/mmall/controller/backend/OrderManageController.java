package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.controller.backend
 * @date 2019/7/23 20:29
 */
@RestController
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private IOrderService iOrderService;

    /**
     * 订单list
     * @param session
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping("list")
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if(user.getRole() == Const.Role.ROLE_CUSTOMER){
            return ServerResponse.createByErrorMessage("不是管理员，无法操作");
        }
        return iOrderService.orderList(user.getId(),pageNum,pageSize);
    }

}
