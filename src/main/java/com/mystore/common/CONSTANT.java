package com.mystore.common;

import lombok.Getter;

//定义一个常量类，用于存放一些常量
public class CONSTANT {
    public static final String LOGIN_USER="loginUser";

    public interface ROLE{
        int CUSTOMER=1;
        int ADMIN=0;
    }

    public static final Integer CATEGORY_ROOT=0;//商品分类根节点

    @Getter
    public enum ProductStatus{

        ON_SALE(1,"on_sale"),//在售
        TAKE_DOWN(2,"take_down"),//下架
        DELETE(3,"delete");//删除

        private final int code;
        private final String description;

        ProductStatus(int code,String description){
            this.code=code;
            this.description=description;
        }

    }

    //支付类型
    @Getter
    public enum PayType{

        ALIPAY(1,"支付宝"),
        WECHAT(2,"微信支付"),
        OTHER(3,"其他类型");

        private final int code;
        private final String description;

        PayType(int code,String description){
            this.code=code;
            this.description=description;
        }

    }

    //订单状态
    @Getter
    public enum OrderStatus{

        CANCEL(1,"已取消"),
        UNPAID(2,"未付款"),
        PAID(3,"已付款"),
        SHIPPED(4,"已发货"),
        SUCCESS(5,"交易成功"),
        CLOSED(6,"交易关闭");

        private final int code;
        private final String description;

        OrderStatus(int code,String description){
            this.code=code;
            this.description=description;
        }
    }

    public static final String PRODUCT_ORDER_BY_PRICE_ASC="price_asc";
    public static final String PRODUCT_ORDER_BY_PRICE_DESC="price_desc";

    public interface CART{
        int CHECKED=1;
        int UNCHECKED=0;
    }

    //支付状态
    public interface AlipayTradeStatus{
        String WAIT_BUYER_PAY="WAIT_BUYER_PAY";
        String TRADE_CLOSED="TRADE_CLOSED";
        String TRADE_SUCCESS="TRADE_SUCCESS";
        String TRADE_FINISHED="TRADE_FINISHED";
    }
    //返回给支付宝
    public interface AlipayCallbackResponse{
        String RESPONSE_SUCCESS="success";
        String RESPONSE_FAILED="failed";
    }

}
