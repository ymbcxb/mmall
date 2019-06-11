package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import javax.servlet.http.HttpSession;
import java.util.UUID;

import static com.mmall.common.Const.TOKEN_PREFIX;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.service
 * @date 2019/5/31 19:28
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //检查用户是否存在
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //todo 密码MD5
        String md5Password = MD5Util.MD5EncodeUtf8((password));

        //检查用户登陆密码是否正确
        User user = userMapper.selectLogin(username,md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户或密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }

    @Override
    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        //检查用户是否存在
        if(!validResponse.success()){
            return validResponse;
        }
        //检查邮箱是否存在
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.success()){
            return validResponse;
        }
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //注册
        user.setRole(0);
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createByErrorMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            boolean flag = false;
            //校验
            if(Const.USERNAME.equals(type)){
                flag = true;
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                flag = true;
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
            if(flag) {
                return ServerResponse.createBySuccessMessage("校验成功");
            }
            else{
                return ServerResponse.createByErrorMessage("参数错误");
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.success()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("该用户未设置找回密码的问题");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            //说明这个问题以及问题答案是这个用户的，并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.success()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token =TokenCache.getKey(TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        //判断Token是否一致
        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误");
        }
        return ServerResponse.createByErrorMessage("修改密码错误");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew,User user) {
        //验证旧密码是否正确
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<String> update_information(HttpSession session,User user) {
        //检查邮箱是否可用
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已经存在，请更换email再尝试");
        }
        //todo 检查电话是否可用
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if( updateCount > 0){
            //重新设置Session的值
            User updateUser = userMapper.selectByPrimaryKey(user.getId());
            updateUser.setPassword(StringUtils.EMPTY);
            session.setAttribute(Const.CURRENT_USER,updateUser);
            return ServerResponse.createBySuccess("更新个人信息成功");
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 校验是否管理员
     * @param user
     * @return
     */
    @Override
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }else{
            return ServerResponse.createByError();
        }
    }

}
