package com.mystore.controller.front;

import com.mystore.common.CommonResponse;
import com.mystore.domain.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/")
public class UserController {

    //1.登录
    @PostMapping("login")
    public CommonResponse<Object> login(){
        return CommonResponse.createForSuccess();
    }

    //2.注册


    //3.修改密码


    //4.修改用户信息

}
