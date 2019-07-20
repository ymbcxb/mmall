package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.sun.net.httpserver.HttpPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.controller.backend
 * @date 2019/6/3 15:58
 */
@RestController
@RequestMapping("/manage/user/")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    /**
     * 管理员登陆
     * @param username
     * @param password
     * @param session
     * @return
     */
    @GetMapping(value = "login")
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.success()){
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN){
                session.setAttribute(Const.CURRENT_USER,response.getData());
            }else{
                return ServerResponse.createByErrorMessage("不是管理员，无法登陆");
            }
        }
        return response;
    }

    /**
     * 查询用户列表
     * @param session
     * @param pageSize
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "list")
    public ServerResponse<PageInfo> list(HttpSession session,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                         @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if( user == null ){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        if( user.getRole() != Const.Role.ROLE_ADMIN){
            return ServerResponse.createByErrorMessage("不是管理员，无法操作");
        }
        return iUserService.getUserList(pageSize,pageNum);
    }
}
