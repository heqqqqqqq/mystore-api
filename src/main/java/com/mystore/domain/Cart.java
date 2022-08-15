package com.mystore.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mystore_cart")
public class Cart {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "user_id")
    private Integer userId;
    @TableField(value = "product_id")
    private Integer productId;

    private Integer quantity;
    private Integer checked;

    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;

}
