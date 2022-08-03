package com.mystore.common;

import lombok.Getter;

@Getter//LomBok注解，Spring框架的做法
//枚举类，用于存放响应码
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    ARGUMENT_ILLEGAL(10,"ARGUMENT_ILLEGAL");//非法参数

    private final int code;
    private final String description;

    ResponseCode(int code,String description){
        this.code=code;
        this.description=description;
    }
}
