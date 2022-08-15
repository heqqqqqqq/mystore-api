package com.mystore.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVO {
    private List<CartItemVO> cartItemVOList;
    private BigDecimal cartTotalPrice;
    private Boolean allSelected;//是否全选
    private String productImageServer;
}
