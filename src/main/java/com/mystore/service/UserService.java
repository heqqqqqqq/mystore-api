package com.mystore.service;

import com.mystore.common.CommonResponse;
import com.mystore.domain.User;

public interface UserService {

    //登录
    CommonResponse<User> login(String username,String password);
    //检查字段是否可用
    CommonResponse<Object> checkField(String fieldName,String fieldValue);
    //注册
    CommonResponse<Object> register(User user);
    //获取登录用户信息
    CommonResponse<User> getUserDetail(Integer id);
    //忘记密码
    CommonResponse<String> getForgetQuestion(String username);
    //提交忘记密码问题的答案
    CommonResponse<String> checkForgetAnswer(String username, String question,String answer);
    //通过忘记密码问题答案重设密码
    CommonResponse<Object> resetForgetPassword(String username, String newPassword, String forgetToken);
    //登录状态重设密码
    CommonResponse<Object> resetPassword(String oldPassword, String newPassword, User user);
    //登录状态修改个人信息
    CommonResponse<Object> updateUserInfo(User user);
}
