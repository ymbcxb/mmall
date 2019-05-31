package com.mmall.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * @author ymbcxb
 * @title
 * @Package com.mmall.common
 * @date 2019/5/31 19:28
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
//保证序列化Json的时候，如果是null的对象，key也会消失
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status){
        this.status = status;
    }

    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, T data){
        this.status = status;
        this.data = data;
    }

    /**
     * 不会被序列化
     * @return
     */
//    @JsonIgnore
    public boolean success(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /**
     * 成功
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    /**
     * 失败
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(Integer errorCode, String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }
}
