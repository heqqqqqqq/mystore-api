package com.mystore.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDetailVO {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String subImages;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;

    //格式化后的时间戳字符串
    private String createTime;
    private String updateTime;

    //该商品的父分类信息
    private Integer parentCategoryId;
    //图片服务器的根域
    private String imageServer;
}
