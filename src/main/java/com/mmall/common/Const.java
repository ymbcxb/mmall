package com.mmall.common;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.common
 * @date 2019/5/31 20:54
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public static final String TOKEN_PREFIX = "token_";

    public static final String PROPERTIE_FILE = "properties/mmall.properties";

    public interface Role{
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1; //管理员
    }
}
