package com.mystore.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mystore_user")
public class User {
    public Integer id;//int类型都使用包装类
    public String username;
    public String password;
    public String email;
    public String phone;

    public String question;
    public String answer;

    private Integer role;//表明是用户还是管理员
}
