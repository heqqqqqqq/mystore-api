package com.mystore.utils.front;

import com.mystore.common.CONSTANT;
import com.mystore.common.CommonResponse;
import com.mystore.domain.User;
import com.mystore.dto.UpdateUserDTO;
import com.mystore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/user/")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    //1.登录
    @PostMapping("login")
    public CommonResponse<User> login(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "密码不能为空") String password,
            HttpSession session){

        CommonResponse<User> result=userService.login(username,password);//返回的结果
        if(result.isSuccess()){//如果成功，就将用户放在session中
            session.setAttribute(CONSTANT.LOGIN_USER,result.getData());
        }
        return result;
    }

    //2.检查相关字段是否可用（是否重复）
    @PostMapping("check_field")
    public CommonResponse<Object> checkField(
            @RequestParam @NotBlank(message = "字段不能为空") String fieldName,
            @RequestParam @NotBlank(message = "字段不能为空") String fieldValue){
        return userService.checkField(fieldName,fieldValue);
    }


    //3.注册
    /*
    1)Spring MVC取值是通过@RequestBody
    2）RequestBody和@Valid完成参数校验
    3）MD5->使用第三方组件时的三种方法（a.自己手动实现。  b.Spring的用法。    c.Springboot的用法）
    */
    @PostMapping("register")
    public CommonResponse<Object> register(
            @RequestBody @Valid User user
    ){
        return userService.register(user);
    }

    //4.获取登录用户信息
    @PostMapping("get_user_detail")
    public CommonResponse<User> getUserDetail(HttpSession session){
        User loginUser=(User)session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser==null){
            return CommonResponse.createForError("用户未登录");
        }
        return userService.getUserDetail(loginUser.getId());
    }

    //5.忘记密码
    @PostMapping("get_forget_question")
    public CommonResponse<String> getForgetQuestion(
            @RequestParam @NotBlank(message = "用户名不能为空") String username){
        return userService.getForgetQuestion(username);
    }

    //6.提交忘记密码问题的答案
    @PostMapping("check_forget_answer")
    public CommonResponse<String> checkForgetAnswer(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "忘记密码问题不能为空") String question,
            @RequestParam @NotBlank(message = "忘记密码问题答案不能为空") String answer){
        return userService.checkForgetAnswer(username,question,answer);
    }

    //7.通过忘记密码问题答案重设密码
    @PostMapping("reset_forget_password")
    public CommonResponse<Object> resetForgetPassword(
            @RequestParam @NotBlank(message = "用户名不能为空") String username,
            @RequestParam @NotBlank(message = "新密码不能为空") String newPassword,
            @RequestParam @NotBlank(message = "重置密码token不能为空") String forgetToken){
        return userService.resetForgetPassword(username,newPassword,forgetToken);
    }

    //8.登录状态重设密码
    @PostMapping("reset_password")
    public CommonResponse<Object> resetPassword(
            @RequestParam @NotBlank(message = "新密码不能为空") String newPassword,
            @RequestParam @NotBlank(message = "密码不能为空") String oldPassword,
            HttpSession session){
        User loginUser=(User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError("用户未登录");
        }
        return userService.resetPassword(oldPassword,newPassword,loginUser);
    }

    //9.登录状态修改个人信息(由于不能修改所有的信息，故需要使用UpdateUserDto)
    @PostMapping("update_user_info")
    public CommonResponse<Object> updateUserInfo(
            @RequestBody @Valid UpdateUserDTO updateUser,
            HttpSession session){
        User loginUser=(User) session.getAttribute(CONSTANT.LOGIN_USER);
        if(loginUser == null){
            return CommonResponse.createForError("用户未登录");
        }
        System.out.println(loginUser.getPassword()+","+loginUser.getUsername());
        loginUser.setEmail(updateUser.getEmail());
        loginUser.setPhone(updateUser.getPhone());
        loginUser.setQuestion(updateUser.getQuestion());
        loginUser.setAnswer(updateUser.getAnswer());

        CommonResponse<Object> result=userService.updateUserInfo(loginUser);
        if(result.isSuccess()){
            loginUser = userService.getUserDetail(loginUser.getId()).getData();
            session.setAttribute(CONSTANT.LOGIN_USER, loginUser);
            return CommonResponse.createForSuccess();
        }
        return CommonResponse.createForSuccess(result.getMessage());
    }

    //10. 退出登录
    @GetMapping("logout")
    public CommonResponse<Object> logout(HttpSession session){
        session.removeAttribute(CONSTANT.LOGIN_USER);
        return CommonResponse.createForSuccess("退出登录成功");
    }

}
