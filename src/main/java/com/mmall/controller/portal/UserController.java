package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.controller.portal
 * @date 2019/5/31 19:21
 */
@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;
    /**
     *用户登陆
     * @param username
     * @param password
     * @param session
     * @return
     */
    @GetMapping(value = "login")
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.success()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 用户登出
     * @param session
     * @return
     */
    @GetMapping(value = "logout")
        public ServerResponse<String> logut(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @GetMapping(value = "register")
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 校验数据
     * @param str 具体的邮箱or用户名
     * @param type str的类型：邮箱or用户名
     * @return
     */
    @GetMapping(value = "check_valid")
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @GetMapping(value = "get_user_info")
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户没有登陆，无法获取当前用户信息");
    }

    /**
     * 忘记密码
     * @param username
     * @return
     */
    @GetMapping(value = "forget_get_question")
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }
}
