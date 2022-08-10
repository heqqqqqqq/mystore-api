package com.mystore.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@TableName("mystore_user")
public class User {
    @TableId(type= IdType.AUTO)//设置主键AUTO是主键生成策略
    public Integer id;//int类型都使用包装类
    @NotBlank(message = "用户名不能为空")
    public String username;
    @NotBlank(message = "密码不能为空")
    public String password;
    @NotBlank(message = "邮箱不能为空")
    public String email;
    @NotBlank(message = "电话号码不能为空")
    public String phone;

    public String question;
    public String answer;

    private Integer role;//表明是用户还是管理员

    //不用Date而用LocalDateTime
    //Java 8之后开始使用LocalDateTime,Date类线程访问不安全
    //MyBatisPlus会自动化将createTime和updateTime命名转化成create_time、update_time，但最好标注
    @TableField(value="create_time")
    private LocalDateTime createTime;
    @TableField(value="update_time")
    private LocalDateTime updateTime;
}
