package com.mmall.common;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.common
 * @date 2019/5/31 19:37
 */
public enum ResponseCode {
    SUCCESS(200,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"用户未登录,请登录"),
    ILLEGAL_ARGUMENT(2,"不合法的参数");

    private final int code;
    private final String desc;

    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode(){
        return code;
    }

    public String getDesc(){
        return desc;
    }
}
