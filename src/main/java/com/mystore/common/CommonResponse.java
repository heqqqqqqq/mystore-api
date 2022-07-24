package com.mystore.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
//SpringBoot Jackson序列化成JSON字符串
@JsonInclude(JsonInclude.Include.NON_NULL)    //不显示空的值
public class CommonResponse<T> {

    private Integer code;//响应码
    private String message;//成功或者失败的信息
    private T data;//响应数据

    protected CommonResponse(Integer code,String message,T data){
        this.code=code;
        this.message=message;
        this.data=data;
    }

    //请求成功，无数据返回
    public static <T> CommonResponse <T> createForSuccess(){//只有code和message
        return new CommonResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDescription(),null);//使用封装好的枚举类，提高项目的可维护性
    }
    //请求成功，并返回响应数据
    public static <T> CommonResponse <T> createForSuccess(T data){//只有code和message
        return new CommonResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getDescription(),data);//使用封装好的枚举类，提高项目的可维护性
    }

    //请求错误，默认错误信息
    public static <T> CommonResponse <T> createForError(){
        return new CommonResponse<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDescription(), null);
    }
    //请求错误，指定错误信息
    public static <T> CommonResponse <T> createForError(String message){
        return new CommonResponse<>(ResponseCode.ERROR.getCode(), message,null);
    }
    //请求错误，指定错误码和信息
    public static <T> CommonResponse <T> createForError(int code,String message){
        return new CommonResponse<>(code, message,null);
    }
}
