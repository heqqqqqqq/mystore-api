package com.mystore.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
//只能修改邮箱、手机号、问题，答案
public class UpdateUserDTO {
    private Integer id;

    @NotBlank(message = "邮箱不能为空")
    private String email;
    @NotBlank(message = "电话号码不能为空")
    private String phone;

    private String question;
    private String answer;
}
