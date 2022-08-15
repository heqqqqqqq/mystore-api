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

    public static final String PRODUCT_ORDER_BY_PRICE_ASC="price_asc";
    public static final String PRODUCT_ORDER_BY_PRICE_DESC="price_desc";

    public interface CART{
        int CHECKED=1;
        int UNCHECKED=0;
    }
}
