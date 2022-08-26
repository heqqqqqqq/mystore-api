package com.mystore.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mystore_pay_info")
public class PayInfo {

    @TableId(type = IdType.AUTO)
    Integer id;
    @TableField("user_id")
    Integer userId;
    @TableField("order_no")
    Long orderNo;
    @TableField("payment_type")
    Integer paymentType;
    @TableField("trade_no")
    String tradeNo;
    @TableField("trade_status")
    String tradeStatus;
    @TableField("create_time")
    LocalDateTime createTime;
    @TableField("update_time")
    LocalDateTime updateTime;

}
