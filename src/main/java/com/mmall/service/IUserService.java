package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

import javax.servlet.http.HttpSession;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.service
 * @date 2019/5/31 19:27
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<String> update_information(HttpSession session,User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse<String> checkAdminRole(User user);
}
