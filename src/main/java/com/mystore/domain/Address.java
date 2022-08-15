package com.mystore.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@TableName("mystore_address")
public class Address {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "user_id")
    private Integer userId;
    @TableField(value = "address_name")
    @NotEmpty(message = "地址名称不能为空")
    private String addressName;
    @TableField(value = "address_phone")
    private String addressPhone;
    @TableField(value = "address_mobile")
    @NotEmpty(message = "手机号码不能为空")
    private String addressMobile;
    @TableField(value = "address_province")
    @NotEmpty(message = "省不能为空")
    private String addressProvince;
    @TableField(value = "address_city")
    @NotEmpty(message = "城市不能为空")
    private String addressCity;
    @TableField(value = "address_district")
    @NotEmpty(message = "区县不能为空")
    private String addressDistrict;
    @TableField(value = "address_detail")
    private String addressDetail;
    @TableField(value = "address_zip")
    private String addressZip;
    @TableField(value = "create_time")
    private LocalDateTime createTime;
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

}
